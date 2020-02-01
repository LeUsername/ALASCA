package simulation.models.controller;

import java.util.ArrayList;
import java.util.Map;
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
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import wattwatt.tools.URIS;

@ModelExternalEvents(exported = { ConsumptionEvent.class, EngineGeneratorProductionEvent.class })

//-----------------------------------------------------------------------------
/**
* The class <code>ControllerStubModel</code> implements a simplified model of 
* a energy provider device
*
* <p><strong>Description</strong></p>
* 
* <p>
* This model is used to test that the controller can receive energy production event from other device
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
public class ControllerStubModel extends AtomicES_Model {

	private static final long serialVersionUID = 1L;

	public static class ControllerStubModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public ControllerStubModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ControllerStubModelReport(" + this.getModelURI() + ")";
		}
	}

	/** URI to be used when creating the model. */
	public static final String URI = URIS.CONTROLLER_STUB_MODEL_URI;

	/** during one use, mean time the hair dryer is at low temperature. */
	protected double meanTimeBetweenSend;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	protected Double consommation;

	protected Double production;

	public ControllerStubModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.meanTimeBetweenSend = 20;
		this.production = 5.0;
		this.consommation = 5.0;

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Duration d1 = new Duration(0, this.getSimulatedTimeUnit());
		Duration d2 = new Duration(2.0 * this.meanTimeBetweenSend * this.rg.nextBeta(1.75, 1.75),
				this.getSimulatedTimeUnit());
		Time t = this.getCurrentStateTime().add(d1).add(d2);
		this.scheduleEvent(new EngineGeneratorProductionEvent(t, this.production));

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
	public ArrayList<EventI> output() {

		assert !this.eventList.isEmpty();
		ArrayList<EventI> ret = super.output();
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

		if (this.nextEvent.equals(ConsumptionEvent.class)) {

			d = new Duration(2.0 * this.meanTimeBetweenSend * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			Time t = this.getCurrentStateTime().add(d);

			this.scheduleEvent(new EngineGeneratorProductionEvent(t, this.production + this.rg.nextUniform(-5, 5) ));

		} else if (this.nextEvent.equals(EngineGeneratorProductionEvent.class)) {
			d = new Duration(2.0 * this.meanTimeBetweenSend * this.rg.nextBeta(1.75, 1.75), this.getSimulatedTimeUnit());
			this.scheduleEvent(new ConsumptionEvent(this.getCurrentStateTime().add(d), this.consommation + this.rg.nextUniform(-5, 5)));

		}

	}

	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new ControllerStubModelReport(this.getURI());
	}

}
