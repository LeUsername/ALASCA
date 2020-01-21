package simulation.models.enginegenerator;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
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
import simulation.events.controller.StartEngineGenerator;
import simulation.events.controller.StopEngineGenerator;
import simulation.events.enginegenerator.AbstractEngineGeneratorEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEvent;
import simulation.events.enginegenerator.StopEvent;
import simulation.tools.enginegenerator.EngineGeneratorState;
import wattwatt.tools.EngineGenerator.EngineGeneratorSetting;

@ModelExternalEvents(imported = { RefillEvent.class, 
								  StartEvent.class, 
								  StopEvent.class,
								  StartEngineGenerator.class,
								  StopEngineGenerator.class,
								  TicEvent.class},
					 exported = {EngineGeneratorProductionEvent.class})
public class EngineGeneratorModel extends AtomicHIOAwithEquations {

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

	private static final long serialVersionUID = 1L;

	/**
	 * URI used to create instances of the model; assumes a singleton, otherwise a
	 * different URI must be given to each instance.
	 */
	public static final String URI = "EngineGeneratorModel";

	private static final String PRODUCTION = "production";
	public static final String PRODUCTION_SERIES = "production-series";
	private static final String QUANTITY = "quantity";
	public static final String QUANTITY_SERIES = "quantity-series";
	
	
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
	protected EmbeddingComponentStateAccessI componentRef;

	protected double production;
	protected double fuelCapacity;

	protected EngineGeneratorState state;

	public EngineGeneratorModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
	}
	
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname = this.getURI() + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdProduction = (PlotterDescription) simParams.get(vname) ;
		this.productionPlotter = new XYPlotter(pdProduction) ;
		this.productionPlotter.createSeries(EngineGeneratorModel.PRODUCTION) ;
		
		vname = this.getURI() + ":" + EngineGeneratorModel.QUANTITY_SERIES + ":"
				+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdFuelQuantity = (PlotterDescription) simParams.get(vname) ;
		this.fuelQuantityPlotter = new XYPlotter(pdFuelQuantity) ;
		this.fuelQuantityPlotter.createSeries(QUANTITY) ;
	}
	
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.state = EngineGeneratorState.OFF ;
		this.triggerReading = false;
		
			this.productionPlotter.initialise() ;
			this.productionPlotter.showPlotter() ;
			this.fuelQuantityPlotter.initialise() ;
			this.fuelQuantityPlotter.showPlotter() ;

		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		this.production = 0.0 ;
		this.fuelCapacity = EngineGeneratorSetting.FULL_CAPACITY;
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
	public Vector<EventI>	output()
	{
		if(this.triggerReading) {
			double reading = this.production ; // Watt

			Vector<EventI> ret = new Vector<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			EngineGeneratorProductionEvent production = new EngineGeneratorProductionEvent(currentTime, reading);
			ret.add(production);
			return ret;
		} else  {
			return null;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(1)) {
			this.logMessage("GroupeElectrogeneModel#userDefinedInternalTransition "
							+ elapsedTime) ;
		}
		if(this.triggerReading) {
			this.updateState();
			this.triggerReading = false;
		}
		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
			super.userDefinedInternalTransition(elapsedTime) ;

			
		if (this.productionPlotter != null) {
			this.productionPlotter.addData(
				PRODUCTION,
				this.getCurrentStateTime().getSimulatedTime(), 
				this.production) ;
		}
		this.logMessage(this.getCurrentStateTime() +
				"|internal|production = " + this.production + " Watt") ;
		}
		if (this.fuelQuantityPlotter != null) {
			this.fuelQuantityPlotter.addData(
				QUANTITY,
				this.getCurrentStateTime().getSimulatedTime(), 
				this.fuelCapacity) ;
		}
		this.logMessage(this.getCurrentStateTime() +
				"|internal|temperature = " + this.fuelCapacity + " L") ;
		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		if (this.hasDebugLevel(2)) {
			this.logMessage("GroupeElectrogeneModel::userDefinedExternalTransition 1");
		}

		// get the vector of current external events
		Vector<EventI> currentEvents = this.getStoredEventAndReset();
		boolean ticReceived = false;
		// when this method is called, there is at least one external event
		assert currentEvents != null;

		Event ce = (Event) currentEvents.get(0);

		if (this.hasDebugLevel(2)) {
			this.logMessage("GroupeElectrogeneModel::userDefinedExternalTransition 2 " + ce.getClass().getCanonicalName());
		}

		// the plot is piecewise constant; this data will close the currently
		// open piece
		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
		this.fuelQuantityPlotter.addData(QUANTITY, this.getCurrentStateTime().getSimulatedTime(), this.fuelCapacity);

		if (this.hasDebugLevel(2)) {
			this.logMessage("GroupeElectrogeneModel::userDefinedExternalTransition 3 " + this.state);
		}

		// execute the current external event on this model, changing its state
		// and intensity level
		if (ce instanceof TicEvent) {
			ticReceived = true;
		} else {
			assert ce instanceof AbstractEngineGeneratorEvent || ce instanceof AbstractControllerEvent;
			ce.executeOn(this);
		}
		if (ticReceived) {
			this.triggerReading = true;
			this.logMessage(this.getCurrentStateTime() + "|external|tic event received.");
		}
		 if (this.hasDebugLevel(1)) {
		 this.logMessage("GroupeElectrogeneModel::userDefinedExternalTransition 4 ");
		 }

		// add a new data on the plotter; this data will open a new piece
		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
		this.fuelQuantityPlotter.addData(QUANTITY, this.getCurrentStateTime().getSimulatedTime(), this.fuelCapacity);

		super.userDefinedExternalTransition(elapsedTime);
		if (this.hasDebugLevel(2)) {
			this.logMessage("GroupeElectrogeneModel::userDefinedExternalTransition 5");
		}
	}
	
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		return new EngineGeneratorModelReport(this.getURI());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void endSimulation(Time endTime) throws Exception {
		this.productionPlotter.addData(PRODUCTION, this.getCurrentStateTime().getSimulatedTime(), this.production);
		this.fuelQuantityPlotter.addData(QUANTITY, this.getCurrentStateTime().getSimulatedTime(), this.fuelCapacity);
		super.endSimulation(endTime);
	}
	
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
			if (this.isEmpty()) {
				this.state = EngineGeneratorState.OFF;
			}
			this.production = 0.0;
		}
	}

}
