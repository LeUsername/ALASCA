package wattwatt.interfaces.devices.schedulable.washingmachine;

import wattwatt.interfaces.devices.schedulable.ISchedulable;

/**
 * The interface <code>IWashingMachine</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for our Washing Machine component.
 * A Washing Machine is a schedulable device that has two mode of washing
 * Premium and Eco. Depending on the washing mode the electric consumption
 * of the washing machine can change.
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IWashingMachine extends ISchedulable{
	
	/**
	 * Set the washing mode to Eco
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void ecoWashing() throws Exception;
	
	/**
	 * Set the washing mode to Eco
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void premiumWashing() throws Exception;
	

}
