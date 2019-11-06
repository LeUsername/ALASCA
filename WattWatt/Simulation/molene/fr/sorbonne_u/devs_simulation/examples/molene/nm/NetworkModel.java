package fr.sorbonne_u.devs_simulation.examples.molene.nm;

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

/**
 * The class <code>NetworkModel</code> implements a DEVS atomic model that
 * simulates in a relatively naive way the delays incurred when transmitting
 * small messages over a network.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model is implemented a an event scheduling type of DEVS model i.e.,
 * a model that can schedule events to be executed at a future simulation
 * time. The basic idea here is to receive events to be "transmitted" and
 * this transmission is simulated by waiting for a random time and then
 * reemit the event.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-10-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//------------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {BatteryLevel.class, WiFiBandwidthReading.class},
	exported = {BatteryLevel.class, WiFiBandwidthReading.class})
//------------------------------------------------------------------------------
public class				NetworkModel
extends		AtomicES_Model
{
	private static final long			serialVersionUID = 1L ;
	public static final String			URI = "NetworkModel" ;
	/** parameters of the Gamma distribution used to model the random
	 *  delay between the issuing and the reception of a message over
	 *  the network.														*/
	public static final String			GAMMA_SHAPE_PARAM_NAME = "shape" ;
	public static final String			GAMMA_SCALE_PARAM_NAME = "scale" ;
	public static final double			GAMMA_SHAPE = 11.0 ;
	public static final double			GAMMA_SCALE = 2.0 ;

	/** a vector to store the events waiting to be reemitted.				*/
	protected final Vector<EventI>		eventsToBeEmitted ;

	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rgTransmissionDelays ;
	/** actual parameters used to generate Gamma distributed values.		*/
	protected double					shape ;
	protected double					scale ;

	/**
	 * create an instance of network model.
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
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception				<i>todo.</i>
	 */
	public				NetworkModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine) ;
		this.eventsToBeEmitted = new Vector<EventI>() ;
		this.rgTransmissionDelays = new RandomDataGenerator() ;
		this.shape = NetworkModel.GAMMA_SHAPE ;
		this.scale = NetworkModel.GAMMA_SCALE ;

		assert	!isDebugModeOn() ;
	}

	/**
	 * 	dd an event to be emitted.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param e	event to be emitted.
	 */
	public void			addEventToBeEmitted(EventI e)
	{
		this.eventsToBeEmitted.addElement(e) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws Exception
	{
		String vname = this.getURI() + ":" +
									NetworkModel.GAMMA_SHAPE_PARAM_NAME ;
		this.shape = (double) simParams.get(vname) ;
		vname = this.getURI() + ":" + NetworkModel.GAMMA_SCALE_PARAM_NAME ;
		this.scale = (double) simParams.get(vname) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;
		this.rgTransmissionDelays.reSeedSecure() ;
		this.eventsToBeEmitted.clear() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.eventsToBeEmitted.size() > 0) {
			return Duration.zero(this.getSimulatedTimeUnit()) ;
		} else {
			// time until the next scheduled event is due to occur.
			return super.timeAdvance() ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public Vector<EventI>	output()
	{
		if (this.eventsToBeEmitted.size() > 0) {
			Vector<EventI> ret = new Vector<EventI>() ;
			ret.addAll(this.eventsToBeEmitted) ;
			this.eventsToBeEmitted.clear() ;
			return ret ;
		} else {
			return null ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime) ;

		Vector<EventI> current = this.getStoredEventAndReset() ;
		
		// delays in msec follow a Gamma distribution with shape = 11.0
		// and scale = 2.0, giving a mean delay of 22 msec.
		// Divide by 1000 to get times in the model simulation time unit
		// i.e. seconds.
		double deliveryDelay =
			this.rgTransmissionDelays.nextGamma(this.shape, this.scale)
																/1000.0 ;
		Time occ =
			this.getCurrentStateTime().add(
					new Duration(deliveryDelay, this.getSimulatedTimeUnit())) ;
		Set<ES_EventI> toBeEmitted = new HashSet<ES_EventI>() ;
		for (EventI e : current) {
			toBeEmitted.add(new EmitEvent(occ, new Container(e))) ;
		}
		this.scheduleEvents(toBeEmitted) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport() throws Exception
	{
		final String uri = this.uri ;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri ;
					}				
				};
	}
}
//------------------------------------------------------------------------------
