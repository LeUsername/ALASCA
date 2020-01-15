package wattwatt.interfaces.devices.suspendable;

import wattwatt.interfaces.devices.IDevices;

public interface ISuspendable extends IDevices {

	public void suspend() throws Exception;
	public void resume() throws Exception;
	
	public boolean isWorking() throws Exception;
}
