package wattwatt.ports.energyproviders.occasional.enginegenerator;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;

public class EngineGeneratorOutPort extends AbstractOutboundPort implements IEngineGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EngineGeneratorOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IEngineGenerator.class, owner);
	}

	@Override
	public int getEnergy() throws Exception {
		return ((IEngineGenerator) this.connector).getEnergy();
	}

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return ((IEngineGenerator) this.connector).fuelIsEmpty();
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return ((IEngineGenerator) this.connector).fuelIsFull();
	}

	@Override
	public int fuelQuantity() throws Exception {
		return ((IEngineGenerator) this.connector).fuelQuantity();
	}

	@Override
	public void on() throws Exception {
		((IEngineGenerator) this.connector).on();

	}

	@Override
	public void off() throws Exception {
		((IEngineGenerator) this.connector).off();

	}

	@Override
	public void addFuel(int quantity) throws Exception {
		((IEngineGenerator) this.connector).addFuel(quantity);

	}

	@Override
	public boolean isOn() throws Exception {
		return ((IEngineGenerator) this.connector).isOn();
	}

}
