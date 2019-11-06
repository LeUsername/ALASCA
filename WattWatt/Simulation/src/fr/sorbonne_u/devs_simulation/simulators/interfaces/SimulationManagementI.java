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

import java.util.Map;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SimulationManagementI</code> declares the methods used
 * by simulation engines to manage simulation runs.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SimulationManagementI
{
	/**
	 * return the time at which the simulation started.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the time at which the simulation starts.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Time			getTimeOfStart() throws Exception ;

	/**
	 * return the time at which the simulation ends.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the time at which the simulation ends.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public Time			getSimulationEndTime() throws Exception ;

	/**
	 * return true if the simulator is ready to perform a simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulator component has been registered and connected.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		readyForSimulation() throws Exception ;

	/**
	 * set the simulation run parameters before performing the simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simParams != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simParams		simulation parameters as a map from parameter names to their individual value.
	 * @throws Exception	<i>to do.</i>
	 */
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception ;

	/**
	 * do a stand alone simulation lasting <code>simulatedTimeDuration</code>
	 * and starting at <code>startTime</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code startTime >= 0.0}
	 * pre	{@code startTime  < Double.POSITIVE_INFINITY}
	 * pre	{@code simulationDuration > 0.0}
	 * pre	{@code simulationDuration <= Double.POSITIVE_INFINITY}
	 * post	!this.isSimulationRunning()
	 * </pre>
	 *
	 * @param startTime				time at which the simulation starts interpreted in the model simulation time.
	 * @param simulationDuration	duration of the simulation interpreted in the model simulation time.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception ;

	/**
	 * start a collaborative simulation at <code>startTime</code> lasting
	 * <code>simulationDuration</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code startTime >= 0.0}
	 * pre	{@code startTime  < Double.POSITIVE_INFINITY}
	 * pre	{@code simulationDuration > 0.0}
	 * pre	{@code simulationDuration <= Double.POSITIVE_INFINITY}
	 * post	this.isSimulationRunning()
	 * </pre>
	 *
	 * @param startTime				time at which the simulation starts interpreted in the model simulation time.
	 * @param simulationDuration	duration of the simulation.
	 * @throws Exception			<i>TO DO</i>.
	 */
	public void			startCollaborativeSimulation(
		double startTime,
		double simulationDuration
		) throws Exception ;

	/**
	 * return true if a simulation run is currently executing.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isSimulationInitialised()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				true if a simulation run is currently executing.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public boolean		isSimulationRunning() throws Exception ;

	/**
	 * interrupt the current simulation and trigger the sending of the
	 * simulation report to the client if appropriately connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isSimulationRunning()
	 * post	!this.isSimulationRunning()
	 * </pre>
	 *
	 * @throws Exception	<i>TO DO</i>.
	 */
	public void			stopSimulation() throws Exception ;

	/**
	 * return the simulation report of the last simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the simulation report of the last simulation run.
	 * @throws Exception	<i>TO DO</i>.
	 */
	public SimulationReportI	getFinalReport() throws Exception ;
}
// -----------------------------------------------------------------------------
