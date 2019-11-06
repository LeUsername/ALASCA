package fr.sorbonne_u.devs_simulation.examples.molene.wbm;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
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

/**
 * The class <code>WiFiBandwidthModel</code> defines a model of WiFi bandwidth
 * evolution over time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model assumes that the WiFi bandwidth follows a brownian motion
 * interrupted by random interruptions which durations are also random.
 * Interruptions and resumptions are represented by events which are
 * imported.
 * </p>
 * <p>
 * The brownian motion is defined in two parts. The continuous evolution is
 * defined by a stochastic differential equation but which is arranged in such
 * a way to have a solution that always remain between 0 and a user defined
 * maximum bandwidth. When interrupted, the bandwidth goes to 0 until
 * resumption. At resumption, the first bandwidth is a random value between
 * 0 and the maximum bandwidth.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {InterruptionEvent.class,
								 ResumptionEvent.class})
// -----------------------------------------------------------------------------
public class			WiFiBandwidthModel
extends		AtomicHIOAwithDE
{
	// ------------------------------------------------------------------------
	// Inner classes
	// ------------------------------------------------------------------------

	/**
	 * The class <code>WiFiBandwithReport</code> implements the simulation
	 * report for the WiFi bandwidth model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-18</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	WiFiBandwidthReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L ;
		public final Vector<DoublePiece>		bandwidthFunction ;

		public			WiFiBandwidthReport(
			String modelURI,
			Vector<DoublePiece> bandwidthFunction
			)
		{
			super(modelURI) ;
			this.bandwidthFunction = bandwidthFunction;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "WiFi Bandwidth Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "bandwidth function = \n" ;
			for (int i = 0 ; i < this.bandwidthFunction.size() ; i++) {
				ret += "    " + this.bandwidthFunction.get(i) + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	/**
	 * The enumeration <code>State</code> defines the state of the model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2018-10-22</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected enum State {
		INTERRUPTED,	// the WiFi is interrupted, the bandwidth == 0.0
		CONNECTED		// The WiFi is operational,  the bandwidth >= 0.0
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	private static final String	SERIES = "wifi bandwidth" ;
	public static final String	URI = "wifiBandwidthModel-1" ;

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum bandwidth.			*/
	public static final String	MAX_BANDWIDTH = "max-bandwidth" ;
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
	/** the maximum bandwidth												*/
	protected double					maxBandwidth ;
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
	/** the square root of the predefined integration step.					*/
//	protected double 					issqrt ;

	/**	Random number generator for the bandwidth after resumption;
	 *  the bandwidth after resumption follows a beta distribution.			*/
	protected final RandomDataGenerator	rgNewBandwidthLevel ;

	/** Random number generator for the bandwidth continuous evolution;
	 * 	the bandwidth brownian motion uses an exponential and a
	 *  uniform distribution.												*/
	protected final RandomDataGenerator	rgBrownianMotion1 ;
	protected final RandomDataGenerator	rgBrownianMotion2 ;

	/** Wifi bandwith in megabits per second.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		bandwidth =
											new Value<Double>(this, 10.0, 0) ;
	/** the current derivative of the bandwidth i.e., the current slope
	 *  of the brownian motion.												*/
//	protected double					bandwithDerivative ;
	/** 	the value of the bandwidth at the next internal transition time.	*/
	protected double					nextBandwidth ;
	protected double					nextDelay ;
	/** time at which the last disconnection ended.							*/
	protected Time						endTimeOfLastDisconnection ;
	/** 	current state of the model.										*/
	protected State						currentState ;

	// Bandwidth function and statistics for the report
	/** average bandwidth during the simulation run.						*/
	protected double					averageBandwidth ;
	/** function giving the bandwidth at all time during the
	 *  simulation run.														*/
	protected final Vector<DoublePiece>	bandwidthFunction ;

	/** Frame used to plot the bandwidth during the simulation.				*/
	protected XYPlotter					plotter ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi bandwidth model.
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
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>todo.</i>
	 */
	public				WiFiBandwidthModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger()) ;

		// Create the random number generators
		this.rgNewBandwidthLevel = new RandomDataGenerator() ;
		this.rgBrownianMotion1 = new RandomDataGenerator() ;
		this.rgBrownianMotion2 = new RandomDataGenerator() ;
		// Create the representation of the bandwidth function for the report
		this.bandwidthFunction = new Vector<DoublePiece>() ;

		assert	this.bandwidth != null ;
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
		// Generate a random bandwidth value at resumption using the Beta
		// distribution 
		double newBandwidth =
			this.maxBandwidth *
						this.rgNewBandwidthLevel.nextBeta(
										this.bandwidthAlphaAtResumption,
										this.bandwidthBetaAtResumption) ;
		assert	newBandwidth >= 0.0 && newBandwidth <= this.maxBandwidth ;
		return newBandwidth ;
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
		String vname = this.getURI() + ":" + WiFiBandwidthModel.MAX_BANDWIDTH ;
		this.maxBandwidth = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + WiFiBandwidthModel.BAAR ;
		this.bandwidthAlphaAtResumption = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + WiFiBandwidthModel.BBAR ;
		this.bandwidthBetaAtResumption = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + WiFiBandwidthModel.BMASSF ;
		this.bandwidthMeanAbsoluteSlopeScaleFactor =
										(double) simParams.get(vname) ;		
		vname = this.getURI() + ":" + WiFiBandwidthModel.BIS ;
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
		this.rgNewBandwidthLevel.reSeedSecure() ;
		this.rgBrownianMotion1.reSeedSecure() ;
		this.rgBrownianMotion2.reSeedSecure() ;

		// the model starts in the interrupted state wating for resumption 
		this.endTimeOfLastDisconnection = initialTime ;
		this.currentState = State.INTERRUPTED ;

		// initialisation of the bandwidth function for the report
		this.bandwidthFunction.clear() ;
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
		double newBandwidth = this.generateBandwidthAtResumption() ;
		this.bandwidth.v = newBandwidth ;
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
		if (this.currentState == State.CONNECTED) {
			return new Duration(this.nextDelay, this.getSimulatedTimeUnit()) ;
		} else {
			assert	this.currentState == State.INTERRUPTED ;
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

		if (this.currentState == State.CONNECTED) {
			double delta_t = this.bandwidthIntegrationStep ;
			double uniform1 = this.rgBrownianMotion1.nextUniform(0.0, 1.0) ;
			// To generate a random number following an exponential distribution,
			// the inverse method says to generate a uniform random number u
			// following U[0,1] and then compute x = -1/M * ln(1 - u) follows
			// an exponential distribution with mean M.
			// Here M = integration step * scale factor
			double quantum = -Math.log(1 - uniform1) / delta_t ;
			quantum = quantum * this.bandwidthMeanAbsoluteSlopeScaleFactor ;
			double uniform2 = this.rgBrownianMotion2.nextUniform(0.0, 1.0) ;
			double threshold =
					(this.maxBandwidth - this.bandwidth.v)/this.maxBandwidth ;
			if (Math.abs(uniform2 - threshold) < 0.000001) {
				// the slope is fixed at 0 to cope for the limit cases
				this.nextBandwidth = this.bandwidth.v ;
				this.nextDelay = delta_t ;
			} else if (uniform2 < threshold) {
				// the quantum will be positive i.e., the bandwidth increases
				double limit = this.maxBandwidth - this.bandwidth.v ;
				if (quantum > limit) {
					// the bandwidth cannot go over the maximum
					this.nextBandwidth = this.maxBandwidth ;
					this.nextDelay = -Math.log(1 - uniform1) / quantum ;
				} else {
					this.nextBandwidth = this.bandwidth.v + quantum ;
					this.nextDelay = delta_t ;
				}
			} else {
				// the quantum will be negative i.e., the bandwidth decreases
				assert	uniform2 > threshold ;
				double limit = this.bandwidth.v ;
				if (quantum > limit) {
					// the bandwidth cannot go under 0
					this.nextBandwidth = 0.0 ;
					this.nextDelay = -Math.log(1 - uniform1) / quantum ;
				} else {
					this.nextBandwidth = this.bandwidth.v - quantum ;
					this.nextDelay = delta_t ;
				}
			}
		} else {
			// When interrupted, the bandwidth remains at 0 until resumption
			assert	this.currentState == State.INTERRUPTED ;
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

			double oldBandwith = this.bandwidth.v ;
			if (this.currentState == State.CONNECTED) {
				// the value of the bandwidth at the next internal transition
				// is computed in the timeAdvance function when computing
				// the delay until the next internal transition.
				this.bandwidth.v = this.nextBandwidth ;
			}
			this.bandwidth.time = this.getCurrentStateTime() ;

			// visualisation and simulation report.
			this.bandwidthFunction.add(
				new DoublePiece(this.bandwidth.time.getSimulatedTime(),
								oldBandwith,
								this.getCurrentStateTime().getSimulatedTime(),
								this.bandwidth.v)) ;
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.bandwidth.v) ;
			}
			this.logMessage(this.getCurrentStateTime() +
					"|internal|bandwidth = " + this.bandwidth.v + " Mbps") ;
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

		if (currentEvents.get(0) instanceof ResumptionEvent) {
			// visualisation and simulation report.
			try {
				Time start = this.getSimulationEngine().getTimeOfStart();
				if (this.getCurrentStateTime().greaterThan(start) &&
						elapsedTime.greaterThan(
									Duration.zero(getSimulatedTimeUnit()))) {
					this.bandwidthFunction.add(
						new DoublePiece(
								this.bandwidth.time.getSimulatedTime(),
								0.0,
								this.getCurrentStateTime().getSimulatedTime(),
								0.0)) ;
					if (this.plotter != null) {
						this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(), 
							0.0) ;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}

			// compute new state after resumption
			this.bandwidth.v = this.generateBandwidthAtResumption() ;
			this.bandwidth.time = this.getCurrentStateTime() ;
			this.currentState = State.CONNECTED ;
			this.computeDerivatives() ;
			this.endTimeOfLastDisconnection = this.getCurrentStateTime() ;

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.bandwidth.v) ;
			}
			this.logMessage(this.getCurrentStateTime() +
									"|external|resume with bandwidth " +
									this.bandwidth.v + " Mbps.") ;
		} else {
			assert	currentEvents.get(0) instanceof InterruptionEvent ;
			// visualisation and simulation report.
			if (elapsedTime.greaterThan(
									Duration.zero(getSimulatedTimeUnit()))) {
				this.bandwidthFunction.add(
						new DoublePiece(
								this.bandwidth.time.getSimulatedTime(),
								this.bandwidth.v,
								this.getCurrentStateTime().getSimulatedTime(),
								this.bandwidth.v)) ;
				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							this.bandwidth.v) ;
				}
			}
			this.bandwidth.v = 0.0 ;
			this.bandwidth.time = this.getCurrentStateTime() ;
			this.endTimeOfLastDisconnection = this.getCurrentStateTime() ;
			this.currentState = State.INTERRUPTED ;
			this.computeDerivatives() ;

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.bandwidth.v) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
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
		this.bandwidthFunction.add(
				new DoublePiece(
						this.endTimeOfLastDisconnection.getSimulatedTime(),
						this.bandwidth.v,
						end.getSimulatedTime(),
						this.bandwidth.v)) ;
		return new WiFiBandwidthReport(this.getURI(),
									  this.bandwidthFunction) ;
	}
}
//------------------------------------------------------------------------------
