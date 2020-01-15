package wattwatt.ports.devices.schedulable.washingmachine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.devices.schedulable.washingmachine.WashingMachine;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;

public class WashingMachineInPort extends AbstractInboundPort implements IWashingMachine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WashingMachineInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IWashingMachine.class, owner);
	}

	@Override
	public boolean isWorking() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).isWorking());
	}

	@Override
	public boolean canDelay(int delay) throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).canDelay(delay));
	}

	@Override
	public boolean canAdvance(int advance) throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).canAdvance(advance));
	}

	@Override
	public int durationWork() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).durationWork());
	}

	@Override
	public int startingTime() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).startingTime());
	}

	@Override
	public int endingTime() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).endingTime());
	}

	@Override
	public int getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).giveConso());
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((WashingMachine) owner).isOn());
	}

	@Override
	public void endBefore(int end) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).endBefore(end);
				return null;
			}
		});

	}

	@Override
	public void startAt(int debut) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).startAt(debut);
				return null;
			}
		});

	}

	@Override
	public void late(int delay) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).late(delay);
				return null;
			}
		});

	}

	@Override
	public void advance(int advance) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).advance(advance);
				return null;
			}
		});

	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).On();
				return null;
			}
		});

	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).Off();
				return null;
			}
		});

	}

	@Override
	public void ecoLavage() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).ecoLavage();
				return null;
			}
		});

	}

	@Override
	public void premiumLavage() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((WashingMachine) this.getServiceOwner()).premiumLavage();
				return null;
			}
		});

	}
}
