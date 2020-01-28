package wattwatt.connectors.devices;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.devices.IDevices;

/**
 * The class <code>WashingMachineConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IDevices</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IDevices</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public abstract class DeviceConnector extends AbstractConnector implements IDevices {

	@Override
	public void On() throws Exception {
		((IDevices) this.offering).On();
	}

	@Override
	public void Off() throws Exception {
		((IDevices) this.offering).Off();
	}

	@Override
	public double getConso() throws Exception {
		return ((IDevices) this.offering).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IDevices) this.offering).isOn();
	}
}
