package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.IControleur;

public class ControleurDataInPort extends AbstractDataInboundPort{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5788843329662501354L;

	public ControleurDataInPort(String uri,ComponentI owner)
			throws Exception {
		super(uri,DataOfferedI.PullI.class,DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return ((IControleur) this.owner).getData(this.getClientPortURI());
	}

}
