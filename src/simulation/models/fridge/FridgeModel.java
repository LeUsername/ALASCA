package simulation.models.fridge;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
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
import simulation.events.fridge.AbstractFridgeEvent;
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
								  TicEvent.class},
					exported = { FridgeConsumptionEvent.class})
public class FridgeModel
extends AtomicHIOAwithEquations
{
	// ------------------------------------------------------------------------
	// Inner classes
	// ------------------------------------------------------------------------
	
	public static class	RefrigerateurModelReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L ;
		public final Vector<DoublePiece>		temperatureFunction ;
		public final Vector<DoublePiece>		intensityFunction ;

		public			RefrigerateurModelReport(
			String modelURI,
			Vector<DoublePiece> temperatureFunction,
			Vector<DoublePiece> intensityFunction
			)
		{
			super(modelURI) ;
			this.temperatureFunction = temperatureFunction;
			this.intensityFunction = intensityFunction;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "RefrigerateurModelReport\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "temperatureFunction = \n" ;
			for (int i = 0 ; i < this.temperatureFunction.size() ; i++) {
				ret += "    " + this.temperatureFunction.get(i) + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	private static final String	TEMPERATURE_SERIES = "refrigerateur temperature" ;
	private static final String	INTENSITY_SERIES = "refrigerateur intensity" ;
	public static final String	URI = "FridgeModel" ;
	/** nominal tension (in Volts) of the fridge. */
	protected static final double TENSION = 220.0; // Volts
	protected static final double CHANGEMENT_TEMPERATURE = 0.05; // �C

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature.			*/
	public static final String	MAX_TEMPERATURE = "max-temperature" ;
	/** name of the run parameter defining the minimum temperature.			*/
	public static final String	MIN_TEMPERATURE = "min-temperature" ;
	/** name of the plotter that displays the temperature.					*/
	public static final String	TEMPERATURE = "temperature" ;
	/** name of the plotter that displays the intensity.					*/
	public static final String	INTENSITY = "intensity" ;

	public static final String	INITIAL_TEMP = "inital-temp" ;
	// Model implementation variables
	/** the maximum temperature												*/
	protected double					maxTemperature ;
	/** the minimum temperature												*/
	protected double					minTemperature ;
	
	protected double initialTemp;


	/**	Random number generator for the bandwidth after resumption;
	 *  the bandwidth after resumption follows a beta distribution.			*/
	protected final RandomDataGenerator	genTemperature ;


	/** the value of the temperature at the next internal transition time.	*/
	protected double					nextTemperature ;
	/** delay until the next update of the bandwidth value.					*/
	protected double					nextDelay ;
	/** time at which the door was last openened.							*/
	protected Time						timeOfLastOpening ;
	/** time at which the systel was last suspended.						*/
	protected Time						timeOfLastSuspending ;
	/** current state of the door.											*/
	protected FridgeDoor		currentDoorState ;
	/** current state of the consumption.									*/
	protected FridgeConsumption	currentState ;

	// Bandwidth function and statistics for the report
	/** average temperature during the simulation run.						*/
	protected double					averageTemp ;
	/** function giving the temperature at all time during the
	 *  simulation run.														*/
	protected final Vector<DoublePiece>	temperatureFunction ;
	/** function giving the intensity at all time during the
	 *  simulation run.														*/
	protected final Vector<DoublePiece>	intensityFunction ;

	/** Frame used to plot the temperature during the simulation.				*/
	protected XYPlotter					temperaturePlotter ;
	/** Frame used to plot the intensity during the simulation.				*/
	protected XYPlotter					intensityPlotter ;
	/** reference on the object representing the component that holds the
	 *  model; enables the model to access the state of this component.		*/
	protected EmbeddingComponentAccessI componentRef ;
	
	protected boolean triggerReading;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in �C.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>				temperature =
											new Value<Double>(this, 10.0, 0) ;
	/** Intensity in Watt.								*/
	protected Double				intensity/* =
											new Double(this, 10.0, 0)*/ ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				FridgeModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger()) ;

		// Create the random number generators
		this.genTemperature = new RandomDataGenerator() ;

		// Create the representation of the temperature function for the report
		this.temperatureFunction = new Vector<DoublePiece>() ;
		// Create the representation of the intensity function for the report
		this.intensityFunction = new Vector<DoublePiece>() ;

		assert	this.temperature != null ;
	}

	// ------------------------------------------------------------------------
	// Simulation protocol and related methods
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
		vname = this.getURI() + ":" + FridgeModel.INTENSITY + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdIntensity = (PlotterDescription) simParams.get(vname) ;
		this.intensityPlotter = new XYPlotter(pdIntensity) ;
		this.intensityPlotter.createSeries(INTENSITY_SERIES) ;
		
		// The reference to the embedding component
		this.componentRef =
			(EmbeddingComponentAccessI) simParams.get(URIS.FRIDGE_URI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// initialisation of the random number generators
		this.genTemperature.reSeedSecure() ;

		// the model starts with a closed door 
		this.timeOfLastOpening = initialTime ;
		this.currentDoorState = FridgeDoor.CLOSED ;
		
		// the model starts in the suspended
		this.timeOfLastSuspending = initialTime ;
		this.currentState = FridgeConsumption.SUSPENDED ;

		// initialisation of the temperature function for the report
		this.temperatureFunction.clear() ;
		
		this.triggerReading = false;
		// initialisation of the temperature function plotter on the screen
		if (this.temperaturePlotter != null) {
			this.temperaturePlotter.initialise() ;
			this.temperaturePlotter.showPlotter() ;
		}
		// initialisation of the intensity function for the report
		this.intensityFunction.clear() ;
		// initialisation of the intensity function plotter on the screen
		if (this.intensityPlotter != null) {
			this.intensityPlotter.initialise() ;
			this.intensityPlotter.showPlotter() ;
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

		// Initialise the model variables, part of the initialisation protocol
		// of HIOA
		double newTemperature = this.initialTemp ;
		this.temperature.v = newTemperature ;
		// The consumption is initialized at 0 because the door is closed and the fridge is
		// in Suspended mode
		this.intensity = 0.0;
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
		if (this.hasDebugLevel(1)) {
			this.logMessage("RefrigerateurModel#userDefinedInternalTransition "
							+ elapsedTime) ;
		}
		if (this.triggerReading) {
			this.triggerReading = false;
		}
		this.computeNextState();
		this.temperature.time = this.getCurrentStateTime() ;

		// visualisation and simulation report.
		this.temperatureFunction.add(
			new DoublePiece(this.temperature.time.getSimulatedTime(),
							0,
							this.getCurrentStateTime().getSimulatedTime(),
							this.temperature.v)) ;
		if (this.temperaturePlotter != null) {
			this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.temperature.v) ;
		}
		this.intensityFunction.add(
				new DoublePiece(
						this.getCurrentStateTime().getSimulatedTime(),
						0,
						this.getCurrentStateTime().getSimulatedTime(),
						this.intensity)) ;
			if (this.intensityPlotter != null) {
				this.intensityPlotter.addData(
					INTENSITY_SERIES,
					this.getCurrentStateTime().getSimulatedTime(), 
					this.intensity) ;
			}
		this.logMessage(this.getCurrentStateTime() +
				"|internal|temperature = " + this.temperature.v + " �C") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(1)) {
			this.logMessage("WiFiBandwithModel#userDefinedExternalTransition "
							+ elapsedTime) ;
		}
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset() ;
		assert	currentEvents != null && currentEvents.size() == 1 ;

		// visualisation and simulation report.
		try {
			Time start = this.getSimulationEngine().getTimeOfStart();
			if (this.getCurrentStateTime().greaterThan(start) &&
					elapsedTime.greaterThan(
								Duration.zero(getSimulatedTimeUnit()))) {
				this.temperatureFunction.add(
					new DoublePiece(
							this.temperature.time.getSimulatedTime(),
							this.temperature.v,
							this.getCurrentStateTime().getSimulatedTime(),
							this.temperature.v)) ;
				if (this.temperaturePlotter != null) {
					this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(), 
						this.temperature.v) ;
				}
				this.intensityFunction.add(
						new DoublePiece(
								this.getCurrentStateTime().getSimulatedTime(),
								this.intensity,
								this.getCurrentStateTime().getSimulatedTime(),
								this.intensity)) ;
					if (this.intensityPlotter != null) {
						this.intensityPlotter.addData(
							INTENSITY_SERIES,
							this.getCurrentStateTime().getSimulatedTime(), 
							this.intensity) ;
					}
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		this.temperature.time = this.getCurrentStateTime() ;
		this.timeOfLastOpening = this.getCurrentStateTime() ;
		this.timeOfLastSuspending = this.getCurrentStateTime();
		Event ce =(Event) currentEvents.get(0);
		if (ce instanceof AbstractFridgeEvent) {
			ce.executeOn(this);
		} else {
			triggerReading = true;
		}
		this.computeNextState();

		// visualisation and simulation report.
		if (this.temperaturePlotter != null) {
			this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.temperature.v) ;
		}
		if (this.intensityPlotter != null) {
			this.intensityPlotter.addData(
				INTENSITY_SERIES,
				this.getCurrentStateTime().getSimulatedTime(), 
				this.intensity) ;
		}
	}
	

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.triggerReading) {
			double reading = this.temperature.v; // Watt

			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			FridgeConsumptionEvent consommation = new FridgeConsumptionEvent(currentTime, reading);
			ret.add(consommation);
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		Time end = this.getSimulationEngine().getSimulationEndTime();
		this.temperatureFunction.add(
				new DoublePiece(
						this.timeOfLastOpening.getSimulatedTime(),
						this.temperature.v,
						end.getSimulatedTime(),
						this.temperature.v)) ;
		this.intensityFunction.add(
				new DoublePiece(
						this.timeOfLastSuspending.getSimulatedTime(),
						this.intensity,
						end.getSimulatedTime(),
						this.intensity)) ;
		return new RefrigerateurModelReport(this.getURI(),
									  this.temperatureFunction,
									  this.intensityFunction) ;
	}
	
	// ------------------------------------------------------------------------
	// Model-specific methods
	// ------------------------------------------------------------------------

	protected void		computeNextState()
	{
		if(this.currentState == FridgeConsumption.RESUMED) {
			this.intensity = FridgeModel.TENSION * FridgeSetting.ACTIVE_CONSUMPTION;
			if (this.currentDoorState == FridgeDoor.OPENED) {
				this.intensity += FridgeSetting.OPENING_ENERGY_CONSUMPTION * FridgeModel.TENSION;
				this.temperature.v = this.temperature.v + 0.8 * FridgeModel.CHANGEMENT_TEMPERATURE;
			} else {
				assert	this.currentDoorState == FridgeDoor.CLOSED ;
				this.temperature.v = this.temperature.v - FridgeModel.CHANGEMENT_TEMPERATURE;
				if(this.temperature.v <= this.minTemperature) {
					this.temperature.v = this.minTemperature;
				}
			}
		} else {
			assert	this.currentState == FridgeConsumption.SUSPENDED ;
			this.intensity = FridgeModel.TENSION * FridgeSetting.PASSIVE_CONSUMPTION;
			if (this.currentDoorState == FridgeDoor.OPENED) {
				this.intensity += FridgeSetting.OPENING_ENERGY_CONSUMPTION * FridgeModel.TENSION;
				this.temperature.v = this.temperature.v + 0.5 * FridgeModel.CHANGEMENT_TEMPERATURE;
			} else {
				assert	this.currentDoorState == FridgeDoor.CLOSED ;
				this.temperature.v = this.temperature.v + 0.25*FridgeModel.CHANGEMENT_TEMPERATURE;
				
			}
		}
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
		return this.intensity;
	}
}
//------------------------------------------------------------------------------
