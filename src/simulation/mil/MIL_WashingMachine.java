package simulation.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.washingmachine.WashingMachineCoupledModel;
import simulation.models.washingmachine.WashingMachineModel;
import simulation.models.washingmachine.WashingMachineUserModel;
import simulation.tools.TimeScale;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;
import wattwatt.tools.washingmachine.WashingMachineSetting;

//-----------------------------------------------------------------------------
/**
* The class <code>MIL_WashingMachine</code> simply tests the simulation architecture
* defined by <code>WashingMachineCoupledModel</code> before attaching it to a
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
public class MIL_WashingMachine {
	public static void main(String[] args) {
		SimulationEngine se;

		try {
			Architecture localArchitecture = WashingMachineCoupledModel.build();
			se = localArchitecture.constructSimulator();
			Map<String, Object> simParams = new HashMap<String, Object>();

			simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTBU,
					WashingMachineUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
			simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWE,
					WashingMachineUserBehaviour.MEAN_TIME_WORKING_ECO);
			simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWP,
					WashingMachineUserBehaviour.MEAN_TIME_WORKING_PREMIUM);
			simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.STD,
					10.0);
			
			simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_ECO,
					WashingMachineSetting.CONSO_ECO_MODE_SIM);
			simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_PREMIUM,
					WashingMachineSetting.CONSO_PREMIUM_MODE_SIM);
			simParams.put(WashingMachineModel.URI + ":" + WashingMachineUserModel.STD,
					10.0);
			
			simParams.put(
					WashingMachineUserModel.URI + ":" + WashingMachineUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("LaveLingeUserModel", "Time (min)", "User actions",
							WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			simParams.put(
					WashingMachineModel.URI + ":" + WashingMachineModel.INTENSITY_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("LaveLingeModel", "Time (min)", "Consommation (W)", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			
			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0);
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, 2*TimeScale.WEEK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
