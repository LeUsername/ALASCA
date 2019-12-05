package wattwattReborn.ports.controleur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwattReborn.composants.DynamicControleur;
import wattwattReborn.interfaces.controleur.IControleurLaunch;

public class ControleurLaunchInBoundPort extends AbstractInboundPort implements IControleurLaunch {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurLaunchInBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleurLaunch.class, owner);
	}
	
	public ControleurLaunchInBoundPort(ComponentI owner) throws Exception {
		super(IControleurLaunch.class, owner);
	}

	@Override
	public void printConso() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((DynamicControleur)this.getServiceOwner()).
						printConso() ;
						return null;
					}
				}) ;
		
	}

}
