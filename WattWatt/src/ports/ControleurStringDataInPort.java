package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.IControleurOffered;

public class ControleurStringDataInPort extends AbstractDataInboundPort {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurStringDataInPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		return((IControleurOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
