package fr.sorbonne_u.devs_simulation.simulators;

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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationEngine</code> defines the basic behaviour
 * of a DEVS simulator declared by the interface <code>SimulatorI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A DEVS simulation engine provides an implementation of a simulation algorithm
 * that complies with the DEVS protocol. It is linked to a DEVS simulation model
 * through the method <code>setSimulatedModel</code>. It then can perform
 * simulation runs by calling <code>doStandAloneSimulation</code> when its model
 * is self-contained or is a coupled model at the root of a DEVS models
 * hierarchy and the simulation algorithm is based on the DEVS standard
 * top-down coordination approach. Simulation runs can also be launched by
 * calling <code>startCollaborativeSimulation</code> when the
 * simulation model is a coupled one and the coordination algorithm rather use
 * a peer-to-peer approach like the ones based on using the real time as the
 * simulation time. Simulations end either by reaching their end simulation
 * time or by calling <code>stopSimulation</code>.
 * </p>
 * 
 * <p>
 * The implementation provided here follows the spirit of the description
 * appearing in:
 * </p>
 * <p>
 * H. Vangheluwe, Discrete Event System Specification (DEVS) formalism,
 * courseware, 2001.
 * </p>
 * 
 * <p>
 * Note: the implementation in non-reentrant, hence simulation runs must be
 * executed in sequence.
 * </p>
 * 
 * <p>
 * A baseline DEVS simulator observe a protocol built around the following
 * operations:
 * </p>
 * <ul>
 * <li>internal event step: processing an internal event at a given simulated
 *   time;</li>
 * <li>external event step: processing a given external event at a given
 *   simulated time.</li>
 * </ul>
 * 
 * <p>
 * See <code>AtomicModelI</code> for a description of internal and external
 * steps. A stand alone simulation is run by iterating internal steps until the
 * end of the simulation. A modular concurrent or distributed simulation
 * requires the exchange of external events, the coordination of clock advances
 * to always execute the next event that occurs and the execution of external
 * event steps when required.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true // TODO
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	SimulationEngine
implements	SimulatorI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;

	// Creation time information

	/** model to be executed by the simulator.								*/
	protected ModelI				simulatedModel ;

	// Composition time information

	/** Local coordination engine for this simulator, or null if
	 *  it stands alone or the parent is remote.							*/
	protected ParentNotificationI	parent ;

	// Run time information

	/** true if the simulation has been started and has not ended.			*/
	protected boolean				isRunning ;
	/** time of the beginning of the simulation.							*/
	protected Time					timeOfStart ;
	/** time at which the simulation must end.								*/
	protected Time					simulationEndTime ;
	/** true if the simulation has been stopped (from outside).				*/
	protected boolean				stoppedSimulation ;
	/** time at which the last event has been executed.						*/
	protected Time					timeOfLastEvent ;
	/** time at which the next event, internal, external or confluent,
	 *  must be executed.													*/
	protected Time					timeOfNextEvent ;
	/** Duration until the time of next event, i.e. the time of next
	 *  event minus the time of the current state.							*/
	protected Duration				nextTimeAdvance ;
	/** Debugging level controlling the execution traces.					*/
	protected int					debugLevel ;
	/** sleep time slowing down the simulation steps for demos.		 		*/
	public static long				SIMULATION_STEP_SLEEP_TIME = 0L ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a DEVS simulation engine either standalone or collaborative
	 * and, if collaborative, using a generated external inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	this.isStandAlone()
	 * </pre>
	 *
	 */
	public				SimulationEngine()
	{
		super() ;

		this.initialise() ;
	}

	protected void		initialise()
	{
		this.parent = null ;
		this.isRunning = false ;
		this.simulatedModel = null ;
		this.timeOfStart = null ;
		this.simulationEndTime = null ;
		this.stoppedSimulation = false ;
		this.timeOfLastEvent = null ;
		this.timeOfNextEvent = null ;
		this.nextTimeAdvance = null ;
		this.debugLevel = 0 ;
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getURI() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit() throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getSimulatedTimeUnit() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParentURI()
	 */
	@Override
	public String		getParentURI() throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getParentURI() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]	getImportedEventTypes()
	throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getImportedEventTypes() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		assert	ec != null ;
		assert	this.isModelSet() ;

		return this.simulatedModel.isImportedEventType(ec) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]		getExportedEventTypes()
	throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getExportedEventTypes() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		assert	ec != null ;
		assert	this.isModelSet() ;

		return this.simulatedModel.isExportedEventType(ec) ;
	}

	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	throws Exception
	{
		assert	simulatedModel != null ;
		assert	!this.isModelSet() ;

		this.simulatedModel = simulatedModel ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isModelSet()
	 */
	@Override
	public boolean		isModelSet() throws Exception
	{
		return this.simulatedModel != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isRoot()
	 */
	@Override
	public boolean		isRoot() throws Exception
	{
		return this.getParent() == null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#closed()
	 */
	@Override
	public boolean		closed() throws Exception
	{
		assert	this.isModelSet() ;

		throw new RuntimeException("Method closed not implemented yet!") ;
	}

	@Override
	public void			setParent(ParentReferenceI p)
	throws Exception
	{
		assert	p != null ;
		assert	this.isModelSet() ;
		assert	!this.isParentSet() ;

		this.parent = p.getParentReference() ;
		this.simulatedModel.setParent(p) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return this.parent != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParent()
	 */
	@Override
	public ParentNotificationI	getParent() throws Exception
	{
		return this.parent ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getDescendentModel(java.lang.String)
	 */
	@Override
	public ModelDescriptionI	getDescendentModel(String uri)
	throws Exception
	{
		return this.simulatedModel.getDescendentModel(uri) ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception
	{
		TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit() ;
		assert	simulationDuration != null ;
		assert	simulationDuration.getTimeUnit().equals(tu) ;
		assert	simulationDuration.greaterThan(Duration.zero(tu)) ;
		assert	this.isModelSet() ;
		assert	!this.isSimulationInitialised() ;

		this.initialiseSimulation(Time.zero(tu), simulationDuration) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit();
		assert	startTime != null ;
		assert	startTime.getTimeUnit().equals(tu) ;
		assert	startTime.greaterThanOrEqual(Time.zero(tu)) ;
		assert	simulationDuration != null ;
		assert	simulationDuration.getTimeUnit().equals(tu) ;
		assert	simulationDuration.greaterThan(Duration.zero(tu)) ;
		assert	this.isModelSet() ;
		assert	!this.isSimulationInitialised() ;

		this.stoppedSimulation = false ;
		this.timeOfStart = startTime ;
		this.timeOfLastEvent = this.timeOfStart ;
		this.simulationEndTime = this.timeOfStart.add(simulationDuration) ;
		this.isRunning = true ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		return	this.isModelSet() &&
				this.getTimeOfStart() != null &&
				this.getTimeOfLastEvent() != null &&
				this.getSimulationEndTime() != null &&
				this.isRunning ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent() throws Exception
	{
		return this.timeOfLastEvent ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent() throws Exception
	{
		return this.timeOfNextEvent ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance() throws Exception
	{
		return this.nextTimeAdvance ;
	}

	// -------------------------------------------------------------------------
	// Simulation management methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart() throws Exception
	{
		return this.timeOfStart ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime() throws Exception
	{
		return this.simulationEndTime ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
//		System.out.println("SimulationEngine#doStandAloneSimulation 1") ;
		assert	startTime >= 0.0 ;
		assert	startTime < Double.POSITIVE_INFINITY ;
		assert	simulationDuration > 0.0 ;
		assert	simulationDuration <= Double.POSITIVE_INFINITY ;
//		System.out.println("SimulationEngine#doStandAloneSimulation 2") ;

		TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit();
		this.initialiseSimulation(
					new Time(startTime, tu),
					new Duration(simulationDuration, tu)) ;
//		System.out.println("SimulationEngine#doStandAloneSimulation 3") ;

		this.simulationEndTime =
			this.timeOfStart.add(new Duration(simulationDuration, tu)) ;
		this.stoppedSimulation = false ;
//		System.out.println("SimulationEngine#doStandAloneSimulation 4") ;

		if (this.hasDebugLevel(2)) {
			System.out.println("#######################################") ;
			this.showCurrentState("", Duration.zero(tu)) ;
			System.out.println("#######################################") ;
		}
		if (this.hasDebugLevel(1)) {
			System.out.println("------------------------------------------"
							   + "--------------------------------------") ;
		}
		while (!this.stoppedSimulation &&
				this.timeOfNextEvent != null &&
				this.timeOfNextEvent.lessThanOrEqual(
												this.simulationEndTime)) {
//			System.out.println("SimulationEngine#doStandAloneSimulation 5") ;
			this.produceOutput(this.timeOfNextEvent) ;
//			System.out.println("SimulationEngine#doStandAloneSimulation 6") ;
			this.internalEventStep() ;
//			System.out.println("SimulationEngine#doStandAloneSimulation 7") ;
			if (this.hasDebugLevel(2)) {
				System.out.println("#######################################") ;
				this.showCurrentState("", Duration.zero(tu)) ;
				System.out.println("#######################################") ;
			}
			Thread.sleep(SimulationEngine.SIMULATION_STEP_SLEEP_TIME) ;
			if (this.hasDebugLevel(1)) {
				System.out.println("-----------------------------------------"
								+ "---------------------------------------") ;
			}
		}
//		System.out.println("SimulationEngine#doStandAloneSimulation 8") ;
		this.endSimulation(this.simulationEndTime) ;
	}

	/**
	 * compute the next event to be simulated and update the internal state
	 * accordingly.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>TO DO</i>.
	 */
	protected void		computeNextEventToBeSimulated() throws Exception
	{
		// Default is to do nothing.
		// Concerns coordination engines but this definition avoids to test
		// what type of engines is called.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.isRunning = false ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startCollaborativeSimulation(double, double)
	 */
	@Override
	public void			startCollaborativeSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		assert	startTime >= 0.0 ;
		assert	startTime < Double.POSITIVE_INFINITY ;
		assert	simulationDuration > 0.0 ;
		assert	simulationDuration <= Double.POSITIVE_INFINITY ;

		// TODO: unfinished...
		TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit() ;
		this.timeOfStart = new Time(startTime, tu) ;
		this.simulationEndTime = new Time(simulationDuration, tu) ;
		this.stoppedSimulation = false ;
		this.isRunning = true ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning() throws Exception
	{
		return this.isRunning ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation() throws Exception
	{
		assert	this.isSimulationRunning() ;

		this.stoppedSimulation = true ;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode()
	{
		if (this.debugLevel > 0) {
			this.debugLevel = 0 ;
		} else {
			this.debugLevel = 1 ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn()
	{
		return this.debugLevel > 0 ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel)
	{
		assert	newDebugLevel >= 0 ;

		this.debugLevel = newDebugLevel ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel)
	{
		assert	debugLevel >= 0 ;

		return this.debugLevel >= debugLevel ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		if (this.simulatedModel == null) {
			return name + "(null)" ;
		} else {
			return name + "(" + this.simulatedModel.getURI() + ")" ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent) throws Exception
	{
		if (this.isModelSet()) {
			return this.simulatedModel.modelAsString(indent) ;
		} else {
			return "null" ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		System.out.println(indent + "---------------------------------") ;
		System.out.println(indent + name + " " + this.getURI()) ;
		System.out.println(indent + "---------------------------------") ;
		this.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent + "---------------------------------") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void		showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		System.out.println(indent + "isRunning = " + this.isRunning) ;
		System.out.println(indent + "timeOfStart = " +
								this.timeOfStart.getSimulatedTime()) ;
		System.out.println(indent + "simulationEndTime = " +
								this.simulationEndTime.getSimulatedTime()) ;
		System.out.println(indent + "stoppedSimulation = " +
								this.stoppedSimulation) ;
		System.out.println(indent + "timeOfLastEvent = " +
								this.timeOfLastEvent.getSimulatedTime()) ;
		System.out.println(indent + "elapsedTime = " +
								elapsedTime.getSimulatedDuration()) ;
		System.out.println(indent + "timeOfNextEvent = " +
								this.timeOfNextEvent.getSimulatedTime()) ;
		System.out.println(indent + "nextTimeAdvance = " +
								this.nextTimeAdvance.getSimulatedDuration()) ;

	}
}
// -----------------------------------------------------------------------------
