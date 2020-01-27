package wattwatt.interfaces.devices.suspendable;

import wattwatt.interfaces.devices.IDevices;

/**
 * The interface <code>ISuspendable</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for all our suspendable devices.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface ISuspendable extends IDevices {

	/**
	 * Suspend the suspendable device
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void suspend() throws Exception;
	
	/**
	 * Resume a suspended suspendable device
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void resume() throws Exception;
	
	/**
	 * Check if a suspendable device is doing some work
	 * @return	true if the device is working
	 * @throws Exception<i>todo.</i>
	 */
	public boolean isWorking() throws Exception;
}
