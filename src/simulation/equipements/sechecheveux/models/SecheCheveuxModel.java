package simulation.equipements.sechecheveux.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
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
import simulation.equipements.compteur.models.events.ConsommationEvent;
import simulation.equipements.sechecheveux.models.events.DecreasePowerEvent;
import simulation.equipements.sechecheveux.models.events.IncreasePowerEvent;
import simulation.equipements.sechecheveux.models.events.SwitchModeEvent;
import simulation.equipements.sechecheveux.models.events.SwitchOffEvent;
import simulation.equipements.sechecheveux.models.events.SwitchOnEvent;
import simulation.equipements.sechecheveux.tools.HairDryerPowerLevel;
import simulation.equipements.sechecheveux.tools.HairDryerState;
import wattwatt.tools.sechecheveux.SecheCheveuxMode;
import wattwatt.tools.sechecheveux.SecheCheveuxReglage;

@ModelExternalEvents(imported = { TicEvent.class, SwitchOnEvent.class, SwitchOffEvent.class, SwitchModeEvent.class,
		IncreasePowerEvent.class, DecreasePowerEvent.class }, exported = { ConsommationEvent.class })
public class SecheCheveuxModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class SecheCheveuxReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public SecheCheveuxReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SecheCheveuxReport(" + this.getModelURI() + ")";
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
	public static final String URI = "SecheCheveuxModel";

	private static final String SERIES = "intensity";

	/** nominal tension (in Volts) of the hair dryer. */
	protected static final double TENSION = 220.0; // Volts

	/** true when a external event triggered a reading. */
	protected boolean triggerReading;
	
	/** plotter for the intensity level over time. */
	protected XYPlotter intensityPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentStateAccessI componentRef;
	
	/** current intensity in Amperes; intensity is power/tension. */
	//@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this, 0.0, 0);
	
	/** Mode dans lequel est le seche cheveux (HOT_AIR, COLD_AIR) */
	protected SecheCheveuxMode mode;
	
	/** Etat dans lequel se trouve le seche cheveux */
	protected HairDryerState state;

	protected HairDryerPowerLevel powerLvl;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public SecheCheveuxModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		PlotterDescription pd = new PlotterDescription("Intensite seche cheveux", "Time (sec)", "Intensity (Amp)", 100,
				0, 600, 400);
		this.intensityPlotter = new XYPlotter(pd);
		this.intensityPlotter.createSeries(SERIES);

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
		this.componentRef = (EmbeddingComponentStateAccessI) simParams.get("componentRef");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.mode = SecheCheveuxMode.COLD_AIR;
		this.powerLvl = HairDryerPowerLevel.MEDIUM;
		this.state = HairDryerState.OFF;

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

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {
		// as the hair dryer starts in mode OFF, its power consumption is 0
		this.currentIntensity.v = 0.0;

		// first data in the plotter to start the plot.
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI> output() {
		if (this.triggerReading) {
			double reading = this.currentIntensity.v * TENSION / 1000; // kW

			Vector<EventI> ret = new Vector<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ConsommationEvent consommation = new ConsommationEvent(currentTime, reading);
			ret.add(consommation);

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
		super.userDefinedInternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 1");
		}

		// get the vector of current external events
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		boolean ticReceived = false;
		// when this method is called, there is at least one external event
		assert currentEvents != null;

		Event ce = (Event) currentEvents.get(0);

		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 3 " + this.getMode());
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
		 this.logMessage("HairDryerModel::userDefinedExternalTransition 4 ");
		 }

		// add a new data on the plotter; this data will open a new piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getIntensity());

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("HairDryerModel::userDefinedExternalTransition 5");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.intensityPlotter.addData(SERIES, endTime.getSimulatedTime(), this.getIntensity());
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new SecheCheveuxReport(this.getURI());
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public void switchOn() {
		this.state = HairDryerState.ON;
		updateIntensity();
	}

	public void switchOff() {
		this.state = HairDryerState.OFF;
		this.currentIntensity.v = 0.0;
	}

	public void setMode(SecheCheveuxMode mode) {
		this.mode = mode;
		updateIntensity();
	}

	public SecheCheveuxMode getMode() {
		return this.mode;
	}

	public double getIntensity() {
		return this.currentIntensity.v;
	}


	public void increasePower() {
		if (this.powerLvl == HairDryerPowerLevel.LOW) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else {
			if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
				this.powerLvl = HairDryerPowerLevel.HIGH;
			}
		}
		updateIntensity();
	}
	
	public boolean isOn() {
		return this.state == HairDryerState.ON;
	}

	public void decreasePower() {
		if (this.powerLvl == HairDryerPowerLevel.HIGH) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else {
			if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
				this.powerLvl = HairDryerPowerLevel.LOW;
			}
		}
		updateIntensity();
	}

	public void updateIntensity() {
		switch (this.getMode()) {
		case HOT_AIR:
			this.currentIntensity.v = SecheCheveuxReglage.CONSO_HOT_MODE * this.powerLvl.getValue() / TENSION;
			break;
		case COLD_AIR:
			this.currentIntensity.v = SecheCheveuxReglage.CONSO_COLD_MODE * this.powerLvl.getValue() / TENSION;
			break;

		}
	}

	public HairDryerPowerLevel getPowerLevel() {
		return this.powerLvl;
	}
}
