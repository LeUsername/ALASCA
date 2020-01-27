package wattwatt.interfaces.energyproviders.random.windturbine;

import wattwatt.interfaces.energyproviders.IEnergyProviders;

/**
 * The interface <code>IWindTurbine</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for our wind turbine.
 * The wind turbine is an random energy source depending on the wind.
 * If there is not much wind he is not working and if there is too much wind as well.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IWindTurbine extends IEnergyProviders {

	/**
	 * Turn on the wind turbine
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void On() throws Exception;

	/**
	 * Turn off the wind turbine
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void Off() throws Exception;

	/**
	 * Check if the wind turbine is on
	 * @return	true if the wind turbine is on
	 * @throws Exception<i>todo.</i>
	 */
	public boolean isOn() throws Exception;

}
