package wattwatt.ports.controleur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.controleur.IControleur;

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

	@Override
	public void refriOn() throws Exception {
		((IControleur)this.connector).refriOn();
		
	}

	@Override
	public void refriOff() throws Exception {
		((IControleur)this.connector).refriOff();
		
	}

	@Override
	public void refriSuspend() throws Exception {
		((IControleur)this.connector).refriSuspend();
		
	}

	@Override
	public void refriResume() throws Exception {
		((IControleur)this.connector).refriResume();
		
	}

	@Override
	public double refriTempH() throws Exception {
		return ((IControleur)this.connector).refriTempH();
	}

	@Override
	public double refriTempB() throws Exception {
		return ((IControleur)this.connector).refriTempB();
	}

	@Override
	public int refriConso() throws Exception {
		return ((IControleur)this.connector).refriConso();
	}


}
