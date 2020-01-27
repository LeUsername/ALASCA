package wattwatt.connectors.devices.uncontrollable.hairdryer;

import wattwatt.connectors.devices.DeviceConnector;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;

/**
 * The class <code>HairDryerConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IHairDryer</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IHairDryer</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
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
