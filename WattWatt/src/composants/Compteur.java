package composants;

import interfaces.ICompteurOffered;
import interfaces.ICompteurRequired;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import ports.compteur.CompteurStringDataInPort;
import ports.compteur.CompteurStringDataOutPort;
import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * La classe <code>Compteur</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

public class Compteur extends AbstractComponent implements ICompteurOffered,
		ICompteurRequired {

	int val = 0;

	/**
	 * Le port par lequel le compteur recoit des donnees representees par la
	 * classe StringData
	 */
	public CompteurStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on envoie des messages: on fait la difference
	 * entre StringData et CompteurData pour le moment
	 */
	public CompteurStringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	protected Timer timer = new Timer();
	protected Random rand = new Random();

	public Compteur(String reflectionInboundPortURI, int nbThreads,
			int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new CompteurStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new CompteurStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

	}

	public Compteur(String reflectionInboundPortURI, int nbThreads,
			int nbSchedulableThreads, String in, String out) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		stringDataOutPort = new CompteurStringDataOutPort(out, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		stringDataInPort = new CompteurStringDataInPort(in, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		val = 900 + rand.nextInt(1000);

	}

	@Override
	public void execute() {
		timer.schedule(new CompteurInformationTask(this), 0, 3000);
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Compteur recoit : "
				+ messages_recus.remove(0).getMessage());
		switch (msg.getMessage()) {
		case "value":
			String message = "compteur" + ":total:" + this.val;
			StringData sD = new StringData();
			sD.setMessage(message);
			messages_envoyes.put("controleur", new Vector<StringData>());
			messages_envoyes.get("controleur").add(sD);
			try {
				sendMessage("controleur");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		this.logMessage("Compteur shutdown");
		timer.cancel();
		try {
			stringDataOutPort.unpublishPort();
			stringDataInPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		stringDataInPort.unpublishPort();
		stringDataOutPort.unpublishPort();
		super.finalise();
	}

	class CompteurInformationTask extends TimerTask {
		Compteur v;

		public CompteurInformationTask(Compteur val) {
			this.v = val;
		}

		public void run() {

			String message = "compteur" + ":total:" + this.v.val;
			this.v.val = 900 + rand.nextInt(1000);

			StringData sD = new StringData();
			sD.setMessage(message);
			messages_envoyes.put("controleur", new Vector<StringData>());
			messages_envoyes.get("controleur").add(sD);
			try {
				v.sendMessage("controleur");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
