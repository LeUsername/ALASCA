package simulation.models.electricmeter;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.tools.fridge.FridgeConsumption;
import wattwatt.tools.URIS;

@ModelExternalEvents(imported = { HairDryerConsumptionEvent.class, TicEvent.class }, exported = { ConsumptionEvent.class })
public class ElectricMeterModel extends AtomicHIOAwithEquations {
	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class ElectricMeterModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public ElectricMeterModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ElectricMeterModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = "ElectricMeterModel";

	private static final String SERIES = "consumption";
	public static final String CONSUMPTION_SERIES = "consumption-series";

	protected double totalConsumption;

	protected double hairDryerConsumption;
	protected double fridgeConsumption;
	protected double washingMachineConsumption;

	/** plotter for the consumption level over time. */
	protected XYPlotter consumptionPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an electric meter model instance.
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
	public ElectricMeterModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		// Initialise the look of the plotter
		String vname = this.getURI() + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.consumptionPlotter = new XYPlotter(pd);
		this.consumptionPlotter.createSeries(SERIES);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.ELECTRIC_METER_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		// initialisation of the intensity plotter on the screen
		if (this.consumptionPlotter != null) {
			this.consumptionPlotter.initialise();
			this.consumptionPlotter.showPlotter();
		}

		// try {
		// // set the debug level triggering the production of log messages.
		// this.setDebugLevel(1) ;
		// } catch (Exception e) {
		// throw new RuntimeException(e) ;
		// }

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {

		// first data in the plotter to start the plot.
		this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getConsumption());

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		// the model does not export any event.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		if (this.componentRef == null) {
			// the model has no internal event, however, its state will evolve
			// upon reception of external events.
			return Duration.INFINITY;
		} else {
			// This is to test the embedding component access facility.
			return new Duration(10.0, TimeUnit.SECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			// This is an example showing how to access the component state
			// from a simulation model; this must be done with care and here
			// we are not synchronising with other potential component threads
			// that may access the state of the component object at the same
			// time.
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
			try {
				this.fridgeConsumption = (double) componentRef.getEmbeddingComponentStateValue("fridgeConsumption");
				this.hairDryerConsumption = (double) componentRef
						.getEmbeddingComponentStateValue("hairDryerConsumption");
				this.washingMachineConsumption = (double) componentRef
						.getEmbeddingComponentStateValue("washingMachineConsumption");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
		} else {
			// TODO
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.componentRef == null) {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

			assert currentEvents != null && currentEvents.size() == 1;

			Event ce = (Event) currentEvents.get(0);

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());

			ce.executeOn(this);
			// add a new data on the plotter; this data will open a new piece

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());

			super.userDefinedExternalTransition(elapsedTime);

		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

			assert currentEvents != null && currentEvents.size() == 1;

			Event ce = (Event) currentEvents.get(0);

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
			try {
				ce.executeOn(this);
				if (ce.equals(FridgeConsumption.class)) {
					componentRef.getEmbeddingComponentStateValue("setFridgeConsumption");
				} else if (ce.equals(HairDryerConsumptionEvent.class)) {
					componentRef.getEmbeddingComponentStateValue("setHairDryerConsumption");
				} else if (ce.equals(WashingMachineConsumptionEvent.class)) {
					componentRef.getEmbeddingComponentStateValue("setWashingMachineConsumption");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// add a new data on the plotter; this data will open a new piece

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());

			super.userDefinedExternalTransition(elapsedTime);

		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.consumptionPlotter.addData(SERIES, endTime.getSimulatedTime(), this.getConsumption());

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new ElectricMeterModelReport(this.getURI());
	}

	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public double getConsumption() {
		this.totalConsumption = this.fridgeConsumption + this.hairDryerConsumption + this.washingMachineConsumption;
		return this.totalConsumption;
	}

	public void setHairDryerConsumption(double p) {
		this.hairDryerConsumption = p;
	}

	public void setWashingMachineConsumption(double p) {
		this.washingMachineConsumption = p;
	}

	public void setFridgeConsumption(double p) {
		this.fridgeConsumption = p;
	}
	
	public double getHairDryerConsumption() {
		return this.hairDryerConsumption;
	}

	public double getWashingMachineConsumption() {
		return this.washingMachineConsumption;
	}

	public double getFridgeConsumption() {
		return this.fridgeConsumption;
	}
}
