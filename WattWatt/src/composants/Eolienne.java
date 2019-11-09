package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.appareils.incontrolables.ISecheCheveuxOffered;
import interfaces.productions.aleatoires.IEolienneRequired;
import ports.eolienne.EolienneStringDataInPort;
import ports.eolienne.EolienneStringDataOutPort;

/**
 * La classe <code>Eolienne</code>
 * 
 * <p>
 * Created on : 2019-11-09
 * </p>
 * 
 * @author 3410456
 *
 */

public class Eolienne extends AbstractComponent implements IEolienneRequired, ISecheCheveuxOffered {
	/**
	 * Le port par lequel l'eolienne recoit des donnees representees par la classe
	 * StringData
	 */
	public EolienneStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on recoit des messages
	 */
	public EolienneStringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	public Eolienne(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new EolienneStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new EolienneStringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(10);
					System.out.println("ici");
					String msg = "hello je suis EOLIENNE";
					StringData m = new StringData();
					m.setMessage(msg);
					messages_envoyes.put("controleur", new Vector<StringData>());
					messages_envoyes.get("controleur").add(m);
					sendMessage("controleur");
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void getMessage(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage("Eolienne recoit : " + messages_recus.remove(0).getMessage());
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}
}
