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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.AtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicEngine</code> implements the DEVS simulation protocol
 * to run simulation models that are either atomic or coupled models which
 * submodels all share this atomic engine (i.e., the same simulation algorithm).
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicEngine
extends		SimulationEngine
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the atomic engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public				AtomicEngine()
	{
		super() ;
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
		assert	this.isModelSet() ;

		return this.simulatedModel.isDescendentModel(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventExchangingDescendentModel(java.lang.String)
	 */
	@Override
	public EventsExchangingI	getEventExchangingDescendentModel(String uri) throws Exception
	{
		return this.simulatedModel.getEventExchangingDescendentModel(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	this.isModelSet() ;

		return this.simulatedModel.getEventAtomicSource(ce) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	this.isModelSet() ;

		Set<CallableEventAtomicSink> aes =
							this.simulatedModel.getEventAtomicSinks(ce) ;
		Set<CallableEventAtomicSink> ret = new HashSet<>() ;
		for (CallableEventAtomicSink as : aes) {
			ret.add(new CallableEventAtomicSink(
							as.importingModelURI,
							as.sourceEventType,
							as.sinkEventType,
							new AtomicSinkReference(this),
							as.converter)) ;
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
		assert	this.isModelSet() ;

		if (this.getURI().equals(modelURI) ||
							this.simulatedModel.isDescendentModel(modelURI)) {
			return this.getURI() ;
		} else {
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getAtomicEngineReference(java.lang.String)
	 */
	@Override
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		) throws Exception
	{
		assert	atomicEngineURI != null ;

		if (this.getURI().equals(atomicEngineURI)) {
			return new AtomicSinkReference(this) ;
		} else {
			return null ;
		}
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
		assert	this.isModelSet() ;
		assert	modelURI != null ;
		assert	this.getURI().equals(modelURI) ||
					this.simulatedModel.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	influencees != null && influencees.size() != 0 ;

		this.simulatedModel.addInfluencees(modelURI, ce, influencees) ;
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
		assert	this.isModelSet() ;
		assert	modelURI != null ;
		assert	this.getURI().equals(modelURI) ||
					this.simulatedModel.isDescendentModel(modelURI) ;
		assert	ce != null ;

		return this.simulatedModel.getInfluencees(modelURI, ce) ;
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
		assert	this.isModelSet() ;
		assert	modelURI != null ;
		assert	this.getURI().equals(modelURI) ||
					this.simulatedModel.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	modelURIs != null && !modelURIs.isEmpty() ;

		return this.simulatedModel.
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
		assert	this.isModelSet() ;
		assert	modelURI != null ;
		assert	this.getURI().equals(modelURI) ||
					this.simulatedModel.isDescendentModel(modelURI) ;
		assert	ce != null ;
		assert	modelURI != null ;

		return this.simulatedModel.
					isInfluencedThrough(modelURI, destinationModelURI, ce) ;
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
	public boolean		isExportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.simulatedModel.isExportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.simulatedModel.isImportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		return this.simulatedModel.getImportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
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
		return this.simulatedModel.getActualExportedVariableValueReference(
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
		this.simulatedModel.setImportedVariableValueReference(
					modelURI, sinkVariableName, sinkVariableType, value) ;
	}

	// -------------------------------------------------------------------------
	// Simulation related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#readyForSimulation()
	 */
	@Override
	public boolean		readyForSimulation()
	{
		throw new RuntimeException(
				"AtomicEngine>>readyForSimulation() not implemented yet!") ;
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
		assert	this.isModelSet() ;

		super.initialiseSimulation(startTime, simulationDuration) ;
		this.simulatedModel.initialiseState(this.timeOfStart) ;
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent() ;
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		assert	this.isModelSet() ;

		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
								"AtomicEngine#isSimulationInitialised ") ;
		}

		return super.isSimulationInitialised() &&
				this.simulatedModel.isStateInitialised() &&
				this.getTimeOfNextEvent() != null &&
				this.getNextTimeAdvance() != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep() throws Exception
	{
		assert	this.isModelSet() ;

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
						"AtomicEngine>>internalEventStep " + this.getURI()) ;
		}
		this.simulatedModel.internalTransition() ;
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime() ;
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent() ;
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance() ;

		assert	this.nextTimeAdvance.equals(
					this.timeOfNextEvent.subtract(this.timeOfLastEvent)) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#causalEventStep(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalEventStep(Time current) throws Exception
	{
		assert	this.isModelSet() ;
		assert	current != null &&
					this.simulatedModel.getCurrentStateTime().
													lessThanOrEqual(current) ;

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
							"AtomicEngine#causalEventStep " + this.getURI()) ;
		}

		this.simulatedModel.causalTransition(current) ;
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime() ;
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent() ;
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance() ;

		assert	this.nextTimeAdvance.equals(
					this.timeOfNextEvent.subtract(this.timeOfLastEvent)) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception
	{
		if (!this.isRoot()) {
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
		if (!this.isRoot()) {
			this.getParent().hasPerformedExternalEvents(modelURI) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current) throws Exception
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
				"AtomicEngine#produceOutput " + this.getURI() + " " + current) ;
			if (!this.isSimulationInitialised()) {
				this.simulatedModel.logMessage(
						"%%% super.isSimulationInitialised() " +
									super.isSimulationInitialised()) ;
				this.simulatedModel.logMessage(
						"%%% this.simulatedModel.isStateInitialised() " +
									this.simulatedModel.isStateInitialised()) ;
				this.simulatedModel.logMessage(
						"%%% this.getTimeOfNextEvent() != null " +
									(this.getTimeOfNextEvent() != null)) ;
				this.simulatedModel.logMessage(
						"%%% this.getNextTimeAdvance() != null " +
									(this.getNextTimeAdvance() != null)) ;
			}
		}
		assert	this.isSimulationInitialised() ;

		this.simulatedModel.produceOutput(current) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI#storeInput(java.lang.String, java.util.Vector)
	 */
	@Override
	public void			storeInput(String destinationURI, Vector<EventI> es)
	throws Exception
	{
		assert	this.isModelSet() ;

		if (this.simulatedModel.getURI().equals(destinationURI)) {
			((EventsExchangingI)this.simulatedModel).
											storeInput(destinationURI, es) ;
		} else if (this.simulatedModel.isDescendentModel(destinationURI)) {
			((CoupledModel)this.simulatedModel).
						getEventExchangingDescendentModel(destinationURI).
											storeInput(destinationURI, es) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		assert	this.isModelSet() ;

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
					"AtomicEngine>>externalEventStep " + this.getURI()) ;
		}

		this.simulatedModel.externalTransition(elapsedTime) ;
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime() ;
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent() ;
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance() ;

		assert	this.nextTimeAdvance.equals(
					this.timeOfNextEvent.subtract(this.timeOfLastEvent)) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#confluentEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentEventStep(Duration elapsedTime) throws Exception
	{
		throw new RuntimeException(
					"AtomicEngine>>confluentEventStep() not implemented yet!") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime) throws Exception
	{
		this.simulatedModel.endSimulation(endTime) ;
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
		this.simulatedModel.setSimulationRunParameters(simParams) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		return this.simulatedModel.getFinalReport() ;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		super.showCurrentStateContent(indent, elapsedTime) ;
		this.simulatedModel.showCurrentState(indent + "    ", elapsedTime) ;
	}
}
// -----------------------------------------------------------------------------
