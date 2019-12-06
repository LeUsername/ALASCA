package wattwattReborn.ports.appareils.incontrolable.sechecheveux;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwattReborn.composants.appareils.incontrolable.sechecheveux.SecheCheveux;
import wattwattReborn.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;

public class SecheCheveuxInPort extends AbstractInboundPort implements ISecheCheveux {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SecheCheveuxInPort(String uri, ComponentI owner) throws Exception {
		super(uri, ISecheCheveux.class, owner);
	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((SecheCheveux) this.getServiceOwner()).on();
				return null;
			}
		});
	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((SecheCheveux) this.getServiceOwner()).off();
				return null;
			}
		});

	}

	@Override
	public int getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((SecheCheveux) owner).giveConso());

	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((SecheCheveux) owner).isOn());

	}

	@Override
	public void switchMode() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((SecheCheveux) this.getServiceOwner()).switchMode();
				return null;
			}
		});

	}

	@Override
	public void increasePower() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((SecheCheveux) this.getServiceOwner()).increasePower();
				return null;
			}
		});

	}

	@Override
	public void decreasePower() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((SecheCheveux) this.getServiceOwner()).decreasePower();
				return null;
			}
		});

	}

}
