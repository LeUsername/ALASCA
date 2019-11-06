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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractCoupledModelFactory</code> provides the
 * basic implementation for coupled model factories.
 *
 * <p><strong>Description</strong></p>
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
public abstract class	AbstractCoupledModelFactory
implements	CoupledModelFactoryI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L ;
	/** models to be composed.												*/
	protected ModelDescriptionI[]							models ;
	/** URI of the resulting coupled model.									*/
	protected String										modelURI ;
	/** simulation engine to be associated with the model or null if none.	*/
	protected SimulatorI									se ;
	/** imported events and their conversion to submodels imported ones.	*/
	protected Map<Class<? extends EventI>,EventSink[]>		imported ;
	/** exported events from submodels and their conversion to
	 *  exported ones.														*/
	protected Map<Class<? extends EventI>,ReexportedEvent>	reexported ;
	/** connections between exported and imported events among submodels.	*/
	protected Map<EventSource,EventSink[]>					connections ;
	/** variables imported by this coupled model (for HIOA).				*/
	protected Map<StaticVariableDescriptor,VariableSink[]>	importedVars ;
	/** variables exported by this coupled model (for HIOA).				*/
	protected Map<VariableSource,StaticVariableDescriptor>	reexportedVars ;
	/** bindings between exported and imported variables among submodels.	*/
	protected Map<VariableSource,VariableSink[]>			bindings ;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#setCoupledModelCreationParameters(fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI[], java.lang.String, fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI, java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public void			setCoupledModelCreationParameters(
		ModelDescriptionI[] models,
		String modelURI,
		SimulatorI se,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		) throws Exception
	{
		assert	!this.modelParametersSet() ;
		assert	modelURI != null ;
		assert	models != null && models.length > 1 ;
		TimeUnit tu = models[0].getSimulatedTimeUnit() ;
		for (int i = 1 ; i < models.length ; i++) {
			assert	tu.equals(models[i].getSimulatedTimeUnit()) ;
		}

		this.models = models ;
		this.modelURI = modelURI ;
		this.se = se ;
		this.imported = imported ;
		this.reexported = reexported ;
		this.connections = connections ;
		this.importedVars = 
				(importedVars != null ?
					importedVars
				:	new HashMap<StaticVariableDescriptor, VariableSink[]>()
				) ;
		this.reexportedVars =
				(reexportedVars != null ?
					reexportedVars
				:	new HashMap<VariableSource, StaticVariableDescriptor>()
				) ;
		this.bindings =
				(bindings != null ?
					bindings
				:	new HashMap<VariableSource, VariableSink[]>()
				) ;

		assert	this.modelParametersSet() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#modelParametersSet()
	 */
	@Override
	public boolean		modelParametersSet()
	{
		return this.models != null && this.modelURI != null &&
				this.importedVars != null && this.reexportedVars != null
				&& this.bindings != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createAtomicSimulationEngine()
	 */
	@Override
	public SimulationEngine		createAtomicSimulationEngine()
	{
		return new AtomicEngine() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createCoordinationEngine()
	 */
	@Override
	public SimulationEngine		createCoordinationEngine()
	{
		return new CoordinationEngine() ;
	}
}
// -----------------------------------------------------------------------------
