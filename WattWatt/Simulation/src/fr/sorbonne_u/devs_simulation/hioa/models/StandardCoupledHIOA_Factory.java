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

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardCoupledHIOA_Factory</code> implements a standard
 * model factory for coupled HIOA models.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: maybe obsolete?
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
public class			StandardCoupledHIOA_Factory
extends		AbstractCoupledHIOA_Factory
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	public static int								DEBUG_LEVEL = 0 ;
	/** class defining the model, taht will be instantiated.				*/
	protected final Class<? extends CoupledModelI>	coupledModelClass ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	coupledModelClass != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coupledModelClass	the class defining the model.
	 */
	public				StandardCoupledHIOA_Factory(
		Class<? extends CoupledModelI> coupledModelClass
		)
	{
		this.coupledModelClass = coupledModelClass ;
		assert	coupledModelClass != null ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createCoupledModel()
	 */
	@Override
	public CoupledModel		createCoupledModel() throws Exception
	{
		Constructor<?> ct =
				this.coupledModelClass.getConstructor(
						new Class<?>[] {String.class,
										TimeUnit.class,
										SimulatorI.class,
										(new ModelDescriptionI[]{}).getClass(),
										Map.class,
										Map.class,
										Map.class,
										Map.class,
										Map.class,
										Map.class}) ;
		CoupledModel ret =
				(CoupledModel) ct.newInstance(
									this.modelURI,
									this.models[0].getSimulatedTimeUnit(),
									this.se,
									this.models,
									this.imported,
									this.reexported,
									this.connections,
									this.importedVars,
									this.reexportedVars,
									this.bindings) ;
		ret.setDebugLevel(DEBUG_LEVEL) ;
		return ret ;
	}
}
// -----------------------------------------------------------------------------
