package simulation.equipements.refrigerateur.models;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE;
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
import simulation.equipements.compteur.models.events.ConsommationEvent;
import simulation.equipements.refrigerateur.models.events.CloseEvent;
import simulation.equipements.refrigerateur.models.events.OpenEvent;
import simulation.equipements.refrigerateur.models.events.ResumeEvent;
import simulation.equipements.refrigerateur.models.events.SuspendEvent;
import simulation.equipements.refrigerateur.tools.RefrigerateurConsommation;
import simulation.equipements.refrigerateur.tools.RefrigerateurPorte;
import wattwatt.tools.refrigerateur.RefrigerateurReglage;


@ModelExternalEvents(imported = { CloseEvent.class, 
								  OpenEvent.class, 
								  ResumeEvent.class, 
								  SuspendEvent.class },
					exported = { ConsommationEvent.class})
public class RefrigerateurModel
extends AtomicHIOAwithDE
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
	public static final String	URI = "RefrigerateurModel" ;
	/** nominal tension (in Volts) of the fridge. */
	protected static final double TENSION = 220.0; // Volts
	protected static final double CHANGEMENT_TEMPERATURE = 0.05; // °C

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature.			*/
	public static final String	MAX_TEMPERATURE = "max-temperature" ;
	/** name of the run parameter defining the minimum temperature.			*/
	public static final String	MIN_TEMPERATURE = "min-temperature" ;
	/** name of the plotter that displays the temperature.					*/
	public static final String	TEMPERATURE = "temperature" ;
	/** name of the plotter that displays the intensity.					*/
	public static final String	INTENSITY = "intensity" ;
	/** name of the run parameter defining the alpha parameter of the gamma
	 *  probability distribution giving the bandwidth at resumption.		*/
	public static final String	BAAR = "bandwidth-alpha-at-resumption" ;
	/** name of the run parameter defining the beta parameter of the gamma
	 *  probability distribution giving the bandwidth at resumption.		*/
	public static final String	BBAR = "bandwidth-beta-at-resumption" ;
	/** name of the run parameter defining the mean slope of the bandwidth
	 *  i.e., the mean parameter of the exponential distribution.			*/
	public static final String	BMASSF = "bandwidth-mean-absolute-slope-scale-factor" ;
	/** name of the run parameter defining the integration step for the
	 *  brownian motion followed by the bandwidth.						*/
	public static final String	BIS = "bandwidth-integration-step" ;

	// Model implementation variables
	/** the maximum temperature												*/
	protected double					maxTemperature ;
	/** the minimum temperature												*/
	protected double					minTemperature ;
	/** the alpha parameter of the gamma probability distribution giving
	 *  the bandwidth at resumption.										*/
	protected double					bandwidthAlphaAtResumption ;
	/** the beta parameter of the gamma probability distribution giving
	 *  the bandwidth at resumption.										*/
	protected double					bandwidthBetaAtResumption ;
	/** the mean slope of the bandwidth i.e., the mean parameter of the
	 *  exponential distribution.											*/
	protected double					bandwidthMeanAbsoluteSlopeScaleFactor ;
	/** the predefined integration step for the brownian motion followed
	 *  by the bandwidth, which can be punctually updated at run time
	 *  when necessary.														*/
	protected double					bandwidthIntegrationStep ;

	/**	Random number generator for the bandwidth after resumption;
	 *  the bandwidth after resumption follows a beta distribution.			*/
	protected final RandomDataGenerator	genTemperature ;

	/** Random number generator for the bandwidth continuous evolution;
	 * 	the bandwidth brownian motion uses an exponential and a
	 *  uniform distribution.												*/
	protected final RandomDataGenerator	rgBrownianMotion1 ;
	protected final RandomDataGenerator	rgBrownianMotion2 ;

	/** the value of the temperature at the next internal transition time.	*/
	protected double					nextTemperature ;
	/** delay until the next update of the bandwidth value.					*/
	protected double					nextDelay ;
	/** time at which the door was last openened.							*/
	protected Time						timeOfLastOpening ;
	/** time at which the systel was last suspended.						*/
	protected Time						timeOfLastSuspending ;
	/** current state of the door.											*/
	protected RefrigerateurPorte		currentDoorState ;
	/** current state of the consumption.									*/
	protected RefrigerateurConsommation	currentConsumption ;

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

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in °C.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>				temperature =
											new Value<Double>(this, 10.0, 0) ;
	/** Intensity in Watt.								*/
	protected Double				intensity/* =
											new Double(this, 10.0, 0)*/ ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				RefrigerateurModel(
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
		this.rgBrownianMotion1 = new RandomDataGenerator() ;
		this.rgBrownianMotion2 = new RandomDataGenerator() ;
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
	 * generate a new bandwidth using a beta distribution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	{@code ret >= 0.0 && ret <= this.maxBandwidth}
	 * </pre>
	 *
	 * @return	a randomly generated bandwidth
	 */
	protected double	generateBandwidthAtResumption()
	{
		// Generate a random temperature value at resumption using the Beta
		// distribution 
		double newTemperature =
			(this.maxTemperature - this.minTemperature) *
						this.genTemperature.nextBeta(
										this.bandwidthAlphaAtResumption,
										this.bandwidthBetaAtResumption) + this.minTemperature ;
		assert	newTemperature >= this.minTemperature && newTemperature <= this.maxTemperature ;
		return newTemperature ;
	}

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
		String vname = this.getURI() + ":" + RefrigerateurModel.MAX_TEMPERATURE ;
		this.maxTemperature = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + RefrigerateurModel.MIN_TEMPERATURE ;
		this.minTemperature = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + RefrigerateurModel.BAAR ;
		this.bandwidthAlphaAtResumption = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + RefrigerateurModel.BBAR ;
		this.bandwidthBetaAtResumption = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + RefrigerateurModel.BMASSF ;
		this.bandwidthMeanAbsoluteSlopeScaleFactor =
										(double) simParams.get(vname) ;		
		vname = this.getURI() + ":" + RefrigerateurModel.BIS ;
		this.bandwidthIntegrationStep = (double) simParams.get(vname) ;

		// Initialise the look of the plotter
		vname = this.getURI() + ":" + RefrigerateurModel.TEMPERATURE + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdTemperature = (PlotterDescription) simParams.get(vname) ;
		this.temperaturePlotter = new XYPlotter(pdTemperature) ;
		this.temperaturePlotter.createSeries(TEMPERATURE_SERIES) ;
		vname = this.getURI() + ":" + RefrigerateurModel.INTENSITY + ":"+ PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pdIntensity = (PlotterDescription) simParams.get(vname) ;
		this.intensityPlotter = new XYPlotter(pdIntensity) ;
		this.intensityPlotter.createSeries(INTENSITY_SERIES) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// initialisation of the random number generators
		this.genTemperature.reSeedSecure() ;
		this.rgBrownianMotion1.reSeedSecure() ;
		this.rgBrownianMotion2.reSeedSecure() ;

		// the model starts with a closed door 
		this.timeOfLastOpening = initialTime ;
		this.currentDoorState = RefrigerateurPorte.CLOSED ;
		
		// the model starts in the suspended
		this.timeOfLastSuspending = initialTime ;
		this.currentConsumption = RefrigerateurConsommation.SUSPENDED ;

		// initialisation of the temperature function for the report
		this.temperatureFunction.clear() ;
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
		double newTemperature = this.generateBandwidthAtResumption() ;
		this.temperature.v = newTemperature ;
		// The consumption is initialized at 0 because the door is closed and the fridge is
		// in Suspended mode
		this.intensity = 0.0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE#initialiseDerivatives()
	 */
	@Override
	protected void		initialiseDerivatives()
	{
		// Initialise the derivatives of the model variables, part of the
		// initialisation protocol of HIOA with differential equations
		this.computeDerivatives() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.currentDoorState == RefrigerateurPorte.CLOSED) {
			return new Duration(this.nextDelay, this.getSimulatedTimeUnit()) ;
		} else {
			assert	this.currentDoorState == RefrigerateurPorte.OPENED ;
			// the model will resume its internal transitions when it will
			// receive the corresponding triggering external event.
			return Duration.INFINITY ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithDE#computeDerivatives()
	 */
	@Override
	protected void		computeDerivatives()
	{
		// For stochastic differential equations, the method compute the
		// next value from a stochastic quantum rather than derivatives.
		// Here, because the bandwidth must remain between 0 and maxBandwidth,
		// the quantum may be limited, hence changing the delay until the limit
		// is reached. This delay becomes the next time advance of the model.

		if (this.currentDoorState == RefrigerateurPorte.CLOSED) {
			double delta_t = this.bandwidthIntegrationStep ;
			double uniform1 = this.rgBrownianMotion1.nextUniform(0.0, 1.0) ;
			// To generate a random number following an exponential distribution,
			// the inverse method says to generate a uniform random number u
			// following U[0,1] and then compute x = -1/M * ln(1 - u) follows
			// an exponential distribution with mean M.
			// Here M = result of the integration step * scale factor
			double quantum = -Math.log(1 - uniform1) / delta_t ;
			quantum = quantum * this.bandwidthMeanAbsoluteSlopeScaleFactor ;
			double uniform2 = this.rgBrownianMotion2.nextUniform(0.0, 1.0) ;
			double threshold =
					(this.maxTemperature - this.temperature.v)/this.maxTemperature ;
			if (Math.abs(uniform2 - threshold) < 0.000001) {
				// the slope is fixed at 0 to cope for the limit cases
				this.nextTemperature = this.temperature.v ;
				this.nextDelay = delta_t ;
			} else if (uniform2 < threshold) {
				// the quantum will be positive i.e., the bandwidth increases
				double limit = this.maxTemperature - this.temperature.v ;
				if (quantum > limit) {
					// the bandwidth cannot go over the maximum
					this.nextTemperature = this.maxTemperature ;
					this.nextDelay = -Math.log(1 - uniform1) / quantum ;
				} else {
					this.nextTemperature = this.temperature.v + quantum ;
					this.nextDelay = delta_t ;
				}
			} else {
				// the quantum will be negative i.e., the bandwidth decreases
				assert	uniform2 > threshold ;
				double limit = this.temperature.v ;
				if (quantum > limit) {
					// the bandwidth cannot go under 0
					this.nextTemperature = this.minTemperature ;
					this.nextDelay = -Math.log(1 - uniform1) / quantum ;
				} else {
					this.nextTemperature = this.temperature.v - quantum ;
					this.nextDelay = delta_t ;
				}
			}
		} else {
			assert	this.currentDoorState == RefrigerateurPorte.OPENED ;
			
		}
	}
	
	protected void		computeNextState()
	{
		if(this.currentConsumption == RefrigerateurConsommation.RESUMED) {
			this.intensity = RefrigerateurModel.TENSION * RefrigerateurReglage.CONSOMMATION_ACTIVE;
			if (this.currentDoorState == RefrigerateurPorte.OPENED) {
				this.temperature.v = this.temperature.v + 0.25 * RefrigerateurModel.CHANGEMENT_TEMPERATURE;
			} else {
				assert	this.currentDoorState == RefrigerateurPorte.CLOSED ;
				this.temperature.v = this.temperature.v - 1.5 * RefrigerateurModel.CHANGEMENT_TEMPERATURE;
				if(this.temperature.v <= this.minTemperature) {
					this.temperature.v = this.minTemperature;
				}
			}
		} else {
			assert	this.currentConsumption == RefrigerateurConsommation.SUSPENDED ;
			this.intensity = RefrigerateurModel.TENSION * RefrigerateurReglage.CONSOMMATION_PASSIVE;
			if (this.currentDoorState == RefrigerateurPorte.OPENED) {
				this.temperature.v = this.temperature.v + 1.5*RefrigerateurModel.CHANGEMENT_TEMPERATURE;
			} else {
				assert	this.currentDoorState == RefrigerateurPorte.CLOSED ;
				this.temperature.v = this.temperature.v + 0.25*RefrigerateurModel.CHANGEMENT_TEMPERATURE;
				
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(1)) {
			this.logMessage("WiFiBandwidthModel#userDefinedInternalTransition "
							+ elapsedTime) ;
		}
		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
			super.userDefinedInternalTransition(elapsedTime) ;

			double oldTemperature = this.temperature.v ;
//			if (this.currentDoorState == Door.CLOSED) {
//				// the value of the bandwidth at the next internal transition
//				// is computed in the timeAdvance function when computing
//				// the delay until the next internal transition.
//				this.temperature.v = this.nextTemperature ;
//			}
			this.computeNextState();
			this.temperature.time = this.getCurrentStateTime() ;

			// visualisation and simulation report.
			this.temperatureFunction.add(
				new DoublePiece(this.temperature.time.getSimulatedTime(),
								oldTemperature,
								this.getCurrentStateTime().getSimulatedTime(),
								this.temperature.v)) ;
			if (this.temperaturePlotter != null) {
				this.temperaturePlotter.addData(
						TEMPERATURE_SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			this.intensityFunction.add(
					new DoublePiece(this.temperature.time.getSimulatedTime(),
									oldTemperature,
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
			this.logMessage(this.getCurrentStateTime() +
					"|internal|temperature = " + this.temperature.v + " °C") ;
		}
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
		Vector<EventI> currentEvents = this.getStoredEventAndReset() ;
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
//		this.temperature.v = this.generateBandwidthAtResumption() ;
		
		if (currentEvents.get(0) instanceof CloseEvent) {
			this.currentDoorState = RefrigerateurPorte.CLOSED ;
			this.logMessage(this.getCurrentStateTime() +
					"|external|Closed.") ;
			System.out.println("Closed");
		} else if(currentEvents.get(0) instanceof OpenEvent) {
			this.currentDoorState = RefrigerateurPorte.OPENED ;
			this.logMessage(this.getCurrentStateTime() +
					"|external|Opened.") ;
			System.out.println("Opened");
		} else if (currentEvents.get(0) instanceof ResumeEvent) {
			this.currentConsumption = RefrigerateurConsommation.RESUMED ;
			this.logMessage(this.getCurrentStateTime() +
					"|external|Resumed.") ;
			System.out.println("Resumed");
		} else {
			assert	currentEvents.get(0) instanceof SuspendEvent ;
			this.currentConsumption = RefrigerateurConsommation.SUSPENDED ;
			this.logMessage(this.getCurrentStateTime() +
					"|external|Suspended.") ;
			System.out.println("Suspended");
		}
//		this.computeDerivatives() ;
		this.computeNextState();

		// visualisation and simulation report.
		if (this.temperaturePlotter != null) {
			this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.temperature.v) ;
			this.temperaturePlotter.addData(
					TEMPERATURE_SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.nextTemperature) ;
		}
		if (this.intensityPlotter != null) {
			this.intensityPlotter.addData(
				INTENSITY_SERIES,
				this.getCurrentStateTime().getSimulatedTime(), 
				this.intensity) ;
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
	public Vector<EventI>	output()
	{
		return null ;
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
}
//------------------------------------------------------------------------------
