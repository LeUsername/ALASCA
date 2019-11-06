package fr.sorbonne_u.devs_simulation.examples.molene.wbsm;

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

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.Map;
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

/**
 * The class <code>WiFiBandwidthSensorModel</code> implements a sensor
 * model as an HIOA that imports a variable giving the current bandwidth
 * of the network and exporting events that contains the value of the current
 * bandwidth. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model shares with the <code>WiFiBandwidthModel</code> the continuous
 * variable <code>bandwidth</code> produced by the latter. It also imports
 * the event <code>TicEvent</code> from the <code>TicModel</code>. Each
 * time the model receives a tic event, it emits an exported event
 * <code>WiFiBandwidthReading</code> containing the latest value of the
 * bandwidth.
 * </p>
 * <p>
 * Because in the DEVS protocol, the processing of an external event cannot
 * trigger the issuing of an external event, the above behaviour is implemented
 * through three steps:
 * </p>
 * <ol>
 * <li>an external tic event is received and processed by toggling the
 *   boolean variable <code>triggerReading</code> at true;</li>
 * <li>when the <code>triggerReading</code> is true, the method
 *   <code>timeAdvance</code> returns a 0 time delay until the next internal
 *   transition;</li>
 * <li>the triggered internal transition first requires a call to the method
 *   <code>output</code>, which emits the battery level event;</li>
 * <li>the internal transition is then executed and toggles
 *   <code>triggerReading</code> back to false.</li>
 * </ol>
 * <p>
 * And then the process can iterate again until the end of the simulation.
 * </p>
 * <p>
 * After the runs, the report returned by this model provides a piecewise
 * constant double function giving the readings of the WiFi bandwidth over
 * the whole simulation duration.
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
@ModelExternalEvents(imported = {TicEvent.class},
					 exported = {WiFiBandwidthReading.class})
// -----------------------------------------------------------------------------
public class				WiFiBandwidthSensorModel
extends		AtomicHIOAwithEquations
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
	public static class	WiFiBandwidthSensorReport
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L ;
		public final Vector<WiFiBandwidthReading>	readings ;

		public			WiFiBandwidthSensorReport(
			String modelURI,
			Vector<WiFiBandwidthReading> readings
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
			ret += "WiFi Bandwidth Sensor Report\n" ;
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
	private static final String		SERIES = "wifi bandwidth" ;
	public static final String		URI = "wifiBandwidthSensorModel-1" ;

	/** Wifi bandwidth in megabits per second.								*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>							bandwidth ;
	/** true when a external event triggered a reading.						*/
	protected boolean								triggerReading ;
	/** the last value emitted as a reading of the bandwidth.			 	*/
	protected double								lastReading ;
	/** the simulation time at the last reading.							*/
	protected double								lastReadingTime ;
	/** history of readings, for the simulation report.						*/
	protected final Vector<WiFiBandwidthReading>	readings ;

	/** Frame used to plot the bandwidth readings during the simulation.	*/
	protected XYPlotter				plotter ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi bandwidth sensor model.
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
	public				WiFiBandwidthSensorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger()) ;

		// Model implementation variable initialisation
		this.lastReading = -1.0 ;

		// Create the representation of the sensor bandwidth function
		this.readings = new Vector<WiFiBandwidthReading>() ;
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
					this.bandwidth.v) ;
				this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.bandwidth.v) ;
			}
			this.lastReading = this.bandwidth.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			Vector<EventI> ret = new Vector<EventI>(1) ;
			WiFiBandwidthReading wbr =
				new WiFiBandwidthReading(this.getCurrentStateTime(),
										this.bandwidth.v) ;
			ret.add(wbr) ;

			this.readings.addElement(wbr) ;
			this.logMessage(this.getCurrentStateTime() +
					"|output|bandwidth reading " +
					this.readings.size() + " with value = " +
					this.bandwidth.v) ;

			this.triggerReading = false ;
			return ret ;
		} else {
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
								"|internal|bandwidth = " +
								this.bandwidth.v + " Mbps.") ;
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
		return new WiFiBandwidthSensorReport(this.getURI(), this.readings) ;
	}
}
// -----------------------------------------------------------------------------
