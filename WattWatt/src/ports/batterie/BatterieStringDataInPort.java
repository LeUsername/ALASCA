package ports.batterie;

import fr.sorbonne_u.components.ComponentI;
import ports.StringDataInPort;

public class BatterieStringDataInPort extends StringDataInPort{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8485318773924818465L;

	public BatterieStringDataInPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
}
