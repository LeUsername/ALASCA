package wattwattRenamed.composants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import wattwattRenamed.data.StringData;
import wattwattRenamed.interfaces.IStringDataOffered;
import wattwattRenamed.interfaces.IStringDataRequired;
import wattwattRenamed.ports.StringDataInPort;
import wattwattRenamed.ports.StringDataOutPort;

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
public class Controleur extends AbstractComponent implements IStringDataOffered, IStringDataRequired {

	// Ajouts du 27/11 Uri et des new methodes plugs
	/**
	 * URI du composant
	 */
	public String COMPTEUR_URI;
	public String BATTERIE_URI;
	public String EOLIENNE_URI;
	public String LAVELINGE_URI;
	public String REFRIGERATEUR_URI;
	public String SECHECHEVEUX_URI;

	//

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
	 * Cet entier va servir a stocker les informations recues du compteur
	 */
	public int consommation = 0;

	/**
	 * Cet entier va servir a stocker les informations recues des unites de
	 * production
	 */
	public int production = 0;

	/**
	 * Cet entier va servir a stocker les informations recues des unites de
	 * production
	 */
	public int batterie = 0;

	/**
	 * Boolean qui permet de verifier si la communication est possible vers les
	 * composants sous nommés
	 */
	public boolean eolienneFonctionne = true;
	public boolean compteurFonctionne = true;
	public boolean laveLingeFonctionne = true;
	public boolean refrigerateurFonctionne = true;

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads, Vector<String> uris) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.URI = uri;
		this.addOfferedInterface(IStringDataOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.uris = uris;
		this.tracer.setRelativePosition(0, 0);
		updateURI();
	}

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
	 * @param reflectionInboundPortURI URI of the inbound port offering the
	 *                                 <code>ReflectionI</code> interface.
	 * @param nbThreads                number of threads to be created in the
	 *                                 component pool.
	 * @param nbSchedulableThreads     number of threads to be created in the
	 *                                 component schedulable pool.
	 * @throws Exception
	 */
	public Controleur(String uri, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);
		this.addOfferedInterface(IStringDataOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
		this.tracer.setRelativePosition(0, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
		Collection<Runnable> tasks = new Vector<Runnable>();

		Runnable compteurTask = new Runnable() {
			public void run() {
				try {
					envoieString(COMPTEUR_URI, "hello from controleur");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable batterieTask = new Runnable() {
			public void run() {
				try {
					envoieString(BATTERIE_URI, Batterie.CHARGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable eolienneTask = new Runnable() {
			public void run() {
				try {
					envoieString(EOLIENNE_URI, Eolienne.SWITCHON);
					Random r = new Random();
					while (eolienneFonctionne) {
						// Pour l'instant on se sert du boolean pour representer les conditions
						// meteorologique pour l'activation de l'eolienne
						if (r.nextBoolean())
							envoieString(EOLIENNE_URI, Eolienne.SWITCHOFF);
						else
							envoieString(EOLIENNE_URI, Eolienne.SWITCHON);
						Thread.sleep(3000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable laveLingeTask = new Runnable() {
			public void run() {
				try {
					envoieString(LAVELINGE_URI, "hello from controleur");
					Random r = new Random();
					while (laveLingeFonctionne) {
						int value = r.nextInt(1000);
						if (value > 950)
							envoieString(LAVELINGE_URI, LaveLinge.RETARD);
						else if (value < 50)
							envoieString(LAVELINGE_URI, LaveLinge.AVANCE);
						Thread.sleep(25000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable frigoTask = new Runnable() {
			public void run() {
				try {
					envoieString(REFRIGERATEUR_URI,Refrigerateur.SWITCHON);
					while (refrigerateurFonctionne) {
						// Si la consommation est trop elevee
						if (consommation > production + 50) {
							envoieString(REFRIGERATEUR_URI, Refrigerateur.SUSPEND);
						} else {
							envoieString(REFRIGERATEUR_URI, Refrigerateur.RESUME);
						}
						Thread.sleep(3000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		tasks.add(compteurTask);
		tasks.add(eolienneTask);
		tasks.add(batterieTask);
		tasks.add(laveLingeTask);
		tasks.add(frigoTask);

		ExecutorService threads = Executors.newFixedThreadPool(5);
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
		refrigerateurFonctionne = false;
		laveLingeFonctionne = false;
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
	 * @param uriCible uri du composant a connecter
	 * @param in       nom du DataInPort de uriCible
	 * @param out      nom du DataOutPort de uriCible
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

	// ajout du 27/11
	public void plugCompteur(String uriCible, String in, String out) throws Exception {
		this.COMPTEUR_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	
	public void plugBatterie(String uriCible, String in, String out) throws Exception {
		this.BATTERIE_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	
	public void plugEolienne(String uriCible, String in, String out) throws Exception {
		this.EOLIENNE_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	
	public void plugLaveLinge(String uriCible, String in, String out) throws Exception {
		this.LAVELINGE_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	
	public void plugSecheCheveux(String uriCible, String in, String out) throws Exception {
		this.SECHECHEVEUX_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	
	public void plugRefrigerateur(String uriCible, String in, String out) throws Exception {
		this.REFRIGERATEUR_URI = uriCible;
		this.stringDataInPort.put(uriCible, new StringDataInPort(in, this));
		this.addPort(stringDataInPort.get(uriCible));
		this.stringDataInPort.get(uriCible).publishPort();
		this.stringDataOutPort.put(uriCible, new StringDataOutPort(out, this));
		this.addPort(stringDataOutPort.get(uriCible));
		this.stringDataOutPort.get(uriCible).publishPort();
	}
	//

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		String message = messages_recus.remove(0).getMessage();
		String[] messageSplit = message.split(":");
		this.logMessage(" Controleur recoit : " + message);
		if (messageSplit[0].equals(BATTERIE_URI)) {
			if (messageSplit[1].equals(Batterie.CHARGE)) {
				if (messageSplit[2].equals("100%")) {
					envoieString(BATTERIE_URI, Batterie.DISCHARGE);
				}
			}
		} else if (messageSplit[0].equals(COMPTEUR_URI)) {
			if (messageSplit[1].equals(Compteur.TOTAL)) {
				consommation = Integer.valueOf(messageSplit[2]);
			}
		} else if (messageSplit[0].equals(EOLIENNE_URI)) {
			production = Integer.valueOf(messageSplit[1]);
		} else if (messageSplit[0].equals(SECHECHEVEUX_URI)) {
			this.logMessage(message);
		}
	}

	/**
	 * Envoie le message <code>msg</code> sur le composant d'URI <code>uri</code>
	 * 
	 * @param uri URI du composant vers lequel on veut envoyer <code>msg</code>
	 * @param msg message à envoyer
	 * @throws Exception
	 */
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

}
