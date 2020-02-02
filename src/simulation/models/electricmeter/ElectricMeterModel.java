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
import simulation.events.fridge.FridgeConsumptionEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import wattwatt.tools.URIS;

@ModelExternalEvents(imported = { HairDryerConsumptionEvent.class, 
								  WashingMachineConsumptionEvent.class,
								  FridgeConsumptionEvent.class, 
								  TicEvent.class }, 
					 exported = { ConsumptionEvent.class })
//-----------------------------------------------------------------------------
/**
* The class <code>ElectricMeterModel</code> implements a simplified model of 
* an electric meter in the house
*
* <p><strong>Description</strong></p>
* 
* <p>
* The electric meter model is used to collect the energy consumption in the 
* house through their respective consumption events. It then aggregates and 
* store their sum in a single variable which is either sent via an event to
* the controller model (in MIL) or retrieved by the controller through the
* controller and electric meter components (in SIL).
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
public class ElectricMeterModel extends AtomicHIOAwithEquations {
	// -------------------------------------------------------------------------
	// Inner class
	// -------------------------------------------------------------------------

	/**
	 * The class <code>ElectricMeterModelReport</code> implements the simulation
	 * report for the electric meter model.
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
	public static final String URI = URIS.ELECTRIC_METER_MODEL_URI;

	private static final String SERIES = "consumption";
	public static final String CONSUMPTION_SERIES = "consumption-series";

	/**
	 * total energy consumption (in Watt)
	 */
	protected double totalConsumption;

	/**
	 * energy consumption (in Watt) of the hair dryer
	 */
	protected double hairDryerConsumption;
	/**
	 * energy consumption (in Watt) of the fridge
	 */
	protected double fridgeConsumption;
	/**
	 * energy consumption (in Watt) of washing machine
	 */
	protected double washingMachineConsumption;

	/** 
	 * true when a external event triggered a reading.					
	 */
	protected boolean triggerReading;

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

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {

		// first data in the plotter to start the plot.
		
		if(componentRef == null) {
			this.hairDryerConsumption = 0.0;
			this.washingMachineConsumption = 0.0;
			this.fridgeConsumption = 0.0;
			this.totalConsumption = 0.0;
			
		} else {
			try {
				this.hairDryerConsumption = (Double)this.componentRef.getEmbeddingComponentStateValue("hairDryerConsumption");
				this.washingMachineConsumption = (Double)this.componentRef.getEmbeddingComponentStateValue("washingMachineConsumption");
				this.fridgeConsumption = (Double)this.componentRef.getEmbeddingComponentStateValue("fridgeConsumption");
				this.totalConsumption = (Double)this.componentRef.getEmbeddingComponentStateValue("totalConsumption");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.getConsumption());
		this.triggerReading = false;
		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.getConsumption(); // Watt
			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ConsumptionEvent consumption = new ConsumptionEvent(currentTime, reading);
			ret.add(consumption);
			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration timeAdvance() {
		if (!this.triggerReading) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.componentRef != null) {
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
			try {
				this.fridgeConsumption = (double) componentRef.getEmbeddingComponentStateValue("fridgeConsumption");
				this.hairDryerConsumption = (double) componentRef
						.getEmbeddingComponentStateValue("hairDryerConsumption");
				this.washingMachineConsumption = (double) componentRef
						.getEmbeddingComponentStateValue("washingMachineConsumption");
				this.totalConsumption = (Double)this.componentRef.getEmbeddingComponentStateValue("totalConsumption");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);
		if (this.componentRef == null) {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

			assert currentEvents != null ;

			Event ce = (Event) currentEvents.get(0);

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());

			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} else {
				ce.executeOn(this);
			}

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());

			

		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();

			assert currentEvents != null;

			Event ce = (Event) currentEvents.get(0);
			
			if(ce instanceof TicEvent) {
				this.triggerReading = true;
			}

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());
			try {
				ce.executeOn(this);
				if (ce instanceof FridgeConsumptionEvent) {
					componentRef.getEmbeddingComponentStateValue("setFridgeConsumption");
				} else if (ce instanceof HairDryerConsumptionEvent) {
					componentRef.getEmbeddingComponentStateValue("setHairDryerConsumption");
				} else if (ce instanceof WashingMachineConsumptionEvent) {
					componentRef.getEmbeddingComponentStateValue("setWashingMachineConsumption");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
					this.getConsumption());


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
	
	public void setConsumption(double p) {
		this.totalConsumption = p;
	}
}
