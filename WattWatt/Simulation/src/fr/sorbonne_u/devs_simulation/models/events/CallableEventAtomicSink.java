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
 * The class <code>CallableEventAtomicSink</code> implements an event
 * sink descriptor that includes a reference to the sink model on which
 * the methods of <code>EventsExchangingI</code> can be directly called 
 *
 * <p><strong>Description</strong></p>
 * 
 * When composing models and connecting them, static sink descriptors that
 * are giving only the URIs of the models are progressively replaced by
 * descriptors containing the references to the created models.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-06-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CallableEventAtomicSink
extends		EventSink
{
	private static final long					serialVersionUID = 1L;
	/** Reference or proxy to the model importing events of type
	 *  <code>eventType</code>.											*/
	public final AbstractAtomicSinkReference	importingAtomicModelReference ;

	/**
	 * creating a sink with the identity event converter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param importingAtomicModelURI			URI of the model importing the events.
	 * @param sourceEventType					type of incoming events.
	 * @param sinkEventType						type of outgoing events.
	 * @param importingAtomicModelReference		sink reference to the consuming atomic model.
	 * @throws Exception						<i>to do.</i>
	 */
	public				CallableEventAtomicSink(
		String importingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		AbstractAtomicSinkReference importingAtomicModelReference
		) throws Exception
	{
		super(importingAtomicModelURI, sourceEventType, sinkEventType) ;

		assert	importingAtomicModelReference != null ;
		if (importingAtomicModelReference.isDirect()) {
			assert	((AtomicSinkReference)
						importingAtomicModelReference).ref.getURI().
										equals(importingAtomicModelURI) ||
					((AtomicSinkReference)
						importingAtomicModelReference).ref.
							isDescendentModel(importingAtomicModelURI) ;
		}

		this.importingAtomicModelReference = importingAtomicModelReference ;
	}

	/**
	 * creating a sink with the given event converter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param importingAtomicModelURI			URI of the model importing the events.
	 * @param sourceEventType					type of incoming events.
	 * @param sinkEventType						type of outgoing events.
	 * @param importingAtomicModelReference		sink reference to the consuming atomic model.
	 * @param converter							conversion function from the source event type to the sink one.
	 * @throws Exception						<i>to do.</i>
	 */
	public				CallableEventAtomicSink(
		String importingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		AbstractAtomicSinkReference importingAtomicModelReference,
		EventConverterI converter
		) throws Exception
	{
		super(importingAtomicModelURI, sourceEventType, sinkEventType,
			  converter);

		assert	importingAtomicModelReference != null ;
		if (importingAtomicModelReference.isDirect()) {
			assert	((AtomicSinkReference)
						importingAtomicModelReference).ref.getURI().
										equals(importingAtomicModelURI) ||
					((AtomicSinkReference)
						importingAtomicModelReference).ref.
							isDescendentModel(importingAtomicModelURI) ;
		}

		this.importingAtomicModelReference = importingAtomicModelReference ;
	}
}
// -----------------------------------------------------------------------------
