package wattwatt.interfaces.devices;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>IDevices</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for all our devices.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IDevices extends OfferedI, RequiredI{
	
	/**
	 * Turn on a device
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void On() throws Exception;
	
	/**
	 * Turn off a device
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void Off() throws Exception;
	
	/**
	 * Get the consommation of a device
	 * @return	the consommation
	 * @throws Exception<i>todo.</i>
	 */
	public int getConso() throws Exception;
	
	/**
	 * Check if a device is on
	 * @return	true if the device is on
	 * @throws Exception<i>todo.</i>
	 */
	public boolean isOn() throws Exception;

}
