package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.ICompteurOffered;

public class CompteurStringDataInPort extends AbstractDataInboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurStringDataInPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return((ICompteurOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
