package ports.eolienne;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataOutPort;

public class EolienneStringDataOutPort extends StringDataOutPort {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6759258525162209864L;

	public EolienneStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
