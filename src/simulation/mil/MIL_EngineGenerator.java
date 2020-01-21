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
			simParams.put(
					EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneUserModel", "Time (min)", "Start / Stop / Refill",
							2*WattWattMain.getPlotterWidth(),
							0, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Production (W)", 2*WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Fuel Quantity (L)", 2*WattWattMain.getPlotterWidth(),
							2*WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
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
