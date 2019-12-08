package wattwatt.ports.sources.intermittent.groupeelectrogene;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.composants.sources.intermittent.groupeelectrogene.GroupeElectrogene;
import wattwatt.interfaces.sources.intermittent.IGroupeElectrogene;

public class GroupeElectrogeneInPort extends AbstractInboundPort implements IGroupeElectrogene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupeElectrogeneInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IGroupeElectrogene.class, owner);
	}

	@Override
	public int getEnergie() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((GroupeElectrogene) owner).getEnergie());
	}

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((GroupeElectrogene) owner).fuelIsEmpty());
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((GroupeElectrogene) owner).fuelIsFull());
	}

	@Override
	public int fuelQuantity() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((GroupeElectrogene) owner).fuelQuantity());
	}

	@Override
	public void on() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((GroupeElectrogene) this.getServiceOwner()).on();
				return null;
			}
		});
	}

	@Override
	public void off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((GroupeElectrogene) this.getServiceOwner()).off();
				return null;
			}
		});

	}

	@Override
	public void addFuel(int quantity) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((GroupeElectrogene) this.getServiceOwner()).addFuel(quantity);
				return null;
			}
		});
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((GroupeElectrogene) owner).isOn());
	}

}
