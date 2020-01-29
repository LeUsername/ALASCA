package wattwatt.ports.energyproviders.occasional.enginegenerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.energyproviders.occasional.enginegenerator.EngineGenerator;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;

/**
 * The class <code>EngineGeneratorInPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The InBound port of the engine generator component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class EngineGeneratorInPort extends AbstractInboundPort implements IEngineGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EngineGeneratorInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IEngineGenerator.class, owner);
	}

	@Override
	public double getEnergy() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((EngineGenerator) owner).getEnergie());
	}

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((EngineGenerator) owner).fuelIsEmpty());
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((EngineGenerator) owner).fuelIsFull());
	}

	@Override
	public int fuelQuantity() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((EngineGenerator) owner).fuelQuantity());
	}

	@Override
	public void on() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((EngineGenerator) this.getServiceOwner()).on();
				return null;
			}
		});
	}

	@Override
	public void off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((EngineGenerator) this.getServiceOwner()).off();
				return null;
			}
		});

	}

	@Override
	public void addFuel(int quantity) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((EngineGenerator) this.getServiceOwner()).addFuel(quantity);
				return null;
			}
		});
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((EngineGenerator) owner).isOn());
	}

}
