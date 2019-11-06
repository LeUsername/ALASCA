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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.AtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventConverterI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;


// -----------------------------------------------------------------------------
/**
 * The class <code>CoordinationEngine</code> defines the basic behaviours
 * of a DEVS coordinator declared by the interface
 * <code>CoordinationI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A DEVS coordination engine is first linked to a DEVS simulation model through
 * the method <code>setSimulatedModel</code>. It then can do simulations by
 * calling <code>doStandAloneSimulation</code> or
 * <code>startCollaborativeSimulation</code>. Simulations end
 * either by reaching their end simulation time or by calling
 * <code>stopSimulation</code>.
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
 * Note: the implementation in non-reentrant, hence simulation runs using
 * this one instantiation of a composed model must be executed in sequence.
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
 * invariant		true		// TODO
 * </pre>
 * 
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				CoordinationEngine
extends		SimulationEngine
implements	CoordinatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	// Composition time information

	private static final long		serialVersionUID = 1L ;
	/** Simulation engines coordinated by this coordination engine.			*/
	protected SimulatorI[]			coordinatedEngines ;
	/** Map from URI of the models enacted by coordinated engines to the
	 *  index of the engine in the array <code>coordinatedEngines</code>. 	*/
	protected Map<String,Integer>	coordinatedURI2index ;
	/** HIOA simulation engines coordinated by this coordination engine
	 *  sorted by their variable production/consumption relationships.		*/
	protected SimulatorI[]			sortedEngines ;
	/** Other simulation engines coordinated by this coordination engine.	*/
	protected SimulatorI[]			unsortedEngines ;
	/** URIs of the sorted coordinated engines.								*/
	protected List<String>			sortedEngineURIs ;
	/** URIs of the unsorted coordinated engines.							*/
	protected Set<String>			unsortedEngineURIs ;

	// Run time information

	/** Elapsed times of subengines since their last internal event (i.e.,
	 *  <code>currentStateTime</code>; conceptually, the time of the
	 *  last internal event plus the elapsed time corresponds to the
	 *  current global simulation time.										*/
	protected Duration[]			elapsedTimes ;
	/** The URI of the submodel that will need to execute the next event.	*/
	protected String				submodelOfNextEventURI ;
	/** Set of URIs of submodels that have external events waiting
	 * 	executed.															*/
	protected Set<String>			activeModelURIs ;
	/** Random number generator used to break the ties when several
	 *  submodels can perform an internal transition at the same time of
	 *  next internal transition simulation time. 							*/
	protected final Random			randomGenerator ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a coordination engine waiting for static initialisation by
	 * setting its associated coupled model and its set of coordinated
	 * engines.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public 				CoordinationEngine()
	{
		super() ;

		// Coupled model initialisation
		this.randomGenerator = new Random(System.currentTimeMillis()) ;
		this.coordinatedEngines = null ;
	}

	// -------------------------------------------------------------------------
	// Static information related methods
	// -------------------------------------------------------------------------

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedModel instanceof CoupledModelI
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	throws Exception
	{
		assert	simulatedModel instanceof CoupledModelI ;

		super.setSimulatedModel(simulatedModel);
	}

	// -------------------------------------------------------------------------
	// Composition related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#setCoordinatedEngines(fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI[])
	 */
	@Override
	public void			setCoordinatedEngines(
		SimulatorI[] coordinatedEngines
		) throws Exception
	{
		assert	!this.coordinatedEnginesSet() ;
		assert	coordinatedEngines != null && coordinatedEngines.length > 1 ;
		assert	this.isModelSet() ;
		for (int i = 0 ; i < coordinatedEngines.length ; i++) {
			assert	coordinatedEngines[i] != null ;
			String modelURI = coordinatedEngines[i].getURI() ;
			assert	((CoupledModelI)this.simulatedModel).
												isSubmodel(modelURI) ;
		}

		this.coordinatedEngines = new SimulatorI[coordinatedEngines.length] ;
		this.coordinatedURI2index = new HashMap<String,Integer>() ;
		for (int i = 0 ; i < coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i] = coordinatedEngines[i] ;
			this.coordinatedURI2index.put(coordinatedEngines[i].getURI(), i) ;
		}

		this.sortedEngineURIs =
				((CoupledModelI)this.simulatedModel).getSortedSubmodelURIs() ;
		this.sortedEngines =
					new SimulatorI[this.sortedEngineURIs.size()] ;
		for (int i = 0 ; i < this.sortedEngineURIs.size() ; i++) {
			this.sortedEngines[i] =
				this.coordinatedEngines[
					this.coordinatedURI2index.get(
								this.sortedEngineURIs.get(i))] ;
		}
		this.unsortedEngineURIs =
				((CoupledModelI)this.simulatedModel).getUnsortedSubmodelURIs() ;
		this.unsortedEngines = new SimulatorI[this.unsortedEngineURIs.size()] ;
		int k = 0 ;
		for (String s : this.unsortedEngineURIs) {
			this.unsortedEngines[k++] =
				this.coordinatedEngines[this.coordinatedURI2index.get(s)] ;
		}

		assert	this.coordinatedEnginesSet() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#coordinatedEnginesSet()
	 */
	@Override
	public boolean		coordinatedEnginesSet() throws Exception
	{
		return this.coordinatedEngines != null ;
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isDescendentModel(java.lang.String)
	 */
	@Override
	public boolean		isDescendentModel(String uri) throws Exception
	{
		boolean found = false ;
		for (int i = 0 ; !found && i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getURI().equals(uri)) {
				found = true ;
			}
		}
		for (int i = 0 ; !found && i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].isDescendentModel(uri)) {
				found = true ;
			}
		}
		return found ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri) throws Exception
	{
		EventsExchangingI ret = null ;
		for (int i = 0 ; ret == null && i < this.coordinatedEngines.length ;
																		i++) {
			try {
				if (this.coordinatedEngines[i].getURI().equals(uri)) {
					ret = this.coordinatedEngines[i] ;
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
		for (int i = 0 ; ret == null && i < this.coordinatedEngines.length ;
																		i++) {
			ret = this.coordinatedEngines[i].getEventExchangingDescendentModel(uri) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	ce != null && this.simulatedModel.isExportedEventType(ce) ;

		ReexportedEvent re =
				((CoupledModelI)this.simulatedModel).getReexportedEvent(ce) ;
		assert	re.sinkEventType.isAssignableFrom(ce) ;
		int index = this.coordinatedURI2index.get(re.exportingModelURI) ;
		EventAtomicSource source =
			this.coordinatedEngines[index].
								getEventAtomicSource(re.sourceEventType) ;
		return new EventAtomicSource(
					source.exportingModelURI,
					source.sourceEventType,
					re.sinkEventType,
					EventConverterI.compose(re.converter, source.converter)
					) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	ce != null && this.simulatedModel.isImportedEventType(ce) ;

		Set<CallableEventAtomicSink> ret = new HashSet<>() ;
		Set<EventSink> submodelSinks =
					((CoupledModelI)this.simulatedModel).getEventSinks(ce) ;
		for (EventSink es : submodelSinks) {
			assert	ce.equals(es.sourceEventType) ;
			assert	((CoupledModelI)this.simulatedModel).
										isSubmodel(es.importingModelURI) ;
			int index =
				this.coordinatedURI2index.get(es.importingModelURI) ;
			Set<CallableEventAtomicSink> submodelAtomicSinks =
				this.coordinatedEngines[index].
									getEventAtomicSinks(es.sinkEventType) ;
			for (CallableEventAtomicSink as : submodelAtomicSinks) {
				assert	es.sinkEventType.equals(as.sourceEventType) ;
				CallableEventAtomicSink sink =
					new CallableEventAtomicSink(
							as.importingModelURI,
							ce,
							as.sinkEventType,
							as.importingAtomicModelReference,
							EventConverterI.compose(as.converter,
												    es.converter)) ;
				ret.add(sink) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#findProxyAtomicEngineURI(java.lang.String)
	 */
	@Override
	public String		findProxyAtomicEngineURI(String modelURI)
	throws Exception
	{
		assert	modelURI != null ;

		if (!this.isDescendentModel(modelURI)) {
			return null;
		} else {
			String ret = null ;
			for (int i = 0 ; ret == null &&
								i < this.coordinatedEngines.length ; i++) {
				ret = this.coordinatedEngines[i].
										findProxyAtomicEngineURI(modelURI) ;
			}
			return ret ;
		}
	}

	@Override
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		) throws Exception
	{
		assert	atomicEngineURI != null ;

		AbstractAtomicSinkReference ret = null ;
		for (int i = 0 ; ret == null &&
								i < this.coordinatedEngines.length ; i++) {
			ret = this.coordinatedEngines[i].
								getAtomicEngineReference(atomicEngineURI) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
					"CoordinationEngine>>addInfluencees " + this.getURI()) ;
		}

		assert	modelURI != null ;
		assert	this.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	influencees != null && influencees.size() != 0 ;
		assert	this.isModelSet() ;

		boolean found = false ;
		for (int i = 0 ; !found && i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getURI().equals(modelURI) ||
					this.coordinatedEngines[i].isDescendentModel(modelURI)) {
				this.coordinatedEngines[i].
								addInfluencees(modelURI, ce, influencees) ;
				found = true ;
			}
		}
		assert	found ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	modelURI != null ;
		assert	this.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	this.isModelSet() ;

		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getURI().equals(modelURI) ||
					this.coordinatedEngines[i].isDescendentModel(modelURI)) {
				return this.coordinatedEngines[i].getInfluencees(modelURI, ce) ;
			}
		}
		return null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> modelURIs,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	modelURI != null ;
		assert	this.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	modelURIs != null && !modelURIs.isEmpty() ;
		assert	this.isModelSet() ;

		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getURI().equals(modelURI) ||
					this.coordinatedEngines[i].isDescendentModel(modelURI)) {
				return this.coordinatedEngines[i].
							areInfluencedThrough(modelURI, modelURIs, ce) ;
			}
		}
		return false ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	modelURI != null ;
		assert	this.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	modelURI != null ;
		assert	this.isModelSet() ;

		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getURI().equals(modelURI) ||
					this.coordinatedEngines[i].isDescendentModel(modelURI)) {
				return this.coordinatedEngines[i].
							isInfluencedThrough(modelURI,
												destinationModelURI,
												ce) ;
			}
		}
		return false ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return this.simulatedModel.isTIOA() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return this.simulatedModel.isOrdered() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(
		String name,
		Class<?> type
		) throws Exception
	{
		return this.simulatedModel.isExportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(
		String name,
		Class<?> type
		) throws Exception
	{
		return this.simulatedModel.isImportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables() throws Exception
	{
		return this.simulatedModel.getImportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables() throws Exception
	{
		return this.simulatedModel.getExportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception
	{
		return this.simulatedModel.
					getActualExportedVariableValueReference(
							modelURI, sourceVariableName, sourceVariableType) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception
	{
		this.simulatedModel.
				setImportedVariableValueReference(
						modelURI, sinkVariableName, sinkVariableType, value) ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#readyForSimulation()
	 */
	@Override
	public boolean		readyForSimulation()
	{
		// TODO Auto-generated method stub
		return true ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		super.initialiseSimulation(startTime, simulationDuration) ;

		for(int i = 0 ; i < this.sortedEngines.length ; i++) {
			this.sortedEngines[i].
						initialiseSimulation(startTime, simulationDuration) ;
		}
		for (int i = 0 ; i < this.unsortedEngines.length ; i++) {
			this.unsortedEngines[i].
						initialiseSimulation(startTime, simulationDuration) ;
		}
		this.elapsedTimes = new Duration[this.coordinatedEngines.length] ;
		TimeUnit tu =  this.simulatedModel.getSimulatedTimeUnit() ;
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
				this.elapsedTimes[i] = Duration.zero(tu) ;
		}

		this.activeModelURIs = new HashSet<String>() ;
		this.timeOfNextEvent =
						this.coordinatedEngines[0].getTimeOfNextEvent() ;
		this.submodelOfNextEventURI = this.coordinatedEngines[0].getURI() ;
		for (int i = 1 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getTimeOfNextEvent().
											lessThan(this.timeOfNextEvent)) {
				this.timeOfNextEvent =
							this.coordinatedEngines[i].getTimeOfNextEvent() ;
				this.submodelOfNextEventURI =
							this.coordinatedEngines[i].getURI() ;
			}
		}
		this.nextTimeAdvance =
						this.timeOfNextEvent.subtract(this.timeOfLastEvent) ;

		// Postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	this.coordinatedEngines[i].getTimeOfLastEvent().
												add(this.elapsedTimes[i]).
									equals(this.getTimeOfLastEvent()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getTimeOfLastEvent()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		boolean ret = super.isSimulationInitialised() ;
		ret &= this.elapsedTimes != null ;
		for (int i = 0 ; ret && i < this.coordinatedEngines.length ; i++) {
			ret &= this.coordinatedEngines[i].isSimulationInitialised() ;
			ret &= this.elapsedTimes[i] != null ;
		}
		ret &= this.submodelOfNextEventURI != null ;
		ret &= this.activeModelURIs != null ;
		ret &= this.getTimeOfNextEvent() != null ;
		ret &= this.getNextTimeAdvance() != null ;
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep() throws Exception
	{
		if (this.hasDebugLevel(1)) {
//			System.out.println(
//				"CoordinationEngine#internalEventStep "
//							+ this.getURI() + " "
//							+ this.submodelOfNextEventURI + " "
//							+ this.getTimeOfNextEvent()) ;
			this.simulatedModel.logMessage(
				"CoordinationEngine#internalEventStep "
							+ this.getURI() + " "
							+ this.submodelOfNextEventURI + " "
							+ this.getTimeOfNextEvent()) ;
		}

		assert	this.getNextTimeAdvance().equals(
								this.getTimeOfNextEvent().subtract(
												this.getTimeOfLastEvent())) ;

		TimeUnit tu = this.getSimulatedTimeUnit() ;
		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getTimeOfLastEvent()) ;
		this.timeOfLastEvent = this.getTimeOfNextEvent() ;
		Set<String> updated = new HashSet<String>() ;
		boolean hasHIOAActive = false ;
		if (this.sortedEngineURIs.contains(this.submodelOfNextEventURI)) {
			for (int i = 0 ; i < this.sortedEngines.length ; i++) {
				if (this.sortedEngineURIs.get(i).equals(
											this.submodelOfNextEventURI)) {
					this.sortedEngines[i].internalEventStep() ;
				} else {
					this.sortedEngines[i].
									causalEventStep(this.timeOfLastEvent) ;
				}
				if (this.sortedEngines[i].getTimeOfLastEvent().
											equals(this.timeOfLastEvent)) {
					int index = this.coordinatedURI2index.get(
											this.sortedEngineURIs.get(i)) ;
					this.elapsedTimes[index] = Duration.zero(tu) ;
					updated.add(this.sortedEngines[i].getURI()) ;
				}
			}
		} else {
			int index =
				this.coordinatedURI2index.get(this.submodelOfNextEventURI) ;
			this.coordinatedEngines[index].internalEventStep() ;
			this.elapsedTimes[index] = Duration.zero(tu) ;
			updated.add(this.submodelOfNextEventURI) ;
		}
		if (this.activeModelURIs.size() > 0) {
			if (!this.isRoot()) {
				this.getParent().hasPerformedExternalEvents(this.getURI()) ;
			}
			for (String uri : this.activeModelURIs) {
				if (this.sortedEngineURIs.contains(uri)
											&& !updated.contains(uri)) {
					hasHIOAActive = true ;
					break ;
				}
			}
			if (hasHIOAActive) {
				for (int i = 0 ; i < this.sortedEngines.length ; i++) {
					String uri = this.sortedEngineURIs.get(i) ;
					if (!updated.contains(uri)) {
						this.sortedEngines[i].causalEventStep(
													this.timeOfLastEvent) ;
						if (this.sortedEngines[i].getTimeOfLastEvent().
											equals(this.timeOfLastEvent)) {
							int index = this.coordinatedURI2index.get(uri) ;
							this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
							updated.add(uri) ;
						}
					}
				}
			}
			for (String uri : this.activeModelURIs) {
				int i = this.coordinatedURI2index.get(uri) ;
				this.coordinatedEngines[i].externalEventStep(
								(updated.contains(uri) ?
									Duration.zero(tu)
								:	this.elapsedTimes[i].add(elapsedTime)
								)) ;
				this.elapsedTimes[i] = Duration.zero(tu) ;
				updated.add(uri) ;
			}
		}
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			String uri = this.coordinatedEngines[i].getURI() ;
			if (!updated.contains(uri)) {
				this.elapsedTimes[i] = this.elapsedTimes[i].add(elapsedTime) ;
			}
		}
		this.activeModelURIs.clear() ;
		this.computeNextEventToBeSimulated() ;

		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
					"CoordinationEngine#internalEventStep 2 "
						+ this.getURI() + " "
						+ this.getTimeOfLastEvent() + " "
						+ this.getTimeOfNextEvent()) ;
		}

		// Postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	((SimulatorI)this.coordinatedEngines[i]).
							getTimeOfLastEvent().add(this.elapsedTimes[i]).
						equals(this.getTimeOfLastEvent()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getTimeOfLastEvent()).
						equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception
	{
		if (this.hasDebugLevel(2)) {
//			System.out.println("CoordinationEngine#hasReceivedExternalEvents "
//					+ this.getURI() + " from " + modelURI) ;
			this.simulatedModel.logMessage(
					"CoordinationEngine#hasReceivedExternalEvents "
					+ this.getURI() + " from " + modelURI) ;
		}

		this.activeModelURIs.add(modelURI) ;
		if (this.isRoot()) {
			// Do nothing
		} else {
			this.getParent().hasReceivedExternalEvents(this.getURI()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	throws Exception
	{
		assert	this.coordinatedURI2index.keySet().contains(modelURI) ;
		assert	this.activeModelURIs.contains(modelURI) ;

		this.activeModelURIs.remove(modelURI) ;
		if (!this.isRoot() && this.activeModelURIs.isEmpty()) {
			this.getParent().hasPerformedExternalEvents(this.getURI()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current) throws Exception
	{
		int index =
			this.coordinatedURI2index.get(this.submodelOfNextEventURI) ;
		this.coordinatedEngines[index].produceOutput(current) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI#storeInput(java.lang.String, java.util.Vector)
	 */
	@Override
	public void			storeInput(
		String destinationURI,
		Vector<EventI> es
		) throws Exception
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
						"CoordinationEngine>>storeInput " + this.getURI()) ;
		}

		assert	this.isDescendentModel(destinationURI) ;

		AbstractAtomicSinkReference aref =
						this.getAtomicEngineReference(destinationURI) ;
		assert	aref.isDirect() ;
		((AtomicSinkReference)aref).ref.storeInput(destinationURI, es) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#computeNextEventToBeSimulated()
	 */
	@Override
	protected void		computeNextEventToBeSimulated() throws Exception
	{
		Duration ta = this.coordinatedEngines[0].getNextTimeAdvance().
											subtract(this.elapsedTimes[0]) ;
		Vector<Integer> currentSet = new Vector<Integer>() ;
		currentSet.add(0) ;
		for (int i = 1 ; i < this.coordinatedEngines.length ; i++) {
			Duration tmp =
				this.coordinatedEngines[i].getNextTimeAdvance().
											subtract(this.elapsedTimes[i]) ;
			if (tmp.equals(ta)) {
				currentSet.add(i) ;
			} else if (tmp.lessThan(ta)) {
				currentSet.clear() ;
				ta = tmp ;
				currentSet.add(i) ;
			}
		}
		this.nextTimeAdvance = ta ;
		this.submodelOfNextEventURI = null ;
		if (ta.lessThan(Duration.INFINITY)) {
			if (currentSet.size() > 1) {
				String[] candidates = new String[currentSet.size()] ;
				for (int i = 0 ; i < currentSet.size() ; i++) {
					candidates[i] =
						this.coordinatedEngines[currentSet.get(i)].getURI() ;
				}
				this.submodelOfNextEventURI =
					((CoupledModelI)this.simulatedModel).select(candidates) ;
			} else {
				this.submodelOfNextEventURI =
						this.coordinatedEngines[currentSet.get(0)].getURI() ;
			}
			this.timeOfNextEvent = this.timeOfLastEvent.add(ta) ;
		} else {
			this.timeOfNextEvent = Time.INFINITY ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#causalEventStep(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalEventStep(Time current) throws Exception
	{
		if (this.hasDebugLevel(1)) {
//			System.out.println(
//				"CoordinationEngine#causalEventStep "
//						+ this.getURI() + " "
//						+ current + " "
//						+ this.getTimeOfNextEvent()) ;
			this.simulatedModel.logMessage(
					"CoordinationEngine#causalEventStep "
							+ this.getURI() + " "
							+ current + " "
							+ this.getTimeOfNextEvent()) ;
		}

		Duration elapsedTime = current.subtract(this.getTimeOfLastEvent()) ;
		this.timeOfLastEvent = current ;
		Set<String> updated = new HashSet<String>() ;
		for (int i = 0 ; i < this.sortedEngines.length ; i++) {
			this.sortedEngines[i].causalEventStep(current) ;
			if (this.sortedEngines[i].getTimeOfLastEvent().equals(current)) {
				int index = this.coordinatedURI2index.get(
											this.sortedEngineURIs.get(i)) ;
				this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
				updated.add(this.sortedEngineURIs.get(i)) ;
			}
		}
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			if (!updated.contains(this.coordinatedEngines[i].getURI())) {
				this.elapsedTimes[i] = this.elapsedTimes[i].add(elapsedTime) ;
			}
		}
		this.activeModelURIs.clear(); 
		this.computeNextEventToBeSimulated() ;

		// Postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	((SimulatorI)this.coordinatedEngines[i]).
							getTimeOfLastEvent().add(this.elapsedTimes[i]).
						equals(this.getTimeOfLastEvent()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getTimeOfLastEvent()).
						equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		if (this.hasDebugLevel(1)) {
//			System.out.println(
//				"CoordinationEngine>>externalEventStep " + this.getURI()) ;
			this.simulatedModel.logMessage(
				"CoordinationEngine>>externalEventStep " + this.getURI()) ;
		}

		// If a coordination engine is called to execute external events but
		// its list of active models is empty, it means that it is the
		// coordination engine that just run its internal step so the external
		// transitions of its submodels have been executed as part of it.

		Set<String> updated = new HashSet<String>() ;
		if (!this.activeModelURIs.isEmpty()) {
			this.timeOfLastEvent = this.timeOfLastEvent.add(elapsedTime) ;
			boolean hasHIOAActive = false ;
			Set<String> activeSortedURIs = new HashSet<String>() ;
			for (String s : this.activeModelURIs) {
				if (this.sortedEngineURIs.contains(s)) {
					hasHIOAActive =  true ;
					activeSortedURIs.add(s) ;
				}
			}
			if (this.hasDebugLevel(2)) {
				this.simulatedModel.logMessage(
						"CoordinationEngine>>externalEventStep 2 "
								+ this.getURI() + " " + hasHIOAActive) ;
			}
			if (hasHIOAActive) {
				for (int i = 0 ; i < this.sortedEngines.length ; i++) {
					this.sortedEngines[i].causalEventStep(
													this.timeOfLastEvent) ;
					if (this.sortedEngines[i].getTimeOfLastEvent().
											equals(this.timeOfLastEvent)) {
						String uri = this.sortedEngineURIs.get(i) ;
						int index = this.coordinatedURI2index.get(uri) ;
						this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
						updated.add(uri) ;
					}
				}
			}
			for (String uri : this.activeModelURIs) {
				int index = this.coordinatedURI2index.get(uri) ;
				this.coordinatedEngines[index].externalEventStep(
							(updated.contains(uri) ?
								Duration.zero(this.getSimulatedTimeUnit())
							:	this.elapsedTimes[index].add(elapsedTime)
							)) ;
				this.elapsedTimes[index] =
							Duration.zero(this.getSimulatedTimeUnit()) ;
				updated.add(uri) ;
			}
			for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
				if (!updated.contains(this.coordinatedEngines[i].getURI())) {
					this.elapsedTimes[i] =
									this.elapsedTimes[i].add(elapsedTime) ;
				}
			}
			this.activeModelURIs.clear() ;
			this.computeNextEventToBeSimulated() ;
		} else {
			// This happens when a submodel had to perform an external
			// event, notified its ancestor models which call for an
			// an external transition that was already performed by their
			// parent model.
			if (this.hasDebugLevel(2)) {
				this.simulatedModel.logMessage(
						"CoupledModel>>externalTransition " +
							this.getURI() + " has no active submodel.") ;
			}
		}

		// Postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	((SimulatorI)this.coordinatedEngines[i]).
							getTimeOfLastEvent().add(this.elapsedTimes[i]).
						equals(this.getTimeOfLastEvent()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getTimeOfLastEvent()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#confluentEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentEventStep(Duration elapsedTime) throws Exception
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(
					"CoordinationEngine>>confluentEventStep() not "
													+ "implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].endSimulation(endTime) ;
		}
		super.endSimulation(endTime) ;
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].setSimulationRunParameters(simParams) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		StandardCoupledModelReport report =
								new StandardCoupledModelReport(this.getURI()) ;
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			report.addReport(this.coordinatedEngines[i].getFinalReport()) ;
		}
		return report ;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		String ret = name + "[" ;
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			ret += this.coordinatedEngines[i].simulatorAsString() ;
			if (i < this.coordinatedEngines.length - 1) {
				ret += ", " ;
			}
		}
		return ret + "]" ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
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
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].
				showCurrentState(indent + "    ", this.elapsedTimes[i]) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void		showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		super.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent + "submodel of next event = " +
											this.submodelOfNextEventURI) ;
	}
}
// -----------------------------------------------------------------------------
