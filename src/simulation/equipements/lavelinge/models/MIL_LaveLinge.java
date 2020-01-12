package simulation.equipements.lavelinge.models;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.Duree;
import simulation.deployment.WattWattMain;
import simulation.equipements.lavelinge.tools.LaveLingeUserBehaviour;
import wattwatt.tools.lavelinge.LaveLingeReglage;

public class MIL_LaveLinge {
	public static void main(String[] args) {
		SimulationEngine se;

		try {
			Architecture localArchitecture = LaveLingeCoupledModel.build();
			se = localArchitecture.constructSimulator();
			Map<String, Object> simParams = new HashMap<String, Object>();

			simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTBU,
					LaveLingeUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
			simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTWE,
					LaveLingeUserBehaviour.MEAN_TIME_WORKING_ECO);
			simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.MTWP,
					LaveLingeUserBehaviour.MEAN_TIME_WORKING_PREMIUM);
			simParams.put(LaveLingeUserModel.URI + ":" + LaveLingeUserModel.STD,
					10.0);
			
			simParams.put(LaveLingeModel.URI + ":" + LaveLingeModel.CONSUMPTION_ECO,
					LaveLingeReglage.CONSO_ECO_MODE_SIM);
			simParams.put(LaveLingeModel.URI + ":" + LaveLingeModel.CONSUMPTION_PREMIUM,
					LaveLingeReglage.CONSO_PREMIUM_MODE_SIM);
			simParams.put(LaveLingeModel.URI + ":" + LaveLingeUserModel.STD,
					10.0);
			
			simParams.put(
					LaveLingeUserModel.URI + ":" + LaveLingeUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("LaveLingeUserModel", "Time (min)", "User actions",
							WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			simParams.put(
					LaveLingeModel.URI + ":" + LaveLingeModel.INTENSITY_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("LaveLingeModel", "Time (min)", "Consommation (W)", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			
			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0);
//			System.out.println(se.simulatorAsString());
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, 2*Duree.DUREE_SEMAINE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
