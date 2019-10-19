package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import interfaces.IControleurOffered;
import interfaces.IControleurRequired;
import ports.ControleurCompteurDataOutPort;
import ports.ControleurStringDataInPort;
import ports.ControleurStringDataOutPort;

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
	 * classe Data
	 */
	public ControleurStringDataInPort stringDataInPort;
	public ControleurCompteurDataOutPort compteurDataOutPort;
	public ControleurStringDataOutPort stringDataOutPort;

	/**
	 * La liste des messages recus
	 */
	public Vector<StringData> messages_recus = new Vector<StringData>();

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

		this.compteurDataOutPort = new ControleurCompteurDataOutPort(randomURIPort, this);
		this.addPort(compteurDataOutPort);
		this.compteurDataOutPort.publishPort();

		randomURIPort = java.util.UUID.randomUUID().toString();

		this.stringDataOutPort = new ControleurStringDataOutPort(randomURIPort, this);
		this.addPort(stringDataOutPort);
		this.stringDataOutPort.publishPort();
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					String msg = "hello";
					StringData m = new StringData();
					m.setMessage(msg);
					controleurMessages.put("compteur", new Vector<StringData>());
					controleurMessages.get("compteur").add(m);
					sendMessage("compteur");
					Thread.sleep(2000);
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
		// TODO Auto-generated method stub

	}

}
