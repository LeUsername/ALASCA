package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.productions.intermittentes.IBatterieOffered;
import interfaces.productions.intermittentes.IBatterieRequired;
import ports.batterie.BatterieStringDataInPort;
import ports.batterie.BatterieStringDataOutPort;

/**
 * La classe <code>Batterie</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

public class Batterie extends AbstractComponent implements IBatterieOffered, IBatterieRequired {

	/**
	 * Le port par lequel la batterie recoit des donnees representees par la classe
	 * StringData
	 */
	public BatterieStringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on envoie des messages
	 */
	public BatterieStringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	public Batterie(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new BatterieStringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new BatterieStringDataInPort(randomURI, this);
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
					String msg = "hello je suis batterie";
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
		this.logMessage("Batterie recoit : " + messages_recus.remove(0).getMessage());

	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}}
