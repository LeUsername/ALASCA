package fr.sorbonne_u.devs_simulation.hioa.models;

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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicHIOAwithEquations</code> implements hybrid
 * input/output automata where the trajectories of continuous variables are
 * defined by algebraic equations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-09-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AtomicHIOAwithEquations
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic hybrid input/output model based on an algebraic
	 * equations solver with the given URI (if null, one will be generated)
	 * and to be run by the given simulator (or by the one of an ancestor
	 * coupled model if null) using the given time unit for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				AtomicHIOAwithEquations(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;
	}

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#causalTransition(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			causalTransition(Time current)
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicHIOAwithEquations#causalTransition "
											+ this.uri + " " + current) ;
		}

		assert	current != null ;
		assert	current.greaterThanOrEqual(this.getCurrentStateTime()) ;

		Duration elapsedTime = current.subtract(this.getCurrentStateTime()) ;
		this.currentStateTime = current ;
		this.userDefinedInternalTransition(elapsedTime) ;
		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent = this.currentStateTime.add(this.nextTimeAdvance) ;
	}

	/**
	 * perform an external transition for an HIOA model with equations.
	 * 
	 * <p>
	 * In an HIOA with equations, model variables are updated to the
	 * current time before executing the external event to reflect the
	 * fact that when an equation is known for the mode variables, it
	 * is not necessary to update the values between events. Hence this
	 * implementation triggers a user defined internal transition
	 * before the method <code>userDefinedExternalTransition</code>
	 * is called.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime)
	 * pre	this.getCurrentStateTime().add(elapsedTime).
	 *                              lessThanOrEqual(this.getTimeOfNextEvent())
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicHIOAwithEquations#externalTransition "
							+ this.uri + " "
							+ this.currentStateTime + " "
							+ elapsedTime) ;
		}

		assert	this.getNextTimeAdvance().greaterThanOrEqual(elapsedTime) ;
		assert	this.getCurrentStateTime().add(elapsedTime).
								lessThanOrEqual(this.getTimeOfNextEvent()) ;

		// if the elapsed time is equal to zero, then no internal event need
		// to be executed as the values of the variables must be up to date.
		if (elapsedTime.greaterThan(
							Duration.zero(this.getSimulatedTimeUnit())))	{
			this.currentStateTime =
							this.getCurrentStateTime().add(elapsedTime) ;
			this.userDefinedInternalTransition(elapsedTime) ;
		}
		if (this.hasDebugLevel(2)) {
			this.logMessage(
					"AtomicHIOAwithEquations#externalTransition 2 "+ this.uri) ;
		}
		super.externalTransition(Duration.zero(this.getSimulatedTimeUnit())) ;
	}
}
// -----------------------------------------------------------------------------
