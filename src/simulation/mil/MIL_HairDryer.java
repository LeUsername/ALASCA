package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.models.hairdryer.HairDryerModel;
import simulation.models.hairdryer.HairDryerUserModel;
import simulation.tools.TimeScale;
import simulation.tools.hairdryer.HairDryerUserBehaviour;

//-----------------------------------------------------------------------------
/**
* The class <code>MIL_HairDryer</code> simply tests the simulation architecture
* defined by <code>HairDryerCoupledModel</code> before attaching it to a
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
public class MIL_HairDryer {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = HairDryerCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			simParams.put(
					HairDryerUserModel.URI + ":" + HairDryerUserModel.INITIAL_DELAY,
					HairDryerUserBehaviour.INITIAL_DELAY) ;
			simParams.put(
					HairDryerUserModel.URI + ":" + HairDryerUserModel.INTERDAY_DELAY,
					HairDryerUserBehaviour.INTERDAY_DELAY) ;
			simParams.put(
					HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_BETWEEN_USAGES,
					HairDryerUserBehaviour.MEAN_TIME_BETWEEN_USAGES) ;
			simParams.put(
					HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_AT_HIGH,
					HairDryerUserBehaviour.MEAN_TIME_AT_HIGH) ;
			simParams.put(
					HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_AT_LOW,
					HairDryerUserBehaviour.MEAN_TIME_AT_LOW) ;
			
			simParams.put(
					HairDryerModel.URI + ":" + HairDryerModel.INTENSITY_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Hair dryer model",
							"Time (min)",
							"Intensity (Watt)",
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
