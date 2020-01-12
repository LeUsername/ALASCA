package simulation.equipements.controleur.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.State;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.controllers.ControllerModel.DecisionPiece;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.Compressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.LowBattery;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.NotCompressing;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.Decision;
import simulation.equipements.compteur.models.events.ConsommationEvent;
import simulation.equipements.compteur.models.events.ProductionEvent;

@ModelExternalEvents(imported = { ConsommationEvent.class, ProductionEvent.class})
public class ControllerModel extends AtomicModel {
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class	DecisionPiece
	{
		public final double		first ;
		public final double		last ;
		public final Decision	d ;

		public			DecisionPiece(
			double first,
			double last,
			Decision d
			)
		{
			super();
			this.first = first;
			this.last = last;
			this.d = d;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "(" + this.first + ", " + this.last + ", " + this.d + ")" ;
		}
	}
	
	public static class ControllerModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public ControllerModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ControllerModelReport(" + this.getModelURI() + ")";
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = "ControllerModel";
	
	public static final String PRODUCTION = "production";
	public static final String ENGINE_GENERATOR = "engine-generator";

	private static final String PRODUCTION_SERIES = "production";
	private static final String ENGINE_GENERATOR_SERIES = "engine-generator";
	
	protected double production;
	protected double consommation;
	
	protected Map<String, Decision> triggeredDecision;
	protected Map<String, Decision> lastDecisions;
	protected Map<String, Double> lastDecisionsTime;
	protected final Vector<DecisionPiece> decisionFunction ;

	protected XYPlotter productionPlotter;
	protected Map<String, XYPlotter> modelsPlotter;

	protected EmbeddingComponentStateAccessI componentRef;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		
		this.decisionFunction = new Vector<ControllerModel.DecisionPiece>();
		this.triggeredDecision = new HashMap<String, Decision>();
		this.lastDecisions = new HashMap<String, Decision>();
		this.lastDecisionsTime = new HashMap<String, Double>();
		this.modelsPlotter = new HashMap<String, XYPlotter>();
		
		
//		this.setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname =
				this.getURI() + ":" + ControllerModel.PRODUCTION + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd1 = (PlotterDescription) simParams.get(vname) ;
		this.productionPlotter = new XYPlotter(pd1) ;
		this.productionPlotter.createSeries(PRODUCTION_SERIES) ;
		
		vname =
				this.getURI() + ":" + ControllerModel.ENGINE_GENERATOR + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd2 = (PlotterDescription) simParams.get(vname) ;
		this.modelsPlotter.put(ControllerModel.ENGINE_GENERATOR, new XYPlotter(pd2));
		this.modelsPlotter.get(ControllerModel.ENGINE_GENERATOR).createSeries(ENGINE_GENERATOR_SERIES);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;

		

		this.triggeredDecision.put(ControllerModel.ENGINE_GENERATOR, Decision.STOP_ENGINE);
		this.lastDecisions.put(ControllerModel.ENGINE_GENERATOR, Decision.STOP_ENGINE);
		this.lastDecisionsTime.put(ControllerModel.ENGINE_GENERATOR, initialTime.getSimulatedTime());
		this.consommation = 0.0;
		this.production = 0.0;
		this.decisionFunction.clear();
		
		if (this.productionPlotter != null) {
			this.productionPlotter.initialise() ;
			this.productionPlotter.showPlotter() ;
			this.productionPlotter.addData(
					ControllerModel.PRODUCTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.production) ;
		}

		for(Map.Entry<String, XYPlotter> elt: modelsPlotter.entrySet()) {
			if (elt.getValue() != null) {
				elt.getValue().initialise() ;
				elt.getValue().showPlotter() ;
				elt.getValue().addData(
						elt.getKey(),
						this.getCurrentStateTime().getSimulatedTime(),
						this.decisionToInteger(this.lastDecisions.get(elt.getKey()))) ;
			}
		}
	}

	/**
	 * return an integer representation to ease the plotting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param s	a state for the controller.
	 * @return	an integer representation to ease the plotting.
	 */
	protected double		decisionToInteger(Decision d)
	{
		assert	d != null ;

		if (d == Decision.START_ENGINE) {
			return 1.0 ;
		} else if (d == Decision.STOP_ENGINE) {
			return 0.0 ;
		} else {
			return -1.0;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		if (this.hasDebugLevel(1)) {
			this.logMessage("output|"
							+ this.lastDecisions + " "
							+ this.triggeredDecision) ;
		}
		Vector<EventI> ret = null ;
		ret = new Vector<EventI>(1) ;
		if (this.triggeredDecision == Decision.COMPRESS) {
			ret.add(new Compressing(this.getCurrentStateTime(), null)) ;
		} else if (this.triggeredDecision == Decision.DO_NOT_COMPRESS) {
			ret.add(new NotCompressing(this.getCurrentStateTime(), null)) ;
		} else {
			assert	this.triggeredDecision == Decision.BATTERY_TOO_LOW ;
			ret.add(new LowBattery(this.getCurrentStateTime(), null)) ;
		}
	
		this.decisionFunction.add(
				new DecisionPiece(this.lastDecisionChangeTime,
							  this.getCurrentStateTime().getSimulatedTime(),
							  this.lastDecisions)) ;

		this.lastDecisions = this.triggeredDecision ;
		this.lastDecisionChangeTime =
							this.getCurrentStateTime().getSimulatedTime() ;
		this.mustTransmitDecision = false ;
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.mustTransmitDecision) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(1)) {
			this.logMessage("userDefinedExternalTransition|"
								+ this.currentState + ">>>>>>>>>>>>>>>") ;
		}
		Vector<EventI> current = this.getStoredEventAndReset() ;
		for (int i = 0 ; i < current.size() ; i++) {
			if (current.get(i) instanceof BatteryLevel) {
				this.currentBatteryLevel =
						((BatteryLevel.Reading)
							((BatteryLevel)current.get(i)).
											getEventInformation()).value ;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|state = " + this.currentState
						+ "|battery level = " + this.currentBatteryLevel) ;
			} else if (current.get(i) instanceof WiFiBandwidthReading) {
				this.currentBandwith =
					((WiFiBandwidthReading.Reading)
							((WiFiBandwidthReading)current.get(i)).
											getEventInformation()).value ;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|state = " + this.currentState
						+ "|bandwidth = " + this.currentBandwith) ;
			}
		}
		State oldState = this.currentState ;
		if (this.currentState == State.COMPRESSING) {
			if (this.currentBatteryLevel > 0 &&
							this.currentBatteryLevel < LOW_BATTERY_LEVEL) {
				this.triggeredDecision = Decision.BATTERY_TOO_LOW ;
				this.currentState = State.LOW_BATTERY ;
				this.mustTransmitDecision = true ;
				this.logMessage("userDefinedExternalTransition|"
				 				+ this.getCurrentStateTime() 
				 				+ "|new state = " + this.currentState) ;
			} else if (this.currentBandwith > 0.0 &&
									this.currentBandwith > HIGH_THRESHOLD) {
				this.triggeredDecision = Decision.DO_NOT_COMPRESS ;
				this.currentState = State.NOT_COMPRESSING ;
				this.mustTransmitDecision = true ;
				this.logMessage("userDefinedExternalTransition|"
						 		+ this.getCurrentStateTime() 
						 		+ "|new state = " + this.currentState) ;
			}
		} else if (this.currentState == State.NOT_COMPRESSING) {
			if (this.currentBatteryLevel > 0 &&
							this.currentBatteryLevel < LOW_BATTERY_LEVEL) {
				this.triggeredDecision = Decision.BATTERY_TOO_LOW ;
				this.currentState = State.LOW_BATTERY ;
				this.mustTransmitDecision = true ;
				this.logMessage("userDefinedExternalTransition|"
				 				+ this.getCurrentStateTime() 
				 				+ "|new state = " + this.currentState) ;
			} if (this.currentBandwith > 0.0 &&
									this.currentBandwith < LOW_THRESHOLD) {
				this.triggeredDecision = Decision.COMPRESS ;
				this.currentState = State.COMPRESSING ;
				this.mustTransmitDecision = true ;
				this.logMessage("userDefinedExternalTransition|"
								+ this.getCurrentStateTime() 
								+ "|new state = " + this.currentState) ;
			}
		} else {
			assert	this.currentState == State.LOW_BATTERY ;
			// Do nothing
			this.triggeredDecision = Decision.BATTERY_TOO_LOW ;
		}

		if (this.productionPlotter != null && oldState != this.currentState) {
			this.productionPlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.decisionToInteger(oldState)) ;
			this.productionPlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.decisionToInteger(this.currentState)) ;

		}
		if (this.hasDebugLevel(1)) {
			this.logMessage("userDefinedExternalTransition|"
								+ this.currentState + "<<<<<<<<<<<<<<<<<<<") ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		if (this.productionPlotter != null) {
			this.productionPlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.decisionToInteger(this.currentState)) ;
		}
		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		final String uri = this.uri ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri ;
					}				
				};
	}
}
// -----------------------------------------------------------------------------
