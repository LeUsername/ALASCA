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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.StandardAtomicModelFactory;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicModelDescriptor</code> describes atomic models in
 * DEVS simulation architectures, associating their instantiation class,
 * their URI, their imported and exported event types, as well as a
 * factory that can be used to instantiate a model object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Atomic model descriptor are meant to capture the essential information
 * needed by the algorithm which takes the description of a complete
 * simulation architecture to create and compose the different models
 * to create a running modular simulator. This algorithm needs to determine
 * in which order and how models must be created and composed, thus it needs
 * to know the static information about models, like their imported and
 * exported events, as well as the creation parameters for the models such
 * as its URI, its simulated time unit, etc. Two ways to create models are
 * provided. If the instantiation class of the model declares the standard
 * constructors inherited from the class <code>AtomicModel</code>, then
 * it suffices to give the instance of <code>Class&lt;T&gt;</code> of this
 * instantiation class and the model will be created using the reflection
 * facility of Java. The programmer can also provide a factory which
 * implements the interface <code>AtomicModelFactoryI</code> that can
 * add its own specific creation parameters when calling the constructor
 * of the model instantiation class.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.modelURI != null
 * invariant	this.modelClass != null
 * invariant	this.simulatedTimeUnit != null
 * invariant	this.engineCreationMode != null
 * invariant	this.importedEvents != null
 * invariant	this.exportedEvents != null
 * </pre>
 * 
 * <p>Created on : 2018-06-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicModelDescriptor
extends		AbstractAtomicModelDescriptor
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L ;
	public static boolean							DEBUG = false ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new atomic model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	engineCreationMode !=
	 *               SimulationEngineCreationMode.COORDINATION_ENGINE
	 * post	AtomicModelDescriptor.checkInvariant(this)
	 * </pre>
	 *
	 * @param modelClass			Java class defining the atomic model.
	 * @param modelURI				URI of the model to be created.
	 * @param importedEvents		events imported by the model.
	 * @param exportedEvents		events exported by the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @throws Exception			<i>TODO</i>.
	 */
	protected			AtomicModelDescriptor(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		SimulationEngineCreationMode engineCreationMode
		) throws Exception
	{
		super(modelClass,modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit, amFactory, engineCreationMode) ;

		assert	modelClass != null ;
		assert	modelURI != null ;
		assert	engineCreationMode != null ;
		assert	engineCreationMode !=
						SimulationEngineCreationMode.COORDINATION_ENGINE ;

		if (AtomicModelDescriptor.DEBUG) {
			for (int i = 0 ; i < this.exportedEvents.length ; i++) {
				System.out.println("AtomicModelDescriptor>>constructor " +
								modelURI + " " +
								this.exportedEvents[i].getName()) ;
			}
		}

		assert	AtomicModelDescriptor.checkInvariant(this) ;
	}

	/**
	 * check the invariant for the given atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	the atomic model descriptor to be checked.
	 * @return	true if <code>d</code> respects the invariant.
	 */
	public static boolean	checkInvariant(AtomicModelDescriptor d)
	{
		assert	d != null ;

		boolean invariant = d.modelClass != null || d.amFactory != null ;
		invariant &= d.engineCreationMode != null ;
		invariant &= d.importedEvents != null ;
		invariant &= d.exportedEvents != null ;

		return invariant ;
	}

	/**
	 * create an atomic model descriptor from a given class, possibly using
	 * the given atomic model factory if one is provided.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	engineCreationMode !=
	 *               	SimulationEngineCreationMode.COORDINATION_ENGINE
	 * post	AbstractAtomicModelDescriptor.checkInvariant(ret)
	 * post	AtomicModelDescriptor.checkInvariant(ret)
	 * </pre>
	 *
	 * @param modelClass			Java class defining the atomic model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @return						the new atomic model descriptor.
	 * @throws Exception			<i>to do.</i>
	 */
	public static AtomicModelDescriptor		create(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		SimulationEngineCreationMode engineCreationMode
		) throws Exception
	{

		@SuppressWarnings("unchecked")
		Class<? extends EventI>[] importedEvents =
			(Class<? extends EventI>[])
				modelClass.getMethod(
							"getExportedEventTypes",
							new Class<?>[] {Class.class}).
					invoke(null, new Object[] {modelClass}) ;
		@SuppressWarnings("unchecked")
		Class<? extends EventI>[] exportedEvents =
			(Class<? extends EventI>[])
				modelClass.getMethod(
							"getExportedEventTypes",
							new Class<?>[] {Class.class}).
					invoke(null, new Object[] {modelClass}) ;

		return new AtomicModelDescriptor(
							modelClass, modelURI, importedEvents,
							exportedEvents, simulatedTimeUnit, amFactory,
							engineCreationMode) ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * create an atomic model from this descriptor.
	 * 
	 * <p>Description</p>
	 * 
	 * The creation of an atomic model uses the factory pattern, but when the
	 * class M defining the model do not need to change the instantiation
	 * protocol (constructor parameters) from the extended abstract classes
	 * provided in the library, then only the object representing the class
	 * M need to be given and a predefined factory called
	 * <code>StandardAtomicModelFactory</code> is automatically used.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the atomic model created from this descriptor.
	 * @throws Exception 	<i>to do.</i>
	 */
	@Override
	public ModelDescriptionI	connectAtomicModel() throws Exception
	{
		AtomicModelFactoryI amFactory = null ;
		if (this.amFactory == null) {
			amFactory = new StandardAtomicModelFactory(this.modelClass) ;
		} else {
			amFactory = this.amFactory ;
		}
		if (this.engineCreationMode ==
							SimulationEngineCreationMode.ATOMIC_ENGINE) {
			amFactory.setAtomicModelCreationParameters(
							this.modelURI,
							this.simulatedTimeUnit,
							amFactory.createAtomicEngine()) ;
			return amFactory.createAtomicModel().getSimulationEngine() ;
		} else {
			assert	this.engineCreationMode ==
									SimulationEngineCreationMode.NO_ENGINE ;
			amFactory.setAtomicModelCreationParameters(
							this.modelURI,
							this.simulatedTimeUnit,
							null) ;
			return amFactory.createAtomicModel() ;
		}
	}
}
// -----------------------------------------------------------------------------
