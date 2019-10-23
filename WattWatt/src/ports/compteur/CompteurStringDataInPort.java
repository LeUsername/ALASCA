package ports.compteur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.ICompteurOffered;

/**
 * La classe <code>CompteurStringDataInPort</code> qui represente le port du
 * Compteur par lequel vont etre envoyer des message de type StringData
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class CompteurStringDataInPort extends AbstractDataInboundPort {



	/**
	 * 
	 */
	private static final long serialVersionUID = -9157641611485316737L;

	public CompteurStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return ((ICompteurOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
