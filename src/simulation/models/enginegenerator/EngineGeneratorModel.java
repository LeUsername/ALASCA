package simulation.models.enginegenerator;

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
import simulation.events.controller.AbstractControllerEvent;
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.enginegenerator.AbstractEngineGeneratorEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEngineEvent;
import simulation.events.enginegenerator.StopEngineEvent;
import simulation.tools.enginegenerator.EngineGeneratorState;
import wattwatt.tools.URIS;
import wattwatt.tools.EngineGenerator.EngineGeneratorSetting;

@ModelExternalEvents(imported = { RefillEvent.class, 
								  StartEngineEvent.class, 
								  StopEngineEvent.class, 
								  StartEngineGeneratorEvent.class,
								  StopEngineGeneratorEvent.class, 
								  TicEvent.class }, 
					 exported = { EngineGeneratorProductionEvent.class })
//-----------------------------------------------------------------------------
/**
* The class <code>EngineGeneratorModel</code> implements a model of a engine
* generator in the house
*
* <p><strong>Description</strong></p>
* 
* <p>
* The engine generator model has two main variables: fuel capacity and
* production that directly impacts each other. The more fuel is consumed,
* the more energy is produced. However, the fuel contained in the engine
* generator is limited and has to be refilled through an user via the 
* <code>RefillEvent</code>.
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
public class EngineGeneratorModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner class
	// -------------------------------------------------------------------------

	/**
	 * The class <code>EngineGeneratorModelReport</code> implements the simulation
	 * report for the engine generator model.
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
	public static class EngineGeneratorModelReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public EngineGeneratorModelReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "EngineGeneratorModelReport(" + this.getModelURI() + ")";
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
	public static final String URI = URIS.ENGINE_GENERATOR_MODEL_URI;

	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";
	private static final String FUEL_QUANTITY = "quantity";
	public static final String FUEL_QUANTITY_SERIES = "quantity-series";
	
	/**
	 * energy production (in Watt)
	 */
	protected double production;
	/**
	 * remaining fuel (in Liters)
	 */
	protected double fuelCapacity;

	/**
	 * State in which the engine generator is in: 	- ON
	 * 												- OFF
	 */
	protected EngineGeneratorState state;

	/** true when a external event triggered a reading. */
	protected boolean triggerReading;

	/** plotter for the production level over time. */
	protected XYPlotter productionPlotter;

	/** plotter for the fuel quantity over time. */
	protected XYPlotter fuelQuantityPlotter;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of engine generator model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 					this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>todo.</i>
	 */
	public EngineGeneratorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams) throws Exception {
		String vname = this.getURI() + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pdProduction = (PlotterDescription) simParams.get(vname);
		this.productionPlotter = new XYPlotter(pdProduction);
		this.productionPlotter.createSeries(EngineGeneratorModel.PRODUCTION);

		vname = this.getURI() + ":" + EngineGeneratorModel.FUEL_QUANTITY_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME;
		PlotterDescription pdFuelQuantity = (PlotterDescription) simParams.get(vname);
		this.fuelQuantityPlotter = new XYPlotter(pdFuelQuantity);
		this.fuelQuantityPlotter.createSeries(FUEL_QUANTITY);

		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.ENGINE_GENERATOR_URI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {

		if (this.componentRef == null) {
			this.state = EngineGeneratorState.OFF;
		} else {
			try {
				this.state = (EngineGeneratorState) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.triggerReading = false;

		if (this.productionPlotter != null) {
			this.productionPlotter.initialise();
			this.productionPlotter.showPlotter();
		}
		if (this.fuelQuantityPlotter != null) {
			this.fuelQuantityPlotter.initialise();
			this.fuelQuantityPlotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void initialiseVariables(Time startTime) {
		super.initialiseVariables(startTime);

		this.production = 0.0;
		if (this.componentRef == null) {
			this.production = 0.0;
			this.fuelCapacity = EngineGeneratorSetting.FULL_CAPACITY;
		} else {
			try {
				this.production = (Double) this.componentRef.getEmbeddingComponentStateValue("production");
				this.fuelCapacity = (Double) this.componentRef.getEmbeddingComponentStateValue("capacity");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.production; // Watt
			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			EngineGeneratorProductionEvent production = new EngineGeneratorProductionEvent(currentTime, reading);
			ret.add(production);
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
		super.userDefinedInternalTransition(elapsedTime);
		if (this.componentRef != null) {

			if (this.productionPlotter != null) {
				this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(),
						this.production);
			}

			if (this.fuelQuantityPlotter != null) {
				this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
						this.fuelCapacity);
			}
			this.updateState();

			if (this.productionPlotter != null) {
				this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(),
						this.production);
			}

			if (this.fuelQuantityPlotter != null) {
				this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
						this.fuelCapacity);
			}
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
			assert currentEvents != null;

			Event ce = (Event) currentEvents.get(0);

			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
			this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
					this.fuelCapacity);

			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} else {
				assert ce instanceof AbstractEngineGeneratorEvent || ce instanceof AbstractControllerEvent;
				ce.executeOn(this);
			}

			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
			this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
					this.fuelCapacity);

			

		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;

			Event ce = (Event) currentEvents.get(0);

			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
			this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),this.fuelCapacity);

			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} 
			try {
				this.state = (EngineGeneratorState) this.componentRef.getEmbeddingComponentStateValue("state");
				this.fuelCapacity = (double) this.componentRef.getEmbeddingComponentStateValue("capacity");
				this.updateState();
				this.componentRef.setEmbeddingComponentStateValue("capacity",new Double(this.fuelCapacity));
				this.componentRef.setEmbeddingComponentStateValue("production",new Double(this.production));
				this.componentRef.setEmbeddingComponentStateValue("state", this.state);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
			this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
					this.fuelCapacity);

		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
		this.fuelQuantityPlotter.addData(FUEL_QUANTITY, this.getCurrentStateTime().getSimulatedTime(),
				this.fuelCapacity);
		super.endSimulation(endTime);
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new EngineGeneratorModelReport(this.getURI());
	}
	
	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	public void refill() {
		this.fuelCapacity = EngineGeneratorSetting.FULL_CAPACITY;
		updateState();
	}

	public void start() {
		this.state = EngineGeneratorState.ON;
		updateState();

	}

	public void stop() {
		this.state = EngineGeneratorState.OFF;
		updateState();
	}

	public boolean isOn() {
		return this.state == EngineGeneratorState.ON;
	}

	public boolean isFull() {
		return this.fuelCapacity == EngineGeneratorSetting.FUEL_CAPACITY;
	}

	public boolean isEmpty() {
		return this.fuelCapacity == 0.0;
	}

	public double getProduction() {
		return this.production;
	}

	public double getCapacity() {
		return this.fuelCapacity;
	}

	public void updateState() {
		if (this.isOn() && !this.isEmpty()) {
			this.production += EngineGeneratorSetting.PROD_THR;
			if (this.fuelCapacity - EngineGeneratorSetting.PROD_THR <= 0) {
				this.fuelCapacity = 0.0;
			} else {
				this.fuelCapacity -= EngineGeneratorSetting.PROD_THR;
			}
		} else {
			this.state = EngineGeneratorState.OFF;
			this.production = 0.0;
		}
	}

}
