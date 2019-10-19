package composants;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import interfaces.IControleur;
import ports.ControleurDataInPort;

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
public class Controleur extends AbstractComponent implements IControleur {

	/**
	 * Le port par lequel le controleur envoie des donnees representees par la
	 * classe Data
	 */
	public ControleurDataInPort dataInPort;

	/**
	 * La liste des messages recues
	 */
	protected ConcurrentHashMap<String, ArrayList<StringData>> controleurMessages = new ConcurrentHashMap<>();

	public Controleur(String uri, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(uri, nbThreads, nbSchedulableThreads);

		this.addOfferedInterface(IControleur.class);
		this.addOfferedInterface(DataOfferedI.PullI.class);

		String dataInPortURI = java.util.UUID.randomUUID().toString();
		this.dataInPort = new ControleurDataInPort(dataInPortURI, this);
		this.addPort(dataInPort);
		this.dataInPort.publishPort();
	}

	@Override
	public DataI getData(String uri) throws Exception {
		return controleurMessages.get(uri).remove(0);
	}

	protected void envoyerMessage(String uri) throws Exception {
		StringData m = controleurMessages.get(uri).get(0);
		controleurMessages.get(uri).remove(m);
		this.dataInPort.send(m);
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					StringData m = new StringData();
					m.setMessage("controleur ici");
					controleurMessages.put("compteur", new ArrayList<StringData>());
					controleurMessages.get("compteur").add(m);
					envoyerMessage("compteur");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
