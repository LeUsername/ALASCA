package composants;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.IStringDataOffered;
import interfaces.IStringDataRequired;
import ports.StringDataInPort;
import ports.StringDataOutPort;


/**
 * La classe <code>Lave linge</code>
 * 
 * <p>
 * Created on : 2019-11-09
 * </p>
 * 
 * @author 3410456
 *
 */

public class LaveLinge extends AbstractComponent implements IStringDataOffered, IStringDataRequired {

	/**
	 * Le port par lequel le lave linge recoit des donnees representees par la
	 * classe StringData
	 */
	public StringDataOutPort stringDataOutPort;

	/**
	 * Les ports par lesquels on envoie des messages: on fait la difference entre
	 * StringData et CompteurData pour le moment
	 */
	public StringDataInPort stringDataInPort;

	/**
	 * La liste des messages recues, representees par la classe StringData.
	 */
	Vector<StringData> messages_recus = new Vector<>();

	/**
	 * La liste des messages a envoyer
	 */
	protected ConcurrentHashMap<String, Vector<StringData>> messages_envoyes = new ConcurrentHashMap<>();

	public LaveLinge(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String randomURI = java.util.UUID.randomUUID().toString();

		stringDataOutPort = new StringDataOutPort(randomURI, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		randomURI = java.util.UUID.randomUUID().toString();
		stringDataInPort = new StringDataInPort(randomURI, this);
		this.addPort(stringDataInPort);
		stringDataInPort.publishPort();

	}
	
	public LaveLinge(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads, String in, String out)
			throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		stringDataOutPort = new StringDataOutPort(out, this);
		this.addPort(stringDataOutPort);
		stringDataOutPort.publishPort();

		stringDataInPort = new StringDataInPort(in, this);
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
					String msg = "hello je suis le lave linge";
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
		this.logMessage("Lave linge recoit : " + messages_recus.remove(0).getMessage());
	}

	@Override
	public StringData sendMessage(String uri) throws Exception {
		StringData m = messages_envoyes.get(uri).get(0);
		messages_envoyes.get(uri).remove(m);
		this.stringDataInPort.send(m);
		return m;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Lave linge shutdown");
		try {
			stringDataOutPort.unpublishPort();
			stringDataInPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}
	
	@Override
	public void finalise() throws Exception {
		stringDataInPort.unpublishPort();
		stringDataOutPort.unpublishPort();

		super.finalise();
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
