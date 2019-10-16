package ports;

import composants.Controleur;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.IControleur;

/***
 * La classe <code>ControleurInBoundPort</code> 
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */

@SuppressWarnings("serial")
public class ControleurInBoundPort extends AbstractInboundPort implements IControleur {

	public ControleurInBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IControleur.class, owner);
	}

	public ControleurInBoundPort(ComponentI owner) throws Exception {
		super(IControleur.class, owner);
	}

	@Override
	public void gestionRefigerateur() throws Exception {
		((Controleur)this.getOwner()).gestionRefigerateur() ;
		
	}

	@Override
	public void gestionLaveLinge() throws Exception {
		((Controleur)this.getOwner()).gestionLaveLinge() ;
		
	}

	@Override
	public void gestionBatterie() throws Exception {
		((Controleur)this.getOwner()).gestionBatterie() ;
		
	}

	@Override
	public void gestionEolienne() throws Exception {
		((Controleur)this.getOwner()).gestionEolienne() ;
		
	}

}
