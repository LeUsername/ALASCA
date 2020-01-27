package wattwatt.interfaces.electricmeter;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>IElectricMeter</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for the Electric meter component.
 * This component will be used to see the overall electric consumption.
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IElectricMeter extends OfferedI, RequiredI{

	
	/**
	 * Get the overall electric consumption
	 * @return	overall electric comsuption
	 * @throws Exception<i>todo.</i>
	 */
	public double getAllConso() throws Exception;
}
