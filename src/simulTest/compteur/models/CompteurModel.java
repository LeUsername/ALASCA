package simulTest.compteur.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulTest.compteur.models.events.Consommation;

@ModelExternalEvents(exported = { Consommation.class })
public class CompteurModel extends AtomicModel {

	public static class CompteurReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public CompteurReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CompteurReport(" + this.getModelURI() + ")";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String URI = "CompteurModel";

	private static final String SERIES = "consommation";

	protected double consommation;

	/** plotter for the intensity level over time. */
	protected XYPlotter intensityPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentStateAccessI componentRef;

	public CompteurModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		PlotterDescription pd = new PlotterDescription("Compteur consommation total", "Time (sec)", "Consommation", 100,
				0, 600, 400);
		this.intensityPlotter = new XYPlotter(pd);
		this.intensityPlotter.createSeries(SERIES);

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef");
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.consommation = 0.0;
		// initialisation of the intensity plotter
		this.intensityPlotter.initialise();
		// show the plotter on the screen
		this.intensityPlotter.showPlotter();

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	public Duration timeAdvance() {
		if (this.componentRef == null) {
			// the model has no internal event, however, its state will evolve
			// upon reception of external events.
			return Duration.INFINITY;
		} else {
			// This is to test the embedding component access facility.
			return new Duration(10.0, TimeUnit.SECONDS);
		}
	}

	@Override
	public Vector<EventI> output() {
		Vector<EventI> ret = null;
		ret = new Vector<EventI>(1);
		assert ret.size() == 1;
		ret.add(new Consommation(this.getCurrentStateTime(), this.consommation));
		return ret;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " + componentRef.getEmbeddingComponentStateValue("consommation"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.intensityPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.getConsommation()) ;
		Thread.sleep(10000L) ;
		this.intensityPlotter.dispose() ;

		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new CompteurReport(this.getURI()) ;
	}

	public double getConsommation() {
		return this.consommation;
	}
}
