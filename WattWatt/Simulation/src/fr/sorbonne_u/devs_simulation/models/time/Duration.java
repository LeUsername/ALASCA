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
 * The class <code>Duration</code> defines a representation for duration
 * values with their time unit as well as methods to manipulate them.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		simulatedDuration &gt;= 0.0
 * invariant		timeUnit != null
 * </pre>
 * 
 * <p>Created on : 2016-02-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Duration
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L ;
	/** the representation of the duration infinity i.e., the largest
	 *  possible.															*/
	public static final Duration	INFINITY =
										new Duration(Double.POSITIVE_INFINITY,
													 TimeUnit.SECONDS) ;
	/** tolerance on the precision of floating points d to consider two
	 *  durations as equal.													*/
	public static final double		TOLERANCE = 0.000000001 ;
	/** the value of the duration represented by this object.				*/
	protected double				simulatedDuration ;
	/** the time unit of this duration, allowing to unambiguously
	 *  interpret it.														*/
	protected TimeUnit				timeUnit ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new duration object with the given duration value and
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedDuration &gt;= 0.0
	 * pre	timeUnit != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param simulatedDuration	represented duration value.
	 * @param timeUnit			time unit of the represented duration.
	 */
	public				Duration(
		double simulatedDuration,
		TimeUnit timeUnit
		)
	{
		super() ;

		double duration = simulatedDuration ;
		if (duration < 0.0) {
			if (duration < -Duration.TOLERANCE) {
				System.out.println("Duration t = " + duration) ;
			} else {
				duration = 0.0 ;
			}
		}
		assert	duration >= 0.0 ;
		assert	timeUnit != null ;

		this.simulatedDuration = simulatedDuration ;
		this.timeUnit = timeUnit ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the duration 0 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 0 in the given time unit.
	 */
	public static Duration	zero(TimeUnit u)
	{
		assert	u != null ;

		return new Duration(0.0, u) ;
	}

	/**
	 * return the duration 1 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	u != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 1 in the given time unit.
	 */
	public static Duration	one(TimeUnit u)
	{
		assert	u != null ;

		return new Duration(1.0, u) ;
	}

	/**
	 * get the simulated duration value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the simulated duration value.
	 */
	public double		getSimulatedDuration()
	{
		return this.simulatedDuration ;
	}

	/**
	 * get this duration object time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	this duration object time unit.
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit ;
	}

	/**
	 * return true if this duration object and <code>d</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Duration d)
	{
		assert	d != null ;

		return (this == Duration.INFINITY || d == Duration.INFINITY
							|| this.getTimeUnit().equals(d.getTimeUnit())) ;
	}

	/**
	 * return true if this duration object and <code>t</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	time object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Time t)
	{
		assert	t != null ;

		return (this == Duration.INFINITY || t == Time.INFINITY
							|| this.getTimeUnit().equals(t.getTimeUnit())) ;
	}

	/**
	 * create a new duration object with the given simulated value and
	 * the same time unit as this one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d &gt;= 0.0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	simulated duration value.
	 * @return	a new duration object with the given simulated value and the same time unit as this one.
	 */
	public Duration		createFromSimulatedDuration(double d)
	{
		assert	d >= 0.0 ;

		return new Duration(d, this.timeUnit) ;
	}

	/**
	 * return a copy of this duration object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	a copy of this duration object.
	 */
	public Duration		copy()
	{
		return new Duration(this.simulatedDuration, this.timeUnit) ;
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code>.
	 */
	public boolean		equals(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			return d == Duration.INFINITY ;
		} else if (d == Duration.INFINITY) {
			return this == Duration.INFINITY ;
		} else {
			return Math.abs(this.simulatedDuration - d.simulatedDuration)
										< Duration.TOLERANCE ;
		}
	}

	/**
	 * return true if this duration object represents a smaller duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a smaller duration than <code>d</code>.
	 */
	public boolean		lessThan(Duration d)
	{
		assert	d != null ;
		assert	(this == Duration.INFINITY
				|| d == (Duration.INFINITY)
				|| this.timeUnit.equals(d.timeUnit)) ;

		if (this == Duration.INFINITY) {
			return false ;
		} else if (d == Duration.INFINITY) {
			return this != Duration.INFINITY ;
		} else {
			return this.simulatedDuration < d.simulatedDuration ;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or less.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or less.
	 */
	public boolean		lessThanOrEqual(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			return d == Duration.INFINITY ;
		} else if (d == Duration.INFINITY) {
			return true ;
		} else {
			return this.simulatedDuration <= d.simulatedDuration ;
		}
	}

	/**
	 * return true if this duration object represents a greater duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a greater duration than <code>d</code>.
	 */
	public boolean		greaterThan(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			return d != Duration.INFINITY ;
		} else if (d == Duration.INFINITY) {
			return false ;
		} else {
			return this.simulatedDuration > d.simulatedDuration ;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or greater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or greater.
	 */
	public boolean		greaterThanOrEqual(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			return true ;
		} else if (d == Duration.INFINITY) {
			return this == Duration.INFINITY ;
		} else {
			return this.simulatedDuration >= d.simulatedDuration ;
		}
	}

	/**
	 * add two durations and return a new duration object representing the
	 * result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	a new duration object representing the sum.
	 */
	public Duration		add(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			return Duration.INFINITY ;
		} else if (d == Duration.INFINITY) {
			return Duration.INFINITY ;
		} else {
			return new Duration(this.simulatedDuration +
										d.simulatedDuration,
							this.timeUnit) ;
		}
	}

	/**
	 * subtract two durations and return a new duration object representing the
	 * result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	d != null
	 * pre	this.hasSameUnit(d)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	a new duration object representing the subtraction.
	 */
	public Duration		subtract(Duration d)
	{
		assert	d != null ;
		assert	this.hasSameUnit(d) ;

		if (this == Duration.INFINITY) {
			if (d == Duration.INFINITY) {
				throw new RuntimeException("Duration not defined!") ;
			} else {
				return Duration.INFINITY ;
			}
		} else if (d == Duration.INFINITY) {
			throw new RuntimeException("Duration not defined!") ;
		} else {
			return new Duration(this.simulatedDuration -
										d.simulatedDuration,
							this.timeUnit) ;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		return "Duration(" + this.getSimulatedDuration() + ", " +
							 this.getTimeUnit() + ")" ;
	}
}
// -----------------------------------------------------------------------------
