package fr.sorbonne_u.devs_simulation.examples.molene.pcsm;

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
import fr.sorbonne_u.devs_simulation.examples.molene.State;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.utils.PlotterDescription;
import fr.sorbonne_u.utils.XYPlotter;

/**
 * The class <code>PortableComputerStateModel</code> implements an atomic model
 * for a portable computer in the Molene example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model has two main functions: keep the current state of the compression
 * mode and simulated the battery level. In order to perform these functions,
 * it must be an HIOA (to have and export a continuous variable for the battery
 * level).
 * </p>
 * <p>
 * When modelling the Molene example, the battery level evolution has been
 * assumed to depend upon the compression mode and the availability of the
 * network. Hence, the model imports two events from the WiFi bandwidth model
 * saying when the WiFi is interrupted or not, and it also imports three
 * events coming from the controller telling when to go to a compression mode
 * to another.
 * </p>
 * <p>
 * For the continuous part of the model, as this model is defined as a purely
 * deterministic equation based model, values of the exported variable are only
 * computed when the importing models need an updated value. This is called a
 * causal transition. It makes the computation much more efficient than updating
 * at the fast rhythm usually required when integrating differential equations.
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
@ModelExternalEvents(imported = {InterruptionEvent.class,
								 ResumptionEvent.class,
								 Compressing.class,
								 NotCompressing.class,
								 LowBattery.class})
// -----------------------------------------------------------------------------
public class			PortableComputerStateModel
extends		AtomicHIOAwithEquations
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PortableComputerReport</code> implements the simulation
	 * report for the portable computer state model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2018-10-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	PortableComputerStateReport
	extends		AbstractSimulationReport
	{
		private static final long			serialVersionUID = 1L;
		public final Vector<DoublePiece>	batteryFunction ;

		public			PortableComputerStateReport(
			String modelURI,
			Vector<DoublePiece> batteryFunction
			)
		{
			super(modelURI) ;
			this.batteryFunction = batteryFunction ;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n" ;
			ret += "Portable Computer State Report\n" ;
			ret += "-----------------------------------------\n" ;
			ret += "battery level function = \n" ;
			for (int i = 0 ; i < this.batteryFunction.size() ; i++) {
				ret += "    " + this.batteryFunction.get(i) + "\n" ;
			}
			ret += "-----------------------------------------\n" ;
			return ret ;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 	serialVersionUID = 1L ;
	private static final String	SERIES1 = "PC state" ;
	private static final String	SERIES2 = "battery level" ;
	public static final String	URI = "PortableComputerStateModel-1" ;
	public static final String	BATTERY_PLOTTING_PARAM_NAME =
													"battery-plotting" ;
	public static final String	STATE_PLOTTING_PARAM_NAME =
													"state-plotting" ;
	public static double		INITIAL_CAPACITY = 7500 ; // mAh
	public static double		MEAN_AUTONOMY = 7500 ; // seconds i.e., 2h
	/** rate of battery draining when compressing.						*/
	public static double		HIGH_RATE =
										1.5 * INITIAL_CAPACITY/MEAN_AUTONOMY ;
	/** rate of battery draining when not compressing.					*/
	public static double		LOW_RATE =
										1.0 * INITIAL_CAPACITY/MEAN_AUTONOMY ;
	/** rate of battery draining when the WiFi is interrupted.			*/
	public static double		NO_RATE =
										0.5 * INITIAL_CAPACITY/MEAN_AUTONOMY ;

	/** current state of the portable computer.								*/
	protected State					currentState ;
	/** remaining capacity in the battery.									*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	remainingCapacity =
											new Value<Double>(
												this, INITIAL_CAPACITY, 0) ;

	/** battery level function as observed, for the simulation report.		*/
	protected final Vector<DoublePiece>	batteryFunction ;
	/** Frame used to plot the battery level during the simulation.	*/
	protected XYPlotter					batteryLevelPlotter ;
	/** Frame used to plot the state during the simulation.			*/
	protected XYPlotter					statePlotter ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create an instance of portable computer state model.
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
	public				PortableComputerStateModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		this.batteryFunction = new Vector<DoublePiece>() ;

		this.setLogger(new StandardLogger()) ;
		this.setDebugLevel(1) ;
	}

	// ------------------------------------------------------------------------
	// Model specific methods
	// ------------------------------------------------------------------------

	/**
	 * return an integer representation to ease the plotting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	s != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param s	a state for the portable computer.
	 * @return	an integer representation to ease the plotting.
	 */
	public static int	state2int(State s)
	{
		assert	s != null ;

		if (s == State.COMPRESSING) {
			return 6 ;
		} else if (s == State.INTERRUPTED_C) {
			return 5 ;
		} else if (s == State.NOT_COMPRESSING) {
			return 4 ;
		} else if (s == State.INTERRUPTED_NC) {
			return 3 ;
		} else if (s == State.LOW_BATTERY) {
			return 2 ;
		} else {
			assert	s == State.INTERRUPTED_LB ;
			return 1 ;
		}
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
		String vname = this.getURI() + ":" +
					PortableComputerStateModel.BATTERY_PLOTTING_PARAM_NAME ;
		PlotterDescription pd = (PlotterDescription) simParams.get(vname) ;
		this.batteryLevelPlotter = new XYPlotter(pd) ;
		this.batteryLevelPlotter.createSeries(SERIES2) ;
		vname = this.getURI() + ":" +
					PortableComputerStateModel.STATE_PLOTTING_PARAM_NAME ;
		pd = (PlotterDescription) simParams.get(vname) ;
		this.statePlotter = new XYPlotter(pd) ;
		this.statePlotter.createSeries(SERIES1) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = State.NOT_COMPRESSING ;

		this.batteryFunction.clear() ;
		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.initialise() ;
			this.batteryLevelPlotter.showPlotter() ;
		}
		if (this.statePlotter != null) {
			this.statePlotter.initialise() ;
			this.statePlotter.showPlotter() ;
		}

		super.initialiseState(initialTime);

		if (this.statePlotter != null) {
			this.statePlotter.addData(
				SERIES1,
				this.getCurrentStateTime().getSimulatedTime(),
				state2int(this.currentState)) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseVariables(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	protected void		initialiseVariables(Time startTime)
	{
		super.initialiseVariables(startTime);
		this.remainingCapacity.v =
							PortableComputerStateModel.INITIAL_CAPACITY ;

		assert	startTime.equals(this.remainingCapacity.time) ;

		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.addData(SERIES2,
											startTime.getSimulatedTime(),
											this.remainingCapacity.v) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// Time is always infinity because, as a purely equation based model,
		// the internal transitions will be triggered by the model importing
		// the variable when it needs an updated value.

		return Duration.INFINITY ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		// the model does not export events.
		return null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime) ;

		double delta_t = elapsedTime.getSimulatedDuration() ;
		this.computeNewLevel(this.getCurrentStateTime(), delta_t) ;
	}

	/**
	 * compute a new battery level given the current state of the PC.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @param delta_t	duration since the last computed level.
	 */
	protected void		computeNewLevel(Time current, double delta_t)
	{
		// This method implements a linear progression of the battery level,
		// which is so simple that we did not implement it as an explicit
		// differential equation.

		double oldCapacity = this.remainingCapacity.v ;
		double oldTime = current.getSimulatedTime() - delta_t ;
		if (this.currentState == State.COMPRESSING) {
			if (this.hasDebugLevel(1)) this.logMessage("HIGH_RATE") ;
			this.remainingCapacity.v =
					this.remainingCapacity.v - delta_t * HIGH_RATE ;
		} else if (this.currentState == State.NOT_COMPRESSING ||
									this.currentState == State.LOW_BATTERY) {
			if (this.hasDebugLevel(1)) this.logMessage("LOW_RATE");
			this.remainingCapacity.v =
					this.remainingCapacity.v - delta_t * LOW_RATE ;			
		} else {
			assert	this.currentState == State.INTERRUPTED_C ||
					this.currentState == State.INTERRUPTED_NC ||
					this.currentState == State.INTERRUPTED_LB ;
			if (this.hasDebugLevel(1)) this.logMessage("NO_RATE");
			this.remainingCapacity.v =
					this.remainingCapacity.v - delta_t * NO_RATE ;			
		}
		this.remainingCapacity.time = current ;

		this.batteryFunction.add(
			new DoublePiece(oldTime,
							oldCapacity,
							current.getSimulatedTime(),
							this.remainingCapacity.v)) ;
		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.addData(SERIES2,
							 				current.getSimulatedTime(),
							 				this.remainingCapacity.v) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		Vector<EventI> currentEvents = this.getStoredEventAndReset() ;
		this.logMessage(
				"PortableComputerStateModel#userDefinedExternalTransition "
								+ this.currentState + " "
								+ currentEvents.get(0)) ;
		assert	currentEvents != null && currentEvents.size() == 1 ;

		State oldState = this.currentState ;

		// Compute the new state given the incoming event
		Event ce = (Event) currentEvents.get(0) ;
		if (this.currentState == State.COMPRESSING) {
			if (ce instanceof Compressing) {
				// not possible
			} else if (ce instanceof NotCompressing) {
				this.currentState = State.NOT_COMPRESSING ;
			} else if (ce instanceof LowBattery) {
				this.currentState = State.LOW_BATTERY ;
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_C ;
			} else {
				assert	ce instanceof ResumptionEvent ;
				// not possible
			}
		} else if (this.currentState == State.INTERRUPTED_C) {
			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				this.currentState = State.INTERRUPTED_LB ;
			} else if (ce instanceof InterruptionEvent) {
				// not possible
			} else {
				assert	ce instanceof ResumptionEvent ;
				this.currentState = State.COMPRESSING ;
			}
		} else if (this.currentState == State.NOT_COMPRESSING) {
			if (ce instanceof Compressing) {
				this.currentState = State.COMPRESSING ;
			} else if (ce instanceof NotCompressing) {
				// not possible
			} else if (ce instanceof LowBattery) {
				this.currentState = State.LOW_BATTERY ;
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_NC ;
			} else {
				assert	ce instanceof ResumptionEvent ;
				// not possible
			}
		} else if (this.currentState == State.INTERRUPTED_NC) {
			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				this.currentState = State.INTERRUPTED_LB ;
			} else if (ce instanceof InterruptionEvent) {
				// not possible.
			} else {
				assert	ce instanceof ResumptionEvent ;
				this.currentState = State.NOT_COMPRESSING ;
			}
		} else if (this.currentState == State.LOW_BATTERY) {
			if (ce instanceof Compressing) {
				// do nothing.
			} else if (ce instanceof NotCompressing) {
				// do nothing.
			} else if (ce instanceof LowBattery) {
				// not possible.
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_LB ;
			} else {
				assert	ce instanceof ResumptionEvent ;
				// not possible.
			}
		} else {
			assert	this.currentState == State.INTERRUPTED_LB ;

			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				// not possible.
			} else if (ce instanceof InterruptionEvent) {
				// not possible.
			} else {
				assert	ce instanceof ResumptionEvent ;
				this.currentState = State.LOW_BATTERY ;
			}
		}

		if (this.statePlotter != null && oldState != this.currentState) {
			this.statePlotter.addData(
					SERIES1,
					this.getCurrentStateTime().getSimulatedTime(),
					state2int(oldState)) ;
			this.statePlotter.addData(
					SERIES1,
					this.getCurrentStateTime().getSimulatedTime(),
					state2int(this.currentState)) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		double delta_t = endTime.getSimulatedTime() -
							this.remainingCapacity.time.getSimulatedTime() ;
		this.computeNewLevel(endTime, delta_t) ;

		if (this.statePlotter != null) {
			this.statePlotter.addData(SERIES1,
									  endTime.getSimulatedTime(),
									  state2int(this.currentState)) ;
		}

		super.endSimulation(endTime) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return new PortableComputerStateReport(this.getURI(),
										 this.batteryFunction) ;
	}
}
// -----------------------------------------------------------------------------
