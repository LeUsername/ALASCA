package wattwattReborn.ports.controleur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwattReborn.composants.Controleur;
import wattwattReborn.interfaces.controleur.IControleur;

public class ControleurInPort extends AbstractInboundPort implements IControleur{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurInPort(String uri, ComponentI owner) throws Exception {
		super(uri,IControleur.class, owner);
	}

//	@Override
//	public int getAllConso() throws Exception {
//		return this.getOwner().handleRequestSync(owner ->((Controleur)owner).getAllConso());
//	}

}
