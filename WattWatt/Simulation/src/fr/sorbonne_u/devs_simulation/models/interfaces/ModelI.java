package fr.sorbonne_u.devs_simulation.models.interfaces;

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

import fr.sorbonne_u.devs_simulation.interfaces.MessageLoggingI;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelI</code> is the root of the type hierarchy defining
 * variants of DEVS simulation models for a component plug-in implementing a
 * family of DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * DEVS is a simulation protocol that implements modular discrete event
 * simulation models which can then be simulated in a centralised or
 * distributed way.
 * </p>
 * <p>
 * B.P. Zeigler, H. Praehofer et T.G. Kim, Theory of Modeling and Simulation,
 * Academic Press, 2nd edition, 2000.
 * </p>
 * 
 * 
 * <p><i>Debugging facilities</i></p>
 * 
 * <p>
 * DEVS models all share the fact that they have a unique identifier (URI), a
 * simulation time unit used by their simulation clock and a simulation engine
 * enacting them. Besides these standard services, models can be traced at
 * execution for debugging purposes. The interface defines methods to control
 * this debugging facility.
 * </p>
 * 
 * <p>Created on : 2016-03-25</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ModelI
extends		ModelDescriptionI
{
	// -------------------------------------------------------------------------
	// Model-model and model-simulator relationships
	// -------------------------------------------------------------------------

	/**
	 * return the simulation engine instance currently running this atomic
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return				the simulation engine instance currently running this model.
	 * @throws Exception	<i>TODO.</i>
	 */
	public SimulatorI	getSimulationEngine()  throws Exception ;

	// -------------------------------------------------------------------------
	// Simulation runs related methods
	// -------------------------------------------------------------------------

	/**
	 * set the simulation parameters for a simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null and simParams.size() > 0}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simParams		map from parameters names to their values.
	 * @throws Exception	<i>TODO.</i>
	 */
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception ;

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the model is initialised and ready to start a
	 * simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the model is initialised.
	 */
	public boolean		isStateInitialised() ;

	/**
	 * initialise the state of the model to make it ready to execute a
	 * simulation run with default initial time set to 0.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	this.isInitialised()
	 * </pre>
	 *
	 */
	public void			initialiseState() ;

	/**
	 * initialise the state of the model to make it ready to execute a
	 * simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	initialTime != null
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.getSimulatedTimeUnit()))
	 * post	this.isInitialised()
	 * </pre>
	 *
	 * @param initialTime	time of the simulation when it will start.
	 */
	public void			initialiseState(Time initialTime) ;

	/**
	 * return true if all the clocks in all the models are synchronised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if all the clocks in all the models are synchronised.
	 */
	public boolean		clockSynchronised() ;

	/**
	 * return the current simulation time at which the model executed its
	 * last event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the current simulation time at which the model executed its last event.
	 */
	public Time			getCurrentStateTime() ;

	/**
	 * return the duration until the next internal event as previously
	 * computed by <code>timeAdvance()</code> but in a side effect free manner.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the DEVS protocol, the function <code>timeAdvance</code> return the
	 * duration until the next internal event, but it is assumed to be a
	 * pure function. In an object-oriented implementation, the user (who
	 * implements <code>timeAdvance</code>) may have to implement it with
	 * some side effects on the object representing the model. Hence, it
	 * should be called only once each time an internal event is executed.
	 * If the user need to access this value again, he/seh must be provided
	 * with a side effect free way to do so. This method does exactly that.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the duration until the next internal event as previously computed by <code>timeAdvance()</code>.
	 */
	public Duration		getNextTimeAdvance() ;

	/**
	 * return the time at which the next internal event will happen in a side
	 * effect free manner.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard purely functional definition of the DEVS protocol, this
	 * method would not be required, as it is the addition of the result of
	 * the function <code>timeAdvance</code> and the current simulated time of
	 * the model. In this object-oriented implementation, the user may
	 * implement <code>timeAdvance</code> with some side effects, hence if
	 * he/she needs to access this information elsewhere, a side effect free
	 * way to do so must be provided. This method does exactly that.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the time at which the next internal event will happen.
	 */
	public Time			getTimeOfNextEvent() ;

	/**
	 * return the time the simulated system remains in the current state
	 * before making a transition to the next sequential state; the method
	 * must be implemented by the user in each atomic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the time the simulated system remains in the current state
	 *			before making an internal transition to the next state.
	 */
	public Duration		timeAdvance() ;

	/**
	 * do the next internal transition, both calling the method
	 * <code>userDefinedInternalTransition</code> and doing all of the
	 * necessary internal catering work.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard DEVS protocol, this method is assumed to be defined by
	 * users in their atomic models. However, having only one method to do so
	 * makes it difficult to include common behaviours for models. Moreover,
	 * DEVS allows to define new types of models where specific common
	 * behaviours must be implemented. With the traditional DEVS vision, this
	 * would end up asking the users to cater for these common behaviours in
	 * each of their implementations the method, an error-prone burden put on
	 * their shoulders. In this library, we prefer to split transitions in two
	 * parts:
	 * </p>
	 * <ul>
	 * <li>a library defined part, here <code>internalTransition</code>, which
	 *   implementations in abstract classes include their common behaviours
	 *   and, for atomic models, calls the user model specific part;</li>
	 * <li>a user model-specific part, here
	 *   <code>userDefinedInternalTransition</code>, which implements the
	 *   execution internal events of the user-defined models
	 *   <i>per se</i>.</li>
	 * </ul> 
	 * <p>
	 * After the execution of the method, the relationships between
	 * <code>timeAdvance</code>, <code>getTimeOfNextEvent()</code>,
	 * <code>getNextTimeAdvance</code> and <code>getCurrentStateTime()</code>
	 * is restored to reflect the wait for the next internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.getNextTimeAdvance().lessThan(Duration.INFINITY)
	 * pre	this.getNextTimeAdvance().equals(
	 * 						this.getTimeOfNextEvent().subtract(
	 * 									this.getCurrentStateTime()))
	 * post	this.getTimeOfNextEvent().equals(Time.INFINITY) or
	 * 				this.getTimeOfNextEvent().subtract(
	 * 										this.getCurrentStateTime()).
	 * 							equals(this.getNextTimeAdvance())
	 * </pre>
	 *
	 */
	public void			internalTransition() ;

	/**
	 * do an internal transition; this method is user model-dependent hence
	 * must be implemented by the user for atomic models; the user
	 * implementation should not change any of the implementation related
	 * variables.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The current simulation time when this method is called can be accessed
	 * by calling the method <code>getCurrentStateTime</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	this.getCurrentStateTime().equals(this.getTimeOfNextEvent())
	 * pre	elapsedTime != null and elapsedTime.lessThan(Duration.INFINITY)
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param elapsedTime	time since the last event.
	 */
	public void			userDefinedInternalTransition(Duration elapsedTime) ;

	/**
	 * do a causal transition i.e., an internal transition triggered by the
	 * internal transition of an influenced model; causal transitions do not
	 * produce external events.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * This method has been introduced by the library when the HIOA models
	 * defined by algebraic equations were introduced in order to allow an
	 * implementation that reevaluates variables on request only. For other
	 * models, new variable values and events are produced by the execution of
	 * events, but in this case, this allows an influenced model to require
	 * new values only when they are needed this reducing the computational
	 * workload.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	current != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current current simulation time.
	 */
	public void			causalTransition(Time current) ;

	/**
	 * do the next external transition, both calling the method
	 * <code>userDefinedExternalTransition</code> and doing all of the
	 * necessary internal catering work.

	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard DEVS protocol, this method is assumed to be defined by
	 * users in their atomic models. However, having only one method to do so
	 * makes it difficult to include common behaviours for models. Moreover,
	 * DEVS allows to define new types of models where specific common
	 * behaviours must be implemented. With the traditional DEVS vision, this
	 * would end up asking the users to cater for these common behaviours in
	 * each of their implementations the method, an error-prone burden put on
	 * their shoulders. In this library, we prefer to split transitions in two
	 * parts:
	 * </p>
	 * <ul>
	 * <li>a library defined part, here <code>externalTransition</code>, which
	 *   implementations in abstract classes include their common behaviours
	 *   and, for atomic models, calls the user model specific part;</li>
	 * <li>a user model-specific part, here
	 *   <code>userDefinedExternalTransition</code>, which implements the
	 *   execution internal events of the user-defined models
	 *   <i>per se</i>.</li>
	 * </ul> 
	 * <p>
	 * After the execution of the method, the relationships between
	 * <code>timeAdvance</code>, <code>getTimeOfNextEvent()</code>,
	 * <code>getNextTimeAdvance</code> and <code>getCurrentStateTime()</code>
	 * is restored to reflect the wait for the next internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	elapsedTime != null
	 * pre	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime)
	 * pre	this.getCurrentStateTime().add(elapsedTime).
	 * 						lessThanOrEqual(this.getTimeOfNextEvent())
	 * post	this.getTimeOfNextEvent().equals(Time.INFINITY) or
	 * 				this.getTimeOfNextEvent().subtract(
	 * 										this.getCurrentStateTime()).
	 * 							equals(this.getNextTimeAdvance())
	 * </pre>
	 *
	 * @param elapsedTime	elapsed time since the last transition.
	 */
	public void			externalTransition(Duration elapsedTime) ;

	/**
	 * do an external transition using a previously stored external input
	 * event; this method is user-model-dependent hence must be implemented
	 * by the user for atomic models; the user implementation should not change
	 * any of the implementation related variables.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The current simulation time when this method is called can be accessed
	 * by calling the method <code>getCurrentStateTime</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	elapsedTime != null
	 * pre	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	public void			userDefinedExternalTransition(Duration elapsedTime) ;

	/**
	 * do a confluent transition i.e., processing internal and external
	 * transitions occurring at the same simulation time using previously
	 * stored external input events.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	elapsedTime != null
	 * pre	elapsedTime.lessThanOrEqual(this.timeAdvance())
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	public void			confluentTransition(Duration elapsedTime) ;

	/**
	 * do a confluent transition i.e., processing internal and external
	 * transitions occurring at the same simulation time using previously
	 * stored external input events; this method is user-model-dependent
	 * hence must be implemented by the user for atomic models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isInitialised()
	 * pre	elapsedTime != null
	 * pre	elapsedTime.lessThanOrEqual(this.timeAdvance())
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	public void			userDefinedConfluentTransition(Duration elapsedTime) ;

	/**
	 * force the model to forward externals event generated before their
	 * next internal transition step.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The implementation follows the principle of the "peer message
	 * exchanging implementation" i.e., the simulators exchanges directly
	 * with each others the exported and imported events without passing
	 * by their parent (and ancestors).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	current != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time at which external events may be output.
	 */
	public void			produceOutput(Time current) ;

	/**
	 * terminate the current simulation.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * This method can be user-defined, allowing to perform some catering work
	 * like gathering data or computing statistics that depends upon the end
	 * time of the simulation (and that would likely be included in the
	 * simulation report of the model).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param endTime		time at which the simulation ends.
	 * @throws Exception	<i>todo.</i>
	 */
	public void			endSimulation(Time endTime) throws Exception ;

	// -------------------------------------------------------------------------
	// Debugging behaviour
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
	 */
	public void			toggleDebugMode() ;

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
	 */
	public boolean		isDebugModeOn() ;

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
	 */
	public boolean		hasDebugLevel(int debugLevel) ;

	/**
	 * set the logger of this model i.e. an object through which
	 * logging can be done during simulation runs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	logger != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param logger	the logger to be set.
	 */
	public void			setLogger(MessageLoggingI logger) ;

	/**
	 * log a message through the looger or do nothing if no logger
	 * is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param message	message to be logged.
	 */
	public void			logMessage(String message) ;

	/**
	 * show the current state of the model during the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent			spacing added  before all printed outputs.
	 * @param elapsedTime	parents knowledge of the elapsed time since the last transition for this model.
	 */
	public void			showCurrentState(String indent, Duration elapsedTime) ;

	/**
	 * show the current state of the model during the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param indent			spacing added  before all printed outputs.
	 * @param elapsedTime	parents knowledge of the elapsed time since the last transition for this model.
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime) ;
}
// -----------------------------------------------------------------------------
