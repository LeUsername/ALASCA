package wattwatt.interfaces.devices.suspendable.fridge;

import wattwatt.interfaces.devices.suspendable.ISuspendable;

public interface IFridge extends ISuspendable {
	
	public double getTempH() throws Exception;
	public double getTempB() throws Exception;

}
