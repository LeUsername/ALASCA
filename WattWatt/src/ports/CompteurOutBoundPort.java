package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.ICompteur;

/***
 * La classe <code>CompteurOutBoundPort</code>
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

@SuppressWarnings("serial")
public class CompteurOutBoundPort extends AbstractOutboundPort implements ICompteur{

	public CompteurOutBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ICompteur.class, owner);
	}

	public CompteurOutBoundPort(ComponentI owner) throws Exception {
		super(ICompteur.class, owner);
	}

	@Override
	public int getAllConsommation() throws Exception {
		return ((ICompteur)this.connector).getAllConsommation() ;
	}

	@Override
	public int getAllProductionsAleatoires() throws Exception {
		return ((ICompteur)this.connector).getAllProductionsAleatoires() ;
	}

	@Override
	public int getAllProductionsIntermittentes() throws Exception {
		return ((ICompteur)this.connector).getAllProductionsIntermittentes() ;
	}

	@Override
	public void reset() throws Exception {
		((ICompteur)this.connector).reset() ;
		
	}


	

	

}
