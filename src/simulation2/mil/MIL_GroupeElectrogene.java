package simulation2.mil;

import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation2.deployment.WattWattMain;
import simulation2.models.groupeelectrogene.GroupeElectrogeneCoupledModel;
import simulation2.models.groupeelectrogene.GroupeElectrogeneModel;
import simulation2.models.groupeelectrogene.GroupeElectrogeneUserModel;
import simulation2.tools.Duree;
import simulation2.tools.groupeelectrogene.GroupeElectrogeneUserBehaviour;

public class MIL_GroupeElectrogene {
	public static void main(String[] args) {
		SimulationEngine se;

		try {
			Architecture localArchitecture = GroupeElectrogeneCoupledModel.build();
			se = localArchitecture.constructSimulator();
			Map<String, Object> simParams = new HashMap<String, Object>();

			simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTBU,
					GroupeElectrogeneUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
			simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTW,
					GroupeElectrogeneUserBehaviour.MEAN_TIME_WORKING);
			simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTR,
					GroupeElectrogeneUserBehaviour.MEAN_TIME_AT_REFILL);
			
			
			simParams.put(
					GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneUserModel", "Time (sec)", "User actions",
							WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			simParams.put(
					GroupeElectrogeneModel.URI + ":" + GroupeElectrogeneModel.PRODUCTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Watt", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			simParams.put(
					GroupeElectrogeneModel.URI + ":" + GroupeElectrogeneModel.QUANTITY + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Litre", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			se.setSimulationRunParameters(simParams);
			se.setDebugLevel(0);
			System.out.println(se.simulatorAsString());
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.doStandAloneSimulation(0.0, Duree.DUREE_SEMAINE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
