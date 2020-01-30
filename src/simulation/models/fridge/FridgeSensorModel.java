package simulation.models.fridge;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.fridge.ResumeEvent;
import simulation.events.fridge.SuspendEvent;
import wattwatt.tools.URIS;

@ModelExternalEvents(imported = { TicEvent.class }, exported = { ResumeEvent.class, SuspendEvent.class })
public class FridgeSensorModel extends AtomicHIOAwithEquations {
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiBandwidthSensorReport</code> implements the simulation
	 * report for the WiFi bandwidth sensor model.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * <strong>Invariant</strong>
	 * </p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>
	 * Created on : 2018-07-18
	 * </p>
	 * 
	 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version $Name$ -- $Revision$ -- $Date$
	 */
	public static class FridgeSensorModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public FridgeSensorModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "FridgeSensorModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private static final String SERIES = "Temperature";
	public static final String URI = URIS.FRIDGE_SENSOR_MODEL_URI;

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature. */
	public static final String MAX_TEMPERATURE = "max-temperature";
	/** name of the run parameter defining the minimum temperature. */
	public static final String MIN_TEMPERATURE = "min-temperature";

	// Model implementation variables
	/** the maximum temperature that should be reached */
	protected double maxTemperature;
	/** the minimum temperature that should be reached */
	protected double minTemperature;
	/** true when a external event triggered a reading. */
	protected boolean triggerReading;


	/** frame used to plot the bandwidth readings during the simulation. */
	protected XYPlotter plotter;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in �C. */
	@ImportedVariable(type = Double.class)
	protected Value<Double> temperature;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public FridgeSensorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);


	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = this.getURI() + ":" + FridgeSensorModel.MAX_TEMPERATURE;
		this.maxTemperature = (double) simParams.get(vname);
		vname = this.getURI() + ":" + FridgeSensorModel.MIN_TEMPERATURE;
		this.minTemperature = (double) simParams.get(vname);
		vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME;

		// Initialise the look of the plotter
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.plotter = new XYPlotter(pd);
		this.plotter.createSeries(SERIES);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.FRIDGE_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		this.triggerReading = false;

		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		if (this.triggerReading) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			if (this.plotter != null) {
				this.plotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.temperature.v);
			}
			this.triggerReading = false;
			if (this.componentRef == null) {
				ArrayList<EventI> ret = new ArrayList<EventI>(1);
				Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());

				if (this.temperature.v <= this.minTemperature) {
					SuspendEvent suspend = new SuspendEvent(currentTime);
					ret.add(suspend);
				} else if (this.temperature.v >= this.maxTemperature) {
					ResumeEvent resume = new ResumeEvent(currentTime);
					ret.add(resume);
				}
				return ret;
				
			} else {
				try {
					if (this.temperature.v <= this.minTemperature) {
						this.componentRef.setEmbeddingComponentStateValue("suspend", null);
					} else if (this.temperature.v >= this.maxTemperature) {
						this.componentRef.setEmbeddingComponentStateValue("resume", null);
					} 
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		} else {
			if (this.plotter != null) {
				this.plotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.temperature.v);
			}
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> current = this.getStoredEventAndReset();
		boolean ticReceived = false;
		for (int i = 0; !ticReceived && i < current.size(); i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true;
			}
		}
		if (ticReceived) {
			this.triggerReading = true;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		if (this.plotter != null) {
			this.plotter.addData(SERIES, endTime.getSimulatedTime(), this.temperature.v);
		}
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new FridgeSensorModelReport(this.getURI());
	}
}
// -----------------------------------------------------------------------------