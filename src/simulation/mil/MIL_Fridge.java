package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.fridge.FridgeCoupledModel;
import simulation.models.fridge.FridgeModel;
import simulation.models.fridge.FridgeSensorModel;
import simulation.models.fridge.FridgeUserModel;
import simulation.tools.TimeScale;

//-----------------------------------------------------------------------------
/**
* The class <code>MIL_Fridge</code> simply tests the simulation architecture
* defined by <code>FridgeCoupledModel</code> before attaching it to a
* component.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
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
public class MIL_Fridge {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = FridgeCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MTBI, 200.0) ;
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MID, 10.0) ;
			simParams.put(
					FridgeUserModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurUserModel",
							"Time (min)",
							"Opened / Closed",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			simParams.put(
					FridgeModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 3.0) ;
			simParams.put(
					FridgeModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
			simParams.put(FridgeModel.URI + ":" + FridgeModel.INITIAL_TEMP, 2.0) ;
			simParams.put(
					FridgeModel.URI + ":" + FridgeModel.TEMPERATURE + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
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
					FridgeModel.URI + ":"  + FridgeModel.CONSUMPTION + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurModel",
							"Time (min)",
							"Consommation (W)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y +
							2*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;

			simParams.put(
					FridgeSensorModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 2.5) ;
			simParams.put(
					FridgeSensorModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
			simParams.put(
					FridgeSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurSensorModel",
							"Time (min)",
							"Temperature (Celcius)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y +
							3*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;

			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, TimeScale.DAY) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
