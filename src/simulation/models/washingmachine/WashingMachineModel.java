package simulation.models.washingmachine;

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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.washingmachine.EcoModeEvent;
import simulation.events.washingmachine.PremiumModeEvent;
import simulation.events.washingmachine.StartAtEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.tools.washingmachine.WashingMachineState;
import wattwatt.tools.URIS;
import wattwatt.tools.washingmachine.WashingMachineMode;
import wattwatt.tools.washingmachine.WashingMachineSetting;

@ModelExternalEvents(imported = { EcoModeEvent.class, 
								  PremiumModeEvent.class, 
								  StartAtEvent.class, 
								  TicEvent.class},
exported = { WashingMachineConsumptionEvent.class})
public class WashingMachineModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class WashingMachineReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WashingMachineReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LaveLingeReport(" + this.getModelURI() + ")";
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
	public static final String URI = URIS.WASHING_MACHINE_MODEL_URI;

	private static final String SERIES = "intensity";
	public static final String INTENSITY_SERIES = "intensity";
	
	public static final String CONSUMPTION_ECO ="consoEco";
	public static final String CONSUMPTION_PREMIUM ="consoPremium";
	public static final String STD ="std";


	/** nominal tension (in Volts) of the hair dryer. */
	protected static final double TENSION = 220.0; // Volts

	/** true when a external event triggered a reading. */
	protected boolean triggerReading;

	/** plotter for the intensity level over time. */
	protected XYPlotter intensityPlotter;

	/** current intensity in Amperes; intensity is power/tension. */
	protected double currentIntensity;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	/** Etat dans lequel se trouve le seche cheveux */
	protected WashingMachineState state;

	protected WashingMachineMode lavage;
	
	protected double startingTimeDelay;
	
	protected  double consoEco;
	protected  double consoPremium;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

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
		String vname = this.getURI() + ":" + CONSUMPTION_ECO ;
		this.consoEco = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + CONSUMPTION_PREMIUM ;
		this.consoPremium = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + STD ;
		this.startingTimeDelay = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + WashingMachineModel.SERIES + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.intensityPlotter = new XYPlotter(pdTemperature) ;
		this.intensityPlotter.createSeries(WashingMachineModel.SERIES) ;
		
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentAccessI) simParams.get(URIS.WASHING_MACHINE_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.lavage = WashingMachineMode.ECO;
		this.state = WashingMachineState.OFF;

		this.triggerReading = false;

		this.intensityPlotter.initialise();
		this.intensityPlotter.showPlotter();

		try {
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		this.currentIntensity = 0.0;

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity);

		super.initialiseVariables(startTime);
	}

	@Override
	public Duration timeAdvance() {
		
		if (!this.triggerReading) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
	}

	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.currentIntensity; // Watt

			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			WashingMachineConsumptionEvent consommation = new WashingMachineConsumptionEvent(currentTime, reading);
			ret.add(consommation);
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(1)) {
			this.logMessage("LaveLingeModel#userDefinedInternalTransition " + elapsedTime);
		}
		if (this.triggerReading) {
			this.updateState();
			this.triggerReading = false;
		} else {
			if (this.componentRef != null) {
				// This is an example showing how to access the component state
				// from a simulation model; this must be done with care and here
				// we are not synchronising with other potential component threads
				// that may access the state of the component object at the same
				// time.
				try {
					this.currentIntensity = (double)componentRef.getEmbeddingComponentStateValue("consumption");
//					this.logMessage("component state = " + this.currentIntensity);
					this.lavage = (WashingMachineMode)componentRef.getEmbeddingComponentStateValue("mode");
//					this.logMessage("component state = " + this.lavage);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
				super.userDefinedInternalTransition(elapsedTime);
	
				if (this.intensityPlotter != null) {
					this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
							this.currentIntensity);
				}
				this.logMessage(this.getCurrentStateTime() + "|internal|intensity = " + this.currentIntensity + " Amp");
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 1");
		}

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		boolean ticReceived = false;
		// when this method is called, there is at least one external event
		assert currentEvents != null;

		Event ce = (Event) currentEvents.get(0);

		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity);

		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 3 " + this.state);
		}

		// execute the current external event on this model, changing its state
		// and intensity level
		if (ce instanceof TicEvent) {
			ticReceived = true;
		} else {
			ce.executeOn(this);
		}
		if (ticReceived) {
			this.triggerReading = true;
			this.logMessage(this.getCurrentStateTime() + "|external|tic event received.");
		}
		if (this.hasDebugLevel(1)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 4 ");
		}

		// add a new data on the plotter; this data will open a new piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity);

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 5");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.intensityPlotter.addData(SERIES, endTime.getSimulatedTime(), this.currentIntensity);
		super.endSimulation(endTime);
	}
	

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new WashingMachineReport(this.getURI());
	}
	
	public double getIntensity() {
		return this.currentIntensity;
	}
	
	public WashingMachineMode getLavage() {
		return this.lavage;
	}
	
	public boolean isOn() {
		return this.state == WashingMachineState.ON || isWorking();
	}
	
	public boolean isWorking() {
		return this.state == WashingMachineState.WORKING;
	}
	
	public double getStartingTimeDelay() {
		return this.startingTimeDelay;
	}
	
	public void startAt(double startingTimeDelay) {
		this.startingTimeDelay = startingTimeDelay;
		this.state = WashingMachineState.WORKING;
		updateState();
	}
	
	public void ecoLavage() {
		this.lavage = WashingMachineMode.ECO;
		this.state = WashingMachineState.OFF;
	}
	
	public void premiumLavage() {
		this.lavage = WashingMachineMode.PREMIUM;
		this.state = WashingMachineState.OFF;
		
	}

	private void updateState() {
		if(this.state == WashingMachineState.WORKING) {
			if(this.lavage == WashingMachineMode.ECO) {
				this.currentIntensity += WashingMachineSetting.CONSO_ECO_MODE_SIM;
			}
			else {
				this.currentIntensity += WashingMachineSetting.CONSO_PREMIUM_MODE;
			}
		}
		else {
			this.currentIntensity = 0.0;
		}
	}

}
