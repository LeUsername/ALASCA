package simulation.models.windturbine;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.windturbine.AbstractEolienneEvent;
import simulation.events.windturbine.SwitchOffEvent;
import simulation.events.windturbine.SwitchOnEvent;
import simulation.events.windturbine.WindReadingEvent;
import simulation.events.windturbine.WindTurbineProductionEvent;
import simulation.tools.windturbine.WindTurbineState;

@ModelExternalEvents(imported = { WindReadingEvent.class, 
								  SwitchOffEvent.class, 
								  SwitchOnEvent.class, 
								  TicEvent.class },
					 exported = { WindTurbineProductionEvent.class })
public class WindTurbineModel extends AtomicHIOAwithEquations {
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class WindTurbineModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WindTurbineModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "WindTurbineModelRepor(" + this.getModelURI() + ")";
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
	public static final String URI = "WindTurbineModel";
	
	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";
	
	/** true when a external event triggered a reading. */
	protected boolean triggerReading;

	protected double production;

	protected WindTurbineState state;

	protected static final double KELVIN_TEMP = 288.15; // On suppose la temperatur en Kelvin et constante

	protected static final int BLADES_AREA = 5; // m2

	protected Time lastWindReadingTime;

	protected Time currentWindReadingTime;
	
	/** plotter for the production level over time. */
	protected XYPlotter productionPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WindTurbineModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		// create a standard logger (logging on the terminal)
//		this.setLogger(new StandardLogger());
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// Initialise the look of the plotter
		String vname = this.getURI() + ":" + WindTurbineModel.PRODUCTION_SERIES + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd =(PlotterDescription) simParams.get(vname) ;
		this.productionPlotter = new XYPlotter(pd);
		this.productionPlotter.createSeries(PRODUCTION);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		// initialisation of the production plotter
		if(this.productionPlotter != null) {
			this.productionPlotter.initialise();
			this.productionPlotter.showPlotter();
		}
		
		this.state = WindTurbineState.OFF;

		this.lastWindReadingTime = new Time(0.0, TimeUnit.SECONDS);
		this.currentWindReadingTime = new Time(0.0, TimeUnit.SECONDS);
		
		this.triggerReading = false;

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
		this.production = 0.0;
		
		// first data in the plotter to start the plot.
		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.production; // kW

			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			WindTurbineProductionEvent production = new WindTurbineProductionEvent(currentTime, reading);
			ret.add(production);

			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
//		if (this.componentRef == null) {
//			// the model has no internal event, however, its state will evolve
//			// upon reception of external events.
//			return Duration.INFINITY;
//		} else {
//			// This is to test the embedding component access facility.
//			return new Duration(10.0, TimeUnit.SECONDS);
//		}
		if (!this.triggerReading) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(this.getSimulatedTimeUnit());
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
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		boolean ticReceived = false;

		if (ce instanceof TicEvent) {
			ticReceived = true;
		} else {
			assert ce instanceof AbstractEolienneEvent;
			if (ce instanceof WindReadingEvent) {
				this.currentWindReadingTime = ((WindReadingEvent) ce).getTimeOfOccurrence();
			}
			ce.executeOn(this);
		}
		if (ticReceived) {
			this.triggerReading = true;
			this.logMessage(this.getCurrentStateTime() + "|external|tic event received.");
		}
		
		// add a new data on the plotter; this data will open a new piece

		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());

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
		this.productionPlotter.addData(PRODUCTION, endTime.getSimulatedTime(), this.getProduction());

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new WindTurbineModelReport(this.getURI());
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public double windDensity(double tempKelvin) {
		return 1.292 * (273.15 / tempKelvin);
	}

	public double getProduction() {
		return this.production;
	}

	public void setProduction(double windSpeed) {
		this.production = 0.5 * (BLADES_AREA
				* windDensity(KELVIN_TEMP)) * windSpeed * windSpeed ;
	}

	public void switchOn() {
		this.state = WindTurbineState.ON;
	}

	public void switchOff() {
		this.state = WindTurbineState.OFF;
	}
	
	public boolean isOn() {
		return this.state == WindTurbineState.ON;
	}
}
