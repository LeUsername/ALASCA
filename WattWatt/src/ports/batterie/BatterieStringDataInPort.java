package ports.batterie;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataInPort;

/**
 * La classe <code>BatterieStringDataInPort</code> qui represente le port de la batterie
 * par lequel vont etre envoyees des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-15
 * </p>
 * 
 * @author 3408625
 *
 */

public class BatterieStringDataInPort extends StringDataInPort{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8485318773924818465L;

	public BatterieStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
