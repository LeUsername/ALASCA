package ports.sechecheveux;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataInPort;

/**
 * La classe <code>SecheCheveuxStringDataInPort</code> qui represente le port du
 * Seche cheveux par lequel vont etre envoyees des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */
public class SecheCheveuxStringDataInPort extends StringDataInPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1890826465352811493L;

	public SecheCheveuxStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

}
