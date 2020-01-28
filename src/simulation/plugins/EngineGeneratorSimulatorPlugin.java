package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;
import wattwatt.tools.URIS;

//------------------------------------------------------------------------------
/**
* The class <code>EngineGeneratorSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>EngineGenerator</code>.
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
public class EngineGeneratorSimulatorPlugin extends AtomicSimulatorPlugin{

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
		simParams.put(URIS.ENGINE_GENERATOR_URI,
					  this.owner) ;
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.INITIAL_DELAY,
				EngineGeneratorUserBehaviour.INITIAL_DELAY);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.INTERDAY_DELAY,
				EngineGeneratorUserBehaviour.INTERDAY_DELAY);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_BETWEEN_USAGES,
				EngineGeneratorUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_USAGE,
				EngineGeneratorUserBehaviour.MEAN_TIME_USAGE);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_REFILL,
				EngineGeneratorUserBehaviour.MEAN_TIME_REFILL);

		
		super.setSimulationRunParameters(simParams) ;
		
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.ENGINE_GENERATOR_URI) ;
	}
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof EngineGeneratorModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("isOn")) {
			return ((EngineGeneratorModel) m).isOn();
		} else if (name.equals("production")) {
			return ((EngineGeneratorModel) m).getProduction();
		} else if (name.equals("capacity")) {
			return ((EngineGeneratorModel) m).getCapacity();
		} else if (name.equals("isEmpty")) {
			return ((EngineGeneratorModel) m).isEmpty();
		}
		else {
			assert name.equals("isFull");
			return ((EngineGeneratorModel) m).isFull();
		}
	}

}
