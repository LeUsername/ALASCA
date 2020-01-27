package wattwatt.ports.electricmeter;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.electricmeter.IElectricMeter;

/**
 * The class <code>ElectricMeterOutPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The OutBound port of the electric meter component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class ElectricMeterOutPort extends AbstractOutboundPort implements IElectricMeter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ElectricMeterOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IElectricMeter.class, owner);
	}

	@Override
	public double getAllConso() throws Exception {
		return ((IElectricMeter)this.connector).getAllConso();
	}



}
