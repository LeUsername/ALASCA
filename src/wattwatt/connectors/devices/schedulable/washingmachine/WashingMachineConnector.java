package wattwatt.connectors.devices.schedulable.washingmachine;

import wattwatt.connectors.devices.schedulable.SchedulableConnector;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;

/**
 * The class <code>WashingMachineConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IWashingMachine</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IWashingMachine</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class WashingMachineConnector extends SchedulableConnector implements IWashingMachine {

	@Override
	public void ecoWashing() throws Exception {
		((IWashingMachine) this.offering).ecoWashing();
		
	}

	@Override
	public void premiumWashing() throws Exception {
		((IWashingMachine) this.offering).premiumWashing();
		
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((IWashingMachine) this.offering).isWorking();
	}

}
