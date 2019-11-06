package fr.sorbonne_u.devs_simulation.hioa.architectures;

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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.models.StandardCoupledHIOA_Factory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledHIOA_Descriptor</code> describes coupled
 * HIOA in model architectures, associating their URI, their static
 * information, as well as a factory that can be used to instantiate
 * a coupled HIOA object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				CoupledHIOA_Descriptor
extends		CoupledModelDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;
	/** Map from variables imported by this coupled HIOA and the submodels
	 *  consuming them.													*/
	public final Map<StaticVariableDescriptor,VariableSink[]>	importedVars ;
	/** Map from variables exported by this coupled HIOA and the submodels
	 *  producing them.													*/
	public final Map<VariableSource,StaticVariableDescriptor>	reexportedVars ;
	/** 	Map between variables exported by submodels to ones imported by
	 *  others.															*/
	public final Map<VariableSource,VariableSink[]>				bindings ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	/**
	 * create a new coupled HIOA creation descriptor.
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
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 */
	public				CoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory, engineCreationMode, importedVars,
			 reexportedVars, bindings, new HIOA_Composer()) ;
	}

	/**
	 * create a new coupled HIOA creation descriptor.
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
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 * @param hioa_mc				model composer to be used.
	 */
	public				CoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		SimulationEngineCreationMode engineCreationMode,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings,
		HIOA_Composer hioa_mc
		)
	{
		super(modelClass, modelURI, submodelURIs,
			  imported, reexported, connections,
			  cmFactory, engineCreationMode, hioa_mc) ;

		if (importedVars != null) {
			this.importedVars = importedVars ;
		} else {
			this.importedVars =
					new HashMap<StaticVariableDescriptor,VariableSink[]>() ;
		}
		if (reexportedVars != null) {
			this.reexportedVars = reexportedVars ;
		} else {
			this.reexportedVars =
					new HashMap<VariableSource,StaticVariableDescriptor>() ;
		}
		if (bindings != null) {
			this.bindings = bindings ;
		} else {
			this.bindings = new HashMap<VariableSource,VariableSink[]>() ;
		}

		assert	CoupledHIOA_Descriptor.checkInternalConsistency(this) ;
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * check the internal consistency of the coupled HIOA i.e., when
	 * only the URIs of the submodels are known but not their own
	 * information (imported and exported variables, ...).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	descriptor != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param descriptor		coupled HIOA descriptor to be checked.
	 * @return				true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkInternalConsistency(
		CoupledHIOA_Descriptor descriptor
		)
	{
		assert	descriptor != null ;

		boolean ret = true ;

		for (StaticVariableDescriptor vd : descriptor.importedVars.keySet()) {
			ret &= !descriptor.reexportedVars.values().contains(vd) ;
			VariableSink[] sinks = descriptor.importedVars.get(vd) ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= descriptor.submodelURIs.
								contains(sinks[i].sinkModelURI) ;
				ret &= sinks[i].importedVariableType.
								isAssignableFrom(vd.getType()) ;
			}
		}
		for (VariableSource vs : descriptor.reexportedVars.keySet()) {
			ret &= descriptor.submodelURIs.contains(vs.exportingModelURI) ;
			StaticVariableDescriptor sink = descriptor.reexportedVars.get(vs) ;
			ret &= !descriptor.importedVars.keySet().contains(sink) ;
			ret &= sink.getType().isAssignableFrom(vs.type) ;
		}
		for(Entry<VariableSource,VariableSink[]> e :
											descriptor.bindings.entrySet()) {
			VariableSource vs = e.getKey() ;
			ret &= descriptor.submodelURIs.contains(vs.exportingModelURI) ;
			VariableSink[] sinks = e.getValue() ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= descriptor.submodelURIs.
								contains(sinks[i].sinkModelURI) ;
				ret &= sinks[i].importedVariableType.
								isAssignableFrom(vs.type) ;
			}
		}
		return ret ;
	}

	/**
	 * check both the internal and the external consistency of the
	 * coupled HIOA i.e., when both the URIs of the submodels are
	 * known and their own information (imported and exported
	 * variables, ...).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	descriptor != null ;
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	for (String uri : desc.submodelURIs) {
	 *          atomicModelDescriptors.keySet().contains(uri) ||
	 *               coupledModelDescriptors.keySet().contains(uri)
	 *      }
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param descriptor					coupled HIOA descriptor to be checked.
	 * @param atomicModelDescriptors		descriptors of the atomic models including the ones that are submodels.
	 * @param coupledModelDescriptors	descriptors of the coupled models including the ones that are submodels.
	 * @return							true if the descriptor satisfies the consistency constraints.
	 */
	public static boolean	checkFullConsistency(
		CoupledHIOA_Descriptor descriptor,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	descriptor != null ;
		assert	atomicModelDescriptors != null ;
		assert	coupledModelDescriptors != null ;
		for (String uri : descriptor.submodelURIs) {
			assert	atomicModelDescriptors.keySet().contains(uri) ||
						coupledModelDescriptors.keySet().contains(uri) ;
		}

		boolean ret =
			CoupledHIOA_Descriptor.checkInternalConsistency(descriptor) ;
		ret &=
			CoupledModelDescriptor.checkFullConsistency(
												descriptor,
												atomicModelDescriptors,
												coupledModelDescriptors) ;

		for (Entry<StaticVariableDescriptor,VariableSink[]> e :
									descriptor.importedVars.entrySet()) {
			StaticVariableDescriptor vd = e.getKey() ;
			VariableSink[] sinks = e.getValue() ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= descriptor.submodelURIs.
									contains(sinks[i].sinkModelURI) ;
				ret &= sinks[i].importedVariableType.
									isAssignableFrom(vd.getType()) ;
				ret &= CoupledHIOA_Descriptor.isImportingVar(
									sinks[i].sinkModelURI,
									new StaticVariableDescriptor(
												sinks[i].sinkVariableName,
												sinks[i].sinkVariableType,
												VariableVisibility.IMPORTED),
									atomicModelDescriptors,
									coupledModelDescriptors) ;
			}
		}
		for (VariableSource vs : descriptor.reexportedVars.keySet()) {
			ret &= CoupledHIOA_Descriptor.isExportingVar(
									vs.exportingModelURI,
									new StaticVariableDescriptor(
												vs.name,
												vs.type,
												VariableVisibility.EXPORTED),
									atomicModelDescriptors,
									coupledModelDescriptors) ;
		}
		for (VariableSource vs : descriptor.bindings.keySet()) {
			ret &= CoupledHIOA_Descriptor.isExportingVar(
									vs.exportingModelURI,
									new StaticVariableDescriptor(
												vs.name,
												vs.type,
												VariableVisibility.EXPORTED),
									atomicModelDescriptors,
									coupledModelDescriptors) ;
			VariableSink[] sinks = descriptor.bindings.get(vs) ;
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= CoupledHIOA_Descriptor.isImportingVar(
									sinks[i].sinkModelURI,
									new StaticVariableDescriptor(
												sinks[i].sinkVariableName,
												sinks[i].sinkVariableType,
									VariableVisibility.IMPORTED),
						atomicModelDescriptors,
						coupledModelDescriptors) ;
			}
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
	 * @param modelURI					URI of the model to be tested.
	 * @param vd						variable descriptor to be tested.
	 * @param atomicModelDescriptors	the set of atomic model descriptors.
	 * @param coupledModelDescriptors	the set of coupled model descriptors.
	 * @return							true if the model is importing the variable.
	 */
	protected static boolean		isImportingVar(
		String modelURI,
		StaticVariableDescriptor vd,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		boolean ret = false ;
		if (atomicModelDescriptors.containsKey(modelURI)) {
			StaticVariableDescriptor[] importedVariables =
				((AtomicHIOA_Descriptor)atomicModelDescriptors.
										get(modelURI)).importedVariables ;
			for (int i = 0 ; !ret && i < importedVariables.length ; i++) {
				ret = importedVariables[i].equals(vd) ;
			}
		} else {
			assert	coupledModelDescriptors.containsKey(modelURI) ;
			ret = ((CoupledHIOA_Descriptor)coupledModelDescriptors.
							get(modelURI)).importedVars.containsKey(vd) ;
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
	 * @param modelURI					URI of the model to be tested.
	 * @param vd						variable descriptor to be tested.
	 * @param atomicModelDescriptors	the set of atomic model descriptors.
	 * @param coupledModelDescriptors	the set of coupled model descriptors.
	 * @return							true if the model is exporting the variable.
	 */
	protected static boolean		isExportingVar(
		String modelURI,
		StaticVariableDescriptor vd,
		Map<String,AtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		boolean ret = false ;
		if (atomicModelDescriptors.containsKey(modelURI)) {
			StaticVariableDescriptor[] exportedVariables =
					((AtomicHIOA_Descriptor)atomicModelDescriptors.
											get(modelURI)).exportedVariables ;
			for (int i = 0 ; !ret && i < exportedVariables.length ; i++) {
				ret = exportedVariables[i].equals(vd) ;
			}
		} else {
			assert	coupledModelDescriptors.containsKey(modelURI) ;
			ret = ((CoupledHIOA_Descriptor)coupledModelDescriptors.
					get(modelURI)).reexportedVars.values().contains(vd) ;
		}
		return ret ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor#createCoupledModel(fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI[])
	 */
	@Override
	public ModelDescriptionI		createCoupledModel(
		ModelDescriptionI[] models
		) throws Exception
	{
		assert	this.mc instanceof HIOA_Composer ;

		HashSet<String> hs = new HashSet<String>() ;
		for (int i = 0 ; i < models.length ; i++) {
			hs.add(models[i].getURI()) ;
		}
		assert	this.submodelURIs.containsAll(hs) &&
										hs.containsAll(this.submodelURIs) ;

		CoupledModelI hioa = null ;
		CoupledModelFactoryI cmFactory = null ;
		if (this.cmFactory == null ) {
			cmFactory =
				new StandardCoupledHIOA_Factory(
						(Class<? extends CoupledModelI>) this.modelClass) ;
		} else {
			cmFactory = this.cmFactory ;
		}
		if (this.engineCreationMode == SimulationEngineCreationMode.NO_ENGINE) {
			hioa = ((HIOA_Composer)this.mc).composeAsAtomicHIOA(
									models,
									this.modelURI,
									null,
									cmFactory,
									this.imported,
									this.reexported,
									this.connections,
									this.importedVars,
									this.reexportedVars,
									this.bindings) ;
			return hioa ;
		} else if (this.engineCreationMode ==
								SimulationEngineCreationMode.ATOMIC_ENGINE) {
			hioa = ((HIOA_Composer)this.mc).composeAsAtomicHIOA(
									models,
									this.modelURI,
									cmFactory.createAtomicSimulationEngine(),
									cmFactory,
									this.imported,
									this.reexported,
									this.connections,
									this.importedVars,
									this.reexportedVars,
									this.bindings) ;
			return hioa.getSimulationEngine() ;
		} else {
			assert	this.engineCreationMode ==
						SimulationEngineCreationMode.COORDINATION_ENGINE ;
			hioa = ((HIOA_Composer)this.mc).compose(
									models,
									this.modelURI,
									cmFactory.createCoordinationEngine(),
									cmFactory,
									this.imported,
									this.reexported,
									this.connections,
									this.importedVars,
									this.reexportedVars,
									this.bindings) ;
			return hioa.getSimulationEngine() ;
		}
	}
}
// -----------------------------------------------------------------------------
