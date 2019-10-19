package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ICompteurOffered;
import interfaces.ICompteurRequired;
import ports.CompteurCompteurDataInPort;
import ports.CompteurStringDataInPort;
import ports.CompteurStringDataOutPort;

/**
 * La classe <code>Compteur</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */
public class Compteur extends AbstractComponent implements ICompteurOffered, ICompteurRequired {

	/**
	 * Le port par lequel le compteur recoit des donnees representees par la classe
	 * Data
	 */
	public CompteurStringDataOutPort stringDataOutPort;

	public CompteurStringDataInPort stringDataInPort;
	public CompteurCompteurDataInPort compteurDataInPort;
	
	/**
	 * La liste des messages recues
	 */
	Vector<StringData> messages_recus = new Vector<>();
	
	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	public Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();
		
		stringDataOutPort = new CompteurStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();
		
		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new CompteurStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();
		
		randomURI = java.util.UUID.randomUUID().toString();
		compteurDataInPort = new CompteurCompteurDataInPort(randomURI, this);
		this.addPort(compteurDataInPort);
		compteurDataInPort.publishPort();
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(1000);
					String msg = "hello";
					StringData m = new StringData();
					m.setMessage(msg);
					messages_envoyes.put("controleur", new Vector<StringData>());
					messages_envoyes.get("controleur").add(m);
					sendMessage("controleur");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Compteur recoit : " + messages_recus.remove(0).getMessage());
		
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}

	@Override
	public CompteurData sendCompteurData(String uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
}
