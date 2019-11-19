package wattwatt.composants;

import java.util.HashMap;
import java.util.Random;
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
 * La classe <code>Refrigerateur</code>
 * 
 * <p>
 * Created on : 2019-11-09
 * </p>
 * 
 * @author 3410456
 *
 */

public class Refrigerateur extends AbstractComponent implements IStringDataOffered, IStringDataRequired {

	/**
	 * Les ports par lesquels l'eolienne envoie des donnees representees par la
	 * classe StringData
	 */
	public HashMap<String, StringDataInPort> stringDataInPort;

	/**
	 * Les ports par lesquels l'eolienne recoit des donnees representees par la
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
	 * Liste des uris
	 */
	protected Vector<String> uris;

	/**
	 * Objet permettant de declencher un comportement
	 */
	protected Timer timer = new Timer();

	/**
	 * Pour l'instant la valeur produite par l'eolienne est decidee al√©atoirement
	 */
	protected Random rand = new Random();

	/**
	 * Definit si l'eolienne est en marche ou non
	 */
	protected boolean isOn = false;

	protected boolean isWorking = true;

	protected int temp = 0;
	protected int MAXTemp = 18;
	protected int MINTemp = 4;

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
	public Refrigerateur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		URI = reflectionInboundPortURI;
		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();
	}

	public Refrigerateur(String uri, int nbThreads, int nbSchedulableThreads, Vector<String> uris) throws Exception {
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
		this.logMessage("Refrigirateur starting");
		this.temp = 10;
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
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
		this.logMessage("Refrigerateur shutdown");
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

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage("Refrigerateur recoit : " + messages_recus.remove(0).getMessage());
		switch (msg.getMessage()) {
		case "switchOn":
			if (!isOn) {
				isOn = true;
				this.logMessage("Demarrage du refrigerateur");
			}
			timer.schedule(new RefriTask(this), 0, 5000);
			break;
		case "switchOff":
			if (isOn) {
				isOn = false;
				this.logMessage("Arret du refrigerateur");
			}
			timer.purge();
			break;
		case "shutdown":
			shutdown();
			break;
		case "suspend":
			isWorking = false;
			break;
		case "resume":
			isWorking = true;
			break;
		case "temp":
			String message = "refrigerateur:temp:" + temp;
			this.logMessage(message);
			envoieString("controleur", message);
			break;
		}
	}

	/**
	 * Envoie le message <code>msg</code> sur le composant d'URI <code>uri</code>
	 * 
	 * @param uri URI du composant vers lequel on veut envoyer <code>msg</code>
	 * @param msg message √† envoyer
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
	 * Classe permettant d'afficher a intervalles reguliers la production
	 * electrique. Elle herite donc de TimerTask pour simuler ces intervalles.
	 * 
	 * <p>
	 * Created on : 2019-11-16
	 * </p>
	 * 
	 * @author Thierno BAH, Pascal ZHENG
	 *
	 */
	class RefriTask extends TimerTask {
		Refrigerateur e;

		public RefriTask(Refrigerateur e) {
			this.e = e;
		}

		@Override
		public void run() {
			if (e.isOn) {
				if (e.isWorking) {
					e.logMessage("working at : " + e.temp + " ∞C");
					if (e.temp > e.MINTemp) {
						e.temp--;
					}
				} else {
					e.logMessage("suspended : " + e.temp + " ∞C");
					if (e.temp < e.MAXTemp) {
						e.temp++;
					}
				}
			}
		}

	}

}
