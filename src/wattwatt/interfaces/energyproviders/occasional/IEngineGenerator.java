package wattwatt.interfaces.energyproviders.occasional;

import wattwatt.interfaces.energyproviders.IEnergyProviders;

/**
 * The interface <code>IEngineGenerator</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for our Engine generator.
 * The Engine generator is an occasional energy source.
 * He can produce energy depending on how much fuel he has.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IEngineGenerator extends IEnergyProviders {

	/**
	 * Check if there is no fuel
	 * @return true if there is no fuel
	 * @throws Exception<i>todo.</i>
	 */
	public boolean fuelIsEmpty() throws Exception;

	/**
	 * Check if the engine generator is full
	 * @return true if the engine generator is full
	 * @throws Exception<i>todo.</i>
	 */
	public boolean fuelIsFull() throws Exception;

	/**
	 * Get the quantity of fuel in the engine generator
	 * @return quantity of fuel in the engine generator
	 * @throws Exception<i>todo.</i>
	 */
	public int fuelQuantity() throws Exception;

	/**
	 * Add fuel into the engine generator
	 * @param quantity of fuel put into the engine generator (can't exceed the max capacity of the engine generator)
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void addFuel(int quantity) throws Exception;

	/**
	 * Turn on the engine generator
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void on() throws Exception; // use fuel to prod electricity

	/**
	 * Turn off the engine generator
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void off() throws Exception;

	/**
	 * Check if the engine generator is on
	 * @return	true if the engine generator is on
	 * @throws Exception<i>todo.</i>
	 */
	public boolean isOn() throws Exception;

}
