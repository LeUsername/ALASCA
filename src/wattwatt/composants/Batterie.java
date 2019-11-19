package wattwatt.composants;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import wattwatt.data.StringData;
import wattwatt.interfaces.IStringDataOffered;
import wattwatt.interfaces.IStringDataRequired;
import wattwatt.ports.StringDataInPort;
import wattwatt.ports.StringDataOutPort;

/**
 * La classe <code>Batterie</code> represente dans notre application une source
 * d'energie fiable mais limitee par sa capacite
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author Thierno BAH, Pascal ZHENG
 *
 */

public class Batterie extends AbstractComponent implements IStringDataOffered, IStringDataRequired {
	/**
	 * Les ports par lesquels la Batterie envoie des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataInPort> stringDataInPort;

	/**
	 * Les ports par lesquels la Batterie recoit des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataOutPort> stringDataOutPort;

	/**
	 * La liste des messages recues, representees par la classe StringData
	 */
	protected Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer, representees par la classe StringData.
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	/**
	 * URI du composant
	 */
	public String URI;

	/**
	 * Variables representant les capacites actuelles et maximales de la batterie,
	 * ainsi que son activite
	 */
	protected int quantite;
	protected int quantiteMax;
	protected boolean isOn;
	
	/**
	 * Liste des uris
	 */
	protected Vector<String> uris;

	/**
	 * Objet permettant de declencher un comportement
	 */
	protected Timer timer = new Timer();

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
	 * pre quantiteMax &gt;= 0
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
	 * @param quantiteMax
	 *            quantite maximum d'energie stockable
	 * @throws Exception
	 */
	public Batterie(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, int quantiteMax)
			throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		URI = reflectionInboundPortURI;

		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.quantite = 0;
		this.quantiteMax = quantiteMax;
	}
	
	public Batterie(String uri, int nbThreads, int nbSchedulableThreads, int quantiteMax,Vector<String> uris) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.URI = uri;
		this.addOfferedInterface(IStringDataOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.uris = uris;
		this.quantite = 0;
		this.quantiteMax = quantiteMax;
		updateURI();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Batterie starting");
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(100);
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

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Batterie shutdown");
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

	/**
	 * Affiche la quantite de batterie disponible a cet instant
	 */
	public void printValue() {
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
			timer.schedule(new DechargeTask(this), 0, 5000);
			break;
		case "value":
			String message = "Batterie à :" + quantite + " sur " + quantiteMax;
			this.logMessage(message);
			envoieString("controleur", message);
			break;
		case "shutdown":
			shutdown();
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
	 * Classe permettant le chargement de la batterie. Cette classe est un TimerTask
	 * afin de pouvoir le relancer toutes les X unites de temps
	 * 
	 * <p>
	 * Created on : 2019-11-16
	 * </p>
	 * 
	 * @author Thierno BAH, Pascal ZHENG
	 *
	 */
	class ChargeTask extends TimerTask {
		Batterie v;

		public ChargeTask(Batterie val) {
			this.v = val;
		}

		public void run() {
			if (v.isOn && v.quantite < v.quantiteMax) {
				v.quantite++;
				v.printValue();
			}
			if (v.quantite == v.quantiteMax) {
				String message = URI + ":charge:100";
				try {
					v.envoieString("controleur", message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Classe permettant le dechargement de la batterie. Cette classe est un
	 * TimerTask afin de pouvoir le relancer toutes les X unites de temps
	 * 
	 * <p>
	 * Created on : 2019-11-16
	 * </p>
	 * 
	 * @author Thierno BAH, Pascal ZHENG
	 *
	 */
	class DechargeTask extends TimerTask {
		Batterie v;

		public DechargeTask(Batterie val) {
			this.v = val;
		}

		public void run() {
			if (v.quantite > 0) {
				v.quantite--;
				v.printValue();
			}
		}
	}
}
