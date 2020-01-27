package wattwatt.connectors.devices.suspendables.fridge;

/**
 * The class <code>WashingMachineConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IFridge</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IFridge</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
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
