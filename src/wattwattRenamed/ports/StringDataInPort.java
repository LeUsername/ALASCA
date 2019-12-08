package wattwattRenamed.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import wattwattRenamed.interfaces.IStringDataOffered;


/**
 * La classe <code>StringDataInPort</code> qui represente le port du
 * Controleur par lequel vont etre envoyees des message de type StringData
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class StringDataInPort extends AbstractDataInboundPort {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringDataInPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DataI get() throws Exception {
		return((IStringDataOffered) this.owner).sendMessage(this.getClientPortURI());
	}

}
