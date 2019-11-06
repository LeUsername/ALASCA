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

import java.io.Serializable;

// -----------------------------------------------------------------------------
/**
 * The class <code>ReexportedEvent</code> implements the description of
 * events that are exported by submodels and re-exported by an
 * ancestor coupled model.
 *
 * <p><strong>Description</strong></p>
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
public class			ReexportedEvent
implements	Serializable
{
	private static final long		serialVersionUID = 1L;
	/** URI of the model that exports the event type
	 *  <code>sourceEventType</code>.									*/
	public final String				exportingModelURI ;
	/** The type of event exported by one of the submodels of a coupled
	 *  model that reexports it.											*/
	public Class<? extends EventI>	sourceEventType ;
	/** The type of event exported by the coupled model.					*/
	public Class<? extends EventI>	sinkEventType ;
	/** A converter that can transforms an event of type
	 *  <code>eventType</code> into an event of a type exported by this
	 *  coupled model.													*/
	public EventConverterI			converter ;

	/**
	 * create a description of a re-exported type of events where the type
	 * of events coming from the source is the same as the one going to the
	 * sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	exportingModelURI != null
	 * pre	eventType != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the model exporting the events.
	 * @param eventType			type of re-exported events.
	 */
	public				ReexportedEvent(
		String exportingModelURI,
		Class<? extends EventI> eventType
		)
	{
		this(exportingModelURI, eventType, eventType,
			 new IdentityConverter(eventType, eventType)) ;

		assert	exportingModelURI != null ;
		assert	eventType != null ;
	}

	/**
	 * create a description of a re-exported type of events where the type
	 * of events coming from the source requires a conversion to the one
	 * going to the sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	exportingModelURI != null
	 * pre	sourceEventType != null and sinkEventType != null
	 * pre	converter.getDomain().isAssignableFrom(sourceEventType)
	 * pre	sinkEventType.isAssignableFrom(converter.getCodomain())
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the model exporting the events.
	 * @param sourceEventType	type of events coming from the source.
	 * @param sinkEventType		type of events going to the sink.
	 * @param converter			conversion function.
	 */
	public				ReexportedEvent(
		String exportingModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI>	sinkEventType,
		EventConverterI converter
		)
	{
		super() ;

		assert	exportingModelURI != null ;
		assert	sourceEventType != null && sinkEventType != null ;
		assert	converter.getDomain().isAssignableFrom(sourceEventType) ;
		assert	sinkEventType.isAssignableFrom(converter.getCodomain()) ;

		this.exportingModelURI = exportingModelURI ;
		this.sourceEventType = sourceEventType ;
		this.sinkEventType = sinkEventType ;
		this.converter = converter ;
	}
}
// -----------------------------------------------------------------------------
