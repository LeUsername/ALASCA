package wattwatt.ports.devices.uncontrollable.hairdryer;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.devices.uncontrollable.hairdryer.HairDryer;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;

/**
 * The class <code>HairDryerInPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The InBound port of the hair dryer component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class HairDryerInPort extends AbstractInboundPort implements IHairDryer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HairDryerInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IHairDryer.class, owner);
	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((HairDryer) this.getServiceOwner()).on();
				return null;
			}
		});
	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((HairDryer) this.getServiceOwner()).off();
				return null;
			}
		});

	}

	@Override
	public double getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((HairDryer) owner).giveConso());

	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((HairDryer) owner).isOn());

	}

	@Override
	public void switchMode() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((HairDryer) this.getServiceOwner()).switchMode();
				return null;
			}
		});

	}

	@Override
	public void increasePower() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((HairDryer) this.getServiceOwner()).increasePower();
				return null;
			}
		});

	}

	@Override
	public void decreasePower() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((HairDryer) this.getServiceOwner()).decreasePower();
				return null;
			}
		});

	}

}
