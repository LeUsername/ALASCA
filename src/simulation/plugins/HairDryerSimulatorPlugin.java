package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.hairdryer.HairDryerModel;
import wattwatt.tools.URIS;

//------------------------------------------------------------------------------
/**
* The class <code>HairDryerSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>HairDryer</code>.
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
public class HairDryerSimulatorPlugin extends AtomicSimulatorPlugin {
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
		simParams.put(URIS.HAIR_DRYER_URI,
					  this.owner) ;
		
		
		super.setSimulationRunParameters(simParams) ;
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.HAIR_DRYER_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof HairDryerModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("mode")) {
			return ((HairDryerModel) m).getMode();
		} else if (name.equals("isOn")) {
			return ((HairDryerModel) m).isOn();
		} else if (name.equals("powerLevel")) {
			return ((HairDryerModel) m).getPowerLevel();
		}else {
			assert name.equals("intensity");
			return ((HairDryerModel) m).getIntensity();
		}
	}
}
