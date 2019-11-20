package wattwatt.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import wattwatt.data.StringData;
import wattwatt.interfaces.IStringDataRequired;

/**
 * La classe <code>StringDataOutPort</code> qui represente le port par lequel
 * vont etre recues des message de type StringData
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class StringDataOutPort extends AbstractDataOutboundPort {

	private static final long serialVersionUID = 1L;

	public StringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((IStringDataRequired) this.getServiceOwner()).getMessage(((StringData) d));
				return null;
			}
		});

	}

}
