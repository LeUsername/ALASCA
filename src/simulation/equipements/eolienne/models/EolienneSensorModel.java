package simulation.equipements.eolienne.models;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.equipements.eolienne.EolienneState;
import simulation.equipements.eolienne.models.events.SwitchOffEvent;
import simulation.equipements.eolienne.models.events.SwitchOnEvent;
import simulation.equipements.eolienne.models.events.WindReadingEvent;

@ModelExternalEvents(exported = { WindReadingEvent.class, SwitchOffEvent.class, SwitchOnEvent.class })
public class EolienneSensorModel extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String URI = "EolienneSensorModel";

	protected double initialDelay;
	protected double interdayDelay;
	
	protected static final double MAX_WIND = 17.0;
	protected static final double MIN_WIND = 3.0;
	
	protected double currentWind;
	
	protected EolienneState state;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;
	

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public EolienneSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();

		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 10.0;
		this.interdayDelay = 100.0;
		
		this.state = EolienneState.OFF;
		
		this.currentWind = MAX_WIND/2;

		this.rg.reSeedSecure();

		// Initialise to get the correct current time.
		super.initialiseState(initialTime);

		Time t = this.getCurrentStateTime();
		this.scheduleEvent(new WindReadingEvent(t, this.currentWind));

		// Redo the initialisation to take into account the initial event
		// just scheduled.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);

		try {
			// set the debug level triggering the production of log messages.
			// this.setDebugLevel(1) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		this.logMessage("EolienneSensorModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
		return d;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public Vector<EventI> output() {
		// output is called just before executing an internal transition
		// in ES models, this corresponds to having at least one event in
		// the event list which time of occurrence corresponds to the current
		// simulation time when performing the internal transition.

		// when called, there must be an event to be executed and it will
		// be sent to other models when they are external events.
		assert !this.eventList.isEmpty();
		// produce the set of such events by calling the super method
		Vector<EventI> ret = super.output();
		// by construction, there will be only one such event
		assert ret.size() == 1;

		// remember which external event was sent (in ES model, events are
		// either internal or external, hence an external event is removed
		// from the event list to be sent and it will not be accessible to
		// the internal transition method; hence, we store the information
		// to keep it for the internal transition)
		this.nextEvent = ret.get(0).getClass();

		this.logMessage("EolienneSensorModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;

		d = new Duration(2.0 * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
		
		Time t = this.getCurrentStateTime().add(d);

		Random r = new Random();
		
		this.currentWind = this.currentWind + (r.nextInt(4)-2);
		if(this.currentWind <= 0.0) {
			this.currentWind = 0.0;
		}
		
		System.out.println(this.currentWind);
		
		if(this.currentWind > EolienneSensorModel.MAX_WIND) {
			if(this.state.equals(EolienneState.ON)) {
				this.scheduleEvent(new SwitchOffEvent(t));
				this.state = EolienneState.OFF;
			}
		} else if (this.currentWind < EolienneSensorModel.MIN_WIND) {
			if(this.state.equals(EolienneState.ON)) {
				this.scheduleEvent(new SwitchOffEvent(t));
				this.state = EolienneState.OFF;
			}
		} else {
			if(this.state.equals(EolienneState.OFF)) {
				this.scheduleEvent(new SwitchOnEvent(t));
				this.state = EolienneState.ON;
			}
		}

		d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
		this.scheduleEvent(new WindReadingEvent(this.getCurrentStateTime().add(d), this.currentWind));

	}
}
