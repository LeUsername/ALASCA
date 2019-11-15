package ports.lavelinge;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import interfaces.appareils.planifiables.ILaveLingeRequired;

/**
 * La classe <code>LaveLingeStringDataOutPort</code> qui represente le port du
 * lave linge par lequel vont etre recues des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-07
 * </p>
 * 
 * @author 3408625
 *
 */
public class LaveLingeStringDataOutPort extends AbstractDataOutboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4366810096253141613L;

	public LaveLingeStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ILaveLingeRequired) this.getServiceOwner()).getMessage((StringData) d);
				return null;
			}
		});
	}
}
