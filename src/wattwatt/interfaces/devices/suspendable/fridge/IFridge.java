package wattwatt.interfaces.devices.suspendable.fridge;

import wattwatt.interfaces.devices.suspendable.ISuspendable;

/**
 * The interface <code>IFridge</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for our fridge device.
 * A Fridge is a suspendable device that has two compartments.
 * Each compartments have a different temperature on which to stay.
 * This component when it's working will try to always keep those compartments
 * at the right temperature.
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IFridge extends ISuspendable {
	
	/**
	 * Check the temperature of the upper compartement
	 * @return	temperature of the upper compartement
	 * @throws Exception<i>todo.</i>
	 */
	public double getTempH() throws Exception;
	
	/**
	 * Check the temperature of the lower compartement
	 * @return	temperature of the lower compartement
	 * @throws Exception<i>todo.</i>
	 */
	public double getTempB() throws Exception;

}
