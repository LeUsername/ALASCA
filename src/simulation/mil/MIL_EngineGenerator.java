package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.tools.TimeScale;
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;

public class MIL_EngineGenerator {
	public static void main(String[] args) {
		SimulationEngine se;

		try {
			Architecture localArchitecture = EngineGeneratorCoupledModel.build();
			se = localArchitecture.constructSimulator();
			Map<String, Object> simParams = new HashMap<String, Object>();

			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTBU,
					EngineGeneratorUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTW,
					EngineGeneratorUserBehaviour.MEAN_TIME_WORKING);
			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTR,
					EngineGeneratorUserBehaviour.MEAN_TIME_AT_REFILL);
			
			
			simParams.put(
					EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneUserModel", "Time (sec)", "User actions",
							WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Watt", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Litre", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0);
			System.out.println(se.simulatorAsString());
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, TimeScale.WEEK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
