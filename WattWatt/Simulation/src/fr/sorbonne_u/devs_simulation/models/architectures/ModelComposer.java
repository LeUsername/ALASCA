package fr.sorbonne_u.devs_simulation.models.architectures;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.ParentReferenceI;
import fr.sorbonne_u.devs_simulation.models.StandardParentReference;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventConverterI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ModelComposer</code> provides methods implementing model
 * composition; it is abstract as it only defines static methods.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Composing standard DEVS models requires to connect models exporting events
 * to other models importing them. A coupled model importing some event must
 * also know to which of its submodels the event must be propagated, and when
 * it exports some event, it must also know which one of its submodels is
 * producing and exporting the events. As coupled models can have other
 * coupled models as submodels, an imported event by a coupled model can be
 * consumed by an atomic model several levels below in the hierarchy, while
 * an exported event can be produced by an atomic model also several levels
 * below in the hierarchy.
 * </p>
 * <p>
 * In this implementation of DEVS, atomic models producing and exporting some
 * events propagate them directly to the atomic models importing and consuming
 * them. Therefore, when composing models, the composition gets the actual
 * reference of the consuming models to connect them directly into the
 * producing model.
 * </p>
 * <p>
 * When a coupled model imports some type of events, it may propagate them
 * to submodels importing different types of events, and then use a conversion
 * function at run time to convert events it receives into events that its
 * submodels can consume. Similarly, when re-exporting events, the type of
 * events exported by the coupled model may be different from the ones
 * exported by its submodels and then a conversion must also be applied. Hence,
 * when connecting two models, a series of conversion may be required to
 * transform the produced event into an event that the receiver can consume.
 * Each point-to-point connection between a producer and a consumer model
 * therefore has to store the composition of the conversion functions that
 * must be applied each tie an event is propagated from the producer to the
 * consumer.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-05-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ModelComposer
implements	Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * compose the models and return the resulting coupled model that can be
	 * used as an atomic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null and models.length > 1}
	 * pre	(forall i : models[i] != null)
	 * pre	newModelURI != null
	 * pre	cmFactory != null
	 * pre	imported != null
	 * pre	reexported != null
	 * pre	connections != null
	 * post	return != null
	 * </pre>
	 *
	 * @param models			models to be composed.
	 * @param newModelURI		URI of the resulting coupled model.
	 * @param simulationEngine	the simulation engine of the model.
	 * @param cmFactory			factory creating the right type of coupled model.
	 * @param imported			imported events and their conversion to submodels imported ones.
	 * @param reexported		exported events from the coupled model and their conversion from exported ones by submodels.
	 * @param connections		connections between exported and imported events among submodels.
	 * @return					coupled model resulting from the composition.
	 * @throws Exception		when the composition is wrong.
	 */
	public CoupledModelI	composeAsAtomicModel(
		ModelDescriptionI[] models,
		String newModelURI,
		SimulatorI simulationEngine,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections
		) throws Exception
	{
		assert	models != null && models.length > 1 ;
		// Assert that all submodels are defined (not null).
		for (int i = 0 ; i < models.length ; i++) {
			assert	models[i] != null ;
		}
		assert	newModelURI != null ;
		assert	simulationEngine == null ||
									simulationEngine instanceof AtomicEngine ;
		assert	cmFactory != null ;
		assert	reexported != null ;
		assert	connections != null ;
		assert	connections != null ;
		assert	ModelComposer.checkConsistency(models,
											  imported,
											  reexported,
											  connections) ;

		// Set the influencees in the submodels i.e., the models that
		// imports their exported events given the prescribed connections.
		this.setInfluencees(models, connections) ;

		if (!cmFactory.modelParametersSet()) {
			cmFactory.setCoupledModelCreationParameters(models,
														newModelURI,
														simulationEngine,
														imported,
														reexported,
														connections,
														null,
														null,
														null);
		}

		CoupledModelI ret = cmFactory.createCoupledModel() ;

		// Set the parent model in the submodels.
		this.setParent(ret, models) ;

		return ret ;
	}

	/**
	 * set the parent reference on each of the submodels.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	parent != null
	 * pre	submodels != null and submodels.length &gt; 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param parent		reference on the parent model.
	 * @param submodels		array of references on the submodels.
	 * @throws Exception	<i>to do.</i>
	 */
	protected void		setParent(
		ParentNotificationI parent,
		ModelDescriptionI[] submodels
		) throws Exception
	{
		
		assert	parent != null ;
		assert	submodels != null && submodels.length > 0 ;

		for (int i = 0 ; i < submodels.length ; i++) {
			submodels[i].setParent(this.makeParentReference(parent)) ;
		}
	}

	/**
	 * make a parent reference object from a parent model reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	parent != null
	 * post	ret != null
	 * </pre>
	 *
	 * @param parent	a reference to a parent model.
	 * @return			a parent reference object abstracting the parent.
	 */
	protected ParentReferenceI	makeParentReference(ParentNotificationI parent)
	{
		assert	parent != null ;
		return new StandardParentReference(parent) ;
	}

	/**
	 * check the consistency of the information provided for compositions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param models		submodels to be composed.
	 * @param imported		imported events by the new coupled model.
	 * @param reexported	reexported events by the new coupled model.
	 * @param connections	connections among event emitters and consumers among the children models.
	 * @return				true if the information is consistent.
	 * @throws Exception	<i>TODO</i>.
	 */
	public static boolean		checkConsistency(
		ModelDescriptionI[] models,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections
		) throws Exception
	{
		boolean ret = true ;

		Map<String,ModelDescriptionI> uris2models =
									new HashMap<String,ModelDescriptionI>() ;
		for (int i = 0 ; i < models.length ; i++) {
			uris2models.put(models[i].getURI(), models[i]) ;
		}

		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (EventSink[] es : imported.values()) {
			for (int i = 0 ; i < es.length ; i++) {
				ModelDescriptionI m = uris2models.get(es[i].importingModelURI) ;
				ret &= m != null ;
				ret &= m.isImportedEventType(es[i].sinkEventType) ;
			}
		}
		// Assert that all reexported event correspond to one and only one
		// existing submodel producing them.
		for (ReexportedEvent re : reexported.values()) {
			int found = 0 ;
			for (int i = 0 ; i < models.length ; i++) {
				if (models[i].isExportedEventType(re.sourceEventType)) {
					found++ ;
				}
			}
			ret &= (found == 1) ;
		}
		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey() ;
			ModelDescriptionI m = uris2models.get(es.exportingModelURI) ;
			ret &= m != null ;
			ret &= m.isExportedEventType(es.sourceEventType) ;
			EventSink[] sinks = e.getValue() ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= !sinks[i].importingModelURI.
											equals(es.exportingModelURI) ;
				ModelDescriptionI sink =
							uris2models.get(sinks[i].importingModelURI) ;
				ret &= sink != null ;
				ret &= sink.isImportedEventType(sinks[i].sinkEventType) ;
			}
		}

		return ret ;
	}

	/**
	 * Set the influencees in the submodels i.e., the models that imports
	 * their exported events given the prescribed connections.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	(forall i : models[i] != null)
	 * pre	connections != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param models		submodels of the coupled model being created.
	 * @param connections	connections of exported events to imported ones among the submodels.
	 * @throws Exception	<i>todo.</i>
	 */
	protected void		setInfluencees(
		ModelDescriptionI[] models,
		Map<EventSource,EventSink[]> connections
		) throws Exception
	{
		assert	models != null && models.length > 1 ;
		assert	connections != null ;
		for (int i = 0 ; i < models.length ; i++) {
			assert	models[i] != null ;
		}

		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey() ;
			ModelDescriptionI directSource =
							findModelByURI(es.exportingModelURI, models) ;
			EventAtomicSource atomicSource =
					directSource.getEventAtomicSource(es.sourceEventType) ;
			Set<CallableEventAtomicSink> hs =
									new HashSet<CallableEventAtomicSink>() ;
			EventSink[] directSinks = e.getValue() ;
			for (int i = 0 ; i < directSinks.length ; i++) {
				ModelDescriptionI directSinkModel = 
					findModelByURI(directSinks[i].importingModelURI, models) ;
				Set<CallableEventAtomicSink> eventAtomicSinks =
					directSinkModel.getEventAtomicSinks(
											directSinks[i].sinkEventType) ;
				for (CallableEventAtomicSink eas : eventAtomicSinks) {
					CallableEventAtomicSink completeAtomicSink =
						new CallableEventAtomicSink(
								eas.importingModelURI,
								atomicSource.sourceEventType,
								eas.sinkEventType,
								eas.importingAtomicModelReference,
								EventConverterI.compose(
										eas.converter,
										EventConverterI.compose(
												directSinks[i].converter,
												atomicSource.converter))) ;
					
					hs.add(completeAtomicSink) ;
				}
			}
			directSource.addInfluencees(atomicSource.exportingModelURI,
										atomicSource.sourceEventType,
										hs) ;
		}
	}

	/**
	 * find a model from its URI in an array of models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	modelDescriptions != null
	 * pre	(forall i : modelDescriptions[i] != null)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the sought model.
	 * @param modelDescriptions	array of models within which the model is sought.
	 * @return					the model found or null if none.
	 * @throws Exception		<i>TODO</i>.
	 */
	public static ModelDescriptionI	findModelByURI(
		String uri,
		ModelDescriptionI[] modelDescriptions
		) throws Exception
	{
		assert	uri != null ;
		assert	modelDescriptions != null ;
		for (int i = 0 ; i < modelDescriptions.length ; i++) {
			assert	modelDescriptions[i] != null ;
		}

		ModelDescriptionI ret = null ;
		for (int i = 0 ; ret == null && i < modelDescriptions.length ; i++) {
			if (modelDescriptions[i].getURI().equals(uri)) {
				ret = modelDescriptions[i] ;
			} else if (modelDescriptions[i].isDescendentModel(uri)) {
				ret = ((CoupledModelI)modelDescriptions[i]).
												getEventExchangingDescendentModel(uri) ;
			}
		}
		return ret ;
	}

	/**
	 * return the model in the array <code>models</code> that is exporting
	 * the event type <code>ce</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ce != null
	 * pre	modelDescriptions != null
	 * pre	(forall i : modelDescriptions[i] != null)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ce				an event type.
	 * @param modelDescriptions	an array of models.
	 * @return					the model in the array <code>models</code> that is exporting the event type <code>ce</code>.
	 * @throws Exception		<i>TODO</i>.
	 */
	protected static ModelDescriptionI	findExportingModel(
		Class<? extends EventI> ce,
		ModelDescriptionI[] modelDescriptions
		) throws Exception
	{
		assert	ce != null ;
		assert	modelDescriptions != null ;
		for (int i = 0 ; i < modelDescriptions.length ; i++) {
			assert	modelDescriptions[i] != null ;
		}

		ModelDescriptionI ret = null ;
		for (int i = 0 ; ret == null && i < modelDescriptions.length ; i++) {
			if (modelDescriptions[i].isExportedEventType(ce)) {
				ret = modelDescriptions[i] ;
			}
		}

		return ret ;
	}

	/**
	 * compose the models and return the resulting coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelDescriptions != null and modelDescriptions.length > 1}
	 * pre	(forall i : modelDescriptions[i] != null)
	 * pre	newModelURI != null
	 * pre	simulationEngine != null and !simulationEngine.isModelSet()
	 * pre	connections != null
	 * post	return != null
	 * </pre>
	 *
	 * @param modelDescriptions	descriptions of the models to be composed.
	 * @param newModelURI		URI of the resulting coupled model.
	 * @param simulationEngine	the simulation engine of the model.
	 * @param cmFactory			factory creating the right type of coupled model.
	 * @param imported			imported events and their conversion to submodels imported ones.
	 * @param reexported		exported events from submodels and their conversion to exported ones.
	 * @param connections		connections between exported and imported events among submodels.
	 * @return					coupled model resulting from the composition.
	 * @throws Exception		when the composition is wrong.
	 */
	public CoupledModelI	compose(
		ModelDescriptionI[] modelDescriptions,
		String newModelURI,
		SimulatorI simulationEngine,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections
		) throws Exception
	{
//		System.out.println("ModelComposer#compose 1") ;
		assert	modelDescriptions != null && modelDescriptions.length > 1 ;
		for (int i = 0 ; i < modelDescriptions.length ; i++) {
			assert	modelDescriptions[i] != null ;
		}
		assert	newModelURI != null ;
		assert	simulationEngine != null &&
							simulationEngine instanceof CoordinationEngine ;
		assert	!simulationEngine.isModelSet() ;
		assert	cmFactory != null ;
		assert	connections != null ;
//		System.out.println("ModelComposer#compose 2") ;

		Map<String,ModelDescriptionI> uris2models =
									new HashMap<String,ModelDescriptionI>() ;
		for (int i = 0 ; i < modelDescriptions.length ; i++) {
			uris2models.put(modelDescriptions[i].getURI(),
							modelDescriptions[i]) ;
		}
//		System.out.println("ModelComposer#compose 3") ;

		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (EventSink[] es : imported.values()) {
			for (int i = 0 ; i < es.length ; i++) {
				ModelDescriptionI m =
								uris2models.get(es[i].importingModelURI) ;
				assert	m != null ;
				assert	m.isImportedEventType(es[i].sinkEventType) ;
			}
		}
//		System.out.println("ModelComposer#compose 4") ;
		// Assert that all reexported event correspond to one and only one
		// existing submodel producing them.
		for (ReexportedEvent re : reexported.values()) {
			int found = 0 ;
			for (int i = 0 ; i < modelDescriptions.length ; i++) {
				if (modelDescriptions[i].isExportedEventType(
													re.sourceEventType)) {
					found++ ;
				} 
			}
			assert	found == 1 ;
		}
//		System.out.println("ModelComposer#compose 5") ;
		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey() ;
			ModelDescriptionI m = uris2models.get(es.exportingModelURI) ;
			assert	m != null ;
			assert	m.isExportedEventType(es.sourceEventType) ;
			EventSink[] sinks = e.getValue() ;
			for (int i = 0 ; i < sinks.length ; i++) {
				assert	!sinks[i].importingModelURI.
											equals(es.exportingModelURI) ;
				ModelDescriptionI sink =
								uris2models.get(sinks[i].importingModelURI) ;
				assert	sink != null ;
				assert	sink.isImportedEventType(sinks[i].sinkEventType) ;
			}
		}
//		System.out.println("ModelComposer#compose 6") ;

		SimulatorI[] ces = new SimulatorI[modelDescriptions.length] ;
		for (int i = 0 ; i < ces.length ; i++) {
			ces[i] = (SimulatorI) modelDescriptions[i] ;
		}
//		System.out.println("ModelComposer#compose 7") ;

		// Set the influencees in the submodels i.e., the models that
		// imports their exported events given the prescribed connections.
		this.setInfluencees(ces, connections) ;
//		System.out.println("ModelComposer#compose 8") ;

		if (!cmFactory.modelParametersSet()) {
			cmFactory.setCoupledModelCreationParameters(ces,
													   newModelURI,
													   simulationEngine,
													   imported,
													   reexported,
													   connections,
													   null,
													   null,
													   null) ;
		}
//		System.out.println("ModelComposer#compose 9") ;
		CoupledModel ret = (CoupledModel) cmFactory.createCoupledModel() ;
		((CoordinationEngine)simulationEngine).setCoordinatedEngines(ces) ;
//		System.out.println("ModelComposer#compose 10 " + simulationEngine) ;

		// Set the parent of the models
		this.setParent(simulationEngine, modelDescriptions) ;
//		System.out.println("ModelComposer#compose 11") ;

		return ret ;
	}
}
// -----------------------------------------------------------------------------
