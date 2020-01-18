package simulation.models.controller;

import java.util.HashMap;
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
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.controller.StartEngineGenerator;
import simulation.events.controller.StopEngineGenerator;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.windturbine.WindTurbineProductionEvent;
import simulation.tools.controller.Decision;
import simulation.tools.enginegenerator.EngineGeneratorState;

@ModelExternalEvents(imported = { ConsumptionEvent.class, EngineGeneratorProductionEvent.class, WindTurbineProductionEvent.class})
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
			return "ControllerModel(" + this.getModelURI() + ")";
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
	
	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";
	private static final String ENGINE_GENERATOR = "engine-generator";
	public static final String ENGINE_GENERATOR_SERIES = "engine-generator-series";
	private static final String CONTROLLER_STUB = "controller-stub";
	public static final String CONTROLLER_STUB_SERIES = "controller-stub-series";
	
	protected boolean mustTransmitDecision;
	protected double consumption;
	protected double productionEngineGenerator;
	protected double productionWindTurbine;
	
	protected EngineGeneratorState EGState;
	
	protected Decision triggeredDecisionEngineGenerator;
	protected Decision lastDecisionEngineGenerator;
	protected double lastDecisionTimeEngineGenerator;
	protected final Vector<DecisionPiece> decisionFunctionEngineGenerator;

	protected XYPlotter productionPlotter;
	protected final Map<String, XYPlotter> modelsPlotter;

	protected EmbeddingComponentStateAccessI componentRef;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ControllerModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		
		this.decisionFunctionEngineGenerator = new Vector<>();
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
				this.getURI() + ":" + ControllerModel.PRODUCTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd1 = (PlotterDescription) simParams.get(vname) ;
		this.productionPlotter = new XYPlotter(pd1) ;
		this.productionPlotter.createSeries(ControllerModel.PRODUCTION) ;
		
		vname =
				this.getURI() + ":" + ControllerModel.CONTROLLER_STUB_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		// if this key is in simParams, it's the MIL that's running
		if(simParams.containsKey(vname)) {
			PlotterDescription pd2 = (PlotterDescription) simParams.get(vname) ;
			this.modelsPlotter.put(ControllerModel.CONTROLLER_STUB, new XYPlotter(pd2));
			this.modelsPlotter.get(ControllerModel.CONTROLLER_STUB).createSeries(ControllerModel.CONTROLLER_STUB);
		} else {
			vname =
					this.getURI() + ":" + ControllerModel.ENGINE_GENERATOR_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
			PlotterDescription pd2 = (PlotterDescription) simParams.get(vname) ;
			this.modelsPlotter.put(ControllerModel.ENGINE_GENERATOR, new XYPlotter(pd2));
			this.modelsPlotter.get(ControllerModel.ENGINE_GENERATOR).createSeries(ControllerModel.ENGINE_GENERATOR);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;

		this.mustTransmitDecision = false;
		this.consumption = 0.0;
		this.productionEngineGenerator = 0.0;
		this.productionWindTurbine = 0.0;
		
		this.EGState = EngineGeneratorState.OFF;
		this.triggeredDecisionEngineGenerator = Decision.STOP_ENGINE;
		this.lastDecisionEngineGenerator = Decision.STOP_ENGINE;
		this.lastDecisionTimeEngineGenerator = initialTime.getSimulatedTime();
		this.decisionFunctionEngineGenerator.clear();
		
		if (this.productionPlotter != null) {
			this.productionPlotter.initialise() ;
			this.productionPlotter.showPlotter() ;
			this.productionPlotter.addData(
					ControllerModel.PRODUCTION,
					this.getCurrentStateTime().getSimulatedTime(),
					0.0) ;
		}

		for(Map.Entry<String, XYPlotter> elt: modelsPlotter.entrySet()) {
			String URI = elt.getKey();
			XYPlotter plotter = elt.getValue();
			if (plotter != null) {
				plotter.initialise() ;
				plotter.showPlotter() ;
				if(URI == ControllerModel.ENGINE_GENERATOR) {
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				} else {
					assert URI.equals(ControllerModel.CONTROLLER_STUB);
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				}
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
	protected int		decisionToInteger(Decision d)
	{
		assert	d != null ;

		if (d == Decision.START_ENGINE) {
			return 1 ;
		} else if (d == Decision.STOP_ENGINE) {
			return 0 ;
		} else {
			// Need to add other decisions
			return -1;
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
							+ this.lastDecisionEngineGenerator + " "
							+ this.triggeredDecisionEngineGenerator) ;
		}
		
		Vector<EventI> ret = null ;
		ret = new Vector<EventI>(1) ;
		
		if (this.triggeredDecisionEngineGenerator == Decision.START_ENGINE) {
			ret.add(new StartEngineGenerator(this.getCurrentStateTime())) ;
		} else if (this.triggeredDecisionEngineGenerator == Decision.STOP_ENGINE) {
			ret.add(new StopEngineGenerator(this.getCurrentStateTime())) ;
		}
	
		this.decisionFunctionEngineGenerator.add(
				new DecisionPiece(this.lastDecisionTimeEngineGenerator,
							  this.getCurrentStateTime().getSimulatedTime(),
							  this.lastDecisionEngineGenerator)) ;

		this.lastDecisionEngineGenerator = this.triggeredDecisionEngineGenerator ;
		this.lastDecisionTimeEngineGenerator =
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
								+ this.EGState + ">>>>>>>>>>>>>>>") ;
		}
		
		Vector<EventI> current = this.getStoredEventAndReset() ;
		
		for (int i = 0 ; i < current.size() ; i++) {
			if (current.get(i) instanceof EngineGeneratorProductionEvent) {
				this.productionEngineGenerator =
						((EngineGeneratorProductionEvent.Reading)
							((EngineGeneratorProductionEvent)current.get(i)).
											getEventInformation()).value ;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|EG production = " + this.productionEngineGenerator) ;
			} else if (current.get(i) instanceof WindTurbineProductionEvent){
				this.productionWindTurbine =
						((WindTurbineProductionEvent.Reading)
							((WindTurbineProductionEvent)current.get(i)).
											getEventInformation()).value ;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|WT production = " + this.productionWindTurbine) ;
			}
			if (current.get(i) instanceof ConsumptionEvent) {
				this.consumption =
						((ConsumptionEvent.Reading)
							((ConsumptionEvent)current.get(i)).
											getEventInformation()).value ;
				
			} 
//			else if (current.get(i) instanceof WiFiBandwidthReading) {
//				this.currentBandwith =
//					((WiFiBandwidthReading.Reading)
//							((WiFiBandwidthReading)current.get(i)).
//											getEventInformation()).value ;
//				this.logMessage("userDefinedExternalTransition|"
//						+ this.getCurrentStateTime()
//						+ "|state = " + this.currentState
//						+ "|bandwidth = " + this.currentBandwith) ;
//			}
		}
//		GroupeElectrogeneState oldState = this.EGState ;
		double production = this.productionEngineGenerator + this.productionWindTurbine ;
		if (this.EGState == EngineGeneratorState.ON) {
			if (production > this.consumption) {
				// on l'eteint
				this.triggeredDecisionEngineGenerator = Decision.STOP_ENGINE;
				this.EGState = EngineGeneratorState.OFF ;
				
				this.mustTransmitDecision = true ;
				
//				this.logMessage("userDefinedExternalTransition|"
//				 				+ this.getCurrentStateTime() 
//				 				+ "|new state = " + this.EGState) ;
			}
		} else{
			assert	this.EGState == EngineGeneratorState.OFF;
			if (production <= this.consumption) {
				// on l'allume
				this.triggeredDecisionEngineGenerator = Decision.START_ENGINE;
				this.EGState = EngineGeneratorState.ON ;
				
				this.mustTransmitDecision = true ;
				
//				this.logMessage("userDefinedExternalTransition|"
//				 				+ this.getCurrentStateTime() 
//				 				+ "|new state = " + this.EGState) ;
			}
		} 
		this.productionPlotter.addData(
				PRODUCTION,
				this.getCurrentStateTime().getSimulatedTime(),
				production) ;
		this.productionPlotter.addData(
				PRODUCTION,
				this.getCurrentStateTime().getSimulatedTime(),
				production) ;
		
		for(Map.Entry<String, XYPlotter> elt: modelsPlotter.entrySet()) {
			String URI = elt.getKey();
			XYPlotter plotter = elt.getValue();
			if (plotter != null) {
				if(URI == ControllerModel.ENGINE_GENERATOR) {
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				} else {
					assert URI.equals(ControllerModel.CONTROLLER_STUB);
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				}
			}
		}
		
		if (this.hasDebugLevel(1)) {
			this.logMessage("userDefinedExternalTransition|"
								+ this.EGState + "<<<<<<<<<<<<<<<<<<<") ;
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
					ControllerModel.PRODUCTION,
					this.getCurrentStateTime().getSimulatedTime(),
					this.productionEngineGenerator + this.productionWindTurbine) ;
		}

		for(Map.Entry<String, XYPlotter> elt: modelsPlotter.entrySet()) {
			String URI = elt.getKey();
			XYPlotter plotter = elt.getValue();
			if (plotter != null) {
				if(URI == ControllerModel.ENGINE_GENERATOR) {
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				} else {
					assert URI.equals(ControllerModel.CONTROLLER_STUB);
					plotter.addData(
							URI,
							this.getCurrentStateTime().getSimulatedTime(),
							this.decisionToInteger(this.lastDecisionEngineGenerator)) ;
				}
			}
		}
		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new ControllerModelReport(this.getURI());
	}
}
// -----------------------------------------------------------------------------
