package composants;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import interfaces.IStringDataOffered;
import interfaces.IStringDataRequired;
import ports.StringDataInPort;
import ports.StringDataOutPort;

/**
 * La classe <code>Compteur</code> qui represente le compteur electrique. Cette
 * classe de recuperer la consommation electrique totale du manoir.
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author Thierno BAH, Pascal ZHENG
 *
 */

public class Compteur extends AbstractComponent implements IStringDataOffered, IStringDataRequired {

	/**
	 * Les ports par lesquels le compteur envoie des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataInPort> stringDataInPort;

	/**
	 * Les ports par lesquels le compteur recoit des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataOutPort> stringDataOutPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	/**
	 * URI du composant
	 */
	public String URI;

	/**
	 * Objet permettant de declencher un comportement
	 */
	protected Timer timer = new Timer();

	/**
	 * Pour l'instant la valeur du compteur est decider aléatoirement
	 */
	protected Random rand = new Random();

	/**
	 * Represente la consomation electrique actuel
	 */
	int val = 0;
	
	/**
	 * Liste des uris
	 */
	protected Vector<String> uris;

	/**
	 * create a passive component if both <code>nbThreads</code> and
	 * <code>nbSchedulableThreads</code> are both zero, and an active one with
	 * <code>nbThreads</code> non schedulable thread and
	 * <code>nbSchedulableThreads</code> schedulable threads otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	nbThreads &gt;= 0
	 * pre	nbSchedulableThreads &gt;= 0
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param reflectionInboundPortURI
	 *            URI of the inbound port offering the <code>ReflectionI</code>
	 *            interface.
	 * @param nbThreads
	 *            number of threads to be created in the component pool.
	 * @param nbSchedulableThreads
	 *            number of threads to be created in the component schedulable pool.
	 * @throws Exception
	 */
	public Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		URI = reflectionInboundPortURI;
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
	}
	
	public Compteur(String uri, int nbThreads, int nbSchedulableThreads, Vector<String> uris) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.URI = uri;
		this.addOfferedInterface(IStringDataOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.uris = uris;
		updateURI();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Compteur starting");
		val = 900 + rand.nextInt(1000);
	}

	@Override
	public void execute() {
		timer.schedule(new CompteurInformationTask(this), 0, 3000);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		timer.cancel();
		try {
			for (String s : stringDataOutPort.keySet()) {
				stringDataOutPort.get(s).unpublishPort();
			}
			for (String s : stringDataInPort.keySet()) {
				stringDataInPort.get(s).unpublishPort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		try {
			for (String s : stringDataOutPort.keySet()) {
				stringDataOutPort.get(s).unpublishPort();
			}
			for (String s : stringDataInPort.keySet()) {
				stringDataInPort.get(s).unpublishPort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

	/**
	 * Creer une connexion entre <code> uriCible </code> et l'appareil
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	uriCible != null
	 * pre	in != null
	 * pre	out != null
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param uriCible
	 *            uri du composant a connecter
	 * @param in
	 *            nom du DataInPort de uriCible
	 * @param out
	 *            nom du DataOutPort de uriCible
	 * @throws Exception
	 */
	public void plug(String uriCible, String in, String out) throws Exception {
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Compteur recoit : " + messages_recus.remove(0).getMessage());
		switch (msg.getMessage()) {
		case "value":
			String message = "compteur" + ":total:" + this.val;
			envoieString("controleur", message);
			break;
		}
	}

	/**
	 * Envoie le message <code>msg</code> sur le composant d'URI <code>uri</code>
	 * 
	 * @param uri
	 *            URI du composant vers lequel on veut envoyer <code>msg</code>
	 * @param msg
	 *            message à envoyer
	 * @throws Exception
	 */
	public void envoieString(String uri, String msg) throws Exception {
		StringData m = new StringData();
		m.setMessage(msg);
		messages_envoyes.put(uri, new Vector<StringData>());
		messages_envoyes.get(uri).add(m);
		sendMessage(uri);
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.get(uri).send(m);
		return m;
	}
	
	/**
	 * Methode permettant d'attribuer des DataIn et DataOut aux differentes URI
	 * 
	 * @throws Exception
	 */
	public void updateURI() throws Exception {
		for (String appareilURI : uris) {
			String randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataInPort.put(appareilURI, new StringDataInPort(randomURIPort, this));
			this.addPort(stringDataInPort.get(appareilURI));
			this.stringDataInPort.get(appareilURI).publishPort();

			randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataOutPort.put(appareilURI, new StringDataOutPort(randomURIPort, this));
			this.addPort(stringDataOutPort.get(appareilURI));
			this.stringDataOutPort.get(appareilURI).publishPort();
		}
	}

	/**
	 * Classe permettant d'envoyer a intervalles reguliers la consommation
	 * electrique de la propriete. Elle herite donc de TimerTask pour simuler ces
	 * intervalles.
	 * 
	 * <p>
	 * Created on : 2019-11-16
	 * </p>
	 * 
	 * @author Thierno BAH, Pascal ZHENG
	 *
	 */
	class CompteurInformationTask extends TimerTask {
		Compteur v;

		public CompteurInformationTask(Compteur val) {
			this.v = val;
		}

		public void run() {
			String message = "compteur" + ":total:" + this.v.val;
			this.v.val = 900 + rand.nextInt(1000);
			try {
				this.v.envoieString("controleur", message);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
