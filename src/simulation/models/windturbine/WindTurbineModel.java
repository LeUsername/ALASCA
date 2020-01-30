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
import simulation.events.windturbine.AbstractWindTurbineEvent;
import simulation.events.windturbine.SwitchOffEvent;
import simulation.events.windturbine.SwitchOnEvent;
import simulation.events.windturbine.WindReadingEvent;
import simulation.events.windturbine.WindTurbineProductionEvent;
import simulation.tools.windturbine.WindTurbineState;
import wattwatt.tools.URIS;

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
	public static final String URI = URIS.WIND_TURBINE_MODEL_URI;
	
	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";
	
	/** true when a external event triggered a reading. */
	protected boolean triggerReading;

	protected double production;

	protected WindTurbineState state;

	public static final double KELVIN_TEMP = 288.15; // On suppose la temperatur en Kelvin et constante

	public static final int BLADES_AREA = 5; // m2

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
		
		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.WIND_TURBINE_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		// initialisation of the production plotter
		
		if(componentRef == null) {
			this.state = WindTurbineState.OFF;
		}else {
			try {
				this.state = (WindTurbineState) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		this.triggerReading = false;

		try {
			// set the debug level triggering the production of log messages.
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if(this.productionPlotter != null) {
			this.productionPlotter.initialise();
			this.productionPlotter.showPlotter();
		}
		
		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {
		if(componentRef == null) {
			this.production = 0.0;
		} else {
			try {
				this.production =  (Double)this.componentRef.getEmbeddingComponentStateValue("production");;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
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
			try {
				this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());
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
		super.userDefinedExternalTransition(elapsedTime);
		if(this.componentRef == null) {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
	
			assert currentEvents != null ;
	
			Event ce = (Event) currentEvents.get(0);
	
			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} else {
				assert ce instanceof AbstractWindTurbineEvent;
				ce.executeOn(this);
			}
			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());
			
		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;
			Event ce = (Event) currentEvents.get(0);
			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} 
			try {
				this.production = (double) this.componentRef.getEmbeddingComponentStateValue("production");
				this.state = (WindTurbineState) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.getProduction());
	
			super.userDefinedExternalTransition(elapsedTime);
		}
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
		if(this.isOn()) {
			this.production = 0.5 * (BLADES_AREA
					* windDensity(KELVIN_TEMP)) * windSpeed * windSpeed ;
			// We tried to calculate realistic value but the production was much too
			// high compared to the consumption thus, we choose to divide this
			// production by 100
			this.production *= 3;
			this.production /= 100.0;
		}
		
		
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
