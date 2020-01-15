package wattwatt.connectors.devices.uncontrollable.hairdryer;

import wattwatt.connectors.devices.DeviceConnector;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;

public class HairDryerConnector extends DeviceConnector implements IHairDryer {

	@Override
	public void switchMode() throws Exception {
		((IHairDryer) this.offering).switchMode();

	}

	@Override
	public void increasePower() throws Exception {
		((IHairDryer) this.offering).increasePower();

	}

	@Override
	public void decreasePower() throws Exception {
		((IHairDryer) this.offering).decreasePower();

	}

}
