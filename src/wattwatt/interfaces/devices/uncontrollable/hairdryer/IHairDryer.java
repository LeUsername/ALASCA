package wattwatt.interfaces.devices.uncontrollable.hairdryer;

import wattwatt.interfaces.devices.IDevices;

/**
 * The interface <code>IHairDryer</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for our hair dryer device.
 * A hair dryer is a uncontrolable device. He have multiple mode
 * and power level, he can blow cold air or hot air and depending
 * on his mode and power level his electric consumption will vary.
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IHairDryer extends IDevices {
	
	/**
	 * Switch the mode of the hair dryer from hot to cold air or
	 * from cold to hot air.
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void switchMode() throws Exception;
	
	/**
	 * Increase the power level of the hair dryer
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void increasePower() throws Exception;
	
	/**
	 * Increase the power level of the hair dryer
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void decreasePower() throws Exception;
	

}
