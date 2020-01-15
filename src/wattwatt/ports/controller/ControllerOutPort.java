package wattwatt.ports.controller;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.controller.IController;

public class ControllerOutPort extends AbstractOutboundPort implements IController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControllerOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IController.class, owner);
	}

	@Override
	public int getAllConso() throws Exception {
		return ((IController)this.connector).getAllConso() ;
	}

	@Override
	public void refriOn() throws Exception {
		((IController)this.connector).refriOn();
		
	}

	@Override
	public void refriOff() throws Exception {
		((IController)this.connector).refriOff();
		
	}

	@Override
	public void refriSuspend() throws Exception {
		((IController)this.connector).refriSuspend();
		
	}

	@Override
	public void refriResume() throws Exception {
		((IController)this.connector).refriResume();
		
	}

	@Override
	public double refriTempH() throws Exception {
		return ((IController)this.connector).refriTempH();
	}

	@Override
	public double refriTempL() throws Exception {
		return ((IController)this.connector).refriTempL();
	}

	@Override
	public int refriConso() throws Exception {
		return ((IController)this.connector).refriConso();
	}


}
