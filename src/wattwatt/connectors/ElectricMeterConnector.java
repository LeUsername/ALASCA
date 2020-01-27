package wattwatt.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.electricmeter.IElectricMeter;

/**
 * The class <code>ElectricMeterConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IElectricMeter</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IElectricMeter</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class ElectricMeterConnector extends AbstractConnector implements IElectricMeter {

	@Override
	public double getAllConso() throws Exception {
		return ((IElectricMeter) this.offering).getAllConso();
	}

}
