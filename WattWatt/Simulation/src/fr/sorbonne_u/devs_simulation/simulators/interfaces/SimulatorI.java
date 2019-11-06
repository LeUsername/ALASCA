package fr.sorbonne_u.devs_simulation.simulators.interfaces;

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

import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;

import java.util.Map;

import fr.sorbonne_u.devs_simulation.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;
import fr.sorbonne_u.devs_simulation.models.events.AbstractAtomicSinkReference;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SimulationI</code> defines the core behaviour of
 * DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p>Created on : 2016-02-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface			SimulatorI
extends		ModelDescriptionI,
			ParentNotificationI,
			EventsExchangingI,
			SimulationManagementI
{
	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * associate the provided simulation model with the simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedModel != null
	 * pre	!this.isModelSet()
	 * post	this.isModelSet()
	 * </pre>
	 *
	 * @param simulatedModel	model to be simulated.
	 * @throws Exception		<i>TO DO</i>.
	 */
	public void			setSimulatedModel(ModelI simulatedModel)
	throws Exception ;

	/**
	 * return true if the simulator has been given its model to be simulated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulator has been given its model to be simulated.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isModelSet() throws Exception ;

	/**
	 * return the URI of the simulation engine acting as proxy to the given
	 * simulation model.
	 * 
	 * <p>
	 * As coupled models can be seen as atomic models (by the closure under
	 * composition property of DEVS models), models need not have a dedicated
	 * simulation engine but rather can share the simulation engine attached to
	 * on of their parent coupled model. This method provides a unique mean to
	 * get the reference on the simulation engine enacting the given model.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the simulation model.
	 * @return				the URI of the simulation engine acting as proxy to the given simulation model.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public String		findProxyAtomicEngineURI(String modelURI)
	throws Exception ;

	/**
	 * return the reference to the atomic engine as an event exchanging entity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	atomicEngineURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param atomicEngineURI	the URI of the atomic engine which reference is sought.
	 * @return					the reference to the atomic engine as an event exchanging entity.
	 * @throws Exception		<i>TO DO</i>.
	 */
	public AbstractAtomicSinkReference	getAtomicEngineReference(
		String atomicEngineURI
		) throws Exception ;

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * set the simulation actual parameters for a simulation run.
	 * 
	 * <p>
	 * As the simulation composed model is accessible only through its root
	 * coupled model, this method has to be called through this root. Hence,
	 * the parameter values map must contain all of the parameters values and
	 * its is propagated to all models in the composed model. Each model should
	 * define its own parameters names that are prefixed by the model URI.
	 * The map is created to contain values attached to these combined strings
	 * (model URI plus parameter name).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && simParams.size() > 0}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simParams	map from parameters names to their values.
	 */
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception ;

	/**
	 * initialise the simulation engine for a run with a time of start set to 0;
	 * for runs that will be stopped by a call to <code>stopSimulation</code>,
	 * the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulationDuration != null
	 * pre	simulationDuration.getTimeUnit().equals(this.getSimulatedTimeUnit())
	 * pre	simulationDuration.greaterThan(Duration.zero(this.getSimulatedTimeUnit()))
	 * pre	this.isModelSet()
	 * pre	!this.isSimulationInitialised()
	 * post	this.isSimulationInitialised()
	 * </pre>
	 *
	 * @param simulationDuration	duration of the simulation to be launched.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception ;

	/**
	 * initialise the simulation engine for a run with a time of start set to
	 * the given time; for runs that will be stopped by a call to
	 * <code>stopSimulation</code>, the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	startTime != null
	 * pre	startTime.getTimeUnit().equals(this.getSimulatedTimeUnit())
	 * pre	startTime.greaterThanOrEqual(Time.zero(this.getSimulatedTimeUnit()))
	 * pre	simulationDuration != null
	 * pre	simulationDuration.getTimeUnit().equals(this.getSimulatedTimeUnit())
	 * pre	simulationDuration.greaterThan(Duration.zero(this.getSimulatedTimeUnit()))
	 * pre	this.isModelSet()
	 * pre	!this.isSimulationInitialised()
	 * post	this.isSimulationInitialised()
	 * </pre>
	 *
	 * @param startTime				time at which the simulation must start.
	 * @param simulationDuration	duration of the simulation to be launched.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception ;

	/**
	 * return true if the simulation has been initialised for a run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulation has been initialised.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isSimulationInitialised() throws Exception ;

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * process the next internal event by advancing the simulation time to
	 * the forecast time of occurrence of this and then do the transition to
	 * the next state in the corresponding simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isSimulationInitialised()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			internalEventStep() throws Exception ;

	/**
	 * perform a causal event step i.e., a local update of the state of the
	 * model without producing outputs.
	 * 
	 * <p>
	 * Causal event steps are used for models with exported variables and
	 * they are triggered by internal steps of another model importing
	 * these variables. With such a mechanism, algebraic equation models
	 * can then be executed only on demand. This is an addition to the
	 * standard DEVS simulation protocol.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isSimulationInitialised()
	 * pre	current != null and 
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current		the simulation clock value to which the mode must advance.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			causalEventStep(Time current) throws Exception ;

	/**
	 * process an external input event at a simulated time which must
	 * correspond to the current value of the global simulated time clock
	 * at the time of the call i.e., the sum of the time of the last
	 * event in this engine and the given elapsed time.
	 * 
	 * <p>
	 * The processing first cancel the previously forecast internal event
	 * if any and then make the transition to a new model state given the
	 * processed external event and then forecast a new internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	elapsedTime != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	time elapsed since the last event executed by this engine.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			externalEventStep(Duration elapsedTime)
	throws Exception ;

	/**
	 * process an external input event or an internal event at the given
	 * simulated time which must correspond to the current value of the
	 * simulated time clock at the time of the call; when more than on event
	 * can be executed, only one is chosen and executed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	elapsedTime != null
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param elapsedTime	time elapsed since the last event executed by this engine.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			confluentEventStep(Duration elapsedTime)
	throws Exception ;

	/**
	 * force the model to forward externals event generated before their
	 * next internal transition step.
	 * 
	 * <p>
	 * The implementation follows the principle of the "peer message
	 * exchanging implementation" of DEVS i.e., the simulators exchanges
	 * directly with each others the exported and imported events without
	 * passing through their parent (and ancestors).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	current != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current		current simulation time at which external events may be output.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			produceOutput(Time current)
	throws Exception ;

	/**
	 * return the time of occurrence of the last event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the time of occurrence of the last event.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Time			getTimeOfLastEvent() throws Exception ;

	/**
	 * return the currently forecast time of occurrence of the next event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the currently forecast time of occurrence of the next event.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Time			getTimeOfNextEvent() throws Exception ;

	/**
	 * return the duration until the next internal event as previously
	 * computed by <code>timeAdvance()</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the duration until the next internal event as previously computed by <code>timeAdvance()</code>.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Duration		getNextTimeAdvance() throws Exception ;

	/**
	 * terminate the current simulation, doing the necessary catering tasks.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	endTime != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param endTime		time at which the simulation ends.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			endSimulation(Time endTime) throws Exception ;


	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * activate the lowest debug level or deactivate the debug mode completely.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>TODO</i>.
	 */
	public void			toggleDebugMode() throws Exception ;

	/**
	 * return true if the debug level is greater than 0.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the debug level is greater than 0.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		isDebugModeOn() throws Exception ;

	/**
	 * true if the current debug level is equal to <code>debugLevel</code>,
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code debugLevel >= 0}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param debugLevel	a debug level to be tested.
	 * @return				true if the current debug level is equal to <code>debugLevel</code>.
	 * @throws Exception	<i>TODO</i>.
	 */
	public boolean		hasDebugLevel(int debugLevel) throws Exception ;

	/**
	 * print the current state.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 * @throws Exception	<i>to do.</i>
	 */
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		) throws Exception ;

	/**
	 * print the content of the current state, without pre- and post-formatting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 * @throws Exception	<i>to do.</i>
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception ;
}
// -----------------------------------------------------------------------------
