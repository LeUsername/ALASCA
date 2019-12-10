package simulTest.models;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.HairDryerModel.State;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

// TODO
public class ControleurModel extends AtomicHIOAwithEquations {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SERIES = "consommation";

	protected double consommation;

	protected State currentState;

	protected XYPlotter intensityPlotter;

	protected EmbeddingComponentStateAccessI componentRef;

	public ControleurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		PlotterDescription pd = new PlotterDescription("Hair dryer intensity", "Time (sec)", "Intensity (Amp)", 100, 0,
				600, 400);
		this.intensityPlotter = new XYPlotter(pd);
		this.intensityPlotter.createSeries(SERIES);

		this.setLogger(new StandardLogger());
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

	public double getConso() {
		return this.consommation;
	}
}
