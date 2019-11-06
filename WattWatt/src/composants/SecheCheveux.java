package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.appareils.incontrolables.ISecheCheveuxOffered;
import interfaces.appareils.incontrolables.ISecheCheveuxRequired;
import sechecheveux.SecheCheveuxStringDataInPort;
import sechecheveux.SecheCheveuxStringDataOutPort;


public class SecheCheveux extends AbstractComponent implements ISecheCheveuxRequired, ISecheCheveuxOffered {

	
	/**
	 * Le port par lequel le compteur recoit des donnees representees par la classe
	 * StringData
	 */
	public SecheCheveuxStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on envoie des messages: on fait la difference entre
	 * StringData et CompteurData pour le moment
	 */
	public SecheCheveuxStringDataInPort stringDataInPort;
	
	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();
	
	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();
	
	public SecheCheveux(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new SecheCheveuxStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new SecheCheveuxStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(1000);
					String msg = "hello je suis le cehsfsjef";
					StringData m = new StringData();
					m.setMessage(msg);
					messages_envoyes.put("controleur", new Vector<StringData>());
					messages_envoyes.get("controleur").add(m);
					sendMessage("controleur");
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage(" Seche cheveux recoit : " + messages_recus.remove(0).getMessage());

	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}
}
