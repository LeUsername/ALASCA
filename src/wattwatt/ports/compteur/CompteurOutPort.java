package wattwatt.ports.compteur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.compteur.ICompteur;

public class CompteurOutPort extends AbstractOutboundPort implements ICompteur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompteurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}

	@Override
	public int getAllConso() throws Exception {
		return ((ICompteur)this.connector).getAllConso();
	}



}
