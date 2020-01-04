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


@ModelExternalEvents(imported = { CloseEvent.class, 
								  OpenEvent.class},
					exported = { ConsommationEvent.class })
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

		public			RefrigerateurModelReport(
			String modelURI,
			Vector<DoublePiece> temperatureFunction
			)
		{
			super(modelURI) ;
			this.temperatureFunction = temperatureFunction;
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

	protected enum Door {
		OPENED,	
		CLOSED		
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	private static final String	SERIES = "refrigerateur temperature" ;
	public static final String	URI = "RefrigerateurModel" ;

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature.			*/
	public static final String	MAX_TEMPERATURE = "max-temperature" ;
	/** name of the run parameter defining the minimum temperature.			*/
	public static final String	MIN_TEMPERATURE = "min-temperature" ;
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
	/** time at which the last disconnection ended.							*/
	protected Time						timeOfLastOpening ;
	/** current state of the model.											*/
	protected Door						currentState ;

	// Bandwidth function and statistics for the report
	/** average temperature during the simulation run.						*/
	protected double					averageTemp ;
	/** function giving the temperature at all time during the
	 *  simulation run.														*/
	protected final Vector<DoublePiece>	temperatureFunction ;

	/** Frame used to plot the bandwidth during the simulation.				*/
	protected XYPlotter					plotter ;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in °C.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>				temperature =
											new Value<Double>(this, 10.0, 0) ;

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
		// Create the representation of the bandwidth function for the report
		this.temperatureFunction = new Vector<DoublePiece>() ;

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
		vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries(SERIES) ;
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

		// the model starts in the interrupted state wating for resumption 
		this.timeOfLastOpening = initialTime ;
		this.currentState = Door.CLOSED ;

		// initialisation of the bandwidth function for the report
		this.temperatureFunction.clear() ;
		// initialisation of the bandwidth function plotter on the screen
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
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
		if (this.currentState == Door.CLOSED) {
			return new Duration(this.nextDelay, this.getSimulatedTimeUnit()) ;
		} else {
			assert	this.currentState == Door.OPENED ;
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

		if (this.currentState == Door.CLOSED) {
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
			assert	this.currentState == Door.OPENED ;
			
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

			double oldBandwith = this.temperature.v ;
			if (this.currentState == Door.CLOSED) {
				// the value of the bandwidth at the next internal transition
				// is computed in the timeAdvance function when computing
				// the delay until the next internal transition.
				this.temperature.v = this.nextTemperature ;
			}
			this.temperature.time = this.getCurrentStateTime() ;

			// visualisation and simulation report.
			this.temperatureFunction.add(
				new DoublePiece(this.temperature.time.getSimulatedTime(),
								oldBandwith,
								this.getCurrentStateTime().getSimulatedTime(),
								this.temperature.v)) ;
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			this.logMessage(this.getCurrentStateTime() +
					"|internal|bandwidth = " + this.temperature.v + " Mbps") ;
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

		if (currentEvents.get(0) instanceof CloseEvent) {
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
					if (this.plotter != null) {
						this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(), 
							this.temperature.v) ;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}

			// compute new state after resumption
			this.temperature.v = this.generateBandwidthAtResumption() ;
			this.temperature.time = this.getCurrentStateTime() ;
			this.currentState = Door.CLOSED ;
			this.computeDerivatives() ;
			this.timeOfLastOpening = this.getCurrentStateTime() ;

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
			}
			this.logMessage(this.getCurrentStateTime() +
									"|external|closing with internal temperature of " +
									this.temperature.v + " °C.") ;
		} else {
			assert	currentEvents.get(0) instanceof OpenEvent ;
			// visualisation and simulation report.
			if (elapsedTime.greaterThan(
									Duration.zero(getSimulatedTimeUnit()))) {
				this.temperatureFunction.add(
						new DoublePiece(
								this.temperature.time.getSimulatedTime(),
								this.temperature.v,
								this.getCurrentStateTime().getSimulatedTime(),
								this.temperature.v)) ;
				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							this.temperature.v) ;
				}
			}
//			this.temperature.v = this.maxTemperature ;
			this.temperature.time = this.getCurrentStateTime() ;
			this.timeOfLastOpening = this.getCurrentStateTime() ;
			this.currentState = Door.OPENED ;
			this.computeDerivatives() ;

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.temperature.v) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.nextTemperature) ;
			}
			this.logMessage(this.getCurrentStateTime() +
												"|external|interrupt.") ;
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
		return new RefrigerateurModelReport(this.getURI(),
									  this.temperatureFunction) ;
	}
}
//------------------------------------------------------------------------------
