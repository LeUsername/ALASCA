package fr.sorbonne_u.devs_simulation.hioa.models;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.hioa.simulators.HIOA_AtomicEngine;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AtomicHIOA</code> implements the base level
 * hybrid input/output automata, having imported, internal and exported
 * variables as well as events which implementation is based on and
 * inherited from DEVS atomic models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * See the package <code>fr.sorbonne_u.devs_simulation.hioa</code>
 * for more information about Hybrid Input/output Automata and their
 * overall implementation as simulation models.
 * </p>
 * 
 * <p>
 * Concrete atomic HIOA are defined as by the Java class (subclasses of
 * <code>AtomicHIOA</code>) by their imported, internal and exported
 * variables as well as their trajectories which are expressed by
 * equations within the simulation protocol methods like
 * <code>userDefinedInternalTransition</code>. Variables are declared
 * as fields in user-defined models and indicated by annotations put on
 * these fields (@see fr.sorbonne_u.devs_simulation.hioa.annotations).
 * Thus, the required information to defien the variables of the HIOA
 * is gathered at run time from these annotations:
 * <code>@ImportedVariable</code>, <code>@ExportedVariable</code> and
 * <code>@InternalVariable</code> put by the user on the declared
 * fields.
 * </p>
 * <p>
 * For example, an exported variable <code>x</code> of type double is
 * declared as follows:
 * </p>
 * <pre>
 *     &#64;ExportedVariable(type = Double.class)
 *     protected final Value&#60;Double&#62; x = new Value&#60;Double&#62;(this, 10.0) ;
 * </pre>
 * <p>
 * The type defined in the annotation is the one used as the generic
 * parameter of the class {@code Value<T>}. Notice that
 * exported variables are declared as <code>final</code>, hence the
 * placeholder for its values, instance of {@code Value<Double>},
 * will remain the same throughout the simulation and will be shared
 * with the other atomic HIOA models that import the variable.
 * </p>
 * <p>
 * An internal variable <code>y</code> of type double is declared as
 * follows:
 * </p>
 * <pre>
 *     &#64;InternalVariable(type = Double.class)
 *     protected final Value&#60;Double&#62; y = new Value&#60;Double&#62;(this, 10.0) ;
 * </pre>
 * <p>
 * For internal variables, the placeholder is used for the sake of
 * homogeneity in the treatment of model variables, as they can't be
 * shared with other models but rather hidden in the declaring one.
 * </p>
 * <p>
 * An imported variable <code>z</code> of type double is declared as in
 * the following:
 * </p>
 * <pre>
 *     &#64;ImportedVariable(type = Double.class)
 *     protected Value&#60;Double&#62; z ;
 * </pre>
 * <p>
 * An imported variable is not <code>final</code> nor initialised because
 * they will be linked to the exported one during the HIOA simulation
 * models composition process.
 * </p>
 * 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true		// TODO
 * </pre>
 * 
 * <p>Created on : 2018-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AtomicHIOA
extends		AtomicModel
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L ;
	/** variable descriptors for this model imported variables.				*/
	protected final VariableDescriptor[]				importedVariables ;
	/** variable descriptors for this model exported variables.				*/
	protected final VariableDescriptor[]				exportedVariables ;
	/** variable descriptors for this model internal variables.				*/
	protected final VariableDescriptor[]				internalVariables ;
	/** map from variable descriptors to their assigned value
	 *  placeholder {@code Value<?>} object.								*/
	protected final Map<VariableDescriptor,Value<?>>	variables2values ;

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * create an atomic hybrid input/output model with the given URI (if null,
	 * one will be generated) and to be run by the given simulator (or by the
	 * one of an ancestor coupled model if null) using the given time unit for
	 * its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	simulatedTimeUnit != null
	 * pre	simulationEngine == null ||
	 * 		    	simulationEngine instanceof HIOA_AtomicEngine
	 * post	this.getURI() != null
	 * post	uri != null implies this.getURI().equals(uri)
	 * post	this.getSimulatedTimeUnit().equals(simulatedTimeUnit)
	 * post	simulationEngine != null implies
	 * 			this.getSimulationEngine().equals(simulationEngine)
	 * post	!isDebugModeOn()
	 * </pre>
	 *
	 * @param uri				unique identifier of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @throws Exception   		<i>TODO</i>.
	 */
	public				AtomicHIOA(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		assert	simulationEngine == null ||
							simulationEngine instanceof HIOA_AtomicEngine ;

		this.importedVariables = this.getImportedVars(this.getClass()) ;
		this.exportedVariables = this.getExportedVars(this.getClass()) ;
		this.internalVariables = this.getInternalVars(this.getClass()) ;
		this.variables2values = new HashMap<VariableDescriptor,Value<?>>() ;
	}

	/**
	 * check the class invariant over the object <code>hioa</code>; this
	 * invariant is true only after the variables have been initialised
	 * by calling the method <code>staticInitialiseVariables</code>.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * The method is meant to check the invariant of the instances, for
	 * example at the end of a constructor. It is static to better control
	 * the time at which the invariant is checked. Indeed, if it was not
	 * static and called at the end of a constructor, it would not work
	 * properly. Take a class A and a subclass B, where B refines
	 * checkInvariant, the chaining of constructors from B to A would make
	 * a call to checkInvariant at the end of A's constructor, a call that
	 * would refer to B's definition of checkInvariant. Because the chaining
	 * through super must appear at the beginning of B's constructor, it is
	 * impossible to initialise B's variables before its checkInvariant
	 * would be called by A's constructor, hence impossible to put the
	 * invariant in place before.
	 * 
	 * When a subclass want to check the invariant of the superclass part,
	 * it must explicitly called its static checkInvariant method.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	hioa != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param hioa			HIOA atomic model to be checked.
	 * @return				true if the invariant of AtomicHIOA is satisfied.
	 * @throws Exception		<i>TODO</i>.
	 */
	public static boolean	checkInvariant(AtomicHIOA hioa)
	throws Exception
	{
		assert	hioa != null ;
		
		boolean invariant = true ;
		invariant &= hioa.importedVariables != null ;
		invariant &= hioa.exportedVariables != null ;
		invariant &= hioa.internalVariables != null ;
		assert	invariant ;

		for (int i = 0 ; i < hioa.exportedVariables.length ; i++) {
			invariant &= hioa.variables2values.keySet().
									contains(hioa.exportedVariables[i]) ;
			invariant &= hioa.variables2values.
									get(hioa.exportedVariables[i]) != null ;
		}
		assert	invariant ;

		for (int i = 0 ; i < hioa.internalVariables.length ; i++) {
			invariant &= hioa.variables2values.keySet().
									contains(hioa.internalVariables[i]) ;
			invariant &= hioa.variables2values.
									get(hioa.internalVariables[i]) != null ;
		}
		assert	invariant ;

		return invariant ;
	}

	//--------------------------------------------------------------------------
	// Internal methods
	//--------------------------------------------------------------------------

	/**
	 * get the imported variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the array of imported variables descriptors.
	 * @throws Exception	<i>todo.</i>
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getImportedVars(
		Class<? extends AtomicHIOA> c
		) throws Exception
	{
		assert	c != null ;

		Class<? extends AtomicHIOA> current = c ;
		VariableDescriptor vd = null ;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>() ;
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields() ;
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true) ;
				if (AtomicHIOA.isImportedField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.IMPORTED) ;
					ret.add(vd) ;
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass() ;
		}
		return ret.toArray(new VariableDescriptor[0]) ;
	}

	/**
	 * get the imported variable static descriptors from the class definition
	 * of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return 				the imported variable static descriptors
	 * @throws Exception	<i>todo.</i>
	 */
	@SuppressWarnings("unchecked")
	public static StaticVariableDescriptor[]	getStaticImportedVars(
		Class<? extends AtomicHIOA> c
		) throws Exception
	{
		assert	c != null ;

		Class<? extends AtomicHIOA> current = c ;
		StaticVariableDescriptor vd = null ;
		Vector<StaticVariableDescriptor> ret =
								new Vector<StaticVariableDescriptor>() ;
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields() ;
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true) ;
				if (AtomicHIOA.isImportedField(allFields[i])) {
					vd = new StaticVariableDescriptor(
								allFields[i].getName(),
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.IMPORTED) ;
					ret.add(vd) ;
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass() ;
		}
		return ret.toArray(new StaticVariableDescriptor[0]) ;
	}

	/**
	 * get the internal variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the variable descriptors.
	 * @throws Exception	<i>todo.</i>
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getInternalVars(
		Class<? extends AtomicHIOA> c
		) throws Exception
	{
		assert	c != null ;

		Class<? extends AtomicHIOA> current = c ;
		VariableDescriptor vd = null ;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>() ;
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields() ;
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true) ;
				if (AtomicHIOA.isInternalField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.INTERNAL) ;
					if (this.hasDebugLevel(2)) {
						this.logMessage("AtomicHIOA#getInternalVars " +
							"name = " + vd.getName() + ", " +
							"type = " + vd.getType().getName() + ", " +
							"visibility = " + vd.getVisibility() + ", " +
							"owner URI = " + vd.getOwner().getURI() + ", " +
							"field = " + vd.getField()) ;
					}
					ret.add(vd) ;
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass() ;
		}
		return ret.toArray(new VariableDescriptor[0]) ;
	}

	/**
	 * get the exported variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the exported variable static descriptors
	 * @throws Exception	<i>todo.</i>
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getExportedVars(
		Class<? extends AtomicHIOA> c
		) throws Exception
	{
		assert	c != null ;

		Class<? extends AtomicHIOA> current = c ;
		VariableDescriptor vd = null ;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>() ;
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields() ;
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true) ;
				if (AtomicHIOA.isExportedField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.EXPORTED) ;
					ret.add(vd) ;
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass() ;
		}
		return ret.toArray(new VariableDescriptor[0]) ;
	}

	/**
	 * get the exported variable static descriptors from the class definition
	 * of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	c != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the array of exported variable descriptors.
	 * @throws Exception	<i>todo.</i>
	 */
	@SuppressWarnings("unchecked")
	public static StaticVariableDescriptor[]	getStaticExportedVars(
		Class<? extends AtomicHIOA> c
		) throws Exception
	{
		assert	c != null ;

		Class<? extends AtomicHIOA> current = c ;
		StaticVariableDescriptor vd = null ;
		Vector<StaticVariableDescriptor> ret =
								new Vector<StaticVariableDescriptor>() ;
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields() ;
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true) ;
				if (AtomicHIOA.isExportedField(allFields[i])) {
					vd = new StaticVariableDescriptor(
								allFields[i].getName(),
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.EXPORTED) ;
					ret.add(vd) ;
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass() ;
		}
		return ret.toArray(new StaticVariableDescriptor[0]) ;
	}

	/**
	 * return true if <code>f</code> represents an imported variable in
	 * the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	f != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an imported variable in the model.
	 */
	protected static boolean	isImportedField(Field f)
	{
		assert	f != null ;

		Annotation a = f.getAnnotation(ImportedVariable.class) ;
		return a != null ;
	}

	/**
	 * return true if <code>f</code> represents an exported variable in the
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	f != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an exported variable in the model.
	 */
	protected static boolean	isExportedField(Field f)
	{
		assert	f != null ;

		Annotation a = f.getAnnotation(ExportedVariable.class) ;
		return a != null ;
	}

	/**
	 * return true if <code>f</code> represents an internal variable in the
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	f != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an internal variable in the model.
	 */
	protected static boolean	isInternalField(Field f)
	{
		assert	f != null ;

		Annotation a = f.getAnnotation(InternalVariable.class) ;
		return a != null ;
	}

	/**
	 * return the type declared by the variable annotation of <code>f</code>
	 * or null of none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	f != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param f	a Java field.
	 * @return	the type declared by the variable annotation of <code>f</code> or null of none.
	 */
	protected static Class<?>	getDeclaredType(Field f)
	{
		assert	f != null ;

		if (AtomicHIOA.isImportedField(f)) {
			return ((ImportedVariable)f.getAnnotation(
											ImportedVariable.class)).type() ;
		} else if (AtomicHIOA.isExportedField(f)) {
			return ((ExportedVariable)f.getAnnotation(
											ExportedVariable.class)).type() ;
		} else if (AtomicHIOA.isInternalField(f)) {
			return ((InternalVariable)f.getAnnotation(
											InternalVariable.class)).type() ;
		} else {
			return null ;
		}
	}

	// -------------------------------------------------------------------------
	// Composition protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#staticInitialiseVariables()
	 */
	@Override
	public void			staticInitialiseVariables()
	{
		// This method is needed because, after testing, I found that the
		// static initialisation of fields is not visible to the reflective
		// accesses through the class java.lang.reflect.Field until after the
		// the constructor has terminated its execution. Hence, copying the
		// Value<?> instances to the variables2values data structure is
		// only possible after the constructor has terminated.

		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			try {
				Field f = this.exportedVariables[i].getField() ;
				f.setAccessible(true) ;
				this.variables2values.put(this.exportedVariables[i],
										  (Value<?>) f.get(this)) ;
				((Value<?>)f.get(this)).
						setVariableDescriptor(this.exportedVariables[i]);
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}

		for (int i = 0 ; i < this.internalVariables.length ; i++) {
			try {
				Field f = this.internalVariables[i].getField() ;
				f.setAccessible(true) ;
				this.variables2values.put(this.internalVariables[i],
										  (Value<?>) f.get(this)) ;
				((Value<?>)f.get(this)).
						setVariableDescriptor(this.internalVariables[i]);
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}

		try {
			assert	AtomicHIOA.checkInvariant(this) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return this.importedVariables.length == 0 &&
								this.exportedVariables.length == 0 ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return !this.isTIOA() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		boolean ret = false ;
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			ret = ret || (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) ;
			if (ret) break ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(
		String sinkVariableName,
		Class<?> sinkVariableType
		)
	{
		boolean ret = false ;
		for (StaticVariableDescriptor vd : this.importedVariables) {
			ret = ret || (vd.getName().equals(sinkVariableName) &&
							sinkVariableType.isAssignableFrom(vd.getType())) ;
			if (ret) break ;
		}
		return ret ;
	}

	/**
	 * return true if the variable is internal for this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	sinkVariableName != null
	 * pre	sinkVariableType != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param sinkVariableName	variable name.
	 * @param sinkVariableType	variable type.
	 * @return					true if the variable is internal for this model.
	 */
	protected boolean	isInternalVariable(
		String sinkVariableName,
		Class<?> sinkVariableType
		)
	{
		assert	sinkVariableName != null ;
		assert	sinkVariableType != null ;

		boolean ret = false ;
		for (StaticVariableDescriptor vd : this.internalVariables) {
			ret = ret || (vd.getName().equals(sinkVariableName) &&
							sinkVariableType.isAssignableFrom(vd.getType())) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	throws Exception
	{
		StaticVariableDescriptor[] ret =
				new StaticVariableDescriptor[this.importedVariables.length] ;
		for (int i = 0 ; i <this.importedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
							this.importedVariables[i].getName(),
							this.importedVariables[i].getType(),
							this.importedVariables[i].getVisibility()) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	throws Exception
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.exportedVariables.length] ;
		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
							this.exportedVariables[i].getName(),
							this.exportedVariables[i].getType(),
							this.exportedVariables[i].getVisibility()) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception
	{
		assert	modelURI != null && modelURI.equals(this.getURI()) ;
		assert	this.isExportedVariable(sourceVariableName,
										sourceVariableType) ;

		Value<?> ret = null ;
		for (VariableDescriptor vd : this.variables2values.keySet()) {
			if (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) {
				ret = this.variables2values.get(vd) ;
				break ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception
	{
		assert	modelURI != null && modelURI.equals(this.getURI()) ;
		assert	this.isImportedVariable(sinkVariableName, sinkVariableType) ;

		VariableDescriptor vd = null ;
		for (VariableDescriptor temp : this.importedVariables) {
			if (temp.getName().equals(sinkVariableName) &&
						sinkVariableType.isAssignableFrom(temp.getType())) {
				vd = temp ;
				break ;
			}
		}
		assert	vd != null ;

		vd.getField().setAccessible(true) ;
		vd.getField().set(this, value) ;
		this.variables2values.put(vd, value) ;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * besides initialising the state of the model, the method also calls
	 * <code>initialiseVariables</code>, which then becomes part of the
	 * simulation protocol.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime) ;
		this.initialiseVariables(initialTime) ;
	}

	/**
	 * Initialise the model variables.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param startTime		simulated time at which the simulation will start.
	 */
	protected void		initialiseVariables(Time startTime)
	{
		for (VariableDescriptor avd : this.variables2values.keySet()) {
			this.variables2values.get(avd).time = startTime ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		if (this.hasDebugLevel(2)) {
			this.logMessage("AtomicHIOA>>internalTransition " + this.uri) ;
		}
		assert	this.getCurrentStateTime().
									lessThanOrEqual(this.timeOfNextEvent) ;

		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getCurrentStateTime()) ;
		this.currentStateTime = this.getTimeOfNextEvent() ;

		this.userDefinedInternalTransition(elapsedTime) ;

		this.nextTimeAdvance = this.timeAdvance() ;
		this.timeOfNextEvent =
					this.currentStateTime.add(this.nextTimeAdvance) ;

		// Postconditions
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	this.getTimeOfNextEvent().subtract(
												this.getCurrentStateTime()).
									equals(this.getNextTimeAdvance()) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#confluentTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			confluentTransition(Duration elapsedTime)
	{
		throw new RuntimeException(
				"AtomicHIOA#userDefinedConfluentTransition not"
												+ " implemented yet!") ;
	}

	//--------------------------------------------------------------------------
	// Debugging
	//--------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		System.out.println(indent + "--------------------") ;
		try {
			System.out.println(indent + "AtomicHIOA " + this.getURI() +
				" " + this.currentStateTime.getSimulatedTime()
				+ " " + elapsedTime.getSimulatedDuration()) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		System.out.println(indent + "--------------------") ;
		this.showCurrentStateContent(indent, elapsedTime) ;
		System.out.println(indent + "--------------------") ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime) ;
		System.out.print(this.modelContentAsString(indent)) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent) {
		try {
			String ret = "" ;
			if (this.getParent() == null) {
				ret += indent +
					"---------------------------------------------------\n" ;
			}
			ret += indent + "Atomic HIOA " + this.getURI() + "\n" ;
			ret += indent +
					"---------------------------------------------------\n" ;
			ret += this.modelContentAsString(indent) ;
			if (this.getParent() == null) {
				ret += indent + 
					"---------------------------------------------------\n" ;
			}
			return ret ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#modelContentAsString(java.lang.String)
	 */
	@Override
	protected String		modelContentAsString(String indent)
	{
		try {
			String ret = "" ;
			ret += super.modelContentAsString(indent) ;
			ret += indent + "Imported variables:\n" ;
			for (VariableDescriptor vd : this.importedVariables) {
				ret += indent + "  " + this.modelVariableAsString(
										vd, this.variables2values.get(vd))
						+ "\n";
			}
			ret += indent + "Exported variables:\n" ;
			for (VariableDescriptor vd : this.exportedVariables) {
				ret += indent + "  " + this.modelVariableAsString(
										vd, this.variables2values.get(vd))
						+ "\n" ;
			}
			ret += indent + "Internal variables:\n" ;
			for (VariableDescriptor vd : this.internalVariables) {
				ret += indent + "  " + this.modelVariableAsString(
										vd, this.variables2values.get(vd))
						+ "\n" ;
			}
			return ret ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	protected String		modelVariableAsString(
		VariableDescriptor vd,
		Value<?> v
		) throws Exception
	{
		String ret = "Variable[VariableDescriptor(" ;
		ret += "name = " + vd.getName() + ", " ;
		ret += "uri = " + vd.getOwner().getURI() + ", " ;
		ret += "type = " + vd.getType() + ", " ;
		ret += "visibility = " + vd.getVisibility() + ", " ;
		ret += "field name = " + vd.getField().getName() + "), Value(" ;
		if (v != null) {
			ret += "uri = " + v.getOwner().getURI() + ", " ;
			ret += "time unit = " + v.getTimeUnit() + ", " ;
			ret += "descriptor = " + v.getDescriptor().getName() + ", " ;
			ret += "v = " + v.v + ", " ;
			ret += "time = " + (v.time != null ?
									v.time.getSimulatedTime()
							   :		"null") + ")]" ;
		} else {
			ret += "null)]" ;
		}
		return ret ;
	}
}
// -----------------------------------------------------------------------------
