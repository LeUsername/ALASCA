package wattwatt.ports.electricmeter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.ElectricMeter;
import wattwatt.interfaces.electricmeter.IElectricMeter;

public class ElectricMeterInPort extends AbstractInboundPort implements IElectricMeter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElectricMeterInPort(String uri, ComponentI owner) throws Exception {
		super(uri,IElectricMeter.class, owner);
		
	}

	@Override
	public int getAllConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((ElectricMeter)owner).giveConso());
	}

}