package wattwatt.connectors.devices.schedulable.washingmachine;

import wattwatt.connectors.devices.schedulable.SchedulableConnector;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;

public class WashingMachineConnector extends SchedulableConnector implements IWashingMachine {

	@Override
	public void ecoLavage() throws Exception {
		((IWashingMachine) this.offering).ecoLavage();
		
	}

	@Override
	public void premiumLavage() throws Exception {
		((IWashingMachine) this.offering).premiumLavage();
		
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((IWashingMachine) this.offering).isWorking();
	}

}
