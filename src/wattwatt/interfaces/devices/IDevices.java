package wattwatt.interfaces.devices;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IDevices extends OfferedI, RequiredI{
	
	public void On() throws Exception;
	public void Off() throws Exception;
	public int getConso() throws Exception;
	public boolean isOn() throws Exception;

}
