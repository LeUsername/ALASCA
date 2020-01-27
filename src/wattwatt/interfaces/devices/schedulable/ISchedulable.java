package wattwatt.interfaces.devices.schedulable;

import wattwatt.interfaces.devices.IDevices;

/**
 * The interface <code>ISchedulable</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for all our schedulable devices.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface ISchedulable extends IDevices{
	
	/**
	 * Check if a schedulable device is doing some work
	 * @return	true if the device is working
	 * @throws Exception<i>todo.</i>
	 */
	public boolean isWorking() throws Exception;
	
	/**
	 * Check if the begining of work of a schedulable device can be delayed for some time
	 * @param delay		time of delay
	 * @return	true if the device can be delayed
	 * @throws Exception<i>todo.</i>
	 */
	public boolean canDelay(int delay) throws Exception;
	
	/**
	 * Check if the begining of work of a schedulable device can be advanced for some time
	 * @param delay		time of advanced
	 * @return	true if the device can be advanced
	 * @throws Exception<i>todo.</i>
	 */
	public boolean canAdvance(int advance) throws Exception;
	
	/**
	 * Get the duration of the work of a schedulable device
	 * @return	the duration of work
	 * @throws Exception<i>todo.</i>
	 */
	public int durationWork() throws Exception;
	
	/**
	 * Get the starting time of the work of a schedulable device
	 * @return	the starting time
	 * @throws Exception<i>todo.</i>
	 */
	public int startingTime() throws Exception;
	
	/**
	 * Get the ending time of the work of a schedulable device
	 * @return	the ending time
	 * @throws Exception<i>todo.</i>
	 */
	public int endingTime() throws Exception;
	
	/**
	 * Set a time before which the schedulable device has to have finished working
	 * @param end		time before wich working must be done
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void endBefore(int end) throws Exception; //time is an int for now
	
	/**
	 * Set a time after which the schedulable device can start working
	 * @param debut		time after wich working can start
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void startAt(int debut) throws Exception;
	
	/**
	 * Delay the starting time of a schedulable device
	 * @param delay		time delay
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void late(int delay) throws Exception;
	
	/**
	 * Advance the starting time of a schedulable device
	 * @param advance	time advance
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void advance(int advance) throws Exception;

}
