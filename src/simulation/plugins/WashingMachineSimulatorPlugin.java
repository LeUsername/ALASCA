package simulation.plugins;

import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.washingmachine.WashingMachineModel;
import simulation.models.washingmachine.WashingMachineUserModel;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;
import wattwatt.tools.URIS;
import wattwatt.tools.washingmachine.WashingMachineSetting;

//------------------------------------------------------------------------------
/**
* The class <code>WashingMachineSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>WashingMachine</code>.
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
public class WashingMachineSimulatorPlugin extends AtomicSimulatorPlugin {
	
	/**
	 * 
	 */
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
		simParams.put(URIS.WASHING_MACHINE_URI,
					  this.owner) ;
		
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTBU,
				WashingMachineUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWE,
				WashingMachineUserBehaviour.MEAN_TIME_WORKING_ECO);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWP,
				WashingMachineUserBehaviour.MEAN_TIME_WORKING_PREMIUM);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.STD,
				10.0);
		
		
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_ECO,
				WashingMachineSetting.CONSO_ECO_MODE_SIM);
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_PREMIUM,
				WashingMachineSetting.CONSO_PREMIUM_MODE_SIM);
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineUserModel.STD,
				10.0);

		
		super.setSimulationRunParameters(simParams) ;
		
		// It is a good idea to remove the binding to avoid other components
		// to get a reference on this owner component i.e., have a reference
		// leak outside the component.
		simParams.remove(URIS.WASHING_MACHINE_URI) ;
	}
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof WashingMachineModel;

		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("isOn")) {
			return ((WashingMachineModel) m).isOn();
		}else if(name.equals("consommation")){
			return ((WashingMachineModel) m).getIntensity();
		} else if(name.equals("lavageMode")) {
			return ((WashingMachineModel) m).getLavage();
		} else {
			assert name.equals("isWorking");
			return ((WashingMachineModel) m).isWorking();
		}
	}

}
