package simulation.models.fridge;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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
import simulation.events.controller.ResumeFridgeEvent;
import simulation.events.controller.SuspendFridgeEvent;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.FridgeConsumptionEvent;
import simulation.events.fridge.OpenEvent;
import simulation.events.fridge.ResumeEvent;
import simulation.events.fridge.SuspendEvent;
import simulation.tools.fridge.FridgeConsumption;
import simulation.tools.fridge.FridgeDoor;
import wattwatt.tools.URIS;
import wattwatt.tools.fridge.FridgeSetting;


@ModelExternalEvents(imported = { CloseEvent.class, 
								  OpenEvent.class, 
								  ResumeEvent.class, 
								  SuspendEvent.class,
								  SuspendFridgeEvent.class, 
								  ResumeFridgeEvent.class,
								  TicEvent.class},
					 exported = { FridgeConsumptionEvent.class})
//-----------------------------------------------------------------------------
/**
* The class <code>FridgeModel</code> implements a simplified model of a fridge
* in the house
*
* <p><strong>Description</strong></p>
* 
* <p>
* The fridge model has multiple variables that represent the state it's in. These 
* are the following: - temperature that gives the inside temperature (in Celsius)
* of the fridge
* 					 - currentState which indicates if the fridge is currently
* cooling down (RESUMED) or not (SUSPENDED)
* 					 - currentDoorState wihch is either OPENED or CLOSED
* 					 - consumption (in Watt) that gives the energy consumption of
* the fridge. This variable depends on a combination of the last two variables
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
public class FridgeModel
extends AtomicHIOAwithEquations
{
	// ------------------------------------------------------------------------
	// Inner class
	// ------------------------------------------------------------------------
	
	/**
	 * The class <code>FridgeModelReport</code> implements the simulation
	 * report for the fridge model.
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
	public static class	FridgeModelReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L ;

		public			FridgeModelReport(String modelURI)
		{
			super(modelURI) ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "FridgeModelReport(" + this.getModelURI() + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	
	private static final String	TEMPERATURE_SERIES = "refrigerateur temperature" ;
	private static final String	CONSUMPTION_SERIES = "refrigerateur intensity" ;
	public static final String	URI = URIS.FRIDGE_MODEL_URI ;
	/** nominal tension (in Volts) of the fridge.							*/
	protected static final double TENSION = 220.0; // Volts
	/** indicates the quantity of degree the internal temperature
	 * of the fridge change each step. This variable can be added 
	 * or substracted to the current temperature depending on the
	 * fridge's state
	 */
	protected static final double TEMPERATURE_CHANGE = 0.05; // in Celsius

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature.			*/
	public static final String	MAX_TEMPERATURE = "max-temperature" ;
	/** name of the run parameter defining the minimum temperature.			*/
	public static final String	MIN_TEMPERATURE = "min-temperature" ;
	/** name of the plotter that displays the temperature.					*/
	public static final String	TEMPERATURE = "temperature" ;
	/** name of the plotter that displays the consumption.					*/
	public static final String	CONSUMPTION = "consumption" ;
	/** name of the plotter that displays the initial temperature.			*/
	public static final String	INITIAL_TEMP = "inital-temp" ;
	// Model implementation variables
	/** the maximum temperature												*/
	protected double maxTemperature ;
	/** the minimum temperature												*/
	protected double minTemperature ;
	/** the initial temperature 											*/
	protected double initialTemp;

	/**	Random number generator for the bandwidth after resumption;
	 *  the bandwidth after resumption follows a beta distribution.			*/
	protected final RandomDataGenerator	genTemperature ;

	/** Consumption in Watt.												*/
	protected Double				consumption ;
	/** current state of the door.											*/
	protected FridgeDoor		currentDoorState ;
	/** current state of the consumption.									*/
	protected FridgeConsumption	currentState ;
	
	/**  true when a external event triggered a reading.					*/
	protected boolean triggerReading;

	/** Frame used to plot the temperature during the simulation.			*/
	protected XYPlotter					temperaturePlotter ;
	/** Frame used to plot the consumption during the simulation.			*/
	protected XYPlotter					consumptionPlotter ;
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in Celsius.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>				temperature =
											new Value<Double>(this, 10.0, 0) ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create an fridge model instance.
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
	public				FridgeModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Create the random number generators
		this.genTemperature = new RandomDataGenerator() ;


		assert	this.temperature != null ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = this.getURI() + ":" + FridgeModel.MAX_TEMPERATURE ;
		this.maxTemperature = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + FridgeModel.MIN_TEMPERATURE ;
		this.minTemperature = (double) simParams.get(vname) ;

		vname = this.getURI() + ":" + FridgeModel.INITIAL_TEMP ;
		this.initialTemp = (double) simParams.get(vname) ;
		
		// Initialise the look of the plotter
		vname = this.getURI() + ":" + FridgeModel.TEMPERATURE + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.temperaturePlotter = new XYPlotter(pdTemperature) ;
		this.temperaturePlotter.createSeries(TEMPERATURE_SERIES) ;
		vname = this.getURI() + ":" + FridgeModel.CONSUMPTION + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdIntensity = (PlotterDescription) simParams.get(vname) ;
		this.consumptionPlotter = new XYPlotter(pdIntensity) ;
		this.consumptionPlotter.createSeries(CONSUMPTION_SERIES) ;
		
		// The reference to the embedding component
		this.componentRef = (EmbeddingComponentAccessI) simParams.get(URIS.FRIDGE_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		
		if (this.componentRef == null) {
			// the model starts with a closed door
			this.currentDoorState = FridgeDoor.CLOSED;

			// the model starts in the suspended
			this.currentState = FridgeConsumption.SUSPENDED;
		} else {
			try {
				this.currentDoorState = (FridgeDoor) this.componentRef.getEmbeddingComponentStateValue("door");
				this.currentState = (FridgeConsumption) this.componentRef.getEmbeddingComponentStateValue("state");
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
		

		
		// initialisation of the random number generators
		this.genTemperature.reSeedSecure() ;
				
		this.triggerReading = false;
		// initialisation of the temperature function plotter on the screen
		if (this.temperaturePlotter != null) {
			this.temperaturePlotter.initialise() ;
			this.temperaturePlotter.showPlotter() ;
		}
		// initialisation of the intensity function plotter on the screen
		if (this.consumptionPlotter != null) {
			this.consumptionPlotter.initialise() ;
			this.consumptionPlotter.showPlotter() ;
		}

		// standard initialisation
		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);

		if(this.componentRef == null) {
			double newTemperature = this.initialTemp ;
			this.temperature.v = newTemperature ;
			this.consumption = 0.0;
		}
		else {
			try {
				this.temperature.v = (Double)this.componentRef.getEmbeddingComponentStateValue("temperature") ;
				this.consumption = (Double)this.componentRef.getEmbeddingComponentStateValue("consumption");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (triggerReading) {
			double reading = this.consumption; // Watt
			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			FridgeConsumptionEvent consommation = new FridgeConsumptionEvent(currentTime, reading);
			ret.add(consommation);
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
	public Duration		timeAdvance()
	{
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
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if(this.componentRef != null) {
			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
			
			this.computeNextState();
			this.temperature.time = this.getCurrentStateTime() ;
		
			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
		}
		
		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);
		if(this.componentRef == null) {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset() ;
			assert	currentEvents != null;

			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.temperature.v) ;
			}
			
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
				
			Event ce =(Event) currentEvents.get(0);
			if (ce instanceof TicEvent) {
				triggerReading = true;
			} else {
				ce.executeOn(this);
			}
			this.computeNextState();
			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
		} else {
			ArrayList<EventI> currentEvents = this.getStoredEventAndReset() ;
			assert	currentEvents != null  ;

			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.temperature.v) ;
			}
			
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
				
			Event ce = (Event) currentEvents.get(0);
			if (ce instanceof TicEvent) {
				triggerReading = true;
			} 
			try {
				this.currentDoorState = (FridgeDoor) this.componentRef.getEmbeddingComponentStateValue("door");
				this.currentState = (FridgeConsumption) this.componentRef.getEmbeddingComponentStateValue("state");
				this.computeNextState();
				this.componentRef.setEmbeddingComponentStateValue("temperature", this.temperature.v) ;
				this.componentRef.setEmbeddingComponentStateValue("consumption", this.consumption);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			if (this.consumptionPlotter != null) {
				this.consumptionPlotter.addData(
					CONSUMPTION_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.consumption) ;
			}
		}
		
	}
	



	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return new FridgeModelReport(this.getURI()) ;
	}
	
	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	protected void		computeNextState()
	{
		if(this.currentState == FridgeConsumption.RESUMED) {
			this.consumption = FridgeModel.TENSION * FridgeSetting.ACTIVE_CONSUMPTION;
			if (this.currentDoorState == FridgeDoor.OPENED) {
				this.consumption += FridgeSetting.OPENING_ENERGY_CONSUMPTION;
				this.temperature.v = this.temperature.v - 0.1 * FridgeModel.TEMPERATURE_CHANGE;
			} else {
				assert	this.currentDoorState == FridgeDoor.CLOSED ;
				this.temperature.v = this.temperature.v - FridgeModel.TEMPERATURE_CHANGE;
				if(this.temperature.v <= this.minTemperature) {
					this.temperature.v = this.minTemperature;
				}
			}
		} else {
			assert	this.currentState == FridgeConsumption.SUSPENDED ;
			this.consumption = FridgeModel.TENSION * FridgeSetting.PASSIVE_CONSUMPTION;
			if (this.currentDoorState == FridgeDoor.OPENED) {
				this.consumption += FridgeSetting.OPENING_ENERGY_CONSUMPTION;
				this.temperature.v = this.temperature.v + 0.5 * FridgeModel.TEMPERATURE_CHANGE;
			} else {
				assert	this.currentDoorState == FridgeDoor.CLOSED ;
				this.temperature.v = this.temperature.v + 0.25*FridgeModel.TEMPERATURE_CHANGE;
			}
		}
		// As consumption were too massive, we choose to divide them by 100
		// to not overshadow other devices' consumtpion
		this.consumption/=100;
	}
	
	public FridgeDoor getDoorState() {
		return this.currentDoorState;
	}
	
	public void closeDoor() {
		this.currentDoorState = FridgeDoor.CLOSED;
	}
	
	public void openDoor() {
		this.currentDoorState = FridgeDoor.OPENED;
	}
	
	public void resume() {
		this.currentState = FridgeConsumption.RESUMED;
	}
	
	public void suspend() {
		this.currentState = FridgeConsumption.SUSPENDED;
	}
	
	public FridgeConsumption getState() {
		return this.currentState;
	}
	
	public double getTemperature() {
		return this.temperature.v;
	}
	
	public double getIntensity() {
		return this.consumption;
	}
}