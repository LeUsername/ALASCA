package wattwattReborn.ports.controleur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwattReborn.interfaces.controleur.IControleurLaunch;

public class ControleurLaunchOutBoundPort extends AbstractOutboundPort implements IControleurLaunch {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurLaunchOutBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleurLaunch.class, owner);
	}
	
	public ControleurLaunchOutBoundPort( ComponentI owner) throws Exception {
		super(IControleurLaunch.class, owner);
	}

	@Override
	public void printConso() throws Exception {
		((IControleurLaunch)this.connector).printConso();
		
	}

}
