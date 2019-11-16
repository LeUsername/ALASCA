package composants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * La classe <code>Controleur</code> qui represente le composant Controleur.
 * C'est l'objet central de l'application qui va decider des actions des autres
 * composants en fonction des donnees qu'il recoit
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author Zheng Pascal - Bah Thierno
 *
 */
public class Controleur extends AbstractComponent implements IStringDataOffered, IStringDataRequired{

	/**
	 * Les ports par lesquels le controleur envoie des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataInPort> stringDataInPort;

	/**
	 * Les ports par lesquels le controleur recoit des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataOutPort> stringDataOutPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	public Vector<StringData> messages_recus = new Vector<StringData>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> controleurMessages = new ConcurrentHashMap<>();

	/**
	 * URI du composant
	 */
	public String URI;

	/**
	 * Liste des uris
	 */
	protected Vector<String> uris;

	/**
	 * Cet entier vas servir a stocker les informations recues du compteur
	 */
	public int consommation = 0;

	/**
	 * Boolean qui permet de verifier si la communication est possible vers les
	 * composants sous nommés
	 */
	public boolean eolienneFonctionne = true;
	public boolean compteurFonctionne = true;

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads, Vector<String> uris) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.URI = uri;
		this.addOfferedInterface(IStringDataOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.uris = uris;
		updateURI();
	}

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
		Collection<Runnable> tasks = new Vector<Runnable>();

		Runnable compteurTask = new Runnable() {
			public void run() {
				try {
					envoieString("compteur", "hello from controleur");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable batterieTask = new Runnable() {
			public void run() {
				try {
					envoieString("batterie", "charge");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable eolienneTask = new Runnable() {
			public void run() {
				try {
					envoieString("eolienne", "switchOn");
					Random r = new Random();
					while (eolienneFonctionne) {
						// Pour l'instant on se sert du boolean pour representer les conditions
						// meteorologique pour l'activation de l'eolienne
						if (r.nextBoolean())
							envoieString("eolienne", "switchOff");
						else
							envoieString("eolienne", "switchOn");
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		tasks.add(compteurTask);
		tasks.add(eolienneTask);
		tasks.add(batterieTask);

		ExecutorService threads = Executors.newFixedThreadPool(3);
		for (Runnable t : tasks)
			threads.execute(t);
	}

	@Override
	public void execute() throws Exception {
		super.execute();

	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		eolienneFonctionne = false;
		compteurFonctionne = false;
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
		eolienneFonctionne = false;
		super.finalise();
	}

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
		String message = messages_recus.remove(0).getMessage();
		this.logMessage(" Controleur recoit : " + message);
		String[] messageSplit = message.split(":");
		if (messageSplit[0].equals("batterie")) {
			if (messageSplit[1].equals("charge")) {
				if (messageSplit[2].equals("100")) {
					envoieString("batterie", "discharge");
				}
			}
		} else if (messageSplit[0].equals("compteur")) {
			if (messageSplit[1].equals("total")) {
				consommation = Integer.valueOf(messageSplit[2]);
			}
		}

	}

	public void envoieString(String uri, String msg) throws Exception {
		StringData m = new StringData();
		m.setMessage(msg);
		controleurMessages.put(uri, new Vector<StringData>());
		controleurMessages.get(uri).add(m);
		sendMessage(uri);
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
			this.stringDataInPort.put(appareilURI, new StringDataInPort(randomURIPort, this));
			this.addPort(stringDataInPort.get(appareilURI));
			this.stringDataInPort.get(appareilURI).publishPort();

			randomURIPort = java.util.UUID.randomUUID().toString();
			this.stringDataOutPort.put(appareilURI, new StringDataOutPort(randomURIPort, this));
			this.addPort(stringDataOutPort.get(appareilURI));
			this.stringDataOutPort.get(appareilURI).publishPort();
		}
	}

}
