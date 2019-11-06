package fr.sorbonne_u.devs_simulation.interfaces;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>ParentNotificationI</code> defines the methods used
 * by child models to notify their parent models about the processing of
 * external events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ParentNotificationI
extends		ModelDescriptionI
{
	/**
	 * notify the parent model that the model <code>modelURI</code> has
	 * received some external events during the execution of another model
	 * internal step and therefore that it will need to perform an external
	 * step during the current simulation step.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		the URI of a submodel that has received some external events.
	 * @throws Exception		<i>TO DO</i>.
	 */
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception ;

	/**
	 * notify the pa	rent model that the child model <code>modelURI</code>
	 * has performed its current external events; used when a coupled model
	 * has its children models perform the external events sent by other
	 * models among its children and need not be called upon its external
	 * event step method anymore.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		the URI of a submodel that has performed its current external events.
	 * @throws Exception		<i>TO DO</i>.
	 */
	public void			hasPerformedExternalEvents(String modelURI)
	throws Exception ;
}
// -----------------------------------------------------------------------------
