package simulation.equipements.refrigerateur.models;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;

public class MIL_Refrigerateur {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = RefrigerateurCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			simParams.put(RefrigerateurUserModel.URI + ":" + RefrigerateurUserModel.MTBI, 200.0) ;
			simParams.put(RefrigerateurUserModel.URI + ":" + RefrigerateurUserModel.MID, 10.0) ;
			simParams.put(
					RefrigerateurUserModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurUserModel",
							"Time (sec)",
							"Opened/Closed",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			simParams.put(
					RefrigerateurModel.URI + ":" + RefrigerateurModel.MAX_TEMPERATURE, 10.0) ;
			simParams.put(
					RefrigerateurModel.URI + ":" + RefrigerateurModel.MIN_TEMPERATURE, 1.0) ;
			simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BAAR, 1.75) ;
			simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BBAR, 1.75) ;
			simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BMASSF, 1.0/11.0) ;
			simParams.put(RefrigerateurModel.URI + ":" + RefrigerateurModel.BIS, 0.5) ;
			simParams.put(
					RefrigerateurModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurModel",
							"Time (sec)",
							"Temperature (�C)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y +
							WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;

			simParams.put(
					RefrigerateurSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurSensorModel",
							"Time (sec)",
							"Bandwidth (Mbps)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y +
							2*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;

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
