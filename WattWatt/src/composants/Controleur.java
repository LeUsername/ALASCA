package composants;

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
	public ControleurStringDataInPort stringDataInPort;
	public ControleurStringDataInPort stringDataInPort2;

	/**
	 * Le port par lequel le compteur envoie des donnees representees par la classe
	 * StringData: pour l'instant il n'existe que des ports vers le compteur
	 */
	public ControleurCompteurDataOutPort compteurDataOutPort;
	public ControleurStringDataOutPort stringDataOutPort;
	public ControleurStringDataOutPort stringDataOutPort2;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	public Vector<StringData> messages_recus = new Vector<StringData>();

	/**
	 * Ses 3 entiers vont servir a stocker les informations recu du compteur
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

		String randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataInPort = new ControleurStringDataInPort(randomURIPort, this);
		this.addPort(stringDataInPort);
		this.stringDataInPort.publishPort();
		
		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataInPort2 = new ControleurStringDataInPort(randomURIPort, this);
		this.addPort(stringDataInPort2);
		this.stringDataInPort2.publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.compteurDataOutPort = new ControleurCompteurDataOutPort(randomURIPort, this);
		this.addPort(compteurDataOutPort);
		this.compteurDataOutPort.publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataOutPort = new ControleurStringDataOutPort(randomURIPort, this);
		this.addPort(stringDataOutPort);
		this.stringDataOutPort.publishPort();
		
		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataOutPort2 = new ControleurStringDataOutPort(randomURIPort, this);
		this.addPort(stringDataOutPort2);
		this.stringDataOutPort2.publishPort();
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}

	@Override
	public StringData sendMessage2(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.stringDataInPort2.send(m);
		return m;
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					String msg = "hello45894894";
					StringData m = new StringData();
					m.setMessage(msg);
					controleurMessages.put("compteur", new Vector<StringData>());
					controleurMessages.get("compteur").add(m);
					sendMessage("compteur");
					
					controleurMessages.put("secheCheveux", new Vector<StringData>());
					controleurMessages.get("secheCheveux").add(m);
					sendMessage2("secheCheveux");
				} catch (Exception e) {
					e.printStackTrace();
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
