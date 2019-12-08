package wattwatt.ports.sources.intermittent.groupeelectrogene;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.sources.intermittent.IGroupeElectrogene;

public class GroupeElectrogeneOutPort extends AbstractOutboundPort implements IGroupeElectrogene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupeElectrogeneOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IGroupeElectrogene.class, owner);
	}

	@Override
	public int getEnergie() throws Exception {
		return ((IGroupeElectrogene) this.connector).getEnergie();
	}

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return ((IGroupeElectrogene) this.connector).fuelIsEmpty();
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return ((IGroupeElectrogene) this.connector).fuelIsFull();
	}

	@Override
	public int fuelQuantity() throws Exception {
		return ((IGroupeElectrogene) this.connector).fuelQuantity();
	}

	@Override
	public void on() throws Exception {
		((IGroupeElectrogene) this.connector).on();

	}

	@Override
	public void off() throws Exception {
		((IGroupeElectrogene) this.connector).off();

	}

	@Override
	public void addFuel(int quantity) throws Exception {
		((IGroupeElectrogene) this.connector).addFuel(quantity);

	}

	@Override
	public boolean isOn() throws Exception {
		return ((IGroupeElectrogene) this.connector).isOn();
	}

}
