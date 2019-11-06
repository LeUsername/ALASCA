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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventConverterI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledModel</code> implements the most general methods and
 * instance variables for DEVS coupled models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class implement a polymorphic coordination algorithm able to take
 * into account the three kinds of atomic simulation models implemented in
 * the library i.e., standard DEVS atomic models, event scheduling view (ES)
 * atomic models and hybrid system models inspired from Lynch et al. hybrid
 * input/output automata (HIOA) and timed input/output automata (TIOA).
 * Hence, it is meant to be used as the base class of any user defined
 * coupled model. However, the library may include in the future other
 * implementations of coupled models including the coordination of other
 * types of simulation models of providing specialised and more efficient
 * coordination algorithms for specific subsets of types of simulation
 * models or even coordination algorithm tailored to homogeneous sets
 * of submodels of the same type.
 * </p>
 * <p>
 * As coordination is concerned, event scheduling view atomic models needs
 * nothing more than what is already required to coordinate standard DEVS
 * atomic models. Both exchanges external events exported by some models
 * and imported by others. When creating a coupled model, it is therefore
 * required to define the connections among the submodels.  it is also
 * required to define the events that are imported by the coupled model,
 * the submodels that consume them and, when necessary, how to convert a
 * coupled model imported event into the type of events imported by each
 * consuming submodel. Finally, it is also required to define the events
 * that are exported by the submodels and reexported by the coupled model.
 * Because the type of event exported by the coupled model nned not be the
 * same as the one exported by the submodel, a conversion function can also
 * be defined.
 * </p>
 * <p>
 * HIOA atomic models add a very different aspect to the standard DEVS atomic
 * models as they are defined over continuous and discrete variables that may
 * be exported and imported among them. In a manner similar to the events,
 * a coupled model must define its imported variables and the submodels that
 * consume them, its exported variables and the submodels that export them
 * and the internal bindings of variables exported by a submodel and imported
 * by other submodels.
 * </p>
 * <p>
 * Besides the management of variables, HIOA models introduce a much tighter
 * coupling between models that share variables. At initialisation time, the
 * order through which variables are evaluated needs to follow their
 * export/import dependencies, exported variables being initialised before
 * their bound imported ones can be used to further initialise other internal
 * or exported variables which need them to be computed. Hence, a coupled
 * model that coordinates HIOA and other types of models must be able to
 * partition them into unsorted models exhibiting no dependencies (but the
 * production/consumption of events) and sorted ones which are sharing
 * variables.
 * </p>
 * <p>
 * In many DEVS coordination algorithms, the protocol assumes that when a
 * submodel is active at some simulation step i.e., performs its next
 * internal transition, other models can only execute external events
 * produced by the internal transition of the active submodel. But this
 * may be restrictive for many kinds of HIOA. HIOA do not impose a way to
 * represent the continuous trajectories. One may use differential equations,
 * but equally well algebraic equations. Most models based on differential
 * equations consider that a variable can be updated by evaluating its
 * equations using the last computed values of the variables upon which
 * they depend. As the integration step is usually small, so is the error
 * made by doing so (some integration algorithms use simple extrapolations
 * without reevaluating these variables <i>per se</i>). When algebraic models
 * are used, it can be important to reevaluate all of the variables before
 * performing the internal step initially decided by the coordination
 * algorithm. Hence, this implementation of DEVS considers that whenever a
 * sorted submodel is selected to perform the next internal transition, all
 * of the sorted submodels is given the possibility to perform an internal
 * transition in their order of dependency.
 * </p>
 * <p>
 * To support the management of sorted/unsorted models, each type of models
 * define a method <code>isOrdered</code>, and when they are ordered, they are
 * placed into the subset of sorted models over which a topological sort is
 * done. Currently, the topological sort is done over the variable
 * production/consumption relationship (partial order). If other types of
 * ordered models must be added, the notion of order will have to be extended
 * to take them into account.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true			// TODO
 * </pre>
 * 
 * <p>Created on : 2018-04-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	CoupledModel
extends		Model
implements	CoupledModelI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// Composition time information

	/** Submodels of this coupled model.									*/
	protected final ModelDescriptionI[]				submodels ;
	/** Inverted index to get the indexes for models in the array of
	 *  submodels.															*/
	protected final Map<ModelDescriptionI,Integer>	submodel2index ;
	/** URIs of submodels of this coupled model.							*/
	protected final String[]						submodelURIs ;
	/** Inverted index to get the indexes for URIs of models in the
	 *  array of submodels.														*/
	protected final Map<String,Integer>				submodelURI2index ;
	/** Map from imported event types to direct submodels consuming them.	*/
	protected final Map<Class<? extends EventI>,EventSink[]>
													imported2sinks ;
	/** Map from event types exported by this coupled model to their
	 *  source submodel.													*/
	protected final Map<Class<? extends EventI>,ReexportedEvent>
													exported2reexported ;
	/** Internal connections from source submodels to sets of sinks
	 *  submodels.															*/
	protected final Map<EventSource,EventSink[]>	internalConnections ;
	/** description of the variables imported by this model.				*/
	protected final StaticVariableDescriptor[]		importedVariables ;
	/** description of the variables exported by this model.				*/
	protected final StaticVariableDescriptor[]		exportedVariables ;
	/** map from imported variable descriptions to the submodels and
	 *  their own imported variables that will consume them.				*/
	protected final Map<StaticVariableDescriptor,
						VariableSink[]>				importedVars2sinks ;
	/** map from description of variables exported by submodels that
	 *  are reexported by this coupled model.								*/
	protected final Map<VariableSource,
						StaticVariableDescriptor>	exportedVars2reexported ;
	/** bindings of variables exported by submodels to imported ones
	 *  by other submodels.													*/
	protected final Map<VariableSource,
						VariableSink[]>				internalBindings ;
	/** URIs of the submodels that exhibit an ordering, such as a
	 *  production/consumption of variables relationship, which impose
	 *  an order of computation, order that is given by the list.			*/
	protected final List<String>					sortedSubmodelURIs ;
	/** the sorted submodels, in the order imposed by their relationship;
	 *  all of the sorted models appear also in <code>submodels</code>;
	 *  <code>sortedModels</code> and <code>unsortedModels</code> form a
	 *  partition of <code>submodels</code>.								*/
	protected final ModelDescriptionI[]				sortedSubmodels ;
	/** URIs of the submodels that do not exhibit a particular ordering
	 *  relationship.														*/
	protected final List<String>					unsortedSubmodelURIs ;
	/** the unsorted submodels in no particular order. all of the unsorted
	 *  models appear also in <code>submodels</code>;
	 *  <code>sortedModels</code> and <code>unsortedModels</code>
	 *  form a partition of <code>submodels</code>.							*/
	protected final ModelDescriptionI[]				unsortedSubmodels ;

	// Simulation time information

	/** Elapsed times of submodels since their last internal event (i.e.,
	 *  <code>currentStateTime</code>; conceptually, the time of the
	 *  last internal event plus the elapsed time corresponds to the
	 *  current global simulation time.										*/
	protected Duration[]							elapsedTimes ;
	/** The submodel that will need to execute the next event.			*/
	protected ModelI								submodelOfNextEvent ;
	/** Set of submodels that have external events waiting to be
	 * 	executed during the current simulation step.						*/
	protected final Set<ModelI>						activeSubmodels ;
	/** Random number generator used to break the ties when several
	 *  submodels can perform an internal transition at the same time of
	 *  next internal transition simulation time. 							*/
	protected final Random							randomGenerator ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a coupled simulation model to be run by the given simulator with
	 * the given URI and with the given time unit for the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete...
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	forall i, submodels[i] != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(TimeUnit.SECONDS)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>TODO</i>.
	 */
	public				CoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		ModelDescriptionI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		this(uri, simulatedTimeUnit, simulationEngine,
			 submodels, imported, reexported, connections,
			 new HashMap<StaticVariableDescriptor,VariableSink[]>(),
			 new HashMap<VariableSource,StaticVariableDescriptor>(),
			 new HashMap<VariableSource,VariableSink[]>()) ;
	}

	/**
	 * create a coupled simulation model to be run by the given simulator with
	 * the given URI and with the given time unit for the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete...
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	simulatedTimeUnit != null
	 * pre	{@code submodels != null and submodels.length > 1}
	 * pre	forall i, submodels[i] != null
	 * post	uri.equals(this.getURI())
	 * post	this.getSimulatedTimeUnit().equals(TimeUnit.SECONDS)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @throws Exception		<i>TODO</i>.
	 */
	@SuppressWarnings("unchecked")
	public				CoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine,
		ModelDescriptionI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;

		// Preconditions
		assert	uri != null ;
		assert	simulatedTimeUnit != null ;
		assert	submodels != null && submodels.length > 1 ;
		for (int i = 0 ; i < submodels.length ; i++) {
			assert	submodels[i] != null ;
		}
		assert	simulationEngine == null ||
							simulationEngine instanceof ModelDescriptionI ;

		// Coupled model initialisation
		this.activeSubmodels = new HashSet<ModelI>() ;
		this.randomGenerator = new Random(System.currentTimeMillis()) ;

		// Submodels management
		this.submodels = new ModelDescriptionI[submodels.length] ;
		this.submodelURIs = new String[submodels.length] ;
		this.submodelURI2index = new HashMap<String,Integer>() ;
		this.submodel2index = new HashMap<ModelDescriptionI,Integer>() ;
		HashMap<String,ModelDescriptionI> uri2models =
								new HashMap<String,ModelDescriptionI>() ;
		for (int i = 0 ; i < submodels.length ; i++) {
			this.submodels[i] = submodels[i] ;
			this.submodelURIs[i] = submodels[i].getURI() ;
			this.submodelURI2index.put(submodels[i].getURI(), i) ;
			this.submodel2index.put(submodels[i], i) ;
			uri2models.put(this.submodels[i].getURI(), this.submodels[i]) ;
		}
		this.elapsedTimes = new Duration[submodels.length] ;

		// Exported events management
		this.exported2reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		Vector<Class<? extends EventI>> tempExported =
								new Vector<Class<? extends EventI>>() ;
		for (Class<? extends EventI> ce : reexported.keySet()) {
			ReexportedEvent re = reexported.get(ce) ;
			this.exported2reexported.put(
					ce,
					new ReexportedEvent(re.exportingModelURI,
										re.sourceEventType,
										re.sinkEventType,
										re.converter)) ;
			if (!tempExported.contains(re.sinkEventType)) {
				tempExported.add(re.sinkEventType) ;
			}
		}
		this.exportedEventTypes =
			(Class<? extends EventI>[]) new Class<?>[tempExported.size()] ;
		for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
			this.exportedEventTypes[i] = tempExported.get(i) ;
		}

		// Imported events management
		this.imported2sinks =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
		this.importedEventTypes =
			(Class<? extends EventI>[])
								new Class<?>[imported.keySet().size()] ;
		{
			int i = 0 ;
			for (Class<? extends EventI> ce : imported.keySet()) {
				this.importedEventTypes[i++] = ce ;
				EventSink[] eks = imported.get(ce) ;
				EventSink[] tempEks = new EventSink[eks.length] ;
				for (int j = 0 ; j < eks.length ; j++) {
					tempEks[j] =
						new EventSink(eks[j].importingModelURI,
									  eks[j].sourceEventType,
									  eks[j].sinkEventType,
									  eks[j].converter) ;
				}
				this.imported2sinks.put(ce, tempEks) ;
			}
		}

		// Connections of imported/exported events between submodels: creation
		// if the internal representation.
		this.internalConnections = new HashMap<EventSource,EventSink[]>() ;
		for (EventSource sourceEP : connections.keySet()) {
			EventSink[] temp =
					new EventSink[connections.get(sourceEP).length] ;
			for (int i = 0 ; i < connections.get(sourceEP).length ; i++) {
				EventSink sink = connections.get(sourceEP)[i] ;
				temp[i] =
					new EventSink(sink.importingModelURI,
								  sink.sourceEventType,
								  sink.sinkEventType,
								  sink.converter) ;
			}
			this.internalConnections.put(
					new EventSource(sourceEP.exportingModelURI,
									sourceEP.sourceEventType,
									sourceEP.sourceEventType),
					temp) ;
			assert	this.submodelURI2index.containsKey(
											sourceEP.exportingModelURI) ;
		}

		// Management of variables

		this.importedVariables =
					new StaticVariableDescriptor[importedVars.size()] ;
		this.exportedVariables =
					new StaticVariableDescriptor[reexportedVars.size()] ;

		// Imported variables
		this.importedVars2sinks =
					new HashMap<StaticVariableDescriptor,VariableSink[]>() ;
		int index = 0 ;
		for (StaticVariableDescriptor vd : importedVars.keySet()) {
			assert	vd.getVisibility() == VariableVisibility.IMPORTED ;
			VariableSink[] sinks = importedVars.get(vd) ;
			VariableSink[] newSinks = new VariableSink[sinks.length] ;
			for (int i = 0 ; i < sinks.length; i++) {
				newSinks[i] = new VariableSink(
									sinks[i].importedVariableName,
									sinks[i].importedVariableType,
									sinks[i].sinkVariableName,
									sinks[i].sinkVariableType,
									sinks[i].sinkModelURI) ;
			}
			StaticVariableDescriptor newVd =
				new StaticVariableDescriptor(vd.getName(), vd.getType(),
														vd.getVisibility()) ;
			this.importedVars2sinks.put(newVd, newSinks) ;
			this.importedVariables[index++] = newVd ;
		}

		// Exported variables
		this.exportedVars2reexported =
					new HashMap<VariableSource,StaticVariableDescriptor>() ;
		index = 0 ;
		for (VariableSource source : reexportedVars.keySet()) {
			StaticVariableDescriptor sink = reexportedVars.get(source) ;
			StaticVariableDescriptor vd =
					new StaticVariableDescriptor(
							sink.getName(),
							sink.getType(),
							VariableVisibility.EXPORTED) ;
			this.exportedVariables[index++] = vd ;
			assert	source.name.equals(sink.getName()) ;
			assert	sink.getType().isAssignableFrom(source.type) ;
			this.exportedVars2reexported.put(
					new VariableSource(source.name,
									  source.type,
									  source.exportingModelURI),
					vd) ;
		}

		// Variable bindings among submodels: binding exported variables to
		// imported ones.
		this.internalBindings = new HashMap<VariableSource,VariableSink[]>() ;
		for (VariableSource source : bindings.keySet()) {
			VariableSink[] sinks = bindings.get(source) ;
			VariableSink[] newSinks = new VariableSink[sinks.length] ;
			for (int i = 0 ; i < sinks.length ; i++) {
				newSinks[i] =
					new VariableSink(sinks[i].importedVariableName,
									 sinks[i].importedVariableType,
									 sinks[i].sinkVariableName,
									 sinks[i].sinkVariableType,
									 sinks[i].sinkModelURI) ;
			}
			this.internalBindings.put(
					new VariableSource(source.name,
									   source.type,
									   source.exportingModelURI),
					newSinks) ;
		}

		Map<String,Set<String>>	models2predecessors =
					CoupledModel.computePredecessors(submodels, bindings) ;
//		showPredecessors(models2predecessors) ;
		this.sortedSubmodelURIs =
					CoupledModel.topologicalSort(models2predecessors) ;
		if (this.sortedSubmodelURIs == null) {
			throw new Exception(
						"Submodels of " + this.getURI() +
						" exhibits a cycle in their export/import bindings") ;
		}
		// Print the sorted model URIs
		if (this.hasDebugLevel(1)) {
			this.logMessage("CoupledModel#CoupledModel sortedModels = {") ;
		}
		this.sortedSubmodels = new ModelDescriptionI[this.sortedSubmodelURIs.size()] ;
		for (int i = 0 ; i < this.sortedSubmodelURIs.size() ; i++) {
			this.sortedSubmodels[i] =
				(ModelDescriptionI)this.submodels[this.submodelURI2index.get(
												this.sortedSubmodelURIs.get(i))] ;
			if (this.hasDebugLevel(1)) {
				this.logMessage(this.sortedSubmodels[i].getURI()) ;
				if (i < this.sortedSubmodelURIs.size() - 1)
					this.logMessage(", ") ;
			}
		}
		if (this.hasDebugLevel(1)) {
			this.logMessage("}") ;
		}
		this.unsortedSubmodelURIs = new ArrayList<String>() ;
		this.unsortedSubmodels =
			new ModelDescriptionI[this.submodels.length -
			                      				this.sortedSubmodels.length] ;
		int k = 0 ;
		for (String s : this.submodelURIs) {
			if (!this.sortedSubmodelURIs.contains(s)) {
				this.unsortedSubmodelURIs.add(s) ;
				this.unsortedSubmodels[k++] =
					(ModelDescriptionI)
						this.submodels[this.submodelURI2index.get(s)] ;
			}
		}

		if (this.hasDebugLevel(1)) {
			StringBuffer tmp =
				new StringBuffer(
						"CoupledModel#CoupledModel unsortedModels = {") ;
			for (int i = 0 ; i < this.unsortedSubmodels.length ; i++) {
				tmp.append(this.unsortedSubmodels[i].getURI()) ;
				if (i < this.unsortedSubmodels.length - 1) {
					tmp.append(", ") ;
				}
			}
			this.logMessage(tmp.append("}").toString()) ;
		}

		// Postconditions
		assert	uri.equals(this.getURI()) ;
		assert	this.getSimulatedTimeUnit().equals(simulatedTimeUnit) ;
		assert	this.getSimulationEngine() == null ||
					this.getSimulationEngine().equals(simulationEngine) ;
		assert	!isDebugModeOn() ;
//		assert	CoupledHIOA.checkInvariant(this) ;
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * compute a map of each submodel URIs to the set of URIs (perhaps empty)
	 * of the other submodels producing variables that they are consuming,
	 * hence providing an order over which submodels can be topologically
	 * sorted.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param submodels		array of the submodels.
	 * @param bindings		definition of the bindings between exported and imported variables among the submodels.
	 * @return				a map of each submodel URIs to the set of URIs of the other submodels producing variables that they are consuming.
	 * @throws Exception 	<i>TODO</i>
	 */
	protected static Map<String,Set<String>>		computePredecessors(
		ModelDescriptionI[] submodels,
		Map<VariableSource,VariableSink[]> bindings
		) throws Exception
	{
		Map<String, Set<String>> models2predecessors =
										new HashMap<String, Set<String>>() ;
		for (int i = 0 ; i < submodels.length ; i++) {
			if (submodels[i].isOrdered()) {
				models2predecessors.put(submodels[i].getURI(),
									    new HashSet<String>()) ;
			}
		}
		for (VariableSource vs : bindings.keySet()) {
			VariableSink[] sinks = bindings.get(vs) ;
			for (int i = 0 ; i < sinks.length ; i++) {
				models2predecessors.get(sinks[i].sinkModelURI).
												add(vs.exportingModelURI) ;
			}
		}
		return models2predecessors ;
	}

	/**
	 * print the set of predecessors for each model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	p != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param p	map from model URIs to sets of their predecessor model URIs.
	 */
	protected static void	showPredecessors(Map<String,Set<String>> p)
	{
		assert	p!= null ;

		String ret = "Predecessors[" ;
		int modelCount = p.size() ;
		for (String uri : p.keySet()) {
			ret += uri + " ==> {" ;
			int predecessorCount = p.get(uri).size() ;
			for (String pred : p.get(uri)) {
				ret += pred ;
				if (--predecessorCount > 0) {
					ret += ", " ;
				}
			}
			ret += "}" ;
			if (--modelCount > 0) {
				ret += ", " ;
			}
		}
		ret += "]" ;
		System.out.println(ret) ;
	}

	/**
	 * return a list of submodel URIs sorted in topological order given the
	 * order defined by <code>models2predecessors</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	models2predecessors != null
	 * pre	for (String uri : models2predecessors.keySet()) {
	 *          models2predecessors.keySet().
	 *                              containsAll(models2predecessors.get(uri))
	 *       }
	 * post	return == null || (return.containsAll(
	 *                                      models2predecessors.keySet()) and
	 *                         models2predecessors.keySet().
	 *                                      containsAll(return))
	 * </pre>
	 *
	 * @param models2predecessors	map from submodel URIs to their imported variables value providers.
	 * @return						a list of submodel URIs sorted in topological order given the order defined by <code>models2predecessors</code>.
	 */
	protected static List<String>	topologicalSort(
		Map<String, Set<String>> models2predecessors
		)
	{
		assert	models2predecessors != null ;
		for (String uri : models2predecessors.keySet()) {
			assert	models2predecessors.keySet().containsAll(
											models2predecessors.get(uri)) ;
		}

		final Set<String> submodelURIs = models2predecessors.keySet() ;

		List<String> ret = new ArrayList<String>() ; ;
		ArrayList<String> current = new ArrayList<String>() ;
		for (String uri : submodelURIs) {
			current.add(uri) ;
		}
		boolean foundNext = true ;
		while (!current.isEmpty() && foundNext) {
			// find the next element to put in the result
			int probe = 0 ;
			String next = null ;
			while (probe < current.size() && next == null) {
				// Check if the element at index probe has no predecessor
				// that are still in the remaining models.
				next = current.get(probe++) ;
				Set<String> predecessors = models2predecessors.get(next) ;
				// If the element next has no predecessors, we have our
				// element otherwise check the predecessors.
				if (!predecessors.isEmpty()) {
					boolean noPredecessorInCurrent = true ;
					Iterator<String> iter = predecessors.iterator() ;
					while (iter.hasNext() && noPredecessorInCurrent) {
						noPredecessorInCurrent &=
											!current.contains(iter.next()) ;
					}
					// If no predecessor in current, we have our element.
					if (!noPredecessorInCurrent) {
						// If a predecessor is still to be extracted and put
						// in the result, we must continue with another
						// candidate.
						next = null ;
					}
				}
			}
			// If foundNext is false, a cycle has been found and the sort
			// fails, otherwise continue.
			if (next != null) {
				ret.add(next) ;
				current.remove(next) ;
			} else {
				foundNext = false ;
			}
		}
		if (foundNext && current.isEmpty()) {
			assert	ret == null || (ret.containsAll(submodelURIs) &&
										submodelURIs.containsAll(ret)) ;
			return ret ;
		} else {
			return null ;
		}
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * get the submodel at index <code>i</code>, returning it as implementing
	 * the interface <code>ModelI</code> rather than the declared
	 * <code>ModelDescriptionI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	i &gt;= 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param i	the index of the submodel.
	 * @return	the submodel at index <code>i</code>.
	 */
	protected ModelI		getSubmodel(int i)
	{
		assert	i >= 0 ;

		return (ModelI)this.submodels[i] ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#isSubmodel(java.lang.String)
	 */
	@Override
	public boolean		isSubmodel(String uri)
	{
		assert	uri != null ;

		return this.submodelURI2index.keySet().contains(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isDescendentModel(java.lang.String)
	 */
	@Override
	public boolean		isDescendentModel(String uri) throws Exception
	{
		if (this.isSubmodel(uri)) {
			return true ;
		} else {
			boolean found = false ;
			for (int i = 0 ; !found && i < this.submodels.length ; i++) {
				found = this.getSubmodel(i).isDescendentModel(uri) ;
			}
			return found ;
		}
	}

	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getDescendentModel(java.lang.String)
	 */
	@Override
	public ModelDescriptionI	getDescendentModel(String uri)
	throws Exception
	{
		if (this.uri.equals(uri)) {
			return this ;
		} else {
			ModelDescriptionI ret = null ;
			for (int i = 0 ; ret == null && i < this.submodels.length ; i++) {
				ret = this.submodels[i].getDescendentModel(uri) ;
			}
			return ret ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri)
	{
		if (this.isSubmodel(uri)) {
			return (EventsExchangingI) this.submodels[
			                               this.submodelURI2index.get(uri)] ;
		} else {
			EventsExchangingI ret = null ;
			for (int i = 0 ; ret == null && i < this.submodels.length ; i++) {
				if (this.getSubmodel(i) instanceof CoupledModel) {
					ret = ((CoupledModel)this.getSubmodel(i)).
									getEventExchangingDescendentModel(uri) ;
				}
			}
			return ret ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getEventSource(java.lang.Class)
	 */
	@Override
	public EventSource	getEventSource(Class<? extends EventI> ce)
	{
		try {
			assert	ce != null && this.isExportedEventType(ce) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getEventSinks(java.lang.Class)
	 */
	@Override
	public Set<EventSink> 	getEventSinks(Class<? extends EventI> ce)
	{
		try {
			assert	ce != null && this.isImportedEventType(ce) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		EventSink[] tmp = this.imported2sinks.get(ce) ;
		Set<EventSink> ret = new HashSet<EventSink>() ;
		for (int i = 0 ; i < tmp.length ; i++) {
			ret.add(tmp[i]) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getReexportedEvent(java.lang.Class)
	 */
	@Override
	public ReexportedEvent	getReexportedEvent(Class<? extends EventI> ce)
	{
		assert	ce != null ;

		return this.exported2reexported.get(ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource		getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	ce != null && this.isExportedEventType(ce) ;

		ReexportedEvent re = this.exported2reexported.get(ce) ;
		assert	re.sinkEventType.isAssignableFrom(ce) ;
		int index = this.submodelURI2index.get(re.exportingModelURI) ;
		EventAtomicSource source =
			this.getSubmodel(index).getEventAtomicSource(re.sourceEventType) ;
		return new EventAtomicSource(
						source.exportingModelURI,
						source.sourceEventType,
						re.sinkEventType,
						EventConverterI.compose(re.converter,
											    source.converter)) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	ce != null && this.isImportedEventType(ce) ;

		Set<CallableEventAtomicSink> ret = new HashSet<CallableEventAtomicSink>() ;
		Set<EventSink> submodelSinks = this.getEventSinks(ce) ;
		for (EventSink es : submodelSinks) {
			assert	ce.equals(es.sourceEventType) ;
			assert	this.isSubmodel(es.importingModelURI) ;
			int submodelIndex =
				this.submodelURI2index.get(es.importingModelURI) ;
			Set<CallableEventAtomicSink> submodelAtomicSinks =
				this.getSubmodel(submodelIndex).
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
			this.logMessage("CoupledModel>>addInfluencees") ;
		}

		assert	modelURI != null ;
		assert	this.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	influencees != null && influencees.size() != 0 ;

		ModelDescriptionI submodel = null ;
		for (int i = 0 ; submodel == null && i < this.submodels.length ; i++) {
			if (this.submodels[i].isDescendentModel(modelURI)) {
				submodel = this.submodels[i] ;
			}
		}
		assert	submodel != null ;
		submodel.addInfluencees(modelURI, ce, influencees) ;
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

		return this.getEventExchangingDescendentModel(modelURI).getInfluencees(modelURI, ce) ;
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

		return this.getEventExchangingDescendentModel(modelURI).
							areInfluencedThrough(modelURI, modelURIs, ce) ;
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
		assert	destinationModelURI != null ;

		return this.getEventExchangingDescendentModel(modelURI).
					isInfluencedThrough(modelURI, destinationModelURI, ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return this.importedVariables.length == 0 &&
										this.exportedVariables.length == 0 ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return !this.isTIOA() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		boolean ret = false ;
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			ret = ret || (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) ;
			if (ret) break ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(
		String sinkVariableName,
		Class<?> sinkVariableType
		)
	{
		boolean ret = false ;
		for (StaticVariableDescriptor vd : this.importedVariables) {
			ret = ret || (vd.getName().equals(sinkVariableName) &&
							sinkVariableType.isAssignableFrom(vd.getType())) ;
			if (ret) break ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.importedVariables.length] ;
		for (int i = 0 ; i < this.importedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
								this.importedVariables[i].getName(),
								this.importedVariables[i].getType(),
								this.importedVariables[i].getVisibility()) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.exportedVariables.length] ;
		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
								this.exportedVariables[i].getName(),
								this.exportedVariables[i].getType(),
								this.exportedVariables[i].getVisibility()) ;
		}
		return ret ;
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
		assert	modelURI != null && modelURI.equals(this.getURI()) ;
		assert	this.isExportedVariable(
							sourceVariableName, sourceVariableType) ;

		for (VariableSource source : this.exportedVars2reexported.keySet()) {
			StaticVariableDescriptor vd =
								this.exportedVars2reexported.get(source) ;
			if (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) {
				return this.getSubmodel(this.submodelURI2index.get(
												source.exportingModelURI)).
							getActualExportedVariableValueReference(
										source.exportingModelURI,
										source.name,
										source.type) ;
			}
		}
		// not found.
		return null;
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
		assert	modelURI != null && modelURI.equals(this.getURI()) ;
		assert	this.isImportedVariable(sinkVariableName, sinkVariableType) ;

		for (StaticVariableDescriptor vd : this.importedVars2sinks.keySet()) {
			if (vd.getName().equals(sinkVariableName) &&
							vd.getType().isAssignableFrom(sinkVariableType)) {
				VariableSink[] sinks = this.importedVars2sinks.get(vd) ;
				for (int i = 0 ; i < sinks.length ; i++) {
					this.setImportedVariableValueReference(
											sinks[i].sinkModelURI,
											sinks[i].sinkVariableName,
											sinks[i].sinkVariableType, 
											value) ;
				}
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getSortedSubmodelURIs()
	 */
	@Override
	public List<String>	getSortedSubmodelURIs()
	{
		List<String> ret = new ArrayList<String>() ;
		for (int i = 0 ; i < this.sortedSubmodelURIs.size() ; i++) {
			ret.add(i, this.sortedSubmodelURIs.get(i)) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#getUnsortedSubmodelURIs()
	 */
	@Override
	public Set<String>	getUnsortedSubmodelURIs()
	{
		Set<String> ret = new HashSet<String>() ;
		for (int i = 0 ; i < this.unsortedSubmodelURIs.size() ; i++) {
			ret.add(this.unsortedSubmodelURIs.get(i)) ;
		}
		return ret ;
	}

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	//
	//    These methods are used only when the coupled model is tightly
	//    integrated with its submodels (and subsubmodels...) by using
	//    a single common simulation engine and then they are Java
	//    objects directly referenced by each others.
	//    When submodels are not co-localised with the coupled model
	//    or when they use different simulation engines, a coordination
	//    engine is used to bring together the simulation engines of the
	//    submodels and then it uses its own method to execute the simulation
	//    by calling the simulation engines of the submodels.
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		boolean ret = super.isStateInitialised() ;

		for (int i = 0 ; i < this.submodels.length ; i++) {
			ret = this.getSubmodel(i).isStateInitialised() ;
		}
		ret &= this.timeOfNextEvent != null ;
		ret &= this.nextTimeAdvance != null ;
		ret &= (this.submodelOfNextEvent != null ||
				this.timeOfNextEvent.equals(Time.INFINITY)) ;
		ret &= this.elapsedTimes != null ;
		ret &= this.activeSubmodels != null ;

		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#clockSynchronised()
	 */
	@Override
	public boolean		clockSynchronised()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;

		for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
			((ModelI)this.sortedSubmodels[i]).initialiseState(initialTime) ;
		}
		for (int i = 0 ; i < this.unsortedSubmodels.length ; i++) {
			((ModelI)this.unsortedSubmodels[i]).initialiseState(initialTime) ;
		}
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
			this.elapsedTimes[i] =
							Duration.zero(this.getSimulatedTimeUnit()) ;
		}

		this.activeSubmodels.clear(); ;

		// find the model with the next internal event transition.
		this.timeOfNextEvent = this.getSubmodel(0).getTimeOfNextEvent() ;
		this.submodelOfNextEvent = this.getSubmodel(0) ;
		for (int i = 1; i < this.submodels.length ; i++) {
			if (this.getSubmodel(i).getTimeOfNextEvent().
										lessThan(this.timeOfNextEvent)) {
				this.timeOfNextEvent =
								this.getSubmodel(i).getTimeOfNextEvent() ;
				this.submodelOfNextEvent = this.getSubmodel(i) ;
			}
		}
		this.nextTimeAdvance =
				this.timeOfNextEvent.subtract(this.currentStateTime) ;

		// Postconditions
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).getCurrentStateTime().add(
													this.elapsedTimes[i]).
					equals(this.getCurrentStateTime()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.nextTimeAdvance ;
	}

	/**
	 * If the submodel of next event is among the HIOA, every HIOA submodels
	 * will be executed at the next internal step so all of them must produce
	 * their outputs, otherwise only the submodel of next event will produce
	 * its output.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	current != null
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		assert	current != null ;

		if (this.hasDebugLevel(2)) {
			this.logMessage("CoupledModel>>produceOutput " + this.uri) ;
		}

		this.submodelOfNextEvent.produceOutput(current) ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception
	{
		assert	modelURI != null ;
		assert	this.isSubmodel(modelURI) ;

		if (this.hasDebugLevel(2)) {
//			System.out.println("CoupledModel>>hasReceivedExternalEvents "
//				+ this.uri + " from " + modelURI) ;
			this.logMessage("CoupledModel>>hasReceivedExternalEvents "
										+ this.uri + " from " + modelURI) ;
		}

		assert	this.isSubmodel(modelURI) ;

		this.activeSubmodels.add(
					this.getSubmodel(this.submodelURI2index.get(modelURI))) ;
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
		assert	modelURI != null ;
		assert	this.submodelURI2index.keySet().contains(modelURI) ;
		ModelI m =
			(ModelI)this.submodels[this.submodelURI2index.get(modelURI)] ;
		assert	this.activeSubmodels.contains(m) ;

		this.activeSubmodels.remove(m) ;
		if (!this.isRoot() && this.activeSubmodels.isEmpty()) {
			this.getParent().hasPerformedExternalEvents(this.getURI()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		// Preconditions
		assert	this.getNextTimeAdvance().equals(
					this.getTimeOfNextEvent().subtract(
											this.getCurrentStateTime())) ;
		assert	this.getTimeOfNextEvent().equals(
							this.submodelOfNextEvent.getTimeOfNextEvent()) ;

		if (this.hasDebugLevel(1)) {
			try {
//				System.out.println("CoupledModel>>internalTransition "
//									+ this.uri + " "
//									+ this.submodelOfNextEvent.getURI()) ;
				this.logMessage("CoupledModel>>internalTransition "
									+ this.uri + " "
									+ this.submodelOfNextEvent.getURI()) ;
			} catch (Exception e1) {
				throw new RuntimeException(e1) ;
			}
		}

		// Implementation of [Vangheluwe, 2001] following the principle
		// of the closure of DEVS models under coupling i.e., when the
		// coupled model is considered as a stand alone atomic model
		// executed by a unique simulation engine.

		// Now perform the internal transition.
		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getCurrentStateTime()) ;
		this.currentStateTime = this.getTimeOfNextEvent() ;
		Set<String> updated = new HashSet<String>() ;
		boolean hasHIOAActive = false ;
		try {
			if (this.sortedSubmodelURIs.contains(
									this.submodelOfNextEvent.getURI())) {
				for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
					if (this.sortedSubmodels[i].getURI().equals(
										this.submodelOfNextEvent.getURI())) {
						((ModelI)this.sortedSubmodels[i]).internalTransition() ;
					} else {
						((ModelI)this.sortedSubmodels[i]).
							causalTransition(this.getCurrentStateTime()) ;
					}
					if (((ModelI)this.sortedSubmodels[i]).getCurrentStateTime().
										equals(this.getCurrentStateTime())) {
						try {
							String uri = this.sortedSubmodelURIs.get(i) ;
							int index = this.submodelURI2index.get(uri) ;
							this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
							updated.add(uri) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}
			} else {
				this.submodelOfNextEvent.internalTransition() ;
				this.elapsedTimes[
				        this.submodel2index.get(this.submodelOfNextEvent)] =
				        			Duration.zero(this.getSimulatedTimeUnit()) ;
				updated.add(this.submodelOfNextEvent.getURI()) ;
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		// Do the next external transition for all currently
		// active submodels.
		if (this.activeSubmodels.size() > 0) {
			try {
				if (!this.isRoot()) {
					this.getParent().hasPerformedExternalEvents(this.uri) ;
				}
				for (ModelI m : this.activeSubmodels) {
					if (this.sortedSubmodelURIs.contains(m.getURI())) {
						hasHIOAActive = true ;
						break ;
					}
				}
				if (hasHIOAActive) {
					for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
						String uri = this.sortedSubmodelURIs.get(i) ;
						if (!updated.contains(uri)) {
							((ModelI)this.sortedSubmodels[i]).
								causalTransition(this.getCurrentStateTime()) ;
						}
						if (((ModelI)this.sortedSubmodels[i]).
									getCurrentStateTime().
										equals(this.getCurrentStateTime())) {
							int index = this.submodelURI2index.get(uri) ;
							this.elapsedTimes[index] =
									Duration.zero(this.getSimulatedTimeUnit()) ;
							updated.add(uri) ;
						}
					}
				}
				for (ModelI m : this.activeSubmodels) {
					int i = this.submodel2index.get(m) ;
					m.externalTransition(
							(updated.contains(m.getURI()) ?
								Duration.zero(this.getSimulatedTimeUnit())
							:	this.elapsedTimes[i].add(elapsedTime)
							)) ;
					this.elapsedTimes[i] =
							Duration.zero(this.getSimulatedTimeUnit()) ;
					updated.add(m.getURI()) ;
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
		for (int j = 0 ; j < this.submodels.length ; j++) {
			try {
				if (!updated.contains(this.submodels[j].getURI())) {
					this.elapsedTimes[j] =
									this.elapsedTimes[j].add(elapsedTime) ;
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
		this.activeSubmodels.clear() ;
		this.computeNextEventToBeSimulated() ;

		if (this.hasDebugLevel(2)) {
			this.showCurrentState(
							"",
							Duration.zero(this.getSimulatedTimeUnit())) ;
		}

		// Postconditions
		for (int i = 0 ; i < this.submodels.length ; i++) {
			assert	((ModelI)this.submodels[i]).getCurrentStateTime().add(
													this.elapsedTimes[i]).
						equals(this.getCurrentStateTime()) ;
		}
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
		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#causalTransition(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalTransition(Time current)
	{
		if (this.hasDebugLevel(1)) {
//			System.out.println("CoupledModel>>causalTransition " + this.uri) ;
			this.logMessage("CoupledModel>>causalTransition " + this.uri) ;
		}

		Duration elapsedTime = current.subtract(this.getCurrentStateTime()) ;
		this.currentStateTime = current ;
		Set<String> updated = new HashSet<String>() ;
		for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
			((ModelI)this.sortedSubmodels[i]).causalTransition(current) ;
			if (((ModelI)this.sortedSubmodels[i]).getCurrentStateTime().
														equals(current)) {
					int index = this.submodelURI2index.get(
											this.sortedSubmodelURIs.get(i)) ;
					this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
					updated.add(this.sortedSubmodelURIs.get(i)) ;
			}
		}
		for (int j = 0 ; j < this.submodels.length ; j++) {
			try {
				if (!updated.contains(this.submodels[j].getURI())) {
					this.elapsedTimes[j] =
							this.elapsedTimes[j].add(elapsedTime) ;
				}
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
		this.activeSubmodels.clear() ;
		this.computeNextEventToBeSimulated() ;

		if (this.isDebugModeOn()) {
			this.showCurrentState(
							"",
							Duration.zero(this.getSimulatedTimeUnit())) ;
		}

		// Postconditions
		for (int i = 0 ; i < this.submodels.length ; i++) {
			assert	((ModelI)this.submodels[i]).getCurrentStateTime().add(
													this.elapsedTimes[i]).
						equals(this.getCurrentStateTime()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * after a transition in a coupled model, compute the next event
	 * to be simulated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	!this.getTimeOfNextEvent().equals(Time.INFINITY) ||
	 *		    this.getTimeOfNextEvent().subtract(
	 *                                           this.getCurrentStateTime()).
	 *                               equals(this.getNextTimeAdvance())
	 * </pre>
	 *
	 */
	@Override
	protected void		computeNextEventToBeSimulated()
	{
		Duration ta = this.getSubmodel(0).getNextTimeAdvance().
											subtract(this.elapsedTimes[0]) ;
		Vector<Integer> currentSet = new Vector<Integer>() ;
		currentSet.add(0) ;
		for (int i = 1 ; i < this.submodels.length ; i++) {
			Duration temp =
				this.getSubmodel(i).getNextTimeAdvance().subtract(
													this.elapsedTimes[i]) ;
			if (temp.equals(ta)) {
				currentSet.add(i) ;
			} else if (temp.lessThan(ta)) {
				ta = temp ;
				currentSet.clear() ;
				currentSet.add(i) ;
			}
		}
		this.nextTimeAdvance = ta ;
		if (ta.lessThan(Duration.INFINITY)) {
			int current = currentSet.get(0) ;
			// If there is a tie, break it using the method select.
			if (currentSet.size() > 1) {
				String[] candidates = new String[currentSet.size()] ;
				for (int i = 0 ; i < currentSet.size() ; i++) {
					try {
						candidates[i] =
								this.submodels[currentSet.get(i)].getURI() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
				String selected = this.select(candidates) ;
				current = this.submodelURI2index.get(selected) ;
			}
			this.timeOfNextEvent = this.currentStateTime.add(ta) ;
			this.submodelOfNextEvent = this.getSubmodel(current) ;
		} else {
			this.timeOfNextEvent = Time.INFINITY ;
			this.submodelOfNextEvent = null ;
		}

		// Postconditions
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#select(java.lang.String[])
	 */
	@Override
	public String		select(String[] candidates)
	{
		assert	candidates != null && candidates.length > 1 ;

		// ******************************************************************
		// TODO:
		//    - the selection should be done knowing the types of the events
		//      that are due to execute;
		//    - a simpler mean to provide a selection function than
		//      creating a subclass should be provided.
		// ******************************************************************

		boolean allCandidatesHIOA = true ;
		Vector<String> newCandidates = new Vector<String>() ;
		for (int i = 0 ; i < candidates.length ; i++) {
			if (!this.sortedSubmodelURIs.contains(candidates[i])) {
				newCandidates.add(candidates[i]) ;
				allCandidatesHIOA &= false ;
			}
		}
		if (allCandidatesHIOA) {
			return candidates[0] ;
		} else {
			assert	newCandidates.size() > 0 ;
			if (newCandidates.size() > 1) {
				// For the time being, random selection.
				return newCandidates.toArray(new String[]{})[
				           this.randomGenerator.nextInt(candidates.length)] ;
			} else {
				return candidates[0] ;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(1)) {
//			System.out.println("CoupledModel>>externalTransition " + this.uri) ;
			this.logMessage("CoupledModel>>externalTransition " + this.uri) ;
		}

		// If a coupled model is called to execute external events but
		// its list of active models is empty, it means that it is the
		// coupled model that just run its internal step so the external
		// transitions of its submodels have been executed as part of it.
		Set<String> updated = new HashSet<String>() ;
		if (!this.activeSubmodels.isEmpty()) {
			this.currentStateTime = this.currentStateTime.add(elapsedTime) ;
			boolean hasHIOAActive = false ;
			Set<ModelI> activeSorted = new HashSet<ModelI>() ;
			for (ModelI m : this.activeSubmodels) {
				try {
					if (this.sortedSubmodelURIs.contains(m.getURI())) {
						hasHIOAActive =  true ;
						activeSorted.add(m) ;
					}
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}
			if (hasHIOAActive) {
				this.activeSubmodels.removeAll(activeSorted) ;
				// Executing the internal transition will also force the
				// execution of external transitions.
				for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
					((ModelI)this.sortedSubmodels[i]).
							causalTransition(this.getCurrentStateTime()) ;
					if (((ModelI)this.sortedSubmodels[i]).getCurrentStateTime().
											equals(this.currentStateTime)) {
						String uri = this.sortedSubmodelURIs.get(i) ;
						int index = this.submodelURI2index.get(uri) ;
						this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
						updated.add(uri) ;
					}
				}
			}
			for (ModelI m : this.activeSubmodels) {
				try {
					int index = this.submodel2index.get(m) ;
					m.externalTransition(
							(updated.contains(m.getURI()) ?
								Duration.zero(this.getSimulatedTimeUnit())
							:	this.elapsedTimes[index].add(elapsedTime)
							)) ;
					this.elapsedTimes[index] =
								Duration.zero(this.getSimulatedTimeUnit()) ;
					updated.add(m.getURI()) ;
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}
			for (int i = 0 ; i < this.submodels.length ; i++) {
				try {
					if(!updated.contains(this.submodels[i].getURI())) {
						this.elapsedTimes[i] =
								this.elapsedTimes[i].add(elapsedTime) ;
				}
				} catch (Exception e) {
					throw new RuntimeException(e) ;
				}
			}
			this.activeSubmodels.clear() ;
			this.computeNextEventToBeSimulated() ;
		} else {
			// This happens when a submodel had to perform an external
			// event, notified its ancestor models which call for an
			// an external transition that was already performed by their
			// parent model.
			if (this.hasDebugLevel(2)) {
				this.logMessage("CoupledModel>>externalTransition " +
								this.uri + " has no active submodel.") ;
			}
		}

		// Postconditions
		for (int i = 0 ; i < this.submodels.length ; i++) {
			assert	((ModelI)this.submodels[i]).getCurrentStateTime().add(
													this.elapsedTimes[i]).
						equals(this.getCurrentStateTime()) ;
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void 			userDefinedExternalTransition(Duration elapsedTime)
	{
		// do nothing, if not redefined by concrete user subclasses.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#confluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException(
					"The method CoupledPolymorphicModel#confluentTransition"
					+ " is not implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedConfluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedConfluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException(
					"No confluent transition on coupled models.") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		for (int i = 0 ; i < this.sortedSubmodels.length ; i++) {
			((ModelI)this.sortedSubmodels[i]).endSimulation(endTime) ;
		}
		for (int i = 0 ; i < this.unsortedSubmodels.length ; i++) {
			((ModelI)this.unsortedSubmodels[i]).endSimulation(endTime) ;
		}
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((ModelI)this.submodels[i]).setSimulationRunParameters(simParams) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		// Default implementation; redefine if a specific report is needed.
		StandardCoupledModelReport report =
							new StandardCoupledModelReport(this.getURI()) ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			report.addReport(this.submodels[i].getFinalReport()) ;
		}
		return report ;
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode()
	{
		super.toggleDebugMode() ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			this.getSubmodel(i).toggleDebugMode() ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		assert	newDebugLevel >= 0 ;

		super.setDebugLevel(newDebugLevel) ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			this.submodels[i].setDebugLevel(newDebugLevel) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		System.out.println(indent + "---------------------------------------------") ;
		String name = this.getClass().getCanonicalName() ;
		if (name.contains(".")) {
			int index = name.lastIndexOf('.') ;
			name = name.substring(index + 1) ;
		}
		System.out.println(
					indent + name + " " + this.uri
					+ " " + this.currentStateTime.getSimulatedTime()
					+ " " + elapsedTime.getSimulatedDuration()) ;
		System.out.println(indent + "---------------------------------------------") ;
		this.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent+ "---------------------------------------------") ;		
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
		String et = "elapsed times = {" ;
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
			try {
				et += "(" + this.submodels[i].getURI() + " => " + this.elapsedTimes[i].getSimulatedDuration() + ")" ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
			if (i < this.elapsedTimes.length - 1) {
				et += ", " ;
			}
		}
		System.out.println(indent + et + "}") ;
		try {
			System.out.println(indent + "next event submodel = " + this.submodelOfNextEvent.getURI()) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		System.out.println(indent + "time of last event = " + this.currentStateTime.getSimulatedTime()) ;
		System.out.println(indent + "next time advance = " + this.nextTimeAdvance.getSimulatedDuration()) ;
		System.out.println(indent + "time of next event = " + this.timeOfNextEvent.getSimulatedTime()) ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			((Model)this.submodels[i]).showCurrentState(
					indent + "    ", this.elapsedTimes[i]) ;
		}

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
		ret += indent +
					"---------------------------------------------------\n" ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			ret += this.submodels[i].modelAsString(indent + "    ") ;
			if (this.submodels[i] instanceof SimulatorI) {
				ret += "\n" ;
			}
			ret += indent +
					"---------------------------------------------------\n" ;
		}
		return ret ;
	}

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation string.
	 * @return				a string representing the model.
	 * @throws Exception	<i>TODO</i>.
	 */
	protected String			modelContentAsString(String indent)
	throws Exception
	{
		String ret = "" ;
		try {
			ret += super.modelAsString(indent) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		ret += indent + "submodels = [" ;
		for (int i = 0 ; i < this.submodels.length ; i++) {
			try {
				ret += this.submodels[i].getURI() ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
			if (i < this.submodels.length - 1) {
				ret += ", " ;
			}
		}
		ret += "]\n" ;
		if (this.internalConnections.size() == 0) {
			ret += indent + "connections = none\n" ;
		} else {
			ret += indent + "connections =\n" ;
			for (EventSource eep : this.internalConnections.keySet()) {
				ret += indent + "  (" + eep.exportingModelURI + ", " +
						eep.sourceEventType.getName() + ") ==> [" ;
				for (int j = 0 ; j < this.internalConnections.get(eep).length ; j++) {
					ret += "(" + this.internalConnections.get(eep)[j].
													importingModelURI
							+ ", " +
							this.internalConnections.get(eep)[j].
													sourceEventType.getName()
							+ ", " +
							this.internalConnections.get(eep)[j].
													sinkEventType.getName()
							+ ")" ;
					if (j < this.internalConnections.get(eep).length - 1) {
						ret += ", " ;
					}
				}
				ret += "]\n" ;
			}
		}
		ret += indent + "Imported variables:\n" ;
		for (StaticVariableDescriptor vd : this.importedVariables) {
			ret += indent + "  VariableDescriptor(" + vd.getName() + ", " +
							vd.getType() + ", " + vd.getVisibility() + ")\n" ;
		}
		ret += indent + "Exported variables:\n" ;
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			ret += indent + "  VariableDescriptor(" + vd.getName() + ", " +
							vd.getType() + ", " + vd.getVisibility() + ")\n" ;
		}
		ret += indent + "Imported variables sinks:\n" ;
		for (StaticVariableDescriptor vd : this.importedVars2sinks.keySet()) {
			ret += indent + "  VariableDescriptor(" + vd.getName() + ", " + 
						vd.getType() + ", " + vd.getVisibility() + ") ==> [" ;
			VariableSink[] sinks = this.importedVars2sinks.get(vd) ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret += "VariableSink(" + sinks[i].sinkModelURI + ", "
						+ sinks[i].importedVariableName + ", "
						+ sinks[i].importedVariableType + ", "
						+ sinks[i].sinkVariableName + ", "
						+ sinks[i].sinkVariableType + ")" ;
				if (i < sinks.length - 1) {
					ret += ", " ;
				}
			}
			ret += "]\n" ;
		}
		ret += indent + "Exported variables sources:\n" ;
		for (VariableSource vs : this.exportedVars2reexported.keySet()) {
			StaticVariableDescriptor vd = this.exportedVars2reexported.get(vs) ;
			ret += indent + "  VariableSource(" + vs.exportingModelURI + ", "
							+ vs.name + ", "
							+ vs.type
							+ ") == > VariableDescriptor("
							+ vd.getName() + ", "
							+ vd.getType() + ", "
							+ vd.getVisibility() + ")\n" ;
		}
		ret += indent + "InternalConnections:\n" ;
		for (VariableSource vs : this.internalBindings.keySet()) {
			ret += indent + "  VariableSource(" + vs.exportingModelURI + ", "
					+ vs.name + ", "
					+ vs.type
					+ ") == > [" ;
			VariableSink[] sinks = this.internalBindings.get(vs) ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret += "VariableSink(" + sinks[i].sinkModelURI + ", "
						+ sinks[i].importedVariableName + ", "
						+ sinks[i].importedVariableType + ", "
						+ sinks[i].sinkVariableName + ", "
						+ sinks[i].sinkVariableType + ")" ;
				if (i < sinks.length - 1) {
					ret += ", " ;
				}
			}
			ret += "]\n" ;
		}
		return ret ;
	}
}
// -----------------------------------------------------------------------------
