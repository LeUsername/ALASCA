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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.StandardAtomicHIOA_Factory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicHIOA_Descriptor</code> describes atomic HIOA in DEVS
 * simulation architectures, associating their URI, their imported and exported
 * event types, their imported and exported variables as well as a factory that
 * can be used to instantiate a model object to run simulations.
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
public class			AtomicHIOA_Descriptor
extends		AtomicModelDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean						DEBUG = false ;
	private static final long 					serialVersionUID = 1L ;
	/** Variables imported by this atomic HIOA.								*/
	public final StaticVariableDescriptor[]		importedVariables ;
	/** Variables exported by this atomic HIOA.								*/
	public final StaticVariableDescriptor[]		exportedVariables ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic HIOA descriptor with given sets of imported and
	 * exported variables added to the imported and exported events of
	 * basic discrete event based models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelClass != null
	 * pre	modelURI != null
	 * pre	engineCreationMode !=
	 *               SimulationEngineCreationMode.COORDINATION_ENGINE
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param importedEvents		array of imported event types.
	 * @param exportedEvents		array of exported event types.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @param importedVariables		variables imported by this HIOA.
	 * @param exportedVariables		variables exported by this HIOA.
	 * @throws Exception			<i>TODO</i>.
	 */
	protected			AtomicHIOA_Descriptor(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		SimulationEngineCreationMode engineCreationMode,
		StaticVariableDescriptor[] importedVariables,
		StaticVariableDescriptor[] exportedVariables
		) throws Exception
	{
		super(modelClass, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit, amFactory, engineCreationMode) ;

		this.importedVariables = importedVariables ;
		if (AtomicHIOA_Descriptor.DEBUG) {
			for (int i = 0 ; i < this.importedVariables.length ; i++) {
				System.out.println("AtomicHIOA_Descriptor+imp " + modelURI + " " 
								   + this.importedVariables[i].getName()) ;
			}
		}

		this.exportedVariables = exportedVariables ;
		if (AtomicHIOA_Descriptor.DEBUG) {
			for (int i = 0 ; i < this.exportedVariables.length ; i++) {
				System.out.println("AtomicHIOA_Descriptor+exp " + modelURI + " " 
								   + this.exportedVariables[i].getName()) ;
			}
		}
	}

	/**
	 * create an atomic HIOA descriptor extracting the sets of imported and
	 * exported variables as well as the imported and exported events from
	 * the model classes.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 * @return						the model descriptor.
	 * @throws Exception			<i>TODO</i>
	 */
	public static AtomicHIOA_Descriptor		create(
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

		StaticVariableDescriptor[] importedVariables =
			(StaticVariableDescriptor[])
				modelClass.getMethod("getStaticImportedVars",
									 new Class<?>[] {Class.class}).
								invoke(null, new Object[] {modelClass}) ;

		StaticVariableDescriptor[] exportedVariables =
			(StaticVariableDescriptor[])
				modelClass.getMethod("getStaticExportedVars",
									 new Class<?>[] {Class.class}).
								invoke(null, new Object[] {modelClass}) ;

		return new AtomicHIOA_Descriptor(
				modelClass, modelURI, importedEvents,
				exportedEvents, simulatedTimeUnit, amFactory,
				engineCreationMode, importedVariables, exportedVariables) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor#connectAtomicModel()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModelDescriptionI	connectAtomicModel() throws Exception
	{
//		System.out.println("AtomicHIOA_Descriptor#createAtomicModel " +
//													this.modelURI) ;
		AtomicHIOA hioa = null ;
		AtomicModelFactoryI amFactory = null ;
		if (this.amFactory == null) {
			amFactory = new StandardAtomicHIOA_Factory(
							(Class<? extends AtomicHIOA>) this.modelClass) ;
		} else {
			amFactory = this.amFactory ;
		}
		if (this.engineCreationMode ==
				SimulationEngineCreationMode.ATOMIC_ENGINE) {
			amFactory.setAtomicModelCreationParameters(
									this.modelURI,
									this.simulatedTimeUnit,
									amFactory.createAtomicEngine()) ;
			hioa = (AtomicHIOA) amFactory.createAtomicModel() ;
			hioa.staticInitialiseVariables() ;
			return hioa.getSimulationEngine() ;
		} else {
			assert	this.engineCreationMode ==
									SimulationEngineCreationMode.NO_ENGINE ;
			hioa = (AtomicHIOA) amFactory.createAtomicModel() ;
			hioa.staticInitialiseVariables() ;
			return hioa ;
		}
	}
}
// -----------------------------------------------------------------------------
