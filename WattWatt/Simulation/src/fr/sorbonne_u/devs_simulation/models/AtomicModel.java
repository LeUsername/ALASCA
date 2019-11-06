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

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.AtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicModel</code> implements the most general methods and
 * instance variables for DEVS atomic models.
 *
 * <p><strong>Description</strong></p>
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
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true			// TODO
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AtomicModel
extends		Model
implements	AtomicModelI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L ;

	// Composition time information.

	/** When not stand alone but run by a shared simulation engine, the
	 *  set of other models that must receive events from this model.	 	*/
	protected Map<Class<? extends EventI>,
				  Set<CallableEventAtomicSink>>		influencees ;
	
	// Simulation time information.

	/** Input events from the current simulation step waiting to be executed
	 *  by this model.														*/
	protected Vector<EventI>						currentStoredEvents ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic simulation model with the given URI (if null, one will
	 * be generated) and to be run by the given simulator (or by the one of an
	 * ancestor coupled model if null) using the given time unit for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine != null implies
	 * 			simulationEngine instanceof AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				AtomicModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		assert	simulationEngine == null ||
							simulationEngine instanceof AtomicEngine ;

		this.importedEventTypes =
				AtomicModel.getImportedEventTypes(this.getClass()) ;
		this.exportedEventTypes =
				AtomicModel.getExportedEventTypes(this.getClass()) ;
		this.influencees = new HashMap<Class<? extends EventI>,
									  Set<CallableEventAtomicSink>>() ;

	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getDescendentModel(java.lang.String)
	 */
	@Override
	public ModelI		getDescendentModel(String uri) throws Exception
	{
		if (this.uri.equals(uri)) {
			return this ;
		} else {
			return null ;
		}
	}

	/**
	 * return the imported event types of this atomic model from the
	 * corresponding annotations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c	the class defining the model.
	 * @return	the imported event types of this atomic model from the corresponding annotations.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends EventI>[] getImportedEventTypes(
		Class<? extends AtomicModel> c
		)
	{
		Vector<Class<? extends EventI>> temp =
							new Vector<Class<? extends EventI>>() ;
		// TODO: add @Inherited to the annotations to avoid this traversal.
		Class<? extends AtomicModel> current = c ;
		while (!current.equals(AtomicModel.class)) {
			ModelExternalEvents a =
				current.getAnnotation(ModelExternalEvents.class) ;
			if (a != null) {
				for (int i = 0 ; i < a.imported().length ; i++) {
					temp.add(a.imported()[i]) ;
				}
			}
			current = (Class<? extends AtomicModel>) current.getSuperclass() ;
		}

		return (Class<? extends EventI>[]) temp.toArray(new Class<?>[] {}) ;
	}

	/**
	 * return the exported event types of this atomic model from the
	 * corresponding annotations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c	the class defining the model.
	 * @return	the exported event types of this atomic model from the corresponding annotations.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends EventI>[] getExportedEventTypes(
		Class<? extends AtomicModel> c
		)
	{
		Vector<Class<? extends EventI>> temp =
							new Vector<Class<? extends EventI>>() ;
		Class<? extends AtomicModel> current = c ;
		while (!current.equals(AtomicModel.class)) {
			ModelExternalEvents a =
				current.getAnnotation(ModelExternalEvents.class) ;
			if (a != null) {
				for (int i = 0 ; i < a.exported().length ; i++) {
					temp.add(a.exported()[i]) ;
				}
			}
			current = (Class<? extends AtomicModel>) current.getSuperclass() ;
		}

		return (Class<? extends EventI>[]) temp.toArray(new Class<?>[] {}) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	this.isExportedEventType(ce) ;

		return new EventAtomicSource(this.getURI(), ce, ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		Set<CallableEventAtomicSink> ret = new HashSet<>() ;
		if (this.isImportedEventType(ce)) {
			CallableEventAtomicSink es =
				new CallableEventAtomicSink(
						this.getURI(), ce, ce, new AtomicSinkReference(this)) ;
			ret.add(es) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void				addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#addInfluencees " +
										this.uri + " " + ce.getName()) ;
		}

		assert	modelURI != null ;
		assert	this.uri.equals(modelURI) ;
		assert	ce != null ;
		assert	influencees != null && influencees.size() != 0 ;

		if (this.influencees.containsKey(ce)) {
			this.influencees.get(ce).addAll(influencees) ;
		} else {
			Set<CallableEventAtomicSink> s =
									new HashSet<CallableEventAtomicSink>() ;
			s.addAll(influencees) ;
			this.influencees.put(ce, s) ;			
		}
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
		assert	this.uri.equals(modelURI) ;

		try {
			assert	ce != null && this.isExportedEventType(ce) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		return this.influencees.get(ce) ;
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
		assert	this.uri.equals(modelURI) ;
		assert	ce != null ;
		assert	modelURIs != null && !modelURIs.isEmpty() ;

		if (this.influencees.containsKey(ce)) {
			boolean ret = true ;
			for (String m : modelURIs) {
				ret &= this.isInfluencedThrough(modelURI, m, ce) ;
			}
			return ret ;
		} else {
			return false ;
		}
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
		assert	this.uri.equals(modelURI) ;
		assert	ce != null ;
		assert	destinationModelURI != null ;

		if (this.influencees.containsKey(ce)) {
			boolean ret = false ;
			for (EventSink es : this.influencees.get(ce)) {
				ret = ret || (destinationModelURI.
										equals(es.importingModelURI)) ;
			}
			return ret ;
		} else {
			return false ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(String name, Class<?> type)
	throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(String name, Class<?> type)
	throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		throw new Exception("Standard atomic DEVS models do not have "
				+ "model variables.") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
	{
		throw new Exception("Standard atomic DEVS models do not have "
				+ "model variables.") ;
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
		throw new Exception("Standard atomic DEVS models do not have "
				+ "model variables.") ;
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
		throw new Exception("Standard atomic DEVS models do not have "
												+ "model variables.") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.VariablesSharingI#staticInitialiseVariables()
	 */
	@Override
	public void			staticInitialiseVariables()
	{
		// Concerns model with variables, so by default do nothing.
	}

	// ------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#isStateInitialised "
					+ "this.currentStoredEvents != null "
							+ (this.currentStoredEvents != null)) ;
		}

		return super.isStateInitialised() &&
									this.currentStoredEvents != null  ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;

		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent = this.currentStateTime.add(this.nextTimeAdvance) ;
		this.currentStoredEvents = new Vector<EventI>() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#clockSynchronised()
	 */
	@Override
	public boolean		clockSynchronised()
	{
		assert	this.isStateInitialised() ;

		// TODO
		return true ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		assert	this.getNextTimeAdvance().lessThan(Duration.INFINITY) ;
		assert	this.getNextTimeAdvance().equals(
					this.getTimeOfNextEvent().subtract(
											this.getCurrentStateTime())) ;

		if (this.hasDebugLevel(2)) {
//			System.out.println(
//						"AtomicModel>>internalTransition " + this.uri) ;
			this.logMessage("AtomicModel>>internalTransition " + this.uri) ;
		}

		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getCurrentStateTime()) ;
		this.currentStateTime = this.getTimeOfNextEvent() ;
		assert	this.getCurrentStateTime().equals(this.getTimeOfNextEvent()) ;
		this.userDefinedInternalTransition(elapsedTime) ;
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
						this.currentStateTime.add(this.nextTimeAdvance) ;

		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		assert	elapsedTime.lessThan(Duration.INFINITY) ;
		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#causalTransition(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalTransition(Time current)
	{
		if (this.hasDebugLevel(2)) {
//			System.out.println("AtomicModel#causalTransition " + this.uri) ;
			this.logMessage("AtomicModel#causalTransition " + this.uri) ;
		}
		// By default, do nothing.
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime)
	 * pre	this.getCurrentStateTime().add(elapsedTime).
	 *                              lessThanOrEqual(this.getTimeOfNextEvent())
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		assert	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime) ;
		assert	this.getCurrentStateTime().add(elapsedTime).
								lessThanOrEqual(this.getTimeOfNextEvent()) ;

		if (this.hasDebugLevel(2)) {
//			System.out.println(
//				"AtomicModel>>externalTransition " + this.uri) ;
			this.logMessage(
						"AtomicModel>>externalTransition " + this.uri) ;
		}

		this.currentStateTime = this.currentStateTime.add(elapsedTime) ;
//		if (this.currentStoredEvents.size() > 1) {
//			// TODO not implemented yet!
//			this.confluentTransition(elapsedTime) ;
//		} else {
			this.userDefinedExternalTransition(elapsedTime) ;
//		}
		if (!this.currentStoredEvents.isEmpty()) {
			this.currentStoredEvents.clear() ;
		}
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
						this.currentStateTime.add(this.nextTimeAdvance) ;

		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * The class <code>Destination</code> gathers information
	 * about the destination for the exchange of external events.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-06</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	protected static class	Destination
	{
		public final String			destinationURI ;
		public EventsExchangingI		destination ;

		public				Destination(
			String destinationURI,
			EventsExchangingI destination
			)
		{
			super();
			this.destinationURI = destinationURI ;
			this.destination = destination;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean		equals(Object obj)
		{
			if (obj instanceof Destination) {
				return this.destinationURI.equals(
										((Destination)obj).destinationURI) ;
			} else {
				return false ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		assert	this.currentStateTime.lessThanOrEqual(this.timeOfNextEvent) ;

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		assert	current != null ;

		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#produceOutput >> " + this.uri) ;
		}
		// Implementation of [Vangheluwe, 2001] following the principle
		// of the closure of DEVS models under coupling i.e., when the
		// coupled model is considered as a standalone atomic model
		// executed by a unique simulation engine. In this case, the
		// behaviour is the same for atomic models and coupled models
		// considered as atomic models.
		Vector<EventI> currentOutputEvents = this.output() ;
		if (currentOutputEvents != null) {
			Map<Destination,Vector<EventI>> tempMap =
								new HashMap<Destination,Vector<EventI>>() ;
			for (int i = 0 ; i < currentOutputEvents.size() ; i++) {
				@SuppressWarnings("unchecked")
				Class<EventI> eventType =
					(Class<EventI>)currentOutputEvents.get(i).getClass() ;
				if (this.influencees.containsKey(eventType)) {
					for (CallableEventAtomicSink es :
										this.influencees.get(eventType)) {
						assert	es.importingAtomicModelReference.isDirect() ;
						Destination dest =
							new Destination(
									es.importingModelURI,
									((AtomicSinkReference)
										es.importingAtomicModelReference).ref) ;
						if (tempMap.containsKey(dest)) {
							tempMap.get(dest).add(es.converter.convert(
											  currentOutputEvents.get(i)));
						} else {
							Vector<EventI> hs = new Vector<EventI>() ;
							hs.add(es.converter.convert(
											currentOutputEvents.get(i))) ;
							tempMap.put(dest, hs) ;
						}
					}
				}
			}
			for (Destination d : tempMap.keySet()) {
				try {
					d.destination.storeInput(
									d.destinationURI, tempMap.get(d)) ;
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}
		}
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#produceOutput << " + this.uri) ;
		}
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.getURI().equals(destinationURI)
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI#storeInput(java.lang.String, java.util.Vector)
	 */
	@Override
	public void			storeInput(String destinationURI, Vector<EventI> es)
	throws Exception
	{
		assert	destinationURI != null ;
		assert	this.getURI().equals(destinationURI) ;
		assert	es != null ;
		for (EventI e : es) {
			assert	this.isImportedEventType(e.getClass()) ;
		}

		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicModel>>storeInput " +
										this.getURI() + " " + es.get(0)) ;
		}
		for (EventI e : es) {
			this.currentStoredEvents.add(e) ;
		}
		// Notify the parent that the model has to perform an
		// external transition during this simulation step.
		if (this.isRoot()) {
			// Do nothing, but should not happen if events are received from
			// other models...
		} else {
			this.getParent().hasReceivedExternalEvents(this.getURI()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#getStoredEventAndReset()
	 */
	@Override
	public Vector<EventI>	getStoredEventAndReset()
	{
		Vector<EventI> ret = new Vector<EventI>() ;
		for (EventI e : this.currentStoredEvents) {
			ret.add(e) ;
		}
		this.currentStoredEvents.clear() ;
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#confluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException("AtomicModel#confluentTransition not"
													+ " implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedConfluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedConfluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException(
					"AtomicModel#userDefinedConfluentTransition not"
													+ " implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		// Default: do nothing.	
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		)
	{
		System.out.println(indent + "--------------------") ;
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		System.out.println(indent + name + " " + this.uri +
				" " + this.currentStateTime.getSimulatedTime()
				+ " " + elapsedTime.getSimulatedDuration()) ;
		System.out.println(indent + "--------------------") ;
		this.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent + "--------------------") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		System.out.println(indent + "currentStateTime = " +
								this.currentStateTime.getSimulatedTime()) ;
		System.out.println(indent + "next time advance = " +
								this.nextTimeAdvance.getSimulatedDuration()) ;
		System.out.println(indent + "time of next event = " +
								this.timeOfNextEvent.getSimulatedTime()) ;
		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	throws Exception
	{
		String ret = "" ;
		if (this.getParent() == null) {
			ret += indent +
					"---------------------------------------------------\n" ;
		}
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		ret += indent + name + " " + this.uri + "\n" ;
		ret += indent +
					"---------------------------------------------------\n" ;
		ret += this.modelContentAsString(indent) ;
		if (this.getParent() == null) {
			ret += indent +
					"---------------------------------------------------\n" ;
		}
		return ret ;
	}

	protected String		modelContentAsString(String indent)
	{
		try {
			String ret = "" ;
			ret += super.modelAsString(indent) ;
			if (this.influencees.size() == 0) {
				ret += indent + "influencees = {}\n" ;
			} else {
				ret += indent + "influencees = {" ;
				int cpt = 0 ;
				for (Class<? extends EventI> ce : this.influencees.keySet()) {
					cpt++ ;
					ret += "[" + ce.getName() + " => <" ;
					int cpt2 = 0 ;
					for (CallableEventAtomicSink es :
												this.influencees.get(ce)) {
						cpt2++ ;
						ret += "(" + es.importingModelURI + ", "
								+ es.importingAtomicModelReference.getClass().
															getName() + ", "
								+ es.sinkEventType.getName() + ")" ;
						if (cpt2 < this.influencees.get(ce).size() - 1) {
							ret += ", " ;
						}
					}
					ret += ">]" ;
					if (cpt < this.influencees.keySet().size() - 1) {
						ret += ", " ;
					}
				}
				ret += "}\n" ;
			}
			return ret ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
