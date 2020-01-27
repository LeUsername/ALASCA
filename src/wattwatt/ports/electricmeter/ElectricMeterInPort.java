package wattwatt.ports.electricmeter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.ElectricMeter;
import wattwatt.interfaces.electricmeter.IElectricMeter;

/**
 * The class <code>ElectricMeterInPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The InBound port of the electric meter component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class ElectricMeterInPort extends AbstractInboundPort implements IElectricMeter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElectricMeterInPort(String uri, ComponentI owner) throws Exception {
		super(uri,IElectricMeter.class, owner);
		
	}

	@Override
	public double getAllConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((ElectricMeter)owner).giveConso());
	}

}
