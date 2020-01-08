package simulation.equipements.eolienne.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.deployment.WattWattMain;
import simulation.equipements.eolienne.models.events.AbstractEolienneEvent;
import simulation.equipements.eolienne.models.events.SwitchOffEvent;
import simulation.equipements.eolienne.models.events.SwitchOnEvent;
import simulation.equipements.eolienne.models.events.WindReadingEvent;
import simulation.equipements.eolienne.tools.EolienneState;

@ModelExternalEvents(imported = { WindReadingEvent.class, SwitchOffEvent.class, SwitchOnEvent.class })
public class EolienneModel extends AtomicHIOAwithEquations {
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class EolienneModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public EolienneModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "EolienneModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = "EolienneModel";

	private static final String SERIES = "production";

	/** plotter for the production level over time. */
	protected XYPlotter productionPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentStateAccessI componentRef;

	protected Value<Double> production = new Value<Double>(this, 0.0);

	protected EolienneState state;

	protected static final double tempKelvin = 288.15; // On suppose la temperatur en Kelvin et constante

	protected static final int bladesArea = 5; // m2

	protected Time lastWindReadingTime;

	protected Time currentWindReadingTime;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public EolienneModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		// creation of a plotter to show the evolution of the intensity over
		// time during the simulation.
		PlotterDescription pd = new PlotterDescription("Production", "Time (min)", "Production (W)",
				3 * WattWattMain.getPlotterWidth(),
				0,
				WattWattMain.getPlotterWidth(),
				WattWattMain.getPlotterHeight());
		this.productionPlotter = new XYPlotter(pd);
		this.productionPlotter.createSeries(SERIES);

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// The reference to the embedding component
		//this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		// initialisation of the intensity plotter
		this.productionPlotter.initialise();
		// show the plotter on the screen
		this.productionPlotter.showPlotter();

		this.state = EolienneState.OFF;

		this.lastWindReadingTime = new Time(0.0, TimeUnit.SECONDS);
		this.currentWindReadingTime = new Time(0.0, TimeUnit.SECONDS);

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {
		// first data in the plotter to start the plot.
		this.productionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI> output() {
		// the model does not export any event.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
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

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			try {
				this.logMessage("component state = " + componentRef.getEmbeddingComponentStateValue("production"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		Vector<EventI> currentEvents = this.getStoredEventAndReset();

		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		assert ce instanceof AbstractEolienneEvent;


		if (ce instanceof WindReadingEvent) {
			this.currentWindReadingTime = ((WindReadingEvent) ce).getTimeOfOccurrence();
		}
		ce.executeOn(this);
		// add a new data on the plotter; this data will open a new piece

		this.productionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());

		if (ce instanceof WindReadingEvent) {
			this.lastWindReadingTime = this.currentWindReadingTime;
		}
		super.userDefinedExternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.productionPlotter.addData(SERIES, endTime.getSimulatedTime(), this.getProduction());

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new EolienneModelReport(this.getURI());
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public double windDensity(double tempKelvin) {
		return 1.292 * (273.15 / tempKelvin);
	}

	public double getProduction() {
		return this.production.v;
	}

	public void setProduction(double windSpeed) {
		this.production.v = 0.5 * (bladesArea
				* windDensity(tempKelvin)) * windSpeed * windSpeed ;
	}

	public void switchOn() {
		this.state = EolienneState.ON;
	}

	public void switchOff() {
		this.state = EolienneState.OFF;
	}
	
	public boolean isOn() {
		return this.state == EolienneState.ON;
	}
}
