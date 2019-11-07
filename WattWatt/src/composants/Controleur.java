package composants;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import interfaces.IControleurOffered;
import interfaces.IControleurRequired;
import ports.controleur.ControleurCompteurDataOutPort;
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

	/**
	 * Le port par lequel le compteur envoie des donnees representees par la classe
	 * StringData: pour l'instant il n'existe que des ports vers le compteur
	 */
	public ControleurCompteurDataOutPort compteurDataOutPort;
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
	protected ConcurrentHashMap<String, Vector<CompteurData>> controleurCompteurData = new ConcurrentHashMap<>();

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleurOffered.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		this.stringDataInPort = new HashMap<>();
		this.stringDataOutPort = new HashMap<>();

		String randomURIPort = java.util.UUID.randomUUID().toString();

		// this.stringDataInPort = new ControleurStringDataInPort(randomURIPort, this);
		this.stringDataInPort.put("compteur", new ControleurStringDataInPort(randomURIPort, this));
		this.addPort(stringDataInPort.get("compteur"));
		this.stringDataInPort.get("compteur").publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		// this.stringDataInPort2 = new ControleurStringDataInPort(randomURIPort, this);
		this.stringDataInPort.put("secheCheveux", new ControleurStringDataInPort(randomURIPort, this));
		this.addPort(stringDataInPort.get("secheCheveux"));
		this.stringDataInPort.get("secheCheveux").publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataInPort.put("laveLinge", new ControleurStringDataInPort(randomURIPort, this));
		this.addPort(stringDataInPort.get("laveLinge"));
		this.stringDataInPort.get("laveLinge").publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.compteurDataOutPort = new ControleurCompteurDataOutPort(randomURIPort, this);
		this.addPort(compteurDataOutPort);
		this.compteurDataOutPort.publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		// this.stringDataOutPort = new ControleurStringDataOutPort(randomURIPort,
		// this);
		this.stringDataOutPort.put("compteur", new ControleurStringDataOutPort(randomURIPort, this));
		this.addPort(stringDataOutPort.get("compteur"));
		this.stringDataOutPort.get("compteur").publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataOutPort.put("secheCheveux", new ControleurStringDataOutPort(randomURIPort, this));
		this.addPort(stringDataOutPort.get("secheCheveux"));
		this.stringDataOutPort.get("secheCheveux").publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataOutPort.put("laveLinge", new ControleurStringDataOutPort(randomURIPort, this));
		this.addPort(stringDataOutPort.get("laveLinge"));
		this.stringDataOutPort.get("laveLinge").publishPort();
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.stringDataInPort.get(uri).send(m);
		return m;
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.runTask(new AbstractTask() {
			public void run() {
				while (true) {
					try {
						String msg = "Hello controleur";
						StringData m = new StringData();
						m.setMessage(msg);
						controleurMessages.put("compteur", new Vector<StringData>());
						controleurMessages.get("compteur").add(m);
						sendMessage("compteur");

						controleurMessages.put("secheCheveux", new Vector<StringData>());
						controleurMessages.get("secheCheveux").add(m);
						sendMessage("secheCheveux");

						controleurMessages.put("laveLinge", new Vector<StringData>());
						controleurMessages.get("laveLinge").add(m);
						sendMessage("laveLinge");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Controleur recoit : " + messages_recus.remove(0).getMessage());
	}

	@Override
	public void getCompteurData(CompteurData msg) throws Exception {
		this.consommation = msg.getConsommation();
		this.productionAleatoire = msg.getProdAlea();
		this.productionIntermittente = msg.getProdInterm();
		this.logMessage(" Controleur a mis a jour les informations sur les quantité d'energie");

	}

}
