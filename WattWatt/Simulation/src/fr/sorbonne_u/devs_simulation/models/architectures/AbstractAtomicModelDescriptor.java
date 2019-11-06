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
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractAtomicModelDescriptor</code> implements a
 * descriptor for atomic models to be included in simulation architectures
 * overall descrpitions.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.modelURI != null
 * invariant	this.simulatedTimeUnit != null
 * invariant	this.importedEvents != null
 * invariant	this.exportedEvents != null
 * </pre>
 * 
 * <p>Created on : 2019-06-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractAtomicModelDescriptor
implements	Serializable,
			ModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	public static boolean							DEBUG = false ;
	/** class defining the atomic model.									*/
	public final Class<? extends AtomicModel>		modelClass ;
	/** URI of the model to be created.										*/
	public final String								modelURI ;
	/** time unit used for the simulation clock.							*/
	public final TimeUnit							simulatedTimeUnit ;
	/** array of event types imported by the atomic model.					*/
	public final Class<? extends EventI>[]			importedEvents ;
	/** array of event types exported by the atomic model.					*/
	public final Class<? extends EventI>[]			exportedEvents ;
	/** atomic model factory allowing to create the model.					*/
	public final AtomicModelFactoryI				amFactory ;
	/** Creation mode for the simulation engine.							*/
	public final SimulationEngineCreationMode		engineCreationMode ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * pre	importedEvents != null
	 * pre	exportedEvents != null
	 * pre	simulatedTimeUnit != null
	 * post	AbstractAtomicModelDescriptor.checkInvariant(this)
	 * </pre>
	 *
	 * @param modelClass			Java class defining the atomic model.
	 * @param modelURI				URI of the model to be created.
	 * @param importedEvents		events imported by the model.
	 * @param exportedEvents		events exported by the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param engineCreationMode	creation mode for the simulation engine.	
	 */
	protected			AbstractAtomicModelDescriptor(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		SimulationEngineCreationMode engineCreationMode
		)
	{
		super() ;

		assert	modelURI != null ;
		assert	importedEvents != null ;
		assert	simulatedTimeUnit != null ;
		assert	exportedEvents != null ;

		this.modelClass = modelClass ;
		this.modelURI = modelURI ;
		this.importedEvents = importedEvents ;
		this.exportedEvents = exportedEvents ;
		this.simulatedTimeUnit = simulatedTimeUnit ;
		this.amFactory = amFactory ;
		this.engineCreationMode = engineCreationMode ;

		assert	AbstractAtomicModelDescriptor.checkInvariant(this) ;
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
	 * @param d		the atomic model descriptor to be checked.
	 * @return		true if <code>d</code> respects the invariant.
	 */
	public static boolean	checkInvariant(AbstractAtomicModelDescriptor d)
	{
		assert	d != null ;

		boolean invariant = (d.modelURI != null) ;
		invariant &= d.importedEvents != null ;
		invariant &= d.exportedEvents != null ;
		invariant &= d.simulatedTimeUnit != null ;

		return invariant ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#isCoupledModelDescriptor()
	 */
	@Override
	public boolean		isCoupledModelDescriptor()
	{
		return false ;
	}

	/**
	 * create an atomic model or simply get the model description reference
	 * to the model and return it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				a reference to a model description.
	 * @throws Exception 	<i>to do.</i>
	 */
	public abstract ModelDescriptionI	connectAtomicModel() throws Exception ;
}
// -----------------------------------------------------------------------------
