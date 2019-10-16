package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.IControleur;

/***
 * La classe <code>ControleurOutBoundPort</code> 
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

@SuppressWarnings("serial")
public class ControleurOutBoundPort extends AbstractOutboundPort implements IControleur  {

	public ControleurOutBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}
	
	public ControleurOutBoundPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void gestionRefigerateur() throws Exception {
		((IControleur)this.connector).gestionRefigerateur() ;
	}

	@Override
	public void gestionLaveLinge() throws Exception {
		((IControleur)this.connector).gestionLaveLinge() ;
	}

	@Override
	public void gestionBatterie() throws Exception {
		((IControleur)this.connector).gestionBatterie() ;
	}

	@Override
	public void gestionEolienne() throws Exception {
		((IControleur)this.connector).gestionEolienne() ;
	}

}
