package simulation.models.hairdryer;

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
import simulation.events.hairdryer.DecreasePowerEvent;
import simulation.events.hairdryer.IncreasePowerEvent;
import simulation.events.hairdryer.SwitchModeEvent;
import simulation.events.hairdryer.SwitchOffEvent;
import simulation.events.hairdryer.SwitchOnEvent;
import wattwatt.tools.URIS;
import wattwatt.tools.hairdryer.HairDryerMode;

@ModelExternalEvents(exported = { SwitchOnEvent.class, SwitchOffEvent.class, SwitchModeEvent.class,
		IncreasePowerEvent.class, DecreasePowerEvent.class })
public class HairDryerUserModel extends AtomicES_Model {

	public static class HairDryerUserModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public HairDryerUserModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "HairDryerUserModelReport(" + this.getModelURI() + ")";
		}
	}

	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	public static final String URI = URIS.HAIR_DRYER_USER_MODEL_URI;

	public static final String INITIAL_DELAY = "initial-delay";
	public static final String INTERDAY_DELAY = "interday-delay";
	public static final String MEAN_TIME_BETWEEN_USAGES = "mean-time-between-usages";
	public static final String MEAN_TIME_AT_HIGH = "mean-time-at-high";
	public static final String MEAN_TIME_AT_LOW = "mean-time-at-low";

	/** initial delay before sending the first switch on event. */
	protected double initialDelay;

	/** delay between uses of the hair dryer from one day to another. */
	protected double interdayDelay;

	/** mean time between uses of the hair dryer in the same day. */
	protected double meanTimeBetweenUsages;

	/** during one use, mean time the hair dryer is at high temperature. */
	protected double meanTimeAtHigh;

	/** during one use, mean time the hair dryer is at low temperature. */
	protected double meanTimeAtLow;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	/** the current state of the hair dryer simulation model. */
	protected HairDryerMode mode;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer user model instance.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri
	 *            unique identifier of the model.
	 * @param simulatedTimeUnit
	 *            time unit used for the simulation clock.
	 * @param simulationEngine
	 *            simulation engine enacting the model.
	 * @throws Exception
	 *             <i>TODO</i>.
	 */
	public HairDryerUserModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();

	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = this.getURI() + ":" + HairDryerUserModel.INITIAL_DELAY;
		this.initialDelay = (double) simParams.get(vname);
		vname = this.getURI() + ":" + HairDryerUserModel.INTERDAY_DELAY;
		this.interdayDelay = (double) simParams.get(vname);
		vname = this.getURI() + ":" + HairDryerUserModel.MEAN_TIME_BETWEEN_USAGES;
		this.meanTimeBetweenUsages = (double) simParams.get(vname);
		vname = this.getURI() + ":" + HairDryerUserModel.MEAN_TIME_AT_HIGH;
		this.meanTimeAtHigh = (double) simParams.get(vname);
		vname = this.getURI() + ":" + HairDryerUserModel.MEAN_TIME_AT_LOW;
		this.meanTimeAtLow = (double) simParams.get(vname);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.HAIR_DRYER_URI);

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.rg.reSeedSecure();

		// Initialise to get the correct current time.
		super.initialiseState(initialTime);

		// Schedule the first SwitchOn event.
		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new SwitchOnEvent(t));

		// Redo the initialisation to take into account the initial event
		// just scheduled.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		// This is just for debugging purposes; the time advance for an ES
		// model is given by the earliest time among the currently scheduled
		// events.
		Duration d = super.timeAdvance();
		return d;
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
			// output is called just before executing an internal transition
			// in ES models, this corresponds to having at least one event in
			// the event list which time of occurrence corresponds to the current
			// simulation time when performing the internal transition.

			// when called, there must be an event to be executed and it will
			// be sent to other models when they are external events.
			assert !this.eventList.isEmpty();
			// produce the set of such events by calling the super method
			ArrayList<EventI> ret = super.output();
			// by construction, there will be only one such event
			assert ret.size() == 1;

			// remember which external event was sent (in ES model, events are
			// either internal or external, hence an external event is removed
			// from the event list to be sent and it will not be accessible to
			// the internal transition method; hence, we store the information
			// to keep it for the internal transition)
			this.nextEvent = ret.get(0).getClass();

//			this.logMessage("HairDryerUserModel::output() " + this.nextEvent.getCanonicalName());
			return ret;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (componentRef == null) {
			// We are in MIL
			// This method implements a usage scenario for the hair dryer.
			// Here, we assume that the hair dryer is used once each cycle (day)
			// and then it starts in low mode, is set in high mode shortly after,
			// used for a while in high mode and then set back in low mode to
			// complete the drying.

			Duration d;
			// See what is the type of event to be executed
			if (this.nextEvent.equals(SwitchOnEvent.class)) {

				d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				// compute the time of occurrence (in the future)
				Time t = this.getCurrentStateTime().add(d);

				this.scheduleEvent(new SwitchModeEvent(t));

			} else if (this.nextEvent.equals(SwitchModeEvent.class)) {

				d = new Duration(2.0 * this.meanTimeAtHigh * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				this.scheduleEvent(new IncreasePowerEvent(this.getCurrentStateTime().add(d)));

			} else if (this.nextEvent.equals(IncreasePowerEvent.class)) {

				d = new Duration(2.0 * this.meanTimeAtHigh * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				this.scheduleEvent(new DecreasePowerEvent(this.getCurrentStateTime().add(d)));

			} else if (this.nextEvent.equals(DecreasePowerEvent.class)) {

				d = new Duration(2.0 * this.meanTimeAtLow * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
				this.scheduleEvent(new SwitchOffEvent(this.getCurrentStateTime().add(d)));

			} else if (this.nextEvent.equals(SwitchOffEvent.class)) {

				d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
				this.scheduleEvent(new SwitchOnEvent(this.getCurrentStateTime().add(d)));
			}
		} else {
			// This method implements a usage scenario for the hair dryer.
			// Here, we assume that the hair dryer is used once each cycle (day)
			// and then it starts in low mode, is set in high mode shortly after,
			// used for a while in high mode and then set back in low mode to
			// complete the drying.
			Duration d;
			// See what is the type of event to be executed
			try {
				if (this.nextEvent.equals(SwitchOnEvent.class)) {
					this.componentRef.setEmbeddingComponentStateValue("switchOn", null);

					d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
					this.scheduleEvent(new SwitchModeEvent(this.getCurrentStateTime().add(d)));
					
				} else if (this.nextEvent.equals(SwitchModeEvent.class)) {
					this.componentRef.setEmbeddingComponentStateValue("switchMode",null);
					d = new Duration(2.0 * this.meanTimeAtHigh * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					this.scheduleEvent(new IncreasePowerEvent(this.getCurrentStateTime().add(d)));
				} else if (this.nextEvent.equals(IncreasePowerEvent.class)) {
					this.componentRef.setEmbeddingComponentStateValue("increasePower",null);

					d = new Duration(2.0 * this.meanTimeAtHigh * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					this.scheduleEvent(new DecreasePowerEvent(this.getCurrentStateTime().add(d)));
				} else if (this.nextEvent.equals(DecreasePowerEvent.class)) {
					this.componentRef.setEmbeddingComponentStateValue("decreasePower",null);

					d = new Duration(2.0 * this.meanTimeAtLow * this.rg.nextBeta(1.75, 1.75),
							this.getSimulatedTimeUnit());
					this.scheduleEvent(new SwitchOffEvent(this.getCurrentStateTime().add(d)));
				} else if (this.nextEvent.equals(SwitchOffEvent.class)) {
					this.componentRef.setEmbeddingComponentStateValue("switchOff",null);

					d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
					this.scheduleEvent(new SwitchOnEvent(this.getCurrentStateTime().add(d)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new HairDryerUserModelReport(this.getURI());
	}
}