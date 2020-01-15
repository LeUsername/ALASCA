package simulation2.models.controleur;

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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation2.events.compteur.ConsommationEvent;
import simulation2.events.groupeelectrogene.ProductionEvent;

@ModelExternalEvents(exported = { ConsommationEvent.class, ProductionEvent.class })
public class ControleurBouchonModel extends AtomicES_Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class ControleurBouchonModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public ControleurBouchonModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ControleurBouchonModelReport(" + this.getModelURI() + ")";
		}
	}

	/** URI to be used when creating the model. */
	public static final String URI = "ControleurBouchonModel";

	// Model simulation implementation variables
	/** initial delay before sending the first switch on event. */
	protected double initialDelay;

	/** delay between uses of the hair dryer from one day to another. */
	protected double interdayDelay;

	/** mean time between uses of the hair dryer in the same day. */
	protected double meanTimeBetweenUsages;

	/** during one use, mean time the hair dryer is at high temperature. */
	protected double meanTimeWorking;

	/** during one use, mean time the hair dryer is at low temperature. */
	protected double meanTimeAtRefill;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	protected Double consommation;

	protected Double production;

	public ControleurBouchonModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		// create a standard logger (logging on the terminal)
		this.setLogger(new StandardLogger());
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.initialDelay = 0.0;
		this.interdayDelay = 100;
		this.meanTimeBetweenUsages = 150;
		this.meanTimeWorking = 20;
		this.meanTimeAtRefill = 20;
		this.production = 100.0;
		this.consommation = 100.0;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(this.initialDelay, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenUsages * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new ProductionEvent(t, this.production));

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.nextTimeAdvance);

	}

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		Duration d = super.timeAdvance();
		this.logMessage("ControleurBouchonModel::timeAdvance() 1 " + d + " " + this.eventListAsString());
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

		this.logMessage("ControleurBouchonModel::output() " + this.nextEvent.getCanonicalName());
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		Duration d;

		if (this.nextEvent.equals(ConsommationEvent.class)) {

			d = new Duration(2.0 * this.meanTimeAtRefill * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);

			this.scheduleEvent(new ProductionEvent(t, this.production + this.rg.nextUniform(-5, 5) ));

		} else if (this.nextEvent.equals(ProductionEvent.class)) {
			d = new Duration(2.0 * this.meanTimeAtRefill * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new ConsommationEvent(this.getCurrentStateTime().add(d), this.consommation + this.rg.nextUniform(-5, 5)));

		}

	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new ControleurBouchonModelReport(this.getURI());
	}

}
