package simulation.mil;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.models.electricmeter.ElectricMeterCoupledModel;

public class MIL_ElectricMeter {
	public static void	main(String[] args)
	{
		SimulationEngine se ;

		try {
			Architecture localArchitecture = ElectricMeterCoupledModel.build() ;
			se = localArchitecture.constructSimulator() ;
			se.setDebugLevel(0) ;
			System.out.println(se.simulatorAsString()) ;
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			se.doStandAloneSimulation(0.0, 500.0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
