package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.windturbine.WindTurbineCoupledModel;
import simulation.models.windturbine.WindTurbineModel;
import simulation.models.windturbine.WindTurbineSensorModel;
import simulation.tools.TimeScale;

public class MIL_WindTurbine {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = WindTurbineCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			simParams.put(
					WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INITIAL_DELAY,
					10.0) ;
			simParams.put(
					WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INTERDAY_DELAY,
					100.0) ;
			
			simParams.put(
					WindTurbineModel.URI + ":" + WindTurbineModel.PRODUCTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Wind turbine model",
							"Time (min)",
							"Production (Watt)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, TimeScale.WEEK) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
