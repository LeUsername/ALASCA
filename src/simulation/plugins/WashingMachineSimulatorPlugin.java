package simulation.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.washingmachine.WashingMachineModel;

public class WashingMachineSimulatorPlugin extends AtomicSimulatorPlugin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof WashingMachineModel;

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
