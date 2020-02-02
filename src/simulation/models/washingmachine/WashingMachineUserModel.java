package simulation.models.washingmachine;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.washingmachine.EcoModeEvent;
import simulation.events.washingmachine.PremiumModeEvent;
import simulation.events.washingmachine.StartWashingEvent;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;
import wattwatt.tools.URIS;

@ModelExternalEvents(exported = { StartWashingEvent.class, 
								  EcoModeEvent.class,
								  PremiumModeEvent.class })
//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineUserModel</code> implements a model of user of
* the washing machine device
*
* <p><strong>Description</strong></p>
* 
* <p>
* This model is used to simulate how a real life user would interact with a
* washing machine
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
public class WashingMachineUserModel extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Inner class
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WashingMachineUserModelReport</code> implements the simulation
	 * report for the washing machine user model.
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
	public static class WashingMachineUserModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WashingMachineUserModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "WashingMachineUserModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	public static final String ACTION = "START_AT/ECO/PREMIUM";
	/** URI to be used when creating the model. */
	public static final String URI = URIS.WASHING_MACHINE_USER_MODEL_URI;
	/**
	 * name of the run parameter defining the mean time between usages.
	 */
	public static final String MTBU = "mtbu";
	/**
	 * name of the run parameter defining the mean time in eco mode.
	 */
	public static final String MTWE = "mtwe";
	/**
	 * name of the run parameter defining the mean time in premium mode.
	 */
	public static final String MTWP = "mtwp";
	/**
	 * name of the run parameter defining the starting delay.
	 */
	public static final String STD = "std";

	// Model simulation implementation variables
	/** initial delay before sending the first switch on event. */
	protected double initialDelay;

	/** delay between uses from one day to another. */
	protected double interdayDelay;

	/** mean time between uses of the washing machine. */
	protected double meanTimeBetweenUsages;

	/** during one use in eco mode, mean time the washing machine is 
	 * consuming energy */
	protected double meanTimeWorkingEco;
	/** during one use in premium mode, mean time the washing machine is 
	 * consuming energy */
	protected double meanTimeWorkingPremium;
	/** delay before the first usage of the washing machine */
	protected double startingTimeDelay;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	protected XYPlotter plotter;
	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an washing machine user model instance.
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
	public WashingMachineUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		String vname = this.getURI() + ":" + MTWE;
		this.meanTimeWorkingEco = (double) simParams.get(vname);

		vname = this.getURI() + ":" + MTBU;
		this.meanTimeBetweenUsages = (double) simParams.get(vname);

		vname = this.getURI() + ":" + MTWP;
		this.meanTimeWorkingPremium = (double) simParams.get(vname);

		vname = this.getURI() + ":" + STD;
		this.startingTimeDelay = (double) simParams.get(vname);

		vname = this.getURI() + ":" + WashingMachineUserModel.ACTION + ":" + PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname);
		this.plotter = new XYPlotter(pdTemperature);
		this.plotter.createSeries(WashingMachineUserModel.ACTION);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.WASHING_MACHINE_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = WashingMachineUserBehaviour.INITIAL_DELAY;
		this.interdayDelay = WashingMachineUserBehaviour.INTER_DAY_DELAY;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new StartWashingEvent(t, this.startingTimeDelay));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);
		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		try {
		} catch (Exception e) {
			throw new RuntimeException(e);
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
		} else {
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
			if (this.nextEvent.equals(StartWashingEvent.class)) {
				Random r = new Random();
				if (r.nextFloat() > 0.75) {
					d = new Duration(
							2.0 * this.meanTimeWorkingPremium + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					Time t = this.getCurrentStateTime().add(d);
					this.scheduleEvent(new PremiumModeEvent(t));
					if (this.plotter != null) {
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 2.0);
					}
				} else {
					d = new Duration(
							2.0 * this.meanTimeWorkingEco + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					Time t = this.getCurrentStateTime().add(d);
					this.scheduleEvent(new EcoModeEvent(t));
					if (this.plotter != null) {
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 1.0);
					}
				}

			} else if (this.nextEvent.equals(PremiumModeEvent.class)) {
				d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
						this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);

				this.scheduleEvent(new StartWashingEvent(t, this.startingTimeDelay));

				Random r = new Random();
				this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(2800) - 1400;

				if (this.plotter != null) {
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 2.0);
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
				}

			} else if (this.nextEvent.equals(EcoModeEvent.class)) {
				d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
						this.getSimulatedTimeUnit());

				this.scheduleEvent(new StartWashingEvent(this.getCurrentStateTime().add(d), this.startingTimeDelay));

				Random r = new Random();
				this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(1000) - 500;

				if (this.plotter != null) {
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 1.0);
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
				}

			}
		} else {
			Duration d;
			if (this.nextEvent.equals(StartWashingEvent.class)) {
				try {
					this.componentRef.setEmbeddingComponentStateValue("start", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Random r = new Random();
				if (r.nextFloat() > 0.75) {
					d = new Duration(
							2.0 * this.meanTimeWorkingPremium + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					Time t = this.getCurrentStateTime().add(d);
					this.scheduleEvent(new PremiumModeEvent(t));
					if (this.plotter != null) {
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 2.0);
					}
				} else {
					d = new Duration(
							2.0 * this.meanTimeWorkingEco + this.startingTimeDelay * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					Time t = this.getCurrentStateTime().add(d);
					this.scheduleEvent(new EcoModeEvent(t));
					if (this.plotter != null) {
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
						this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 1.0);
					}
				}
			} else if (this.nextEvent.equals(PremiumModeEvent.class)) {
				try {
					this.componentRef.setEmbeddingComponentStateValue("premiumMode", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
						this.getSimulatedTimeUnit());
				Time t = this.getCurrentStateTime().add(d);

				this.scheduleEvent(new StartWashingEvent(t, this.startingTimeDelay));

				Random r = new Random();
				this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(2800) - 1400;

				if (this.plotter != null) {
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 2.0);
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
				}

			} else if (this.nextEvent.equals(EcoModeEvent.class)) {
				try {
					this.componentRef.setEmbeddingComponentStateValue("ecoMode", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				d = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
						this.getSimulatedTimeUnit());

				this.scheduleEvent(new StartWashingEvent(this.getCurrentStateTime().add(d), this.startingTimeDelay));

				Random r = new Random();
				this.startingTimeDelay = this.meanTimeBetweenUsages + r.nextInt(1000) - 500;

				if (this.plotter != null) {
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 1.0);
					this.plotter.addData(ACTION, this.getCurrentStateTime().getSimulatedTime(), 0.0);
				}
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.plotter.addData(ACTION, endTime.getSimulatedTime(), 0.0);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new WashingMachineUserModelReport(this.getURI());
	}

}
