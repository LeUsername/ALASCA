package simulation.models.enginegenerator;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEvent;
import simulation.events.enginegenerator.StopEvent;
import simulation.tools.enginegenerator.EngineGeneratorUserAction;

@ModelExternalEvents(exported = { StartEvent.class, RefillEvent.class,
		 StopEvent.class})
public class EngineGeneratorUserModel extends AtomicES_Model {

	public static class EngineGeneratorUserModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public EngineGeneratorUserModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "EngineGeneratorUserModelReport(" + this.getModelURI() + ")";
		}
	}

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String ACTION = "START/STOP/REFILL";
	
	/** URI to be used when creating the model. */
	public static final String URI = "EngineGeneratorUserModel";
	/**
	 * name of the run parameter defining the intial delay.
	 */
	public static final String INITIAL_DELAY = "initial-delay";
	/**
	 * name of the run parameter defining the interday delay.
	 */
	public static final String INTERDAY_DELAY = "interday-delay";
	/**
	 * name of the run parameter defining the mean time between usages.
	 */
	public static final String MEAN_TIME_BETWEEN_USAGES = "mtbu";
	/**
	 * name of the run parameter defining the mean duration of usages.
	 */
	public static final String MEAN_TIME_USAGE = "mean-time-usage";
	/**
	 * name of the run parameter defining the mean duration of refills.
	 */
	public static final String MEAN_TIME_REFILL = "mean-time-refill";
	

	// Model simulation implementation variables
	/** initial delay before sending the first switch on event. */
	protected double initialDelay;

	/** delay between uses  from one day to another. */
	protected double interdayDelay;

	/** mean time between uses of the engine generator. */
	protected double meanTimeBetweenUsages;

	/** during one use, mean time the engine generator produces energy. */
	protected double meanTimeUsing;

	/** during one refill, mean time the engine generator needs to have a full tank. */
	protected double meanTimeAtRefill;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	protected double fuelCapacity;

	/**
	 * The plotter corresponding to the action taken by a user
	 * 
	 */
	protected XYPlotter actionPlotter;

	public EngineGeneratorUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();

		// create a standard logger (logging on the terminal)
//		this.setLogger(new StandardLogger());
	}
	
	@Override
	public void initialiseState(Time initialTime) {
//		this.initialDelay = EngineGeneratorUserBehaviour.INITIAL_DELAY;
//		this.interdayDelay = EngineGeneratorUserBehaviour.INTER_DAY_DELAY;
//		this.meanTimeBetweenUsages = GroupeElectrogeneUserBehaviour.MEAN_TIME_BETWEEN_USAGES;
//		this.meanTimeWorking = GroupeElectrogeneUserBehaviour.MEAN_TIME_WORKING;
//		this.meanTimeAtRefill = GroupeElectrogeneUserBehaviour.MEAN_TIME_AT_REFILL;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new StartEvent(t));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
		if (this.actionPlotter != null) {
			this.actionPlotter.initialise() ;
			this.actionPlotter.showPlotter() ;
		}

		try {
			this.setDebugLevel(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String 	vname = this.getURI() + ":" + EngineGeneratorUserModel.INITIAL_DELAY ;
		this.initialDelay = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + EngineGeneratorUserModel.INTERDAY_DELAY ;
		this.interdayDelay = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + EngineGeneratorUserModel.MEAN_TIME_BETWEEN_USAGES ;
		this.meanTimeBetweenUsages = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + EngineGeneratorUserModel.MEAN_TIME_USAGE ;
		this.meanTimeUsing = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + EngineGeneratorUserModel.MEAN_TIME_REFILL ;
		this.meanTimeAtRefill = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + EngineGeneratorUserModel.ACTION + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.actionPlotter = new XYPlotter(pdTemperature) ;
		this.actionPlotter.createSeries(EngineGeneratorUserModel.ACTION) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		this.logMessage("EngineGeneratorUserModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public Vector<EventI> output() {
		
		assert !this.eventList.isEmpty();
		Vector<EventI> ret = super.output();
		assert ret.size() == 1;

		this.nextEvent = ret.get(0).getClass();

		this.logMessage("EngineGeneratorUserModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;
		
		if (this.nextEvent.equals(StartEvent.class) ) {

			d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);

			this.scheduleEvent(new StopEvent(t));
			if (this.actionPlotter != null) {
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.STOP)) ;
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.START)) ;
			}
		} else if (this.nextEvent.equals(StopEvent.class)) {

			d = new Duration(2.0 * this.meanTimeAtRefill * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new RefillEvent(this.getCurrentStateTime().add(d)));
			if (this.actionPlotter != null) {
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.START)) ;
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.REFILL)) ;
			}
		} else if (this.nextEvent.equals(RefillEvent.class)) {

			d = new Duration(2.0 * this.meanTimeUsing * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new StartEvent(this.getCurrentStateTime().add(d)));
			if (this.actionPlotter != null) {
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.REFILL)) ;
				this.actionPlotter.addData(
						ACTION,
						this.getCurrentStateTime().getSimulatedTime(),
						actionToInteger(EngineGeneratorUserAction.STOP)) ;
			}
		}
	}
	
	public int actionToInteger(EngineGeneratorUserAction action) {
		assert	action != null ;

		if (action == EngineGeneratorUserAction.START) {
			return 1 ;
		} else if (action == EngineGeneratorUserAction.STOP) {
			return 0 ;
		} else {
			assert action == EngineGeneratorUserAction.REFILL;
			return 2 ;
		}
	}
	
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new EngineGeneratorUserModelReport(this.getURI());
	}
}
