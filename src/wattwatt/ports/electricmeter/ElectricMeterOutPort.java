package wattwatt.ports.electricmeter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.electricmeter.IElectricMeter;

public class ElectricMeterOutPort extends AbstractOutboundPort implements IElectricMeter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElectricMeterOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IElectricMeter.class, owner);
	}

	@Override
	public int getAllConso() throws Exception {
		return ((IElectricMeter)this.connector).getAllConso();
	}



}
