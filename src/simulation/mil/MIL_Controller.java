package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.controller.ControllerCoupledModel;
import simulation.models.controller.ControllerModel;

public class MIL_Controller {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = ControllerCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>();
			simParams.put(					ControllerModel.URI + ":" + ControllerModel.PRODUCTION + ":"
					+ PlotterDescription.PLOTTING_PARAM_NAME,
			new PlotterDescription("ControllerModel", "Time (sec)", "W", WattWattMain.ORIGIN_X,
					WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
					WattWattMain.getPlotterHeight()));
			simParams.put(					ControllerModel.URI + ":" + ControllerModel.CONTROLLER_STUB + ":"
					+ PlotterDescription.PLOTTING_PARAM_NAME,
			new PlotterDescription("ControllerModel", "Time (sec)", "Decision", WattWattMain.ORIGIN_X,
					WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
					WattWattMain.getPlotterHeight()));
			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
