package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.electricmeter.ElectricMeterCoupledModel;
import simulation.models.electricmeter.ElectricMeterModel;

//-----------------------------------------------------------------------------
/**
* The class <code>MIL_ElectricMeter</code> simply tests the simulation architecture
* defined by <code>ElectricMeterCoupledModel</code> before attaching it to a
* component.
* The Coupled model use a Stub to represent the devices sending their energy consumption to the energy meter.
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
public class MIL_ElectricMeter {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = ElectricMeterCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			simParams.put(
					ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Electric meter model",
							"Time (min)",
							"Consumption (Watt)",
							WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
