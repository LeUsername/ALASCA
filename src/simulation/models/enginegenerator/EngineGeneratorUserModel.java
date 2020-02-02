package simulation.models.enginegenerator;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
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
import simulation.events.enginegenerator.StartEngineEvent;
import simulation.events.enginegenerator.StopEngineEvent;
import simulation.tools.enginegenerator.EngineGeneratorUserAction;
import wattwatt.tools.URIS;

@ModelExternalEvents(exported = { StartEngineEvent.class, 
								  RefillEvent.class,
								  StopEngineEvent.class})
//-----------------------------------------------------------------------------
/**
* The class <code>EngineGeneratorUserModel</code> implements a model of user of
* the energy generator device
*
* <p><strong>Description</strong></p>
* 
* <p>
* This model is used to simulate how a real life user would interact with an
* engine generator to provides its house more energy
* </p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true	// TODO
* </pre>
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
//-----------------------------------------------------------------------------
public class EngineGeneratorUserModel extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Inner class
	// -------------------------------------------------------------------------

	/**
	 * The class <code>EngineGeneratorUserModelReport</code> implements the simulation
	 * report for the engine generator user model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
 	 * <p>
 	 * Created on : 2020-01-27
	 * </p>
	 * 
	 * @author
	 *         <p>
	 *         Bah Thierno, Zheng Pascal
	 *         </p>
	 */
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

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	/** URI to be used when creating the model. */
	public static final String URI = URIS.ENGINE_GENERATOR_USER_MODEL_URI;
	
	public static final String ACTION = "START/STOP/REFILL";

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

	/** delay between uses from one day to another. */
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

	/** the remaining fuel in the engine
	 * This variable should be exported from the engine generator
	 */
	protected double fuelCapacity;

	/**
	 * The plotter corresponding to the action taken by a user
	 */
	protected XYPlotter actionPlotter;
	
	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an engine generator user model instance.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	simulatedTimeUnit != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri
	 *            URI of the model.
	 * @param simulatedTimeUnit
	 *            time unit used for the simulation time.
	 * @param simulationEngine
	 *            simulation engine to which the model is attached.
	 * @throws Exception
	 *             <i>to do.</i>
	 */
	public EngineGeneratorUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();

	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
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
		
		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.ENGINE_GENERATOR_URI);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new StartEngineEvent(t));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
		if (this.actionPlotter != null) {
			this.actionPlotter.initialise() ;
			this.actionPlotter.showPlotter() ;
		}

		
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (componentRef != null) {
			this.nextEvent = super.output().get(0).getClass();
			return null;
		}
		else {
			assert !this.eventList.isEmpty();
			ArrayList<EventI> ret = super.output();
			assert ret.size() == 1;

			this.nextEvent = ret.get(0).getClass();

			return ret;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		return d;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (componentRef == null) {
			Duration d;
			if (this.nextEvent.equals(StartEngineEvent.class) ) {

				d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);

				this.scheduleEvent(new StopEngineEvent(t));
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
			} else if (this.nextEvent.equals(StopEngineEvent.class)) {

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
				this.scheduleEvent(new StartEngineEvent(this.getCurrentStateTime().add(d)));
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
		}else {
			try {
				Duration d;
				if (this.nextEvent.equals(StartEngineEvent.class)) {
					
					d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					Time t = this.getCurrentStateTime().add(d);
					this.scheduleEvent(new StopEngineEvent(t));
					if (this.actionPlotter != null) {
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.STOP));
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.START));
					}
					
					this.componentRef.setEmbeddingComponentStateValue("start", null);
					
				} else if (this.nextEvent.equals(StopEngineEvent.class)) {
					
					d = new Duration(2.0 * this.meanTimeAtRefill * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					this.scheduleEvent(new RefillEvent(this.getCurrentStateTime().add(d)));
					if (this.actionPlotter != null) {
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.START));
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.REFILL));
					}
					
					this.componentRef.setEmbeddingComponentStateValue("stop", null);
					
				} else if (this.nextEvent.equals(RefillEvent.class)) {
					
					d = new Duration(2.0 * this.meanTimeUsing * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					this.scheduleEvent(new StartEngineEvent(this.getCurrentStateTime().add(d)));
					if (this.actionPlotter != null) {
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.REFILL));
						this.actionPlotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(),
								actionToInteger(EngineGeneratorUserAction.STOP));
					}
					
					this.componentRef.setEmbeddingComponentStateValue("refill", null);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new EngineGeneratorUserModelReport(this.getURI());
	}
	
	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------
	
	/**
	 * return an integer representation to ease the plotting.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param action
	 *            an action taken by the user.
	 * @return an integer representation to ease the plotting.
	 */
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
}
