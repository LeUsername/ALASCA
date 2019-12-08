package wattwatt.ports.compteur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.composants.Compteur;
import wattwatt.interfaces.compteur.ICompteur;

public class CompteurInPort extends AbstractInboundPort implements ICompteur{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurInPort(String uri, ComponentI owner) throws Exception {
		super(uri,ICompteur.class, owner);
		
	}

	@Override
	public int getAllConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Compteur)owner).giveConso());
	}

}
