package simulation2.models.refrigerateurs;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.BooleanPiece;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;
import simulation2.events.refrigerateur.CloseEvent;
import simulation2.events.refrigerateur.OpenEvent;
import simulation2.tools.refrigerateur.RefrigerateurPorte;

//-----------------------------------------------------------------------------
@ModelExternalEvents(exported = { CloseEvent.class, 
								 OpenEvent.class})
//-----------------------------------------------------------------------------
public class RefrigerateurUserModel 
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class	RefrigerateurUserModelReport
	extends		AbstractSimulationReport
	{
		private static final long			serialVersionUID = 1L ;
		public final int					numberOfTimesOpen ;
		public final double					availability ;
		public final Vector<BooleanPiece>	closeFunction ;

		public			RefrigerateurUserModelReport(
			String modelURI,
			int numberOfTimesOpen,
			double availability,
			Vector<BooleanPiece> closeFunction
			)
		{
			super(modelURI) ;
			this.numberOfTimesOpen =
					numberOfTimesOpen ;
			this.availability = availability ;
			this.closeFunction = closeFunction ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "RefrigerateurUserModelReport\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of time the door was opened = " +
								this.numberOfTimesOpen + "\n" ;
			ret += "availability = " + this.availability + "\n" ;
			ret += "closeFunction = \n" ;
			for (int i = 0 ; i < this.closeFunction.size() ; i++) {
				ret += "    " + this.closeFunction.get(i) + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** name of the series used in the plotting.							*/
	private static final String	SERIES = "Refrigerateur's door opened/closed" ;
	/** URI to be used when creating the model.								*/
	public static final String	URI = "RefrigerateurUserModel" ;
	/** name of the run parameter defining the mean time between
	 *  interruptions.														*/
	public static final String	MTBI = "mtbi" ;
	/** name of the run parameter defining the mean duration of
	 *  interruptions.														*/
	public static final String	MID = "mid" ;

	// Model simulation implementation variables
	/** the time between interruptions follows an exponential
	 *  distribution with mean <code>meanTimeBetweenInterruptions</code>.	*/
	protected double					meanTimeBetweenInterruptions ;
	/** the duration of the interruptions follows an exponential
	 *  distribution with mean <code>meanInterruptionDuration</code>.		*/
	protected double					meanInterruptionDuration ;
	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rgInterruptionIntervals ;
	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rgInterruptionDurations ;
	/** 	the current state of the refrigerateur's door.					*/
	protected RefrigerateurPorte currentState ;

	// Report generation
	/** piecewise boolean function giving the opened time and the closed time
	 *  of the door since the beginning of the run.							*/
	protected Vector<BooleanPiece>		closeFunction ;
	/** total time the door was closed since the beginning of the run.		*/
	protected double					closedTime ;
	/** total time of the run.												*/
	protected double					totalTime ;
	/** number of time the door was opened since the beginning of the run.	*/
	protected int						numberOfTimeOpened ;
	/** the time at which the last opening occurred.						*/
	protected double					timeOfLastOpening ;
	/** the time at which the last closing occurred.						*/
	protected double					timeOfLastClosing ;

	// Plotting
	/** Frame used to plot the bandwidth interruption function during
	 *  the simulation.														*/
	protected XYPlotter					plotter ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi disconnection model.
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
	public				RefrigerateurUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger()) ;

		// Create the random number generators
		this.rgInterruptionIntervals = new RandomDataGenerator() ;
		this.rgInterruptionDurations = new RandomDataGenerator() ;
		// Create the representation of the event occurrences function
		this.closeFunction = new Vector<BooleanPiece>() ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

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
		String vname = this.getURI() + ":" + MTBI ;
		this.meanTimeBetweenInterruptions = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + MID ;
		this.meanInterruptionDuration = (double) simParams.get(vname) ;

		vname = this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		// Initialise the look of the plotter
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries(SERIES) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// statistics gathered during each run and put in the report.
		this.numberOfTimeOpened = 0 ;
		this.closedTime = 0.0 ;
		this.totalTime = 0.0 ;

		// variables used to produce a function representing event occurrences
		// in the report and a plot on the screen
		this.timeOfLastOpening = -1.0 ;
		this.timeOfLastClosing = 0.0 ;

		// initialisation of the random number generators
		this.rgInterruptionIntervals.reSeedSecure() ;
		this.rgInterruptionDurations.reSeedSecure() ;

		// initialisation of the event occurrences function for the report
		this.closeFunction.clear() ;
		// initialisation of the event occurrences plotter on the screen
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		// standard initialisation (including the current state time)
		super.initialiseState(initialTime) ;

		// The model is set to start in the state interrupted and with a
		// resumption event that occurs at time 0.
		this.currentState = RefrigerateurPorte.OPENED ;
		this.scheduleEvent(new CloseEvent(initialTime)) ;
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.currentState == RefrigerateurPorte.OPENED) {
			// The event that forced the execution of an internal transition
			// is a resumption event.

			// Log the event occurrence
			this.logMessage(this.getCurrentStateTime() +
												"|open event.") ;
			// Switch to connected state.
			this.currentState = RefrigerateurPorte.CLOSED ;

			// Include a new point in the event occurrences function (report)
			if (this.timeOfLastOpening >= 0.0) {
				this.closeFunction.add(
					new BooleanPiece(
								this.timeOfLastOpening,
								this.getCurrentStateTime().getSimulatedTime(),
								true)) ;
			}
			this.timeOfLastClosing =
							this.getCurrentStateTime().getSimulatedTime() ;
			// Update the statistics for the report
			double d = this.getCurrentStateTime().getSimulatedTime() -
											this.timeOfLastOpening ;
			this.totalTime += d ;

			// Generate the next interruption event after a random delay and
			// schedule it to be triggered at the corresponding time.
			this.scheduleEvent(
				this.generateNextOpening(this.getCurrentStateTime())) ;

			// Update the plotter with the nex event occurrence
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0) ;
			}
		} else {
			assert	this.currentState == RefrigerateurPorte.CLOSED ;
			// The event that forced the execution of an internal transition
			// is an interruption event.

			// Log the event occurrence
			this.logMessage(this.getCurrentStateTime() +
												"|interrupt transmission.") ;

			// Switch to interrupted state.
			this.currentState = RefrigerateurPorte.OPENED ;

			// Include a new point in the event occurrences function (report)
			this.closeFunction.add(
				new BooleanPiece(
							this.timeOfLastClosing,
							this.getCurrentStateTime().getSimulatedTime(),
							false)) ;
			this.timeOfLastOpening =
							this.getCurrentStateTime().getSimulatedTime() ;
			// Update the statistics for the report
			double d = this.getCurrentStateTime().getSimulatedTime() -
												this.timeOfLastClosing ;
			this.totalTime += d ;
			this.closedTime += d ;

			// Generate the next resumption event after a random delay and
			// schedule it to be triggered at the corresponding time.
			this.scheduleEvent(this.generateNextClose(
												this.getCurrentStateTime())) ;

			// Include a new point in the event occurrences function (report)
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0) ;
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0) ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		// advance the time at the current (ending) time and finish the
		// event occurrences function for the report.
		double end = endTime.getSimulatedTime() ;
		if (this.currentState == RefrigerateurPorte.OPENED) {
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 0.0) ;
			}
		} else {
			assert	this.currentState == RefrigerateurPorte.CLOSED ;
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 1.0) ;
			}
		}
		// end the simulation run for this model.
		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	throws Exception
	{
		// Collect the event occurrences function and the statistics to
		// create the report and return it.
		Time end = this.getSimulationEngine().getSimulationEndTime() ;
		if (this.timeOfLastOpening < end.getSimulatedTime() &&
						this.timeOfLastClosing < end.getSimulatedTime()) {
			if (this.timeOfLastOpening < this.timeOfLastOpening) {
				// The last event was an interruption
				this.closeFunction.add(
						new BooleanPiece(this.timeOfLastOpening,
										 end.getSimulatedTime(),
										 true)) ;
			} else {
				// The last event was an resumption
				this.closeFunction.add(
						new BooleanPiece(this.timeOfLastClosing,
										 end.getSimulatedTime(),
										 false)) ;
				
			}
		}
		return new RefrigerateurUserModelReport(
								this.getURI(),
								this.numberOfTimeOpened,
								this.closedTime/this.totalTime,
								this.closeFunction) ;
	}

	// -------------------------------------------------------------------------
	// WiFi disconnection model proper methods
	// -------------------------------------------------------------------------

	/**
	 * create and return the next interruption event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @return			the next interruption event.
	 */
	protected OpenEvent		generateNextOpening(Time current)
	{
		// Generate the random delay until the next interruption
		double delay =
			this.rgInterruptionIntervals.nextExponential(
										this.meanTimeBetweenInterruptions) ;
		// Compute the corresponding tie of occurrence
		Time interruptionOccurrenceTime =
			current.add(new Duration(delay, this.getSimulatedTimeUnit())) ;
		// Create the interruption event at the corresponding time of occurrence
		OpenEvent gie =
						new OpenEvent(interruptionOccurrenceTime) ;
		// Update the statistics
		this.numberOfTimeOpened++ ;
		// Return the created event
		return gie ;
	}

	/**
	 * create and return the next resumption event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @return			the next resumption event.
	 */
	protected CloseEvent 	generateNextClose(Time current)
	{
		// Generate the random delay until the next interruption
		double interruptedTime =
				this.rgInterruptionDurations.nextExponential(
											this.meanInterruptionDuration) ;
		Time endOfInterruption =
				current.add(new Duration(interruptedTime,
							this.getSimulatedTimeUnit())) ;
		// Create and return the resumption event at the corresponding
		// time of occurrence
		return new CloseEvent(endOfInterruption) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#modelContentAsString(java.lang.String)
	 */
	@Override
	protected String	modelContentAsString(String indent)
	{
		return super.modelContentAsString(indent) +
							indent + "door = " + this.currentState ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent + "door = " + this.currentState) ;
	}
}
// -----------------------------------------------------------------------------