package wattwattReborn.ports.controleur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwattReborn.interfaces.controleur.IControleur;

public class ControleurOutPort extends AbstractOutboundPort implements IControleur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}

	@Override
	public int getAllConso() throws Exception {
		return ((IControleur)this.connector).getAllConso() ;
	}


}
