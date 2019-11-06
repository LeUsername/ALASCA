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
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.StandardCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledModelDescriptor</code> describes coupled
 * models in model architectures, associating their URI, their static
 * information, as well as a factory that can be used to instantiate
 * a coupled model object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				CoupledModelDescriptor
implements	Serializable,
			ModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L ;
	/** class defining the coupled model.									*/
	public final Class<? extends CoupledModelI>			modelClass ;
	/** URI of the model to be created.										*/
	public final String									modelURI ;
	/** Set of URIs of the submodels of this coupled model.					*/
	public final Set<String>							submodelURIs ;
	/** Map from imported event types to the internal sinks importing
	 *  them.																*/
	public final Map<Class<? extends EventI>,EventSink[]>		imported ;
	/** Map from event types exported by submodels to event types exported
	 *  by this coupled model.												*/
	public final Map<Class<? extends EventI>,ReexportedEvent>	reexported ;
	/** Map connecting submodel exported event types to submodels imported
	 *  ones which will consume them.										*/
	public final Map<EventSource,EventSink[]>			connections ;
	/** Coupled model factory allowing to create the model.					*/
	public final CoupledModelFactoryI					cmFactory ;
	/** Creation mode for the simulation engine.							*/
	public final SimulationEngineCreationMode			engineCreationMode ;
	public final ModelComposer							mc ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory, engineCreationMode, new ModelComposer()) ;
	}

	/**
	 * create a new coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.
	 * @param mc					model composer to be used.
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode,
		ModelComposer mc
		)
	{
		super() ;

		assert	modelClass != null ;
		assert	modelURI != null ;
		assert	submodelURIs != null && submodelURIs.size() > 1 ;
		assert	mc != null ;

		this.modelClass = modelClass ;
		this.modelURI = modelURI ;
		this.submodelURIs = submodelURIs ;
		if (imported != null) {
			this.imported = imported ;
		} else {
			this.imported = new HashMap<Class<? extends EventI>,EventSink[]>() ;
		}
		if (reexported != null) {
			this.reexported = reexported ;	
		} else {
			this.reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		}
		if (connections != null) {
			this.connections = connections ;	
		} else {
			this.connections = new HashMap<EventSource,EventSink[]>() ;
		}
		this.cmFactory = cmFactory ;
		this.engineCreationMode = engineCreationMode ;
		this.mc = mc ;

		assert	CoupledModelDescriptor.checkInternalConsistency(this) ;
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * check the internal consistency of the coupled model i.e., when
	 * only the URIs of the submodels are known but not their own
	 * information (imported and exported event types, ...).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param desc	coupled model descriptor to be checked.
	 * @return		true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkInternalConsistency(
		CoupledModelDescriptor desc
		)
	{
//		System.out.println("cic " + desc.modelURI + " -------------------->") ;
		boolean ret = true ;
		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (Entry<Class<? extends EventI>,EventSink[]> e :
													desc.imported.entrySet()) {
			EventSink[] es = e.getValue() ;
			for (int i = 0 ; i < es.length ; i++) {
				ret &= desc.submodelURIs.contains(es[i].importingModelURI) ;
			}
		}
		assert	ret ;
		// Assert that all reexported event correspond to one existing
		// submodel producing them.
		for (Entry<Class<? extends EventI>,ReexportedEvent> e :
												desc.reexported.entrySet()) {
			ReexportedEvent re = e.getValue() ;
			ret &= desc.submodelURIs.contains(re.exportingModelURI) ;
		}
		assert	ret ;
		// Assert that all connections are among existing submodels.
		for (Entry<EventSource,EventSink[]> e : desc.connections.entrySet()) {
			EventSource es = e.getKey() ;
			ret &= desc.submodelURIs.contains(es.exportingModelURI) ;
			assert	ret ;
			EventSink[] sinks = e.getValue() ;
//			System.out.println("  " + es.exportingModelURI + " ("
//					+ es.sourceEventType + ")") ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= !sinks[i].importingModelURI.
								equals(es.exportingModelURI) ;
//				System.out.println("    " + es.exportingModelURI + " ==> "
//									+ sinks[i].importingModelURI + " ("
//									+ es.sourceEventType + ")") ;
				assert	ret ;
				ret &= desc.submodelURIs.
								contains(sinks[i].importingModelURI) ;
				assert	ret ;
			}
		}
		assert	ret ;
//		System.out.println("cic " + desc.modelURI + " --------------------") ;
		return ret ;
	}

	/**
	 * check both the internal and the external consistency of the
	 * coupled model i.e., when both the URIs of the submodels are
	 * known and their own information (imported and exported event
	 * types, ...).
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	desc != null
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	for (String uri : desc.submodelURIs) {
	 *          atomicModelDescriptors.keySet().contains(uri) ||
	 *               coupledModelDescriptors.keySet().contains(uri)
	 *      }
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param desc						coupled model descriptor to be checked.
	 * @param atomicModelDescriptors		descriptors of the atomic models including the ones that are submodels.
	 * @param coupledModelDescriptors	descriptors of the coupled models including the ones that are submodels.
	 * @return							true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkFullConsistency(
		CoupledModelDescriptor desc,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	desc != null ;
		assert	atomicModelDescriptors != null ;
		assert	coupledModelDescriptors != null ;
		for (String uri : desc.submodelURIs) {
			assert	atomicModelDescriptors.keySet().contains(uri) ||
						coupledModelDescriptors.keySet().contains(uri) ;
		}

		boolean ret = CoupledModelDescriptor.checkInternalConsistency(desc) ;

		for (String uri : desc.submodelURIs) {
			ret &= atomicModelDescriptors.keySet().contains(uri) ||
						coupledModelDescriptors.keySet().contains(uri) ;
			SimulationEngineCreationMode submodelEngineCreationMode ;
			if (atomicModelDescriptors.keySet().contains(uri)) {
				submodelEngineCreationMode =
						atomicModelDescriptors.get(uri).engineCreationMode ;
			} else {
				submodelEngineCreationMode =
						coupledModelDescriptors.get(uri).engineCreationMode ;
			}
			ret &=	(desc.engineCreationMode ==
								SimulationEngineCreationMode.NO_ENGINE ?
						submodelEngineCreationMode ==
								SimulationEngineCreationMode.NO_ENGINE
					:	true
					) ;
			ret &=	(desc.engineCreationMode ==
								SimulationEngineCreationMode.ATOMIC_ENGINE ?
						submodelEngineCreationMode ==
								SimulationEngineCreationMode.NO_ENGINE
					:	true
					) ;
			ret &=	(desc.engineCreationMode ==
								SimulationEngineCreationMode.COORDINATION_ENGINE ?
						submodelEngineCreationMode ==
								SimulationEngineCreationMode.ATOMIC_ENGINE ||
						submodelEngineCreationMode ==
								SimulationEngineCreationMode.COORDINATION_ENGINE
					:	true
					) ;
		}

		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (Entry<Class<? extends EventI>,EventSink[]> e :
													desc.imported.entrySet()) {
			EventSink[] es = e.getValue() ;
			for (int i = 0 ; i < es.length ; i++) {
				CoupledModelDescriptor.isSubmodelImportingEventType(
											es[i].importingModelURI,
											es[i].sinkEventType,
											atomicModelDescriptors,
											coupledModelDescriptors) ;
			}
		}
		// Assert that all reexported event correspond to one and only one
		// existing submodel producing them.
		for (Entry<Class<? extends EventI>,ReexportedEvent> e :
												desc.reexported.entrySet()) {
			int found = 0 ;
			ReexportedEvent re = e.getValue() ;
			for (String submodelURI : atomicModelDescriptors.keySet()) {
				if (CoupledModelDescriptor.isSubmodelExportingEventType(
						submodelURI,
						re.sourceEventType,
						atomicModelDescriptors,
						coupledModelDescriptors)) {
					found++ ;
				}
			}
			ret &= (found == 1) ;
		}
		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : desc.connections.entrySet()) {
			EventSource es = e.getKey() ;
			ret &= CoupledModelDescriptor.isSubmodelExportingEventType(
												es.exportingModelURI,
												es.sourceEventType,
												atomicModelDescriptors,
												coupledModelDescriptors) ;
			EventSink[] sinks = e.getValue() ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= !sinks[i].importingModelURI.
											equals(es.exportingModelURI) ;
				ret &= CoupledModelDescriptor.isSubmodelImportingEventType(
												sinks[i].importingModelURI,
												sinks[i].sinkEventType,
												atomicModelDescriptors,
												coupledModelDescriptors) ;
			}
		}
		return ret ;
	}

	/**
	 * return true if the given submodel imports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	submodelURI != null
	 * pre	et != null
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	atomicModelDescriptors.containsKey(submodelURI) or
	 * 			coupledModelDescriptors.containsKey(submodelURI)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel imports the given event type.
	 */
	protected static boolean	isSubmodelImportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null ;
		assert	et != null ;
		assert	atomicModelDescriptors != null ;
		assert	coupledModelDescriptors != null ;
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
						coupledModelDescriptors.containsKey(submodelURI) ;

		boolean ret = false ;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] importedEvents =
					atomicModelDescriptors.get(submodelURI).importedEvents ;
			for (int i = 0 ; !ret && i < importedEvents.length ; i++) {
				ret = et.equals(importedEvents[i]) ;
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI) ;
			ret = coupledModelDescriptors.get(submodelURI).
											  imported.keySet().contains(et) ;
		}
		return ret ;
	}

	/**
	 * return true if the given submodel exports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	submodelURI != null
	 * pre	et != null
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	atomicModelDescriptors.containsKey(submodelURI) or
	 * 			coupledModelDescriptors.containsKey(submodelURI)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel exports the given event type.
	 */
	protected static boolean	isSubmodelExportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null ;
		assert	et != null ;
		assert	atomicModelDescriptors != null ;
		assert	coupledModelDescriptors != null ;
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
						coupledModelDescriptors.containsKey(submodelURI) ;

		boolean ret = false ;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] exportedEvents =
					atomicModelDescriptors.get(submodelURI).exportedEvents ;
			for (int i = 0 ; !ret && i < exportedEvents.length ; i++) {
				ret = et.equals(exportedEvents[i]) ;
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI) ;
			for (ReexportedEvent re : coupledModelDescriptors.get(submodelURI).
													reexported.values()) {
				ret = et.equals(re.sinkEventType) ;
				if (ret){
					break ;
				}
			}
		}
		return ret ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#isCoupledModelDescriptor()
	 */
	@Override
	public boolean		isCoupledModelDescriptor()
	{
		return true ;
	}

	/**
	 * create a coupled model from this descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param models		model descriptions for the submodels of this coupled model.
	 * @return				the new coupled model as a model description.
	 * @throws Exception	<i>TODO</i>.
	 */
	public ModelDescriptionI	createCoupledModel(
		ModelDescriptionI[] models
		) throws Exception
	{
//		System.out.println("CoupledModelDescriptor#createCoupledModel 1") ;
		HashSet<String> hs = new HashSet<String>() ;
		for (int i = 0 ; i < models.length ; i++) {
			hs.add(models[i].getURI()) ;
		}
		assert	this.submodelURIs.containsAll(hs) &&
										hs.containsAll(this.submodelURIs) ;

//		System.out.println("CoupledModelDescriptor#createCoupledModel 2") ;
		CoupledModelI m = null ;
		CoupledModelFactoryI cmFactory = null ;
		if (this.cmFactory == null) {
			cmFactory = new StandardCoupledModelFactory(this.modelClass) ;
		} else {
			cmFactory = this.cmFactory ;
		}

//		System.out.println("CoupledModelDescriptor#createCoupledModel 3") ;
		if (this.engineCreationMode == SimulationEngineCreationMode.NO_ENGINE) {
//			System.out.println("CoupledModelDescriptor#createCoupledModel 4") ;
			m = this.mc.composeAsAtomicModel(
								models,
								this.modelURI,
								null,
								cmFactory,
								this.imported,
								this.reexported,
								this.connections) ;
//			System.out.println("CoupledModelDescriptor#createCoupledModel 5") ;
			return m ;
		} else if (this.engineCreationMode ==
								SimulationEngineCreationMode.ATOMIC_ENGINE) {
//			System.out.println("CoupledModelDescriptor#createCoupledModel 6") ;
			m = this.mc.composeAsAtomicModel(
								models,
								this.modelURI,
								cmFactory.createAtomicSimulationEngine(),
								cmFactory,
								this.imported,
								this.reexported,
								this.connections) ;
//			System.out.println("CoupledModelDescriptor#createCoupledModel 7") ;
			return m.getSimulationEngine() ;
		} else {
//			System.out.println("CoupledModelDescriptor#createCoupledModel 8 " + this.mc) ;
			assert	this.engineCreationMode ==
							SimulationEngineCreationMode.COORDINATION_ENGINE ;
			m = this.mc.compose(models,
								this.modelURI,
								cmFactory.createCoordinationEngine(),
								cmFactory,
								this.imported,
								this.reexported,
								this.connections) ;
//			System.out.println("CoupledModelDescriptor#createCoupledModel 9") ;
			return m.getSimulationEngine() ;
		}
	}
}
// -----------------------------------------------------------------------------
