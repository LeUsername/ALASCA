package ports.batterie;

import fr.sorbonne_u.components.ComponentI;

import ports.StringDataOutPort;
/**
 * La classe <code>BatterieStringDataOutPort</code> qui represente le port de la batterie
 * par lequel vont etre recues des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-15
 * </p>
 * 
 * @author 3408625
 *
 */
public class BatterieStringDataOutPort extends StringDataOutPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118419417154055565L;

	public BatterieStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
