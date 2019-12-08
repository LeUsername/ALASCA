package simulTest.models;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// TODO
public class ControleurModel extends AtomicHIOAwithEquations {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vector<EventI> output() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Duration timeAdvance() {
		// TODO Auto-generated method stub
		return null;
	}

}
