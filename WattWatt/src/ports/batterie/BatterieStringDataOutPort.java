package ports.batterie;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataOutPort;

public class BatterieStringDataOutPort extends StringDataOutPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4118419417154055565L;

	public BatterieStringDataOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
