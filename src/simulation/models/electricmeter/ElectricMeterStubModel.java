package simulation.models.electricmeter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.electricmeter.ConsumptionEvent;
import wattwatt.tools.URIS;

@ModelExternalEvents(exported = { ConsumptionEvent.class })
//-----------------------------------------------------------------------------
/**
* The class <code>ElectricMeterStubModel</code> implements a simplified model of 
* a energy consumer device
*
* <p><strong>Description</strong></p>
* 
* <p>
* This model is used in MIL_ElectricMeter to test that the electric meter can receive energy 
* consumption event from other devices
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
public class ElectricMeterStubModel extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	public static final String URI = URIS.ELECTRIC_METER_STUB_MODEL_URI;

	/**
	 * the initial delay before the emission of a consumption event
	 */
	protected double initialDelay;
	/**
	 * the delay betweeen emissions of consumption events
	 */
	protected double delayBetweenEachSending;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public ElectricMeterStubModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
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
		this.delayBetweenEachSending = 100.0;

		this.rg.reSeedSecure();

		// Initialise to get the correct current time.
		super.initialiseState(initialTime);

		Time t = this.getCurrentStateTime();
		this.scheduleEvent(new ConsumptionEvent(t, 1));

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
		return d;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI> output() {
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

		this.scheduleEvent(new ConsumptionEvent(t, 1));

		d = new Duration(this.delayBetweenEachSending, this.getSimulatedTimeUnit());
		this.scheduleEvent(new ConsumptionEvent(this.getCurrentStateTime().add(d), 1));

	}
}
