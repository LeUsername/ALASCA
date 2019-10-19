package composants;

import java.util.ArrayList;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ICompteur;
import ports.CompteurDataOutPort;

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
public class Compteur extends AbstractComponent implements ICompteur {

	/**
	 * Le port par lequel le compteur recoit des donnees representees par la classe
	 * Data
	 */
	public CompteurDataOutPort dataOutPort;

	/**
	 * La liste des messages recues
	 */
	ArrayList<StringData> messages_recus = new ArrayList<>();

	public Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new CompteurDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
	}

	@Override
	public void sendData(StringData msg) throws Exception {
		messages_recus.add(msg);
		this.logMessage("Frigo : " + messages_recus.get(0).getMessage());
	}
	
	@Override
	public void sendData(CompteurData msg) throws Exception {
		
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	
}
