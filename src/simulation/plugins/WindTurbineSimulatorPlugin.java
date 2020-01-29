package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.windturbine.WindTurbineModel;
import simulation.models.windturbine.WindTurbineSensorModel;
import wattwatt.tools.URIS;

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
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// Here, we are at a good place to capture the reference to the owner
		// component and pass it to the simulation model.
		simParams.put(URIS.WIND_TURBINE_URI,
					  this.owner) ;

		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INITIAL_DELAY,
				10.0) ;
		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INTERDAY_DELAY,
				100.0) ;
		
		super.setSimulationRunParameters(simParams) ;
		
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.WIND_TURBINE_URI) ;
	}
	
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
