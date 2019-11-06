package fr.sorbonne_u.devs_simulation.interfaces;

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

import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelDescriptionI</code> defines the methods that
 * can be used to get information from simulation models directly or through
 * their simulation engine or any other proxy reference.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-05-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ModelDescriptionI
{
	// -------------------------------------------------------------------------
	// Model manipulation related methods (e.g., definition, composition, ...)
	// -------------------------------------------------------------------------
	/**
	 * return the unique identifier of this simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return				the unique identifier of this simulation model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public String		getURI() throws Exception ;

	/**
	 * return the time unit of the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return				the time unit of the simulation clock.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public TimeUnit		getSimulatedTimeUnit() throws Exception ;

	/**
	 * return the URI of the parent (coupled) model of null if the model
	 * is the root of the coupled model hierarchy.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	 			the URI of the parent (coupled) model or null if the model is the root.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public String		getParentURI() throws Exception ;

	/**
	 * return true if the parent reference is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the parent reference is set.
	 * @throws Exception	<i>to do.</i>
	 */
	public boolean		isParentSet() throws Exception ;

	/**
	 * set the parent of this model to <code>cm</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	cm != null
	 * post	this.getParent().equals(p)
	 * post	this.getParentURI().equals(p.getURI())
	 * </pre>
	 *
	 * @param p				the new parent model.
	 * @throws Exception	<i>TODO</i>.
	 */
	public void			setParent(ParentReferenceI p) throws Exception ;

	/**
	 * return the parent (coupled) model of null if the model
	 * is the root of the coupled model hierarchy or if the
	 * model is a coupled model but its submodels are simulated
	 * by different simulation engines.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	 the parent (coupled) model or null if the model is the root.
	 * @throws Exception	<i>TODO</i>.
	 */
	public ParentNotificationI	getParent() throws Exception ;

	/**
	 * return true if this model is the root of a composition.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if this model is the root of a composition.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isRoot() throws Exception ;

	/**
	 * return true if <code>uri</code> is the URI of a descendant model of
	 * this coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			a model URI.
	 * @return				true if <code>uri</code> is the URI of a descendant model of this coupled model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isDescendentModel(String uri) throws Exception ;

	/**
	 * get a Java reference on the object representing a model that is a
	 * descendant or the model executing the method.
	 * 
	 * <p>Description</p>
	 * 
	 * As this method returns a reference on a Java object, it must be used
	 * with much care. Models are not callable through RMI, hence it should
	 * not be called through RMI (i.e., among components). Also, direct access
	 * to the object representing a model should be read-only, as changing the
	 * state of the model can interfere severely with the simulation protocol.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	this.isDescendentModel(uri)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			this model or a submodel URI.
	 * @return				the model with the corresponding URI.
	 * @throws Exception	<i>TODO</i>.
	 */
	public ModelDescriptionI	getDescendentModel(String uri)
	throws Exception ;

	/**
	 * return a direct reference or proxy on the model with URI
	 * <code>uri</code> which can receive external events.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			a model URI.
	 * @return				a direct reference or proxy on the model with URI <code>uri</code>.
	 * @throws Exception	<i>TODO</i>.
	 */
	public EventsExchangingI	getEventExchangingDescendentModel(String uri)
	throws Exception ;

	/**
	 * return true if the model is closed.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * A model is closed if it has no imported event. A model can be
	 * considered as closed even if it exports events because they can
	 * be executed with their exported events not consumed by any other
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the model is closed (no imported event).
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		closed() throws Exception ;

	/**
	 * return an array of event types imported by the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				an array of event types imported by the model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Class<? extends EventI>[]	getImportedEventTypes()
	throws Exception ;

	/**
	 * return true if <code>ec</code> is an imported event type of
	 * this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ec != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ec			event type to be tested.
	 * @return				true if <code>ec</code> is an imported event type of this model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception ;

	/**
	 * return an array of event types exported by the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				an array of event types exported by the model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Class<? extends EventI>[]	getExportedEventTypes()
	throws Exception ;

	/**
	 * return true if <code>ec</code> is an exported event type of
	 * this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ec != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ec			event type to be tested.
	 * @return				true if <code>ec</code> is an exported event type of this model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception ;

	/**
	 * return the atomic event source (description of an exporting model)
	 * for the given exported event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ce != null and this.isExportedEventType(ce)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ce			an event type.
	 * @return				the atomic event source for the given event type.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception ;

	/**
	 * return the set of atomic event sinks (description of an importing
	 * model) for the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ce != null and this.isImportedEventType(ce)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ce			an event type.
	 * @return				the set of atomic event sinks for the given imported event type.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception ;

	/**
	 * add the given influencees (models that import events exported by this
	 * model) to the ones of the given model during in a composition.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	ce != null this.isExportedEventType(ce)
	 * pre	influencees != null and influencees.size() != 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the atomic model to which the influencees must be added.
	 * @param ce			type of event exported by this model.
	 * @param influencees	atomic models influenced by this model through <code>ce</code>.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception ;

	/**
	 * return the set of URIs of models that are influenced by this model
	 * through the given type of events.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	ce != null and this.isExportedEventType(ce)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be queried.
	 * @param ce			the type of events for which influencees are sought.
	 * @return				the set of event sinks describing the models that are influenced by this model through the given type of events.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception ;

	/**
	 * return true if all of the models with the given URIs are influenced by
	 * this model through the exported events of the class <code>ce</code> in
	 * the current composition; this method should be called after composing
	 * the model as it tests for models importing events that are exported by
	 * this model modulo a translation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	ce != null and this.isExportedEventType(ce)
	 * pre	modelURIs != null and modelURIs.size() != 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be queried.
	 * @param destinationModelURIs	set of model URIs to be tested.
	 * @param ce					class of events exported by this model through which the influence is tested.
	 * @return						true if all of the given models are influenced by this model through the events of the class <code>ce</code>.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception ;

	/**
	 * return true if the given model is influenced by this model through the
	 * events of the class <code>ce</code> in the current composition; this
	 * method should be called after composing the model as it tests for
	 * models importing events that are exported by this model modulo a
	 * translation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	ce != null and this.isExportedEventType(ce)
	 * pre	modelURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be queried.
	 * @param destinationModelURI	URI of the model to be tested.
	 * @param ce					class of events exported by this model through which the influence is tested.
	 * @return						true if the given model is influenced by this model through the events of the class <code>ce</code>.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception ;

	/**
	 * return true if the HIOA model is a TIOA i.e., has no exported or
	 * imported variables (TIOA imports and exports only events).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the HIOA model is a TIOA i.e., has no exported or imported variables.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		isTIOA() throws Exception ;

	/**
	 * return true if the model has a precedence relationship with some other
	 * models that imposes particular processing, like the
	 * production/consumption of variables imposing an order in their
	 * initialisation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the model has a precedence relationship with some other models.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		isOrdered() throws Exception ;

	/**
	 * return true if this model exports the designated variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	name != null
	 * pre	type != null.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param name			name of the variable.
	 * @param type			type of the variable.
	 * @return				true if this model exports the designated variable.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		isExportedVariable(
		String name,
		Class<?> type
		) throws Exception ;

	/**
	 * return true if this model imports the designated variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	name != null
	 * pre	type != null.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param name			name of the variable.
	 * @param type			type of the variable.
	 * @return				true if this model imports the designated variable.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		isImportedVariable(
		String name,
		Class<?> type
		) throws Exception ;

	/**
	 * return the variables imported by this HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the variables imported by this HIOA.
	 * @throws Exception	<i>TODO</i>.
	 */
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception ;

	/**
	 * return the variables exported by this HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the variables exported by this HIOA.
	 * @throws Exception	<i>TODO</i>.
	 */
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception ;

	/**
	 * search among descendant HIOA of the model with URI
	 * <code>modelURI</code> the atomic one that defines the designated
	 * variable or its alias and return the reference to the placeholder
	 * for its value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	sourceVariableName != null
	 * pre	sourceVariableType != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model that exports the designated variable.
	 * @param sourceVariableName	name of the sought variable.
	 * @param sourceVariableType	type of the sought variable.
	 * @return						the reference to the placeholder for the value of the designated exported variable.
	 * @throws Exception			<i>TODO</i>.
	 */
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception ;

	/**
	 * search among descendant HIOA of the model with URI
	 * <code>modelURI</code> all of the the atomic ones that use the
	 * designated variable or its aliases and set in them the reference
	 * to the placeholder for its value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	sinkVariableName != null
	 * pre	sinkVariableType != null
	 * pre	value != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI			URI of the model that imports the designated variable.
	 * @param sinkVariableName	name of the sought variable.
	 * @param sinkVariableType	type of the sought variable.
	 * @param value				the placeholder for the value of the designated imported variable.
	 * @throws Exception		<i>TODO</i>.
	 */
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception ;

	/**
	 * generate and return the final report of the simulation, usually called
	 * upon the final state at the end of the simulation.
	 * 
	 * TODO: is it at the right place?
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the final report of the simulation for this model.
	 * @throws Exception	<i>TODO</i>.
	 */
	public SimulationReportI	getFinalReport() throws Exception ;

	// ------------------------------------------------------------------------
	// Debugging behaviour
	// ------------------------------------------------------------------------

	/**
	 * set the debug level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newDebugLevel >= 0}
	 * post	this.hasDebugLevel(newDebugLevel)
	 * </pre>
	 *
	 * @param newDebugLevel	the new debug level of the model
	 * @throws Exception	<i>TODO</i>.
	 */
	public void			setDebugLevel(int newDebugLevel) throws Exception ;

	/**
	 * return the model information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent		indenting string.
	 * @return				the model information as a string.
	 * @throws Exception	<i>TODO</i>.
	 */
	public String		modelAsString(String indent) throws Exception ;

	/**
	 * return the simulator information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the simulator information as a string.
	 * @throws Exception	<i>TODO</i>.
	 */
	public String		simulatorAsString() throws Exception ;
}
// -----------------------------------------------------------------------------
