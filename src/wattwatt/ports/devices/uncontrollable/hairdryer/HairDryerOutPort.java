package wattwatt.ports.devices.uncontrollable.hairdryer;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;

public class HairDryerOutPort extends AbstractOutboundPort implements IHairDryer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HairDryerOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IHairDryer.class, owner);
		
	}

	@Override
	public void On() throws Exception {
		((IHairDryer)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IHairDryer)this.connector).Off();
		
	}

	@Override
	public int getConso() throws Exception {
		return ((IHairDryer)this.connector).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IHairDryer)this.connector).isOn();
	}

	@Override
	public void switchMode() throws Exception {
		((IHairDryer)this.connector).switchMode();
		
	}

	@Override
	public void increasePower() throws Exception {
		((IHairDryer)this.connector).increasePower();
		
	}

	@Override
	public void decreasePower() throws Exception {
		((IHairDryer)this.connector).decreasePower();
		
	}

}
