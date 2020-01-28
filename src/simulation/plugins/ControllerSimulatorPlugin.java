package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.controller.ControllerModel;
import wattwatt.tools.URIS;

//------------------------------------------------------------------------------
/**
* The class <code>ControllerSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>Controller</code>.
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
public class ControllerSimulatorPlugin extends AtomicSimulatorPlugin {

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
		simParams.put(URIS.CONTROLLER_URI,
					  this.owner) ;

		
		super.setSimulationRunParameters(simParams) ;
		
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.CONTROLLER_URI) ;
	}
	

	public Object getModelStateValue(String modelURI, String name) throws Exception {

		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof ControllerModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		
		// TODO
		return null;

	}

}
