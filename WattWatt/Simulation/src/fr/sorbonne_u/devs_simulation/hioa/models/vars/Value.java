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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>Value</code> defines a placeholder for the values of
 * atomic HIOA models with a history mechanism that memorise a bounded
 * predefined number of the immediately preceding values.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class is meant to serve as indirection for the value of variables
 * that are shared among the model (unique by rule) that exports them
 * and models that import them. Because variables take different values
 * over the simulation time, a simulation time is attached to each value
 * when it is computed (i.e., when the variable is assigned a new value).
 * </p>
 * <p>
 * The class also defines a history mechanism able to memorise values of the
 * variable over time. The size of the history can be set when creating the
 * instance of <code>Value</code>. However, the class does not define any
 * processing for these values. Subclasses should therefore introduce such
 * processing, like means to predict values at a given time, either
 * interpolating between memorised values or extrapolating outside the time
 * interval covered by memorised values.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.descriptor == null ||
 *								this.owner == this.descriptor.owner
 * invariant	this.timeUnit == null ||
 *					this.timeUnit.equals(this.owner.getSimulatedTimeUnit())
 * invariant    this.time == null ||
 *					this.time.getTimeUnit().equals(this.timeUnit) ;
 * invariant	{@code this.historySize >= 0}
 * invariant	{@code (this.historySize == 0 ?
 * 					this.valueHistory == null
 * 				:	this.valueHistory != null &&
 * 					this.valueHistory.getCurrentSize() >= 0 &&
 * 					this.valueHistory.getCurrentSize() <= this.historySize
 * 				)}
 * </pre>
 * 
 * <p>Created on : 2018-04-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Value<Type>
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	/** the model owning the variable and this value object.				*/
	protected final AtomicHIOA		owner ;
	/** the time unit in which the time associated to the value must be
	 *  interpreted.														*/
	protected final TimeUnit		timeUnit ;
	/** 	the descriptor of the variable.									*/
	protected VariableDescriptor	descriptor ;

	/** the current value.													*/
	public Type						v ;
	/** the simulated time at which the value was computed/assigned.		*/
	public Time						time ;

	/** the size (number of values) of the history.							*/
	protected final int				historySize ;
	/** the values stored in the history.									*/
	public final ValueHistory<Type>	valueHistory ;

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * create a value instance for the given variable and the given owner
	 * with an initial value but without history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	initialTime.getTimeUnit().equals(owner.getSimulatedTimeUnit())
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit))
	 * pre	{@code historySize >= 0}
	 * post	this.owner.equals(owner)
	 * post this.v.equals(initialValue)
	 * post	this.time.equals(initialTime)
	 * post	Value.checkInvariant(this)
	 * </pre>
	 *
	 * @param owner			model owning the variable.
	 * @param initialValue	the initial value has been computed/assigned.
	 * @throws Exception	<i>todo.</i>
	 */
	public				Value(
		AtomicHIOA owner,
		Type initialValue
		) throws Exception
	{
		this(owner, initialValue, 0) ;
	}


	/**
	 * create a value instance for the given variable and the given owner
	 * with an initial value computed at the given initial time and with
	 * the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	initialTime.getTimeUnit().equals(owner.getSimulatedTimeUnit())
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit))
	 * pre	{@code historySize >= 0}
	 * post	this.owner.equals(owner)
	 * post this.v.equals(initialValue)
	 * post	this.time.equals(initialTime)
	 * post	Value.checkInvariant(this)
	 * </pre>
	 *
	 * @param owner			model owning the variable.
	 * @param initialValue	initial value of the variable.
	 * @param historySize	predefined size of the history.
	 * @throws Exception	<i>todo.</i>
	 */
	public				Value(
		AtomicHIOA owner,
		Type initialValue,
		int historySize
		) throws Exception
	{
		assert	owner != null ;
		assert	historySize >= 0 ;

		this.owner = owner ;
		this.historySize = historySize ;
		if (this.historySize > 0) {
			this.valueHistory = new ValueHistory<Type>(this.historySize) ;
		} else {
			this.valueHistory = null ;
		}
		this.v = initialValue ;
		this.timeUnit = owner.getSimulatedTimeUnit() ;

		assert	Value.checkInvariant(this) ;
	}

	/**
	 * create a value instance for the given variable and the given owner
	 * and the given variable descriptor with an initial value computed at
	 * the given initial time and with the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner != null
	 * pre	descriptor != null
	 * pre	initialTime.getTimeUnit().equals(owner.getSimulatedTimeUnit())
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit))
	 * pre	{@code historySize >= 0}
	 * post	this.owner.equals(owner)
	 * post	this.descriptor.equals(descriptor)
	 * post this.v.equals(initialValue)
	 * post	this.time.equals(initialTime)
	 * post	Value.checkInvariant(this)
	 * </pre>
	 *
	 * @param owner			model owning the variable.
	 * @param descriptor	descriptor of the variable.
	 * @param initialValue	initial value of the variable.
	 * @param initialTime	the time at which the initial value has been computed/assigned.
	 * @param historySize	predefined size of the history.
	 * @throws Exception	<i>todo.</i>
	 */
	public				Value(
		AtomicHIOA owner,
		VariableDescriptor descriptor,
		Type initialValue,
		Time initialTime,
		int historySize
		) throws Exception
	{
		this(owner, initialValue, historySize) ;

		assert	owner != null ;
		assert	descriptor != null ;
		assert	initialTime.getTimeUnit().equals(owner.getSimulatedTimeUnit()) ;
		assert	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit)) ;
		assert	historySize >= 0 ;

		this.descriptor = descriptor ;
		this.time = initialTime ;

		assert	Value.checkInvariant(this) ;
	}

	/**
	 * check the invariant.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	val != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param val			a value to be checked.
	 * @return				true if the invariant is observed.
	 * @throws Exception	<i>TODO</i>
	 */
	public static boolean	checkInvariant(Value<?> val) throws Exception
	{
		assert	val != null ;

		boolean invariant = true ;
		invariant &=	val.descriptor == null ||
									val.owner == val.descriptor.owner ;
		invariant &=	val.timeUnit == null ||
							val.timeUnit.equals(
										val.owner.getSimulatedTimeUnit()) ;
		invariant &=	val.time == null ||
								val.time.getTimeUnit().equals(val.timeUnit) ;
		invariant &= 	val.historySize >= 0 ;
		invariant &= 	(val.historySize == 0 ? val.valueHistory == null
						:	val.valueHistory != null &&
							val.valueHistory.getCurrentSize() >= 0 &&
							val.valueHistory.getCurrentSize() <=
														val.historySize
						) ;

		return invariant ;
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * set the variable descriptor for this value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	cvd != null
	 * pre	!this.isVariableDescriptorSet()
	 * pre	cvd.getOwner() == this.owner
	 * pre	cvd.getField().get(this.owner) == this
	 * post	this.descriptor.equals(cvd)
	 * post	this.isVariableDescriptorSet()
	 * </pre>
	 *
	 * @param vd			the variable descriptor for this value.
	 * @throws Exception	<i>TODO</i>
	 */
	public void			setVariableDescriptor(VariableDescriptor vd)
	throws Exception
	{
		assert	vd != null ;
		assert	!this.isVariableDescriptorSet() ;
		assert	vd.getOwner() == this.owner ;
		assert	vd.getField().get(this.owner) == this ;

		this.descriptor = vd ;
	}

	/**
	 * return true if the variable descriptor of this value has been set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the variable descriptor of this value has been set.
	 */
	public boolean		isVariableDescriptorSet()
	{
		return this.descriptor != null ;
	}

	/**
	 * initialise the value for a given simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	initialTime != null
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit))
	 * post	this.v == initialValue
	 * post	this.time.equals(initialTime)
	 * </pre>
	 *
	 * @param initialValue	value to set this value holder.
	 * @param initialTime	simulated time at which the initial value corresponds.
	 */
	public void			initialiseValue(
		Type initialValue,
		Time initialTime
		)
	{
		assert	initialTime != null ;
		assert	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit)) ;

		this.v = initialValue ;
		this.time = initialTime ;
	}

	/**
	 * initialise the value for a given simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	initialTime != null
	 * pre	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit))
	 * post	this.time.equals(initialTime)
	 * </pre>
	 *
	 * @param initialTime	simulated time at which the initial value corresponds.
	 */
	public void			initialiseTime(
		Time initialTime
		)
	{
		assert	initialTime != null ;
		assert	initialTime.greaterThanOrEqual(Time.zero(this.timeUnit)) ;

		this.time = initialTime ;
	}

	/**
	 * return the owner of the variable.
	 * 
	 * @return	the owner of the variable.
	 */
	public AtomicHIOA	getOwner()
	{
		return owner ;
	}

	/**
	 * return the timeUnit of the time associated to variable values.
	 * 
	 * @return	the timeUnit of the time associated to variable values.
	 */
	public TimeUnit		getTimeUnit()
	{
		return timeUnit ;
	}

	/**
	 * return the descriptor of the variable.
	 * 
	 * @return	the descriptor of the variable.
	 */
	public VariableDescriptor	getDescriptor()
	{
		return descriptor;
	}

	/**
	 * return true if this value has an history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if this value has an history.
	 */
	public boolean		hasValueHistory()
	{
		return this.historySize > 0 ;
	}

	/**
	 * push the current value to the history, this value becoming the
	 * newest in the history (index == 0).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.hasValueHistory()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception		<i>todo.</i>
	 */
	public void			pushCurrentValueToHistory() throws Exception
	{
		assert	this.hasValueHistory() ;

		this.valueHistory.add(this) ;
	}

	/**
	 * return the number of value currently in the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the number of value currently in the history.
	 */
	public int			getCurrentSizeOfValueHistory()
	{
		return	(this.historySize == 0 ?
					0
				:	this.valueHistory.getCurrentSize()
				) ;
	}

	/**
	 * get a value in the history at a given index of the sought value,
	 * the index <code>0</code> giving the newest value,
	 * <code>this.getCurrentSizeOfValueHistory() - 1</code> giving the oldest.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.hasValueHistory()
	 * pre	{@code index >= 0 && index < this.getCurrentSizeOfValueHistory()}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param index	index of the sought value, 0 is the newest, this.getCurrentSizeOfValueHistory() - 1 is the oldest.
	 * @return		the value in the history corresponding to index.
	 */
	public Value<Type>	getValueFromHistory(int index)
	{
		assert	this.hasValueHistory() ;
		assert	index >= 0 && index < this.getCurrentSizeOfValueHistory() ;

		return this.valueHistory.get(index) ;
	}
}
// -----------------------------------------------------------------------------
