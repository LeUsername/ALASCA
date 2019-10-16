package composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.examples.pingpong.connectors.PingPongConnector;
import fr.sorbonne_u.components.examples.pingpong.interfaces.PingPongI;
import fr.sorbonne_u.components.examples.pingpong.ports.PingPongInboundPort;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import interfaces.ICompteur;
import ports.CompteurInBoundPort;
import ports.CompteurOutBoundPort;

public class Compteur extends AbstractComponent {

	protected final String uri;
	protected String coutUri;
	protected String cinUri;
	protected CompteurOutBoundPort compteurOutboundPort;
	protected CompteurInBoundPort compteurInboundPort;

	protected Compteur(String uri, String out, String in) throws Exception {
		super(uri, 1, 1);
		this.uri = uri;
		this.coutUri = out;
		this.cinUri = in;
		
		this.init();
	}

	public void init() throws Exception {
		this.addRequiredInterface(ICompteur.class);
		this.addOfferedInterface(ICompteur.class);
		this.compteurOutboundPort = new CompteurOutBoundPort(this);
		this.compteurOutboundPort.localPublishPort();
		this.compteurInboundPort = new CompteurInBoundPort(this.cinUri, this);
		this.compteurInboundPort.publishPort();
	}
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
//		this.doPortConnection(
//				this.compteurOutboundPort.getPortURI(),
//				this.player2PingPongInboundPortURI,
//				PingPongConnector.class.getCanonicalName()) ;
//		this.doPortConnection(
//				this.pingPongDataInboundPort.getPortURI(),
//				this.player2PingPongDataOutboundPortURI,
//				DataConnector.class.getCanonicalName()) ;
	}
	

	public int getAllConsommation() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public void reset() throws Exception {
		// TODO Auto-generated method stub

	}

	public int getAllProductionsAleatoires() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAllProductionsIntermittentes() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
