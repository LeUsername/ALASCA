package ports.compteur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import interfaces.ICompteurOffered;

/**
 * La classe <code>CompteurCompteurDataInPort</code> qui represente le port du
 * Compteur par lequel vont etre envoyer des message de type CompteurData
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class CompteurCompteurDataInPort extends AbstractDataInboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurCompteurDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public DataI get() throws Exception {
		return ((ICompteurOffered) this.owner).sendCompteurData(this.getClientPortURI());
	}
}
