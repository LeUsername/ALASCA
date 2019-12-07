package wattwattReborn.ports.appareils.suspensible.refrigerateur;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

public class RefrigerateurOutPort extends AbstractOutboundPort implements IRefrigerateur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefrigerateurOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IRefrigerateur.class, owner);
	}

	@Override
	public void suspend() throws Exception {
		((IRefrigerateur)this.connector).suspend();
		
	}

	@Override
	public void resume() throws Exception {
		((IRefrigerateur)this.connector).resume();
		
	}

	@Override
	public void On() throws Exception {
		((IRefrigerateur)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IRefrigerateur)this.connector).Off();
		
	}

	@Override
	public int getConso() throws Exception {
		return ((IRefrigerateur)this.connector).getConso();
	}

	@Override
	public double getTempH() throws Exception {
		return ((IRefrigerateur)this.connector).getTempH();
	}

	@Override
	public double getTempB() throws Exception {
		return ((IRefrigerateur)this.connector).getTempB();
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((IRefrigerateur)this.connector).isWorking();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IRefrigerateur)this.connector).isOn();
	}

}
