package composants;

import java.util.Vector;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ICompteur;
import ports.CompteurDataOutPort;

public class Compteur extends AbstractComponent implements ICompteur {

	public CompteurDataOutPort dataOutPort;
	Vector<StringData> messages_recu = new Vector<>();

	public Compteur(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) throws Exception {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		
		String dataOutPortURI = java.util.UUID.randomUUID().toString();
		dataOutPort = new CompteurDataOutPort(dataOutPortURI, this);
		this.addPort(dataOutPort);
		dataOutPort.publishPort();
	}

	@Override
	public DataI getData(String uri) throws Exception {
		// A faire plus tard ?
		return null;
	}

	@Override
	public void sendData(StringData msg) throws Exception {
		messages_recu.add(msg);
		this.logMessage("Frigo : " + messages_recu.get(0).getMessage());
	}
	
	@Override
	public  void start() throws ComponentStartException {
		super.start();
		this.runTask(new AbstractTask() {
			public void run() {
				try {
					Thread.sleep(5000);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
