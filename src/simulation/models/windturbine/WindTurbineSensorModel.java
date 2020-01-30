package simulation.models.windturbine;

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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import simulation.events.windturbine.SwitchOffEvent;
import simulation.events.windturbine.SwitchOnEvent;
import simulation.events.windturbine.WindReadingEvent;
import simulation.tools.windturbine.WindTurbineState;
import wattwatt.tools.URIS;

@ModelExternalEvents(exported = { WindReadingEvent.class, SwitchOffEvent.class, SwitchOnEvent.class })
public class WindTurbineSensorModel extends AtomicES_Model {
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class WindTurbineSensorModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WindTurbineSensorModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "WindTurbineSensorModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	public static final String URI = URIS.WIND_TURBINE_SENSOR_MODEL_URI;

	public static final String INITIAL_DELAY = "initial-delay";
	public static final String INTERDAY_DELAY = "interday-delay";

	protected double initialDelay;
	protected double interdayDelay;

	protected static final double MAX_WIND = 14.0;
	protected static final double MIN_WIND = 3.0;

	protected double currentWind;

	protected WindTurbineState state;

	/** next event to be sent. */
	protected Class<?> nextEvent;

	/** a random number generator from common math library. */
	protected final RandomDataGenerator rg;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WindTurbineSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.rg = new RandomDataGenerator();

		// create a standard logger (logging on the terminal)
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
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = this.getURI() + ":" + WindTurbineSensorModel.INITIAL_DELAY;
		this.initialDelay = (double) simParams.get(vname);
		vname = this.getURI() + ":" + WindTurbineSensorModel.INTERDAY_DELAY;
		this.interdayDelay = (double) simParams.get(vname);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.WIND_TURBINE_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {

		if (this.componentRef == null) {
			this.state = WindTurbineState.OFF;
		} else {
			try {
				this.state = (WindTurbineState) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.currentWind = WindTurbineSensorModel.MAX_WIND * this.rg.nextBeta(2., 2.);

		this.rg.reSeedSecure();

		super.initialiseState(initialTime);

		Time t = this.getCurrentStateTime();
		this.scheduleEvent(new WindReadingEvent(t, this.currentWind));

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
			assert !this.eventList.isEmpty();
			ArrayList<EventI> ret = super.output();
			assert ret.size() == 1;

			this.nextEvent = ret.get(0).getClass();

			return ret;
		}

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {

		if (componentRef == null) {
			Duration d;

			d = new Duration(2.0 * this.rg.nextBeta(2.5, 2.5), this.getSimulatedTimeUnit());

			Time t = this.getCurrentStateTime().add(d);

			this.currentWind += this.rg.nextInt(0, 2) - 1;

			if (this.currentWind <= 0.0) {
				this.currentWind = 0.0;
			}

			if (this.currentWind > WindTurbineSensorModel.MAX_WIND) {
				if (this.state.equals(WindTurbineState.ON)) {
					this.scheduleEvent(new SwitchOffEvent(t));
					this.state = WindTurbineState.OFF;
				}
			} else if (this.currentWind < WindTurbineSensorModel.MIN_WIND) {
				if (this.state.equals(WindTurbineState.ON)) {
					this.scheduleEvent(new SwitchOffEvent(t));
					this.state = WindTurbineState.OFF;
				}
			} else {
				if (this.state.equals(WindTurbineState.OFF)) {
					this.scheduleEvent(new SwitchOnEvent(t));
					this.state = WindTurbineState.ON;
				}
			}

			d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
			this.scheduleEvent(new WindReadingEvent(this.getCurrentStateTime().add(d), this.currentWind));
		} else {
			try {
				Duration d;

				this.state = (WindTurbineState) this.componentRef.getEmbeddingComponentStateValue("state");

				d = new Duration(2.0 * this.rg.nextBeta(2.5, 2.5), this.getSimulatedTimeUnit());

				Time t = this.getCurrentStateTime().add(d);

				this.currentWind += this.rg.nextInt(0, 2) - 1;

				if (this.currentWind <= 0.0) {
					this.currentWind = 0.0;
				}

				if (this.currentWind > WindTurbineSensorModel.MAX_WIND) {
					if (this.state.equals(WindTurbineState.ON)) {
						
						this.scheduleEvent(new SwitchOffEvent(t));
						this.state = WindTurbineState.OFF;
						this.componentRef.setEmbeddingComponentStateValue("stop", null);
					}
				} else if (this.currentWind < WindTurbineSensorModel.MIN_WIND) {
					if (this.state.equals(WindTurbineState.ON)) {
						
						this.scheduleEvent(new SwitchOffEvent(t));
						this.state = WindTurbineState.OFF;
						this.componentRef.setEmbeddingComponentStateValue("stop", null);
					}
				} else {
					if (this.state.equals(WindTurbineState.OFF)) {
						
						this.scheduleEvent(new SwitchOnEvent(t));
						this.state = WindTurbineState.ON;
						this.componentRef.setEmbeddingComponentStateValue("start", null);
					}
				}
				
				d = new Duration(this.interdayDelay, this.getSimulatedTimeUnit());
				this.scheduleEvent(new WindReadingEvent(this.getCurrentStateTime().add(d), this.currentWind));
				this.componentRef.setEmbeddingComponentStateValue("production", new Double(this.currentWind));
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
		return new WindTurbineSensorModelReport(this.getURI());
	}
}
