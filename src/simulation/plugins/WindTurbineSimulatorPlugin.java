package simulation.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.windturbine.WindTurbineModel;

//------------------------------------------------------------------------------
/**
* The class <code>WindTurbineSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>WindTurbine</code>.
*
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
public class WindTurbineSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof WindTurbineModel;

		if (name.equals("production")) {
			return ((WindTurbineModel) m).getProduction();
		} else {
			assert name.equals("isOn");
			return ((WindTurbineModel) m).isOn();
		}
	}

}
