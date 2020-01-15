package wattwatt.connectors.energyproviders.occasional.enginegenerator;

import wattwatt.connectors.energyproviders.EnergyProviderConnector;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;

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
	public int fuelQuantity() throws Exception {
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
