package simulation.equipements.lavelinge.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.equipements.lavelinge.models.LaveLingeModel;

public class LaveLingeSimulatorPlugin extends AtomicSimulatorPlugin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof LaveLingeModel;

		if (name.equals("isOn")) {
			return ((LaveLingeModel) m).isOn();
		}else if(name.equals("consommation")){
			return ((LaveLingeModel) m).getIntensity();
		} else if(name.equals("lavageMode")) {
			return ((LaveLingeModel) m).getLavage();
		} else {
			assert name.equals("isWorking");
			return ((LaveLingeModel) m).isWorking();
		}
	}

}
