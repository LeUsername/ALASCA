package simulation.mil;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.models.sechecheveux.HairDryerCoupledModel;
import simulation.tools.TimeScale;

public class MIL_HairDryer {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = HairDryerCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, TimeScale.WEEK) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
