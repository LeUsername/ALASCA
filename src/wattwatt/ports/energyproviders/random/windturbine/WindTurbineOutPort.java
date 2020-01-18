package wattwatt.ports.energyproviders.random.windturbine;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.energyproviders.random.windturbine.IWindTurbine;

public class WindTurbineOutPort extends AbstractOutboundPort implements IWindTurbine{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WindTurbineOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IWindTurbine.class, owner);
	}

	@Override
	public int getEnergy() throws Exception {
		return ((IWindTurbine)this.connector).getEnergy();
	}

	@Override
	public void On() throws Exception {
		((IWindTurbine)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IWindTurbine)this.connector).Off();
		
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IWindTurbine)this.connector).isOn();
	}

}