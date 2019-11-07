package ports.lavelinge;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.appareils.planifiables.ILaveLingeOffered;

/**
 * La classe <code>LaveLingeStringDataInPort</code> qui represente le port du
 * lave linge par lequel vont etre envoyer des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-07
 * </p>
 * 
 * @author 3410456
 *
 */
public class LaveLingeStringDataInPort extends AbstractDataInboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 670417311716327954L;

	public LaveLingeStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return ((ILaveLingeOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
