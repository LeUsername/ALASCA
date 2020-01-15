package wattwatt.connectors.devices.suspendables.fridge;

import wattwatt.connectors.devices.suspendables.SuspendableConnector;
import wattwatt.interfaces.devices.suspendable.fridge.IFridge;

public class FridgeConnector extends SuspendableConnector implements IFridge {

	@Override
	public double getTempH() throws Exception {
		return ((IFridge) this.offering).getTempH();
	}

	@Override
	public double getTempB() throws Exception {
		return ((IFridge) this.offering).getTempB();
	}
}
