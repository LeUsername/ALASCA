package sechecheveux;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.ICompteurOffered;
import interfaces.appareils.incontrolables.ISecheCheveuxOffered;

/**
 * La classe <code>SecheCheveuxStringDataInPort</code> qui represente le port du
 * Seche cheveux par lequel vont etre envoyer des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */
public class SecheCheveuxStringDataInPort extends AbstractDataInboundPort {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SecheCheveuxStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return ((ISecheCheveuxOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
