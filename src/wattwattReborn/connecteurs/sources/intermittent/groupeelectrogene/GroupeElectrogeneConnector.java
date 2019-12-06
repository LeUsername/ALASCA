package wattwattReborn.connecteurs.sources.intermittent.groupeelectrogene;

import wattwattReborn.connecteurs.sources.SourceConnector;
import wattwattReborn.interfaces.sources.intermittent.IGroupeElectrogene;

public class GroupeElectrogeneConnector extends SourceConnector implements IGroupeElectrogene {

	@Override
	public boolean fuelIsEmpty() throws Exception {
		return ((IGroupeElectrogene) this.offering).fuelIsEmpty();
	}

	@Override
	public boolean fuelIsFull() throws Exception {
		return ((IGroupeElectrogene) this.offering).fuelIsFull();
	}

	@Override
	public int fuelQuantity() throws Exception {
		return ((IGroupeElectrogene) this.offering).fuelQuantity();
	}

	@Override
	public void on() throws Exception {
		((IGroupeElectrogene) this.offering).on();
	}

	@Override
	public void off() throws Exception {
		((IGroupeElectrogene) this.offering).off();
	}

}
