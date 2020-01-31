package wattwatt.connectors.energyproviders.occasional.enginegenerator;

import wattwatt.connectors.energyproviders.EnergyProviderConnector;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;


/**
 * The class <code>EngineGeneratorConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IEngineGenerator</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IEngineGenerator</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class EngineGeneratorConnector extends EnergyProviderConnector implements IEngineGenerator {

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return ((IEngineGenerator) this.offering).fuelIsEmpty();
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return ((IEngineGenerator) this.offering).fuelIsFull();
	}

	@Override
	public double fuelQuantity() throws Exception {
		return ((IEngineGenerator) this.offering).fuelQuantity();
	}

	@Override
	public void on() throws Exception {
		((IEngineGenerator) this.offering).on();
	}

	@Override
	public void off() throws Exception {
		((IEngineGenerator) this.offering).off();
	}

	@Override
	public void addFuel(int quantity) throws Exception {
		((IEngineGenerator) this.offering).addFuel(quantity);

	}

	@Override
	public boolean isOn() throws Exception {

		return ((IEngineGenerator) this.offering).isOn();
	}

}
