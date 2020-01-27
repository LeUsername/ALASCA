package wattwatt.interfaces.energyproviders;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * The interface <code>IEnergyProviders</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Defines the interface for all our energy sources.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public interface IEnergyProviders extends OfferedI, RequiredI {
	
	/**
	 * Get the energy production of a energy provider
	 * @return quantity of energy provided
	 * @throws Exception<i>todo.</i>
	 */
	public int getEnergy() throws Exception;
}
