package fr.sorbonne_u.devs_simulation.models;

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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.MessageLoggingI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>Model</code> is the root of the class hierarchy implementing
 * variants of DEVS simulation models for a component plug-in implementing a
 * family of DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Models have two major distinct but interrelated definitions: a static
 * definition and a dynamic one. The static definition gives an URI to the
 * model and it declares the model imported and exported events. This
 * information is used for model composition prior to simulation runs.
 * The dynamic part attaches a simulation engine to the model (which can
 * be null if the model is enacted by the simulation engine of its parent
 * or an ancestor coupled model). It also includes a simulation time unit
 * (which at this point is assumed to be the same throughout the simulation
 * model architecture) as well as all the data and methods used to execute
 * the simulation.
 * </p>
 * <p>
 * Atomic DEVS simulation models defines the model behaviour in terms of
 * functions, which are translated here in method implementations. These 
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete...
 * 
 * <pre>
 * invariant		all submodels have the same simulation time unit.
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	Model
implements	ModelI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 				serialVersionUID = 1L ;

	// Creation time information

	/** Unique identifier of the model.									*/
	protected final String					uri ;
	/** Array of this model imported event types.					 	*/
	protected Class<? extends EventI>[]		importedEventTypes ;
	/** Array of this model exported event types.					 	*/
	protected Class<? extends EventI>[]		exportedEventTypes ;
	/** The simulation engine enacting this model.						*/
	protected final SimulatorI				simulationEngine ;
	/** Time unit used for the simulation clock.							*/
	protected final TimeUnit				simulatedTimeUnit ;

	// Composition time information

	/** 	The parent (coupled) model or null if none.						*/
	protected ParentNotificationI			parent ;

	// Simulation time information

	/** Object through which logging can be done during simulation runs.	*/
	protected MessageLoggingI				logger ;
	/** Time in the simulation corresponding to this current state.		*/
	protected Time							currentStateTime ;
	/** time at which the next internal event must be executed.			*/
	protected Time							timeOfNextEvent ;
	/** Duration until the time of next event, i.e. the time of next
	 *  event minus the time of the current state.						*/
	protected Duration						nextTimeAdvance ;
	/** Debugging level controlling the execution traces.					*/
	protected int							debugLevel ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model instance with the given URI (if null, one will be
	 * generated) and to be run by the given simulator (or by the one of an
	 * ancestor coupled model if null) using the given time unit for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * post	!this.isDebugModeOn()
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri					URI of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation time of this model.
	 * @param simulationEngine		simulation engine enacting the model (can be the same as the one used by the parent coupled model).
	 * @throws Exception			<i>TODO</i>.
	 */
	public				Model(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super() ;

		assert	simulatedTimeUnit != null ;
		assert	simulationEngine == null || !simulationEngine.isModelSet() ;

		if (uri != null) {
			this.uri = uri ;
		} else {
			this.uri = this.generateModelURI() ;
		}
		this.simulatedTimeUnit = simulatedTimeUnit ;
		this.simulationEngine = simulationEngine ;
		this.debugLevel = 0 ;
		if (this.simulationEngine != null) {
			this.simulationEngine.setSimulatedModel(this) ;
		}

		// Postconditions
		assert	this.getURI() != null ;
		assert	uri == null || this.getURI().equals(uri) ;
		assert	this.getSimulatedTimeUnit().equals(simulatedTimeUnit) ;
		assert	simulationEngine == null ||
					this.getSimulationEngine().equals(simulationEngine) ;
		assert	!this.isDebugModeOn() ;
	}

	// -------------------------------------------------------------------------
	// Redefinition of base object behaviours
	// -------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean		equals(Object obj)
	{
		if (obj instanceof ModelI) {
			return this.uri.equals(((Model)obj).uri) ;
		} else {
			return false ;
		}
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * generate a unique identifier for the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return	a distributed system-wide unique id.
	 */
	protected String	generateModelURI()
	{
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		String ret = java.util.UUID.randomUUID().toString() ;

		assert	ret != null ;

		return this.getClass().getSimpleName() + "-" + ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return this.uri ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit()
	{
		return this.simulatedTimeUnit ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getParentURI()
	 */
	@Override
	public String		getParentURI() throws Exception
	{
		assert	!this.isRoot() ;

		return this.getParent().getURI() ;
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
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isDescendentModel(java.lang.String)
	 */
	@Override
	public boolean		isDescendentModel(String uri) throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri)
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#closed()
	 */
	@Override
	public boolean		closed() throws Exception
	{
		throw new RuntimeException("Method closed not implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedEventTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends EventI>[]		getImportedEventTypes()
	throws Exception
	{
		if (this.importedEventTypes == null) {
			return null ;
		} else {
			Class<? extends EventI>[] ret =
							new Class[this.importedEventTypes.length] ;
			for (int i = 0 ; i < this.importedEventTypes.length ; i++) {
				ret[i] = this.importedEventTypes[i] ;
			}
			return ret ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		boolean ret = false ;
		if (this.importedEventTypes != null) {
			for (int i = 0 ; !ret && i < this.importedEventTypes.length ; i++) {
				ret = (this.importedEventTypes[i].equals(ec)) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedEventTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends EventI>[]		getExportedEventTypes()
	throws Exception
	{
		if (this.exportedEventTypes == null) {
			return null ;
		} else {
			Class<? extends EventI>[] ret =
								new Class[this.exportedEventTypes.length] ;
			for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
				ret[i] = this.exportedEventTypes[i] ;
			}
			return ret ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		boolean ret = false ;
		if (this.exportedEventTypes != null) {
			for (int i = 0 ; !ret && i < this.exportedEventTypes.length ; i++) {
				ret = (this.exportedEventTypes[i].equals(ec)) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return false;
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
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setParent(fr.sorbonne_u.devs_simulation.models.ParentReferenceI)
	 */
	@Override
	public void			setParent(ParentReferenceI p)
	throws Exception
	{
		assert	p != null ;

		this.parent = p.getParentReference() ;

		assert	this.getParent().equals(p.getParentReference()) ;
	}

	// -------------------------------------------------------------------------
	// Model-simulator relationships
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulationEngine()
	 */
	@Override
	public SimulatorI	getSimulationEngine() throws Exception
	{
		if (this.simulationEngine == null) {
			if (this.getParent() != null) {
				// The only case in which a model may not have its own
				// simulation engine is when the model has been composed
				// with other models sharing the same engine and this engine
				// is linked with one of its ancestor coupled model.
				assert	this.getParent() instanceof CoupledModel ;
				return ((CoupledModelI)this.getParent()).
												getSimulationEngine() ;
			} else {
				// In this case, the engine has not been set yet.
				return null ;
			}
		} else {
			return this.simulationEngine ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getCurrentStateTime()
	 */
	@Override
	public Time			getCurrentStateTime()
	{
		return this.currentStateTime ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent()
	{
		return this.timeOfNextEvent ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance()
	{
		return this.nextTimeAdvance ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		if (this.hasDebugLevel(2)) {
			String prefix = "Model#isStateInitialised " + this.uri + " " ;
			this.logMessage(prefix +
							"this.currentStateTime != null " +
											(this.currentStateTime != null)) ;
			this.logMessage(prefix +
					"this.nextTimeAdvance != null " +
											(this.nextTimeAdvance != null)) ;
			this.logMessage(prefix +
					"this.timeOfNextEvent != null " +
											(this.timeOfNextEvent != null)) ;
		}

		return this.currentStateTime != null &&
						this.nextTimeAdvance != null &&
									this.timeOfNextEvent != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState()
	 */
	@Override
	public void			initialiseState()
	{
		this.initialiseState(Time.zero(this.getSimulatedTimeUnit()));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		assert	initialTime != null ;
		assert	initialTime.greaterThanOrEqual(
						Time.zero(this.getSimulatedTimeUnit())) ;

		this.currentStateTime = initialTime.copy() ;
	}

	/**
	 * after a transition in a coupled model, compute the next event
	 * to be simulated and update the state of the model accordingly.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	protected void		computeNextEventToBeSimulated()
	{
		// Default is to do nothing.
		// Concerns coupled models but this definition avoids to test
		// what type of models is called.
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		throw new Exception("The method getFinalReport must be redefined " +
							"by user's models.") ;
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setLogger(fr.sorbonne_u.devs_simulation.interfaces.MessageLoggingI)
	 */
	@Override
	public void			setLogger(MessageLoggingI logger)
	{
		this.logger = logger ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#logMessage(java.lang.String)
	 */
	@Override
	public void			logMessage(String message)
	{
		if (this.logger != null && this.isDebugModeOn()) {
			this.logger.logMessage(this.uri, message) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#toggleDebugMode()
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
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		assert	newDebugLevel >= 0 ;

		this.debugLevel = newDebugLevel ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn()
	{
		return this.debugLevel > 0 ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel)
	{
		assert	debugLevel >= 0 ;

		return this.debugLevel == debugLevel ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	throws Exception
	{
		try {
			String ret =
				indent + "parent uri = " + (this.isRoot() ? "null"
										: this.getParentURI()) + " \n" ;
			ret += indent + "time unit  = " + this.getSimulatedTimeUnit() + " \n";
			if (this.importedEventTypes == null) {
				ret += indent + "imported   = []\n" ;
			} else {
				ret += indent + "imported   = [" ;
				for (int i = 0 ; i < this.importedEventTypes.length ; i++) {
					ret += this.importedEventTypes[i].getName() ;
					if (i < this.importedEventTypes.length - 1) {
						ret += ", " ;
					}
				}
				ret += "]\n" ;
			}
			if (this.exportedEventTypes == null) {
				ret += indent + "exported   = []\n" ;
			} else {
				ret += indent + "exported   = [" ;
				for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
					ret += this.exportedEventTypes[i].getName() ;
					if (i < this.exportedEventTypes.length - 1) {
						ret += ", " ;
					}
				}
				ret += "]\n" ;
			}
			ret += indent + "simulation engine = " ;
			if (this.simulationEngine != null) {
				ret += this.getSimulationEngine().simulatorAsString() + "\n" ;
			} else {
				ret += "null\n" ;
			}
			ret += indent + "debugging level = " + this.debugLevel + "\n" ;
			return ret ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		if (this.simulationEngine != null) {
			return this.simulationEngine.simulatorAsString() ;
		} else {
			return "null" ;
		}
	}
}
// -----------------------------------------------------------------------------
