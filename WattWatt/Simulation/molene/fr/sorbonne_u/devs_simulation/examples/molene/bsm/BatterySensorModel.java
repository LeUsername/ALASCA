package fr.sorbonne_u.devs_simulation.examples.molene.bsm;

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
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.PortableComputerStateModel;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

// -----------------------------------------------------------------------------
/**
 * The class <code>BatterySensorModel</code> implements an atomic model that
 * acts as a sensor for the battery in the PC.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model imports a continuous variable giving the current battery level and
 * tic events emitted by another atomic model, TicModel, at a regular rate.
 * Each time the model receives a tic event, it emits a battery level event with
 * the current value of the battery level.
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
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {TicEvent.class},
					 exported = {BatteryLevel.class})
// -----------------------------------------------------------------------------
public class			BatterySensorModel
extends		AtomicHIOAwithEquations
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>BatterySensorReport</code> implements the simulation
	 * report for the battery sensor model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2019-11-05</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	BatterySensorReport
	extends		AbstractSimulationReport
	{
		private static final long				serialVersionUID = 1L ;
		protected final Vector<BatteryLevel>	readings ;

		public			BatterySensorReport(
			String modelURI,
			Vector<BatteryLevel> readings
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
			ret += "Battery Level Sensor Report\n" ;
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

	private static final long				serialVersionUID = 1L ;
	/** an URI to be used when create an instance of the model.				*/
	public static final String				URI = "BatterySensorModel" ;

	/** the continuous variable imported from the PC model.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>					remainingCapacity ;
	/** true when a external event triggered a reading.						*/
	protected boolean						triggerReading ;
	/** the last value emitted as a reading of the battery level.		 	*/
	protected double						lastReading ;
	/** the simulation time at the last reading.							*/
	protected double						lastReadingTime ;
	/** history of readings, for the simulation report.						*/
	protected final Vector<BatteryLevel>	readings ;

	/** frame used to plot the battery level readings during the
	 *  simulation.															*/
	protected XYPlotter						plotter ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating a battery level sensor model instance.
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
	 * @throws Exception				<i>todo.</i>
	 */
	public				BatterySensorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.setLogger(new StandardLogger()) ;
		this.readings = new Vector<BatteryLevel>() ;
		this.lastReading = -1.0 ;
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
		String vname =
			this.getURI() + ":" + PlotterDescription.PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.plotter = new XYPlotter(pd) ;
		this.plotter.createSeries("standard") ;
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

		super.initialiseState(initialTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		this.remainingCapacity.v = PortableComputerStateModel.INITIAL_CAPACITY ;

		super.initialiseVariables(startTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			// immediate internal event when a reading is triggered.
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
			// Plotting, plays no role in the simulation
			if (this.plotter != null) {
				this.plotter.addData(
						"standard",
						this.lastReadingTime,
						this.remainingCapacity.v) ;
				this.plotter.addData(
						"standard",
						this.getCurrentStateTime().getSimulatedTime(),
						this.remainingCapacity.v) ;
			}
			// Memorise a new last reading
			this.lastReading = this.remainingCapacity.v ;
			this.lastReadingTime =
					this.getCurrentStateTime().getSimulatedTime() ;

			// Create and emit the battery level event.
			Vector<EventI> ret = new Vector<EventI>(1) ;
			BatteryLevel bl = new BatteryLevel(this.getCurrentStateTime(),
											  this.remainingCapacity.v) ;
			ret.add(bl) ;

			// Memorise the reading for the simulation report.
			this.readings.add(bl) ;
			// Trace the execution
			this.logMessage(this.getCurrentStateTime() +
					"|output|battery reading " +
					this.readings.size() + " with value = " +
					this.remainingCapacity.v) ;

			// The reading that was triggered has now been processed.
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
		if (this.hasDebugLevel(1)) {
			this.logMessage(this.getCurrentStateTime() +
							"|internal|battery = " +
							this.remainingCapacity.v + " mAh.") ;
		}
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
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return new BatterySensorReport(this.getURI(), this.readings) ;
	}
}
// -----------------------------------------------------------------------------
