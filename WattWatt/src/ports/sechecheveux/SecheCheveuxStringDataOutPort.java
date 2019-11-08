package ports.sechecheveux;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataOutPort;

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
public class SecheCheveuxStringDataOutPort extends StringDataOutPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5325366480013582988L;

	public SecheCheveuxStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
