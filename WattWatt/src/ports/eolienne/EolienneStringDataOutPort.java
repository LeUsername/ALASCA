package ports.eolienne;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataOutPort;
/**
 * La classe <code>ControleurStringDataOutPort</code> qui represente le port de
 * l'eolienne par lequel vont etre envoyees des message de type StringData
 * 
 * <p>
 * Created on : 2019-11-15
 * </p>
 * 
 * @author 3408625
 *
 */
public class EolienneStringDataOutPort extends StringDataOutPort {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6759258525162209864L;

	public EolienneStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
