package wattwatt.interfaces.devices.schedulable.washingmachine;

import wattwatt.interfaces.devices.schedulable.ISchedulable;

public interface IWashingMachine extends ISchedulable{
	
	public void ecoLavage() throws Exception;
	public void premiumLavage() throws Exception;
	

}
