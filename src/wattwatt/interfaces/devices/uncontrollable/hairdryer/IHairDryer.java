package wattwatt.interfaces.devices.uncontrollable.hairdryer;

import wattwatt.interfaces.devices.IDevices;

public interface IHairDryer extends IDevices {
	
	public void switchMode() throws Exception;
	public void increasePower() throws Exception;
	public void decreasePower() throws Exception;

}
