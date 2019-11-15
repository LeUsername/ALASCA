package ports.eolienne;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataInPort;

/**
 * La classe <code>ControleurStringDataOutPort</code> qui represente le port de
 * l'eolienne par lequel vont etre recues des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-15
 * </p>
 * 
 * @author 3408625
 *
 */

public class EolienneStringDataInPort extends StringDataInPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4617818627981052527L;

	public EolienneStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
