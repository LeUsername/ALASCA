package ports.eolienne;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataInPort;

public class EolienneStringDataInPort extends StringDataInPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4617818627981052527L;

	public EolienneStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
