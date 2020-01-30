package simulation.models.washingmachine;

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
import simulation.events.controller.StartWashingMachineEvent;
import simulation.events.controller.StopWashingMachineEvent;
import simulation.events.washingmachine.AbstractWashingMachineEvent;
import simulation.events.washingmachine.EcoModeEvent;
import simulation.events.washingmachine.PremiumModeEvent;
import simulation.events.washingmachine.StartAtEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.tools.washingmachine.WashingMachineState;
import wattwatt.tools.URIS;
import wattwatt.tools.washingmachine.WashingMachineMode;
import wattwatt.tools.washingmachine.WashingMachineSetting;

@ModelExternalEvents(imported = { EcoModeEvent.class, 
								  PremiumModeEvent.class, 
								  StartAtEvent.class, 
								  TicEvent.class,
								  StopWashingMachineEvent.class,
								  StartWashingMachineEvent.class },
					 exported = { WashingMachineConsumptionEvent.class})
public class WashingMachineModel extends AtomicHIOAwithEquations {

	// -------------------------------------------------------------------------
	// Inner classes and types
	// -------------------------------------------------------------------------

	public static class WashingMachineReport extends AbstractSimulationReport {
		private static final long serialVersionUID = 1L;

		public WashingMachineReport(String modelURI) {
			super(modelURI);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LaveLingeReport(" + this.getModelURI() + ")";
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
	public static final String URI = URIS.WASHING_MACHINE_MODEL_URI;

	private static final String SERIES = "intensity";
	public static final String CONSUMPTION_SERIES = "intensity";
	
	public static final String CONSUMPTION_ECO ="consoEco";
	public static final String CONSUMPTION_PREMIUM ="consoPremium";
	public static final String STD ="std";


	/** true when a external event triggered a reading. */
	protected boolean triggerReading;

	/** plotter for the intensity level over time. */
	protected XYPlotter consumptionPlotter;

	/** current intensity in Amperes; intensity is power/tension. */
	protected double currentConsumption;

	/**
	 * reference on the object representing the component that holds the model;
	 * enables the model to access the state of this component.
	 */
	protected EmbeddingComponentAccessI componentRef;

	/** Etat dans lequel se trouve le seche cheveux */
	protected WashingMachineState state;

	protected WashingMachineMode washingMode;
	
	protected double startingTimeDelay;
	
	protected  double consoEco;
	protected  double consoPremium;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine) throws Exception {
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
		String vname = this.getURI() + ":" + CONSUMPTION_ECO ;
		this.consoEco = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + CONSUMPTION_PREMIUM ;
		this.consoPremium = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + STD ;
		this.startingTimeDelay = (double) simParams.get(vname) ;
		
		vname = this.getURI() + ":" + WashingMachineModel.CONSUMPTION_SERIES + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.consumptionPlotter = new XYPlotter(pdTemperature) ;
		this.consumptionPlotter.createSeries(WashingMachineModel.SERIES) ;
		
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentAccessI) simParams.get(URIS.WASHING_MACHINE_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void initialiseState(Time initialTime) {
		if(componentRef == null) {
			this.washingMode = WashingMachineMode.ECO;
			this.state = WashingMachineState.OFF;
		} else {
			try {
				this.washingMode = (WashingMachineMode) this.componentRef.getEmbeddingComponentStateValue("mode");
				this.state = (WashingMachineState) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.triggerReading = false;

		if(this.consumptionPlotter != null) {
			this.consumptionPlotter.initialise();
			this.consumptionPlotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	@Override
	protected void initialiseVariables(Time startTime) {
		
		if(componentRef == null) {
			this.currentConsumption = 0.0;
		} else {
			try {
				this.currentConsumption =  (Double)this.componentRef.getEmbeddingComponentStateValue("consumption");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentConsumption);

		super.initialiseVariables(startTime);
	}

	@Override
	public Duration timeAdvance() {
		
		if (!this.triggerReading) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
	}

	@Override
	public ArrayList<EventI> output() {
		if (this.triggerReading) {
			double reading = this.currentConsumption; // Watt
			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			WashingMachineConsumptionEvent consommation = new WashingMachineConsumptionEvent(currentTime, reading);
			ret.add(consommation);
			this.triggerReading = false;
			return ret;
			
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
			if (this.componentRef != null) {
				this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
						this.currentConsumption);
				this.updateState();
				this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(),
						this.currentConsumption);
			}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);
		if(this.componentRef == null) {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;
	
			Event ce = (Event) currentEvents.get(0);
	
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentConsumption);

			if (ce instanceof TicEvent) {
				this.triggerReading = true;
			} else {
				assert ce instanceof AbstractWashingMachineEvent || ce instanceof AbstractControllerEvent;
				ce.executeOn(this);
			}
			this.consumptionPlotter.addData(SERIES, this.getCurrentStateTime().getSimulatedTime(), this.currentConsumption);
	
		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
			assert currentEvents != null;
	
			Event ce = (Event) currentEvents.get(0);

			if(ce instanceof TicEvent) {
				this.triggerReading = true;
			}
			try {
				this.state = (WashingMachineState) this.componentRef.getEmbeddingComponentStateValue("state");
				this.washingMode = (WashingMachineMode)this.componentRef.getEmbeddingComponentStateValue("mode");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.consumptionPlotter.addData(SERIES, endTime.getSimulatedTime(), this.currentConsumption);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new WashingMachineReport(this.getURI());
	}
	
	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------
	
	public double getIntensity() {
		return this.currentConsumption;
	}
	
	public WashingMachineMode getLavage() {
		return this.washingMode;
	}
	
	public boolean isOn() {
		return this.state == WashingMachineState.ON || isWorking();
	}
	
	public boolean isWorking() {
		return this.state == WashingMachineState.WORKING;
	}
	
	public double getStartingTimeDelay() {
		return this.startingTimeDelay;
	}
	
	public void startAt(double startingTimeDelay) {
		this.startingTimeDelay = startingTimeDelay;
		this.state = WashingMachineState.WORKING;
		updateState();
	}
	
	public void ecoLavage() {
		this.washingMode = WashingMachineMode.ECO;
		this.state = WashingMachineState.OFF;
	}
	
	public void premiumLavage() {
		this.washingMode = WashingMachineMode.PREMIUM;
		this.state = WashingMachineState.OFF;
	}
	
	public void start() {
		this.state = WashingMachineState.ON;
	}
	
	public void stop() {
		this.state = WashingMachineState.OFF;
	}

	private void updateState() {
		if(this.state == WashingMachineState.WORKING) {
			if(this.washingMode == WashingMachineMode.ECO) {
				
				this.currentConsumption += WashingMachineSetting.CONSO_ECO_MODE_SIM;
			}
			else {
				this.currentConsumption += WashingMachineSetting.CONSO_PREMIUM_MODE;
			}
		}
		else {
			this.currentConsumption = 0.0;
		}
	}
}
