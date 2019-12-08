package wattwatt.ports.appareils.planifiable.lavelinge;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.composants.appareils.planifiable.lavelinge.LaveLinge;
import wattwatt.interfaces.appareils.planifiable.lavelinge.ILaveLinge;

public class LaveLingeInPort extends AbstractInboundPort implements ILaveLinge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LaveLingeInPort(String uri, ComponentI owner) throws Exception {
		super(uri, ILaveLinge.class, owner);
	}

	@Override
	public boolean isWorking() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).isWorking());
	}

	@Override
	public boolean canDelay(int delay) throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).canDelay(delay));
	}

	@Override
	public boolean canAdvance(int advance) throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).canAdvance(advance));
	}

	@Override
	public int durationWork() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).durationWork());
	}

	@Override
	public int startingTime() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).startingTime());
	}

	@Override
	public int endingTime() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).endingTime());
	}

	@Override
	public int getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).giveConso());
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((LaveLinge) owner).isOn());
	}

	@Override
	public void endBefore(int end) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).endBefore(end);
				return null;
			}
		});

	}

	@Override
	public void startAt(int debut) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).startAt(debut);
				return null;
			}
		});

	}

	@Override
	public void late(int delay) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).late(delay);
				return null;
			}
		});

	}

	@Override
	public void advance(int advance) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).advance(advance);
				return null;
			}
		});

	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).On();
				return null;
			}
		});

	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).Off();
				return null;
			}
		});

	}

	@Override
	public void ecoLavage() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).ecoLavage();
				return null;
			}
		});

	}

	@Override
	public void premiumLavage() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((LaveLinge) this.getServiceOwner()).premiumLavage();
				return null;
			}
		});

	}
}
