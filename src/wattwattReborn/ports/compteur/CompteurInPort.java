package wattwattReborn.ports.compteur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwattReborn.composants.Compteur;
import wattwattReborn.interfaces.compteur.ICompteur;

public class CompteurInPort extends AbstractInboundPort implements ICompteur{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri,ICompteur.class, owner);
		
	}

	@Override
	public int giveAllConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Compteur)owner).giveConso());
	}

}
