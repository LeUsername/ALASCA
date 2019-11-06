package sechecheveux;

import data.StringData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import interfaces.appareils.incontrolables.ISecheCheveuxRequired;

/**
 * La classe <code>SecheCheveuxStringDataOutPort</code> qui represente le port du
 * Seche Cheveux par lequel vont etre recu des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */
public class SecheCheveuxStringDataOutPort extends AbstractDataOutboundPort {


	/**
	 * 
	 */
	private static final long serialVersionUID = -980940227955490454L;

	public SecheCheveuxStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((ISecheCheveuxRequired) this.getServiceOwner()).getMessage((StringData) d);
				return null;
			}
		});
	}
}
