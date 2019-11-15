package composants;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.appareils.incontrolables.ISecheCheveuxOffered;
import interfaces.productions.aleatoires.IEolienneRequired;
import ports.eolienne.EolienneStringDataInPort;
import ports.eolienne.EolienneStringDataOutPort;

/**
 * La classe <code>Eolienne</code>
 * 
 * <p>
 * Created on : 2019-11-09
 * </p>
 * 
 * @author 3410456
 *
 */

public class Eolienne extends AbstractComponent implements IEolienneRequired, ISecheCheveuxOffered {
	/**
	 * Le port par lequel l'eolienne recoit des donnees representees par la classe
	 * StringData
	 */
	public EolienneStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on recoit des messages
	 */
	public EolienneStringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	/**
	 * 
	 */
	protected boolean isOn = false;
	protected Timer timer = new Timer();

	public Eolienne(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new EolienneStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new EolienneStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

	}

	public Eolienne(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String in, String out)
			throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		stringDataOutPort = new EolienneStringDataOutPort(out, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		stringDataInPort = new EolienneStringDataInPort(in, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(10);
					System.out.println("ici");
					String msg = "hello je suis EOLIENNE";
					StringData m = new StringData();
					m.setMessage(msg);
					messages_envoyes.put("controleur", new Vector<StringData>());
					messages_envoyes.get("controleur").add(m);
					sendMessage("controleur");
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage("Eolienne recoit : " + messages_recus.remove(0).getMessage());
		switch (msg.getMessage()) {
		case "switchOn":
			if (!isOn) {
				isOn = true;
				this.logMessage("Demarrage de l'eolienne");
			}
			timer.schedule(new ProductionTask(this), 0, 500);
			break;
		case "switchOff":
			if (isOn) {
				isOn = false;
				this.logMessage("Arret de l'eolienne");
			}
			timer.purge();
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
		this.logMessage("Eolienne shutdown");
		try {
			stringDataOutPort.unpublishPort();
			stringDataInPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	public void print() {
		// Il faut faire un truc de prod plus realiste
		this.logMessage("Eolienne tourne: production de X kW");
	}

	class ProductionTask extends TimerTask {
		Eolienne e;

		public ProductionTask(Eolienne e) {
			this.e = e;
		}

		@Override
		public void run() {
			if (e.isOn) {
				e.print();
			}
		}

	}

}
