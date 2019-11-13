package composants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import interfaces.IControleurOffered;
import interfaces.IControleurRequired;
import ports.controleur.ControleurStringDataInPort;
import ports.controleur.ControleurStringDataOutPort;

/**
 * La classe <code>Controleur</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */
public class Controleur extends AbstractComponent implements IControleurOffered, IControleurRequired {

	/**
	 * Le port par lequel le controleur envoie des donnees representees par la
	 * classe StringData
	 */
	// public ControleurStringDataInPort stringDataInPort;
	// public ControleurStringDataInPort stringDataInPort2;
	public HashMap<String, ControleurStringDataInPort> stringDataInPort;

	// public ControleurStringDataOutPort stringDataOutPort;
	// public ControleurStringDataOutPort stringDataOutPort2;
	public HashMap<String, ControleurStringDataOutPort> stringDataOutPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	public Vector<StringData> messages_recus = new Vector<StringData>();

	/**
	 * Ces 3 entiers vont servir a stocker les informations recues du compteur
	 */
	public int consommation = 0;
	public int productionAleatoire = 0;
	public int productionIntermittente = 0;
	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> controleurMessages = new ConcurrentHashMap<>();
	/**
	 * Liste des uris
	 */
	protected Vector<String> uris;

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads, Vector<String> uris) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleurOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();

		this.uris = uris;

		for (String appareilURI : uris) {
			String randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataInPort.put(appareilURI, new ControleurStringDataInPort(randomURIPort, this));
			this.addPort(stringDataInPort.get(appareilURI));
			this.stringDataInPort.get(appareilURI).publishPort();

			randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataOutPort.put(appareilURI, new ControleurStringDataOutPort(randomURIPort, this));
			this.addPort(stringDataOutPort.get(appareilURI));
			this.stringDataOutPort.get(appareilURI).publishPort();
		}
	}

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);

		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();

	}

	@Override
	public void start() throws ComponentStartException {
		super.start();

		Collection<Runnable> tasks = new Vector<Runnable>();
		Runnable task2 = new Runnable() {
			public void run() {
				try {
					// Thread.sleep(100);
					// String msg = "charge";
					// StringData m = new StringData();
					// m.setMessage(msg);
					// controleurMessages.put("batterie", new Vector<StringData>());
					// controleurMessages.get("batterie").add(m);
					// sendMessage("batterie");
					//
					// Thread.sleep(2000);
					// msg = "discharge";
					// m = new StringData();
					// m.setMessage(msg);
					// controleurMessages.put("batterie", new Vector<StringData>());
					// controleurMessages.get("batterie").add(m);
					// sendMessage("batterie");
					//
					// Thread.sleep(3000);
					// msg = "value";
					// m = new StringData();
					// m.setMessage(msg);
					// controleurMessages.put("batterie", new Vector<StringData>());
					// controleurMessages.get("batterie").add(m);
					// sendMessage("batterie");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Runnable task1 = new Runnable() {
			public void run() {
				try {
					String msg = "Hello from controlla";
					StringData m = new StringData();
					m.setMessage(msg);
					// for (String appareilURI : uris) {
					// controleurMessages.put(appareilURI, new Vector<StringData>());
					// controleurMessages.get(appareilURI).add(m);
					controleurMessages.put("compteur", new Vector<StringData>());
					controleurMessages.get("compteur").add(m);
					sendMessage("compteur");
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		tasks.add(task1);
		tasks.add(task2);

		ExecutorService threads = Executors.newFixedThreadPool(2);
		for (Runnable t : tasks)
			threads.execute(t);
	}

	@Override
	public void execute() throws Exception {
		super.execute();

	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Controleur recoit : " + messages_recus.remove(0).getMessage());
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.stringDataInPort.get(uri).send(m);
		return m;
	}

	public void updateURI() throws Exception {
		for (String appareilURI : uris) {
			String randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataInPort.put(appareilURI, new ControleurStringDataInPort(randomURIPort, this));
			this.addPort(stringDataInPort.get(appareilURI));
			this.stringDataInPort.get(appareilURI).publishPort();

			randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataOutPort.put(appareilURI, new ControleurStringDataOutPort(randomURIPort, this));
			this.addPort(stringDataOutPort.get(appareilURI));
			this.stringDataOutPort.get(appareilURI).publishPort();
		}
	}
	
	public void plug(String uriCible, String in, String out) throws Exception {
		this.stringDataInPort.put(uriCible, new ControleurStringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new ControleurStringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
}
