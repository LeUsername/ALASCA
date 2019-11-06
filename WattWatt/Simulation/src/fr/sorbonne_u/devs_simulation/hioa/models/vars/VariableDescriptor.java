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

import java.lang.reflect.Field;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;

// -----------------------------------------------------------------------------
/**
 * The class <code>VariableDescriptor</code> extends the class
 * <code>StaticVariableDescriptor</code> with dynamic information about
 * variables: the owner model and the Java field that defines the variable
 * in the owner model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>{@code
 * invariant    this.owner != null
 * invariant    this.f != null
 * invariant    this.name.equals(this.f.getName())
 * invariant	Value.class.isAssignableFrom(cvd.f.getType())
 * invariant	this.f.getDeclaringClass().isAssignableFrom(this.owner.getClass())
 * invariant	let beginIndex = f.getGenericType().getTypeName().indexOf("<") + 1
 * 				and endIndex = f.getGenericType().getTypeName().indexOf(">")
 * 				in f.getGenericType().getTypeName().substring(beginIndex, endIndex).
 * 									equals(type.getCanonicalName())
 * }</pre>
 * 
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			VariableDescriptor
extends		StaticVariableDescriptor
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	/** the model owning the variable.								*/
	protected final AtomicHIOA		owner ;
	/** the Java field which represents the variable in the model. 	*/
	protected final Field			f ;

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * create a complete variable descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>{@code
	 * pre	owner != null
	 * pre	AtomicHIOA.class.isAssignableFrom(owner.getClass())
	 * pre	f != null
	 * pre	f.getDeclaringClass().isAssignableFrom(owner.getClass())
	 * pre	AtomicHIOA.class.isAssignableFrom(f.getDeclaringClass())
	 * pre	Value.class.isAssignableFrom(f.getType())
	 * pre	let beginIndex = f.getGenericType().getTypeName().indexOf("<") + 1
	 * 		and endIndex = f.getGenericType().getTypeName().indexOf(">")
	 * 		in f.getGenericType().getTypeName().substring(beginIndex, endIndex).
	 * 									equals(type.getCanonicalName())
	 * post	owner.equals(this.getOwner())
	 * post	f.equals(this.getField)
	 * }</pre>
	 *
	 * @param owner			model that owns this variable.
	 * @param f				field declared to represent the variable.
	 * @param type			actual type of the model variable.
	 * @param visibility	visibility of the variable (imported, exported, internal).
	 */
	public				VariableDescriptor(
		AtomicHIOA owner,
		Field f,
		Class<?> type,
		VariableVisibility visibility
		)
	{
		super(f.getName(), type, visibility);

		assert	owner != null ;
		assert	AtomicHIOA.class.isAssignableFrom(owner.getClass()) ;
		assert	f != null ;
		assert	f.getDeclaringClass().isAssignableFrom(owner.getClass()) ;
		assert	Value.class.isAssignableFrom(f.getType()) ;
		String genericTypeName = f.getGenericType().getTypeName() ;
		int beginIndex = genericTypeName.indexOf("<") + 1 ;
		int endIndex = genericTypeName.indexOf(">") ;
		assert	beginIndex >= 0 && beginIndex < genericTypeName.length() &&
				endIndex >= beginIndex && endIndex < genericTypeName.length() &&
				f.getGenericType().getTypeName().substring(beginIndex, endIndex).
										equals(type.getCanonicalName()) ;

		this.owner = owner ;
		this.f = f ;

		assert	this.getOwner().equals(owner) ;
		assert	this.getField().equals(f) ;
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * return the model owner of the variable.
	 * 
	 * @return	the model owner of the variable.
	 */
	public AtomicHIOA	getOwner()
	{
		return owner;
	}

	/**
	 * return the Java field defining the variable.
	 * 
	 * @return	the Java field defining the variable.
	 */
	public Field		getField()
	{
		return f ;
	}
}
// -----------------------------------------------------------------------------
