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

import java.util.Map;
import java.util.HashMap;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelComposer;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Composer</code> provides methods implementing HIOA
 * composition; it is abstract as it only defines static methods.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HIOA_Composer
extends		ModelComposer
{
	private static final long serialVersionUID = 1L;

	/**
	 * compose the HIOA and return the resulting coupled HIOA that can be
	 * used as an atomic HIOA.
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
	 * pre	importedVars != null
	 * pre	reexportedVars != null
	 * pre	bindings != null
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
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @return					coupled HIOA resulting from the composition.
	 * @throws Exception		<i>TODO</i>.
	 */
	public CoupledModelI	composeAsAtomicHIOA(
		ModelDescriptionI[] models,
		String newModelURI,
		SimulatorI simulationEngine,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings
		) throws Exception
	{
		assert	models != null && models.length > 1 ;
		// Assert that all submodels are defined (not null).
		for (int i = 0 ; i < models.length ; i++) {
			assert	models[i] != null ;
		}
		assert	newModelURI != null ;
		assert	simulationEngine ==
					null || simulationEngine instanceof AtomicEngine ;
		assert	cmFactory != null ;
		assert	imported != null ;
		assert	reexported != null ;
		assert	connections != null ;
		assert	importedVars != null ;
		assert	reexportedVars != null ;
		assert	bindings != null ;

		this.setBindings(models, bindings) ;

		cmFactory.setCoupledModelCreationParameters(models,
												   newModelURI,
												   simulationEngine,
												   imported,
												   reexported, 
												   connections,
												   importedVars,
												   reexportedVars,
												   bindings) ;

		return this.composeAsAtomicModel(
									models,
									newModelURI,
									simulationEngine,
									cmFactory,
									imported,
									reexported,
									connections) ;
	}

	/**
	 * passes the placeholders for values of exported variables to imported
	 * variables among submodels as prescribed by <code>bindings</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	(forall i : models[i] != null)
	 * pre	bindings != null
	 * </pre>
	 *
	 * @param models		models to be composed.
	 * @param bindings		bindings between exported and imported variables among submodels.
	 * @throws Exception	<i>TODO</i>.
	 */
	protected void		setBindings(
		ModelDescriptionI[] models,
		Map<VariableSource,VariableSink[]> bindings
		) throws Exception
	{
		Map<String,ModelDescriptionI> uris2models =
								new HashMap<String,ModelDescriptionI>() ;
		for (int i = 0 ; i < models.length ; i++) {
			uris2models.put(models[i].getURI(), models[i]) ;
		}

		for (VariableSource source : bindings.keySet()) {
			ModelDescriptionI sourceHIOA =
								uris2models.get(source.exportingModelURI) ;
			Value<?> valueReference =
				sourceHIOA.getActualExportedVariableValueReference(
											source.exportingModelURI,
											source.name,
											source.type) ;
			VariableSink[] sinks = bindings.get(source) ;
			for (int i = 0 ; i < sinks.length; i++) {
				ModelDescriptionI sinkHIOA =
									uris2models.get(sinks[i].sinkModelURI) ;
				sinkHIOA.setImportedVariableValueReference(
											sinks[i].sinkModelURI,
											sinks[i].sinkVariableName,
											sinks[i].sinkVariableType,
											valueReference) ;
			}
		}
	}

	/**
	 * compose the HIOA and return the resulting coupled HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null and models.length > 1}
	 * pre	(forall i : models[i] != null)
	 * pre	newModelURI != null
	 * pre	{@code simulationEngine != null &&
	 *          simulationEngine instanceof HIOA_CoordinationEngine}
	 * pre	cmFactory != null
	 * pre	imported != null
	 * pre	reexported != null
	 * pre	connections != null
	 * pre	importedVars != null
	 * pre	reexportedVars != null
	 * pre	bindings != null
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
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @return					coupled HIOA resulting from the composition.
	 * @throws Exception		<i>TODO</i>.
	 */
	public CoupledModelI	compose(
		ModelDescriptionI[] models,
		String newModelURI,
		SimulatorI simulationEngine,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings
		) throws Exception
	{
		assert	models != null && models.length > 1 ;
		// Assert that all submodels are defined (not null).
		for (int i = 0 ; i < models.length ; i++) {
			assert	models[i] != null ;
		}
		assert	newModelURI != null ;
		assert	simulationEngine != null ;
		assert	cmFactory != null ;
		assert	imported != null ;
		assert	reexported != null ;
		assert	connections != null ;
		assert	importedVars != null ;
		assert	reexportedVars != null ;
		assert	bindings != null ;

		this.setBindings(models, bindings) ;


		cmFactory.setCoupledModelCreationParameters(models,
												   newModelURI,
												   simulationEngine,
												   imported,
												   reexported, 
												   connections,
												   importedVars,
												   reexportedVars,
												   bindings) ;

		return this.compose(models,
							newModelURI,
							simulationEngine,
							cmFactory,
							imported,
							reexported,
							connections) ;
	}
}
// -----------------------------------------------------------------------------
