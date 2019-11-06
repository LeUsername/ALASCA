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

import fr.sorbonne_u.devs_simulation.interfaces.ParentNotificationI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardParentReference</code> represent a reference on a
 * parent model that is a simple Java reference.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In this implementation, the reference can be to an instance of
 * <code>CoupledModel</code> or <code>CoordinationEngine</code>, but
 * in other complementary implementation, this could be other sorts of
 * references like remote object references using RMI.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2019-06-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardParentReference
implements	ParentReferenceI
{
	private static final long serialVersionUID = 1L;
	/** reference ot the parent model.										*/
	protected final ParentNotificationI	ref ;

	/**
	 * create a parent reference with the given Java reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ref != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ref	a reference to the parent model or a proxy.
	 */
	public				StandardParentReference(
		ParentNotificationI	ref
		)
	{
		assert	ref != null ;

		this.ref = ref ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.ParentReferenceI#getParentReference()
	 */
	@Override
	public ParentNotificationI	getParentReference()
	{
		return this.ref ;
	}
}
// -----------------------------------------------------------------------------
