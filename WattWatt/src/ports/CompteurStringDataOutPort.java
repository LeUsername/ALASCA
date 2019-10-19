package ports;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import interfaces.ICompteurRequired;

public class CompteurStringDataOutPort extends AbstractDataOutboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class,DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ICompteurRequired) this.getServiceOwner()).getMessage((StringData) d);
				return null;
			}
		});
	}
}
