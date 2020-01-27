package wattwatt.connectors.devices.suspendables;

/**
 * The class <code>SuspendableConnector</code> implements a connector between
 * the <code>IController</code> and the <code>ISuspendable</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>ISuspendable</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
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
