package fr.sorbonne_u.devs_simulation.models.time;

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
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>Time</code> defines a representation for time values with
 * their time unit as well as methods to manipulate them.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		{@code simulatedTime >= 0.0}
 * invariant		timeUnit != null
 * </pre>
 * 
 * <p>Created on : 2016-02-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Time
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	/** the representation of the time infinity i.e., the largest
	 *  possible.															*/
	public static final Time		INFINITY =
										new Time(Double.POSITIVE_INFINITY,
												 TimeUnit.SECONDS) ;
	/** tolerance on the precision of floating points t to consider two
	 *  times as equal.														*/
	public static final double		TOLERANCE = 0.000000001 ;
	/**  the value of the time represented by this object.					*/
	protected double				simulatedTime ;
	/** the time unit of this time, allowing to unambiguously interpret it.	*/
	protected TimeUnit				timeUnit ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new time object with the given time value and time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTime >= 0.0}
	 * pre	timeUnit != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simulatedTime	represented time value.
	 * @param timeUnit		time unit of the represented time.
	 */
	public				Time(
		double simulatedTime,
		TimeUnit timeUnit
		)
	{
		super() ;
		this.simulatedTime = simulatedTime ;
		this.timeUnit = timeUnit ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the time 0 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new time object.
	 * @return	the time 0 in the given time unit.
	 */
	public static Time	zero(TimeUnit u)
	{
		assert	u != null ;

		return new Time(0.0, u) ;
	}

	/**
	 * get the represented simulated time value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the represented simulated time value.
	 */
	public double		getSimulatedTime()
	{
		return this.simulatedTime ;
	}

	/**
	 * get the represented simulated time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the represented simulated time unit.
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit ;
	}

	/**
	 * return true if this time object and <code>t</code> have the same
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object and <code>t</code> have the same time unit.
	 */
	public boolean		hasSameTimeUnit(Time t)
	{
		assert	t != null ;

		return (this == Time.INFINITY || t == Time.INFINITY
							|| this.getTimeUnit().equals(t.getTimeUnit())) ;
	}

	/**
	 * return true if this time object and <code>d</code> have the same
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	duration object.
	 * @return	true if this time object and <code>t</code> have the same time unit.
	 */
	public boolean		hasSameTimeUnit(Duration d)
	{
		assert	d != null ;

		return (this == Time.INFINITY || d == Duration.INFINITY
							|| this.getTimeUnit().equals(d.getTimeUnit())) ;
	}

	/**
	 * create a new time object with the given simulated time value and
	 * the same time unit as this one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	the time value of the new object.
	 * @return	a new time object with the time value <code>t</code> and the time unit of this object.
	 */
	public Time			createFromSimulatedTime(double t)
	{
		return new Time(t, this.timeUnit) ;
	}

	/**
	 * create a new duration object with the given simulated duration value
	 * and the same time unit as this time object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	the duration value of the new object.
	 * @return	a new duration object with the value <code>d</code> and the time unit of this object.
	 */
	public Duration		createFromSimulatedDuration(double d)
	{
		return new Duration(d, this.timeUnit) ;
	}

	/**
	 * return a copy of this time object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	a copy of this time object.
	 */
	public Time			copy()
	{
		return new Time(this.simulatedTime, this.timeUnit) ;
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		other time object.
	 * @return		true if this time object represents the same time as <code>t</code>.
	 */
	public boolean		equals(Time t)
	{
		assert	t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			return t == Time.INFINITY ;
		} else if (t == Time.INFINITY) {
			return this == Time.INFINITY ;
		} else {
			return Math.abs(this.simulatedTime - t.simulatedTime) < TOLERANCE ;
		}
	}

	/**
	 * return true if this time object represents a time preceding
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		other time object.
	 * @return		true if this time object represents a time preceding <code>t</code>.
	 */
	public boolean		lessThan(Time t)
	{
		assert	t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			return false ;
		} else if (t == Time.INFINITY) {
			return this != Time.INFINITY ;
		} else {
			return this.getSimulatedTime() < t.getSimulatedTime() ;
		}
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code> or precedes it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		other time object.
	 * @return		true if this time object represents the same time as <code>t</code> or precedes it.
	 */
	public boolean		lessThanOrEqual(Time t)
	{
		assert	t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			return t == Time.INFINITY ;
		} else if (t == Time.INFINITY) {
			return true ;
		} else {
			return this.getSimulatedTime() <= t.getSimulatedTime() ;
		}
	}


	/**
	 * return true if this time object represents a time following
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		other time object.
	 * @return		true if this time object represents a time following <code>t</code>.
	 */
	public boolean		greaterThan(Time t)
	{
		assert	t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			return t != Time.INFINITY ;
		} else if (t == Time.INFINITY) {
			return false ;
		} else {
			return this.getSimulatedTime() > t.getSimulatedTime() ;
		}
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code> or following it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t		other time object.
	 * @return		true if this time object represents the same time as <code>t</code> or following it.
	 */
	public boolean		greaterThanOrEqual(Time t)
	{
		assert	t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			return true ;
		} else if (t == Time.INFINITY) {
			return this == Time.INFINITY ;
		} else {
			return this.getSimulatedTime() >= t.getSimulatedTime() ;
		}
	}

	/**
	 * add a duration to this time returning the new time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameTimeUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	duration to be added.
	 * @return	the new time adding <code>d</code> to this time.
	 */
	public Time			add(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameTimeUnit(d) ;

		if (this == Time.INFINITY) {
			return Time.INFINITY ;
		} else if (d == Duration.INFINITY) {
			return Time.INFINITY ;
		} else {
			return  this.createFromSimulatedTime(
						this.simulatedTime + d.getSimulatedDuration()) ;
		}
	}

	/**
	 * subtract a time from this time returning the duration between the two.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * pre	this.hasSameTimeUnit(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	time to be subtracted.
	 * @return	the duration between <code>t</code> and this time.
	 */
	public Duration		subtract(Time t)
	{
		assert t != null ;
		assert	this.hasSameTimeUnit(t) ;

		if (this == Time.INFINITY) {
			if (t.equals(Time.INFINITY)) {
				throw new RuntimeException("Time not defined!") ;
			} else {
				return Duration.INFINITY ;
			}
		} else if (t == Time.INFINITY) {
			throw new RuntimeException("Time not defined!") ;
		} else {
			return new Duration(
						this.getSimulatedTime() - t.getSimulatedTime(),
						this.timeUnit) ;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		return "Time(" + this.getSimulatedTime() + ", " +
												this.getTimeUnit() + ")" ;
	}
}
// -----------------------------------------------------------------------------
