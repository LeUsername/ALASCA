package wattwatt.connectors.devices;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.devices.IDevices;

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
	public int getConso() throws Exception {
		return ((IDevices) this.offering).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IDevices) this.offering).isOn();
	}
}
