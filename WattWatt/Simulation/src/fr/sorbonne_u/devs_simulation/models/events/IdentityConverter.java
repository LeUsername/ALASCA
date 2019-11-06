package fr.sorbonne_u.devs_simulation.models.events;

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
 * The class <code>IdentityConverter</code> implements an identity event
 * converter as the identity function.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: use Java 8 lambdas...
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-05-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			IdentityConverter
extends		AbstractEventConverter
implements	EventConverterI
{
	private static final long serialVersionUID = 1L;

	public				IdentityConverter(
		Class<? extends EventI> domain,
		Class<? extends EventI> codomain
		)
	{
		super(domain, codomain);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventConverterI#isIdentityConverter()
	 */
	@Override
	public boolean		isIdentityConverter()
	{
		return true ;
	}

	/**
	 * return the event <code>e</code> without change.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventConverterI#convert(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public EventI		convert(EventI e)
	{
		assert	e != null ;

		return e ;
	}
}
// -----------------------------------------------------------------------------
