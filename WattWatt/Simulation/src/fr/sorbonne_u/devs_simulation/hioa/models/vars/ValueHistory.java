package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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
 * The class <code>ValueHistory</code> implements a history mechanism for model
 * variables to memorise a bounded and predefined number of the most recent
 * values of a variable in a bounded buffer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The history mechanism is initially defined with a bounded capacity and then
 * values can be pushed into the history managed as a bounded buffer which
 * values can be accessed from the most recent with index 0 to the least
 * recent with index <code>getCurrentSize()</code>.
 * </p>
 * <p>
 * When beginning to use the history object, the number of memorised values
 * will gradually increase with the calls to <code>add</code> until it reaches
 * <code>getCapacity()</code>. After the capacity is reached, the newly added
 * values will replace the oldest.
 * </p>
 * <p>
 * The history mechanism does not implement any kind of processing on the values;
 * processing should be added at the level of the variables using the history
 * mechanism as a store for older values.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	vh.getCurrentSize() &gt;= 0 and
 *					vh.getCurrentSize() &lt;= vh.getCapacity()
 * invariant	vh.next &gt;= 0
 * invariant	(vh.getCurrentSize() &lt; vh.getCapacity() ?
 * 					vh.next &lt;= vh.getCurrentSize()
 * 				:	vh.next &lt; vh.getCapacity()
 * 				)
 * invariant	vh.getCurrentSize() == 0 or
 * 					vh.first &gt;= 0 and vh.first &lt; vh.getCurrentSize()
 * </pre>
 * 
 * <p>Created on : 2018-09-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ValueHistory<Type>
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** array holding the history of values.								*/
	protected final Value<Type>[]	history ;
	/** index in the array where to put the next value to be pushed into
	 *  this history.														*/
	protected int 					next ;
	/** index of the first or newest value in the history.					*/
	protected int					first ;
	/** current number of values in the history.							*/
	protected int					size ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new vale history with the given capacity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	capacity &gt; 0
	 * post	this.getCurrentSize() == 0
	 * post	ValueHistory.checkInvariant(this)
	 * </pre>
	 *
	 * @param capacity	the capacity in number of values of the history.
	 */
	@SuppressWarnings("unchecked")
	public				ValueHistory(int capacity)
	{
		super() ;

		assert	capacity > 0 ;

		this.history = new Value[capacity] ;
		this.next = 0 ;
		this.first = -1 ;
		this.size = 0 ;

		assert	this.getCurrentSize() == 0 ;
		assert	ValueHistory.checkInvariant(this) ;
	}

	/**
	 * check the invariant of the given value history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	vh != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param vh	an instance to be checked.
	 * @return		true if the invariant is observed.
	 */
	public static boolean	checkInvariant(ValueHistory<?> vh)
	{
		assert	vh != null ;

		boolean invariant = true ;
		invariant &=		vh.getCurrentSize() >= 0 &&
									vh.getCurrentSize() <= vh.getCapacity() ;
		invariant &=		vh.next >= 0 ;
		invariant &=		(vh.getCurrentSize() < vh.getCapacity() ?
							vh.next <= vh.getCurrentSize()
						:	vh.next < vh.getCapacity()
						) ;
		invariant &=		vh.getCurrentSize() == 0 ||
							(vh.first >= 0 && vh.first < vh.getCurrentSize()) ;

		return invariant ;
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * return the capacity of the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the capacity of the history.
	 */
	public int			getCapacity()
	{
		return this.history.length ;
	}

	/**
	 * return the current number of values in the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the current number of values in the history.
	 */
	public int			getCurrentSize()
	{
		return this.size ;
	}

	/**
	 * add a value to the history, this value becoming its newest one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	value != null
	 * post	{@code this.getCurrentSize()-at-pre < this.getCapacity() ?
	 * 			this.getCurrentSize() == this.getCurrentSize()-at-pre + 1
	 *		:	this.getCurrentSize() == this.getCapacity()
	 *		)}
	 * </pre>
	 *
	 * @param value			value to be added to the history.
	 * @throws Exception	<i>todo.</i>
	 */
	public void			add(Value<Type> value) throws Exception
	{
		assert	value != null ;
		int size_pre = this.getCurrentSize() ;

		this.history[this.next] =
			new Value<Type>(value.owner, value.descriptor,
											value.v, value.time, 0) ;
		this.first = this.next ;
		this.next = (this.next + 1) % this.history.length ;
		if (this.size < this.history.length) {
			this.size++ ;
		}

		assert	(size_pre < this.getCapacity() ?
					this.getCurrentSize() == size_pre + 1
				:	this.getCurrentSize() == this.getCapacity()
				) ;
	}

	/**
	 * get a value in the history at a given index of the sought value,
	 * the index <code>0</code> giving the newest value,
	 * <code>this.getCurrentSize() - 1</code> giving the oldest.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code this.getCurrentSize() > 0}
	 * pre	{@code index >= 0 and index < this.getCurrentSize()}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param index	index of the sought value, 0 is the newest, this.getCurrentSize() - 1 is the oldest.
	 * @return		the value in the history corresponding to index.
	 */
	public Value<Type>	get(int index)
	{
		assert	this.getCurrentSize() > 0 ;
		assert	index >= 0 && index < this.getCurrentSize() ;

		int i = this.first - index ;
		if (i < 0) {
			i = this.size + i ;
		}
		assert	i >= 0 && i < this.size ;

		return this.history[i] ;
	}
}
// -----------------------------------------------------------------------------
