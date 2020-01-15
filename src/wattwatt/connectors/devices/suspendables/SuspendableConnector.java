package wattwatt.connectors.devices.suspendables;

import wattwatt.connectors.devices.DeviceConnector;
import wattwatt.interfaces.devices.suspendable.ISuspendable;

public abstract class SuspendableConnector extends DeviceConnector implements ISuspendable {

	@Override
	public void suspend() throws Exception {
		((ISuspendable) this.offering).suspend();

	}

	@Override
	public void resume() throws Exception {
		((ISuspendable) this.offering).resume();
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((ISuspendable) this.offering).isWorking();
	}
}
