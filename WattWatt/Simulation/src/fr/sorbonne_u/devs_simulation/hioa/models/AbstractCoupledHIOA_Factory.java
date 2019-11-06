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

import fr.sorbonne_u.devs_simulation.hioa.simulators.HIOA_AtomicEngine;
import fr.sorbonne_u.devs_simulation.models.AbstractCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractCoupedHIOAFactory</code> defines the shared
 * behaviours among coupled HIOA factories.
 *
 * <p><strong>Description</strong></p>
 * 
 * The abstract class specifically defines a generic implementation for the
 * method <code>generateCoupledModelFactory</code> that is suitable for
 * coupled HIOA that uses only the standard parameters for their creation.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractCoupledHIOA_Factory
extends		AbstractCoupledModelFactory
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AbstractCoupledModelFactory#createAtomicSimulationEngine()
	 */
	@Override
	public SimulationEngine	createAtomicSimulationEngine()
	{
		return new HIOA_AtomicEngine();
	}
}
// -----------------------------------------------------------------------------
