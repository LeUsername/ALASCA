package composants;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.productions.intermittentes.IBatterieOffered;
import interfaces.productions.intermittentes.IBatterieRequired;
import ports.batterie.BatterieStringDataInPort;
import ports.batterie.BatterieStringDataOutPort;

/**
 * La classe <code>Batterie</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

public class Batterie extends AbstractComponent implements IBatterieOffered, IBatterieRequired {

	/**
	 * Le port par lequel la batterie recoit des donnees representees par la classe
	 * StringData
	 */
	public BatterieStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on envoie des messages
	 */
	public BatterieStringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	protected Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	/**
	 * Variables representant les capacites actuelle et maximale de la batterie
	 */
	protected int quantite;
	protected int quantiteMax;
	protected Timer timer = new Timer();
	protected boolean isOn;

	public Batterie(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, int quantiteMax)
			throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new BatterieStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new BatterieStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

		this.quantite = 0;
		this.quantiteMax = quantiteMax;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Batterie started");
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void execute() throws Exception {
		super.execute();
	}

	public void print() {
		this.logMessage(String.valueOf(quantite));
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage("Batterie recoit : " + messages_recus.remove(0).getMessage());
		timer.purge();
		switch (msg.getMessage()) {
		case "charge":
			isOn = true;
			this.logMessage("La batterie est allumée");
			timer.schedule(new ChargeTask(this), 0, 300);
			break;
		case "discharge":
			isOn = false;
			this.logMessage("La batterie est éteinte");
			timer.schedule(new DechargeTask(this), 0, 300);
			break;
		case "value":
			String message = "Batterie à :" + quantite + " sur " + quantiteMax;
			StringData sD = new StringData();
			sD.setMessage(message);
			messages_envoyes.put("controleur", new Vector<StringData>());
			messages_envoyes.get("controleur").add(sD);
			this.logMessage(message);
			this.sendMessage("controleur");
			break;
		case "shutdown":
			shutdown();
			break;

		}
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Batterie shutdown");
		try {
			stringDataOutPort.unpublishPort();
			stringDataInPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	class ChargeTask extends TimerTask {
		Batterie v;

		public ChargeTask(Batterie val) {
			this.v = val;
		}

		public void run() {
			if (v.isOn && v.quantite < v.quantiteMax) {
				v.quantite++;
				v.print();
			}
		}
	}

	class DechargeTask extends TimerTask {
		Batterie v;

		public DechargeTask(Batterie val) {
			this.v = val;
		}

		public void run() {
			if (v.quantite > 0) {
				v.quantite--;
				v.print();
			}
		}
	}
}
