package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.electricmeter.ElectricMeterModel;
import wattwatt.tools.URIS;

//------------------------------------------------------------------------------
/**
* The class <code>ElectricMeterSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>ElectricMeter</code>.
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
public class ElectricMeterSimulatorPlugin extends AtomicSimulatorPlugin {
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
		simParams.put(URIS.ELECTRIC_METER_URI,
					  this.owner) ;

		
		super.setSimulationRunParameters(simParams) ;
		
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.ELECTRIC_METER_URI) ;
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

		assert m instanceof ElectricMeterModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		ElectricMeterModel model = (ElectricMeterModel) m;

		if (name.equals("fridgeConsumption")) {
			return model.getFridgeConsumption();
		} else if (name.equals("hairDryerConsumption")) {
			return model.getHairDryerConsumption();
		} else {
			assert name.equals("washingMachineConsumption");
			return model.getWashingMachineConsumption();
		}
	}
}
