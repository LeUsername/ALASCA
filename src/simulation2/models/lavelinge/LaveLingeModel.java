package simulation2.models.lavelinge;

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
import simulation2.deployment.WattWattMain;
import simulation2.events.lavelinge.EcoLavageEvent;
import simulation2.events.lavelinge.PremiumLavageEvent;
import simulation2.events.lavelinge.StartAtEvent;
import simulation2.tools.lavelinge.LaveLingeLavage;
import simulation2.tools.lavelinge.LaveLingeState;
import wattwatt.tools.lavelinge.LaveLingeReglage;

@ModelExternalEvents(imported = { EcoLavageEvent.class, PremiumLavageEvent.class, StartAtEvent.class , TicEvent.class})
public class LaveLingeModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class LaveLingeReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public LaveLingeReport(String modelURI) {
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
	public static final String URI = "LaveLingeModel";

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
	protected final Value<Double> currentIntensity = new Value<Double>(this, 0.0, 0);

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentStateAccessI componentRef;

	/** Etat dans lequel se trouve le seche cheveux */
	protected LaveLingeState state;

	protected LaveLingeLavage lavage;
	
	protected double startingTimeDelay;
	
	protected  double consoEco;
	protected  double consoPremium;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public LaveLingeModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		PlotterDescription pd = new PlotterDescription("Intensite lave linge", "Time (sec)", "Intensity (Amp)",
				WattWattMain.ORIGIN_X + 2 * WattWattMain.getPlotterWidth(), WattWattMain.ORIGIN_Y,
				WattWattMain.getPlotterWidth(), WattWattMain.getPlotterHeight());

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
		String vname = this.getURI() + ":" + CONSUMPTION_ECO ;
		this.consoEco = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + CONSUMPTION_PREMIUM ;
		this.consoPremium = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + STD ;
		this.startingTimeDelay = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + LaveLingeModel.SERIES + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.intensityPlotter = new XYPlotter(pdTemperature) ;
		this.intensityPlotter.createSeries(LaveLingeModel.SERIES) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.lavage = LaveLingeLavage.ECO;
		this.state = LaveLingeState.OFF;

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
		this.currentIntensity.v = 0.0;

		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity.v);

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
	public Vector<EventI> output() {
		return null;
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
		}
		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
			super.userDefinedInternalTransition(elapsedTime);

			if (this.intensityPlotter != null) {
				this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
						this.currentIntensity.v);
			}
			this.logMessage(this.getCurrentStateTime() + "|internal|intensity = " + this.currentIntensity.v + " Amp");
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
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		boolean ticReceived = false;
		// when this method is called, there is at least one external event
		assert currentEvents != null;

		Event ce = (Event) currentEvents.get(0);

		if (this.hasDebugLevel(2)) {
			this.logMessage("LaveLingeModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity.v);

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
			this.logMessage("HairDryerModel::userDefinedExternalTransition 4 ");
		}

		// add a new data on the plotter; this data will open a new piece
		this.intensityPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentIntensity.v);

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
		this.intensityPlotter.addData(SERIES, endTime.getSimulatedTime(), this.currentIntensity.v);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new LaveLingeReport(this.getURI());
	}
	
	public double getIntensity() {
		return this.currentIntensity.v;
	}
	
	public LaveLingeLavage getLavage() {
		return this.lavage;
	}
	
	public boolean isOn() {
		return this.state == LaveLingeState.ON || isWorking();
	}
	
	public boolean isWorking() {
		return this.state == LaveLingeState.WORKING;
	}
	
	public double getStartingTimeDelay() {
		return this.startingTimeDelay;
	}
	
	public void startAt(double startingTimeDelay) {
		this.startingTimeDelay = startingTimeDelay;
		this.state = LaveLingeState.WORKING;
		updateState();
	}
	
	public void ecoLavage() {
		this.lavage = LaveLingeLavage.ECO;
		this.state = LaveLingeState.OFF;
	}
	
	public void premiumLavage() {
		this.lavage = LaveLingeLavage.PREMIUM;
		this.state = LaveLingeState.OFF;
		
	}

	private void updateState() {
		if(this.state == LaveLingeState.WORKING) {
			if(this.lavage == LaveLingeLavage.ECO) {
				this.currentIntensity.v += LaveLingeReglage.CONSO_ECO_MODE_SIM;
			}
			else {
				this.currentIntensity.v += LaveLingeReglage.CONSO_PREMIUM_MODE;
			}
		}
		else {
			this.currentIntensity.v = 0.0;
		}
	}

}
