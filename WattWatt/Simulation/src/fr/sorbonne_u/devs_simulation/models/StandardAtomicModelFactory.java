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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardAtomicModelFactory</code> defines an atomic model
 * factory that will create models from their Java representation class,
 * assuming that this model class declares a constructor with the three
 * standard parameters: the URI of the model, the simulation time unit and
 * the simulation engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * To create a model, this factory uses reflection to get the standard
 * constructor with the three mentioned parameters and then invokes it.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-09-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardAtomicModelFactory
extends		AbstractAtomicModelFactory
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L ;
	public static int								DEBUG_LEVEL = 0 ;
	/** class defining the atomic model, to be instantiated.				*/
	protected final Class<? extends AtomicModelI>	atomicModelClass ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a standard model factory for the given Java class representing
	 * the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	atomicModelClass != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelClass	the class defining the model.
	 */
	public				StandardAtomicModelFactory(
		Class<? extends AtomicModelI> atomicModelClass
		)
	{
		assert	atomicModelClass != null ;

		this.atomicModelClass = atomicModelClass ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#createAtomicModel()
	 */
	@Override
	public AtomicModel	createAtomicModel()
	{
		try {
			Constructor<?> ct =
				this.atomicModelClass.getConstructor(
										new Class<?>[]{String.class,
													   TimeUnit.class,
													   SimulatorI.class}) ;
			AtomicModel ret =
				(AtomicModel) ct.newInstance(this.modelURI,
											this.simulatedTimeUnit,
											this.simulationEngine) ;
			ret.setDebugLevel(DEBUG_LEVEL) ;
			return ret ;
		} catch (NoSuchMethodException | SecurityException |
				 InstantiationException | IllegalAccessException |
				 IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
