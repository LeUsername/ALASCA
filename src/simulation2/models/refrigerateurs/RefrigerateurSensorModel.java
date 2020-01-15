package simulation2.models.refrigerateurs;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOAwithEquations;
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
import simulation2.events.refrigerateur.ResumeEvent;
import simulation2.events.refrigerateur.SuspendEvent;
import simulation2.events.refrigerateur.TemperatureReadingEvent;

@ModelExternalEvents(imported = { TicEvent.class }, exported = {ResumeEvent.class, SuspendEvent.class})
public class RefrigerateurSensorModel extends		AtomicHIOAwithEquations
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiBandwidthSensorReport</code> implements the
	 * simulation report for the WiFi bandwidth sensor model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-18</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	RefrigerateurSensorModelReport
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<TemperatureReadingEvent>	readings ;

		public			RefrigerateurSensorModelReport(
			String modelURI,
			Vector<TemperatureReadingEvent> readings
			)
		{
			super(modelURI) ;
			this.readings = readings ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "RefrigerateurSensorModelReport\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "number of readings = " + this.readings.size() + "\n" ;
			ret += "Readings:\n" ;
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	private static final String		SERIES = "Temperature" ;
	public static final String		URI = "RefrigerateurSensorModel" ;

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum temperature.			*/
	public static final String	MAX_TEMPERATURE = "max-temperature" ;
	/** name of the run parameter defining the minimum temperature.			*/
	public static final String	MIN_TEMPERATURE = "min-temperature" ;
	
	// Model implementation variables
	/** the maximum temperature that should be reached						*/
	protected double					maxTemperature ;
	/** the minimum temperature	that should be reached						*/
	protected double					minTemperature ;
	/** true when a external event triggered a reading.						*/
	protected boolean								triggerReading ;
	/** the last value emitted as a reading of the bandwidth.			 	*/
	protected double								lastReading ;
	/** the simulation time at the last reading.							*/
	protected double								lastReadingTime ;
	/** history of readings, for the simulation report.						*/
	protected final Vector<TemperatureReadingEvent>	readings ;

	/** frame used to plot the bandwidth readings during the simulation.	*/
	protected XYPlotter				plotter ;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Temp in �C.								*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>							temperature ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				RefrigerateurSensorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.lastReading = -1.0 ;

		this.readings = new Vector<TemperatureReadingEvent>() ;
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
		String vname =
				this.getURI() + ":" + RefrigerateurSensorModel.MAX_TEMPERATURE ;
		this.maxTemperature = (double) simParams.get(vname) ;
		vname =
				this.getURI() + ":" + RefrigerateurSensorModel.MIN_TEMPERATURE ;
		this.minTemperature = (double) simParams.get(vname) ;
		vname =
				this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;

		// Initialise the look of the plotter
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
		this.triggerReading = false ;

		this.lastReadingTime = initialTime.getSimulatedTime() ;
		this.readings.clear() ;
		if (this.plotter != null) {
			this.plotter.initialise() ;
			this.plotter.showPlotter() ;
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			return Duration.INFINITY ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		if (this.triggerReading) {
			if (this.plotter != null) {
				this.plotter.addData(
					SERIES,
					this.lastReadingTime,
					this.temperature.v) ;
				this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.temperature.v) ;
			}
			this.lastReading = this.temperature.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			Vector<EventI> ret = new Vector<EventI>(1) ;
			Time currentTime = 
					this.getCurrentStateTime().add(this.getNextTimeAdvance()) ;
			TemperatureReadingEvent temp =
					new TemperatureReadingEvent(currentTime, this.temperature.v) ;
//			ret.add(temp) ;
			this.readings.addElement(temp) ;
//			this.logMessage(this.getCurrentStateTime() +
//					"|output|temperature reading " +
//					this.readings.size() + " with value = " +
//					this.temperature.v) ;

			this.triggerReading = false ;
			if(this.temperature.v <= this.minTemperature) {
				SuspendEvent suspend =
						new SuspendEvent(currentTime) ;
				ret.add(suspend) ;
			} else if (this.temperature.v > this.maxTemperature ) {
				ResumeEvent resume =
						new ResumeEvent(currentTime) ;
				ret.add(resume) ;
			} else {
				ResumeEvent resume =
						new ResumeEvent(currentTime) ;
				ret.add(resume) ;
			}
			return ret ;
		} else {
			if (this.plotter != null) {
				this.plotter.addData(
					SERIES,
					this.lastReadingTime,
					this.temperature.v) ;
				this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.temperature.v) ;
			}
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;
		this.logMessage(this.getCurrentStateTime() +
								"|internal|temperature = " +
								this.temperature.v + " �C") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		boolean	ticReceived = false ;
		for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true ;
			}
		}
		if (ticReceived) {
			this.triggerReading = true ;
			this.logMessage(this.getCurrentStateTime() +
									"|external|tic event received.") ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		if (this.plotter != null) {
			this.plotter.addData(SERIES,
								 endTime.getSimulatedTime(),
								 this.lastReading) ;
		}

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return new RefrigerateurSensorModelReport(this.getURI(), this.readings) ;
	}
}
// -----------------------------------------------------------------------------