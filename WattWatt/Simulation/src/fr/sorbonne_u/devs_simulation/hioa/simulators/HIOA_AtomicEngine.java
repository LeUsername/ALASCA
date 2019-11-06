package fr.sorbonne_u.devs_simulation.hioa.simulators;

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

import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.interfaces.VariablesSharingI;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_AtomicEngine</code> implements the atomic simulation
 * engine for HIOA DEVS simulation models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The major additions from the standard atomic engine target the continuous
 * variables management, both at composition, initialisation and run time.
 * </p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				HIOA_AtomicEngine
extends		AtomicEngine
implements	VariablesSharingI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an HIOA atomic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public				HIOA_AtomicEngine()
	{
		super() ;
	}

	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		assert	this.simulatedModel != null ;

		return this.simulatedModel.isTIOA() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#isOrdered()
	 */
	@Override
	public boolean		isOrdered() throws Exception
	{
		return this.simulatedModel.isOrdered() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.simulatedModel.isExportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(String name, Class<?> type)
	throws Exception
	{
		return this.simulatedModel.isImportedVariable(name, type) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables() throws Exception
	{
		return this.simulatedModel.getImportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables() throws Exception
	{
		return this.simulatedModel.getExportedVariables() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		) throws Exception
	{
		return this.simulatedModel.getActualExportedVariableValueReference(
							modelURI, sourceVariableName, sourceVariableType) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		) throws Exception
	{
		this.simulatedModel.setImportedVariableValueReference(
						modelURI, sinkVariableName, sinkVariableType, value) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.interfaces.VariablesSharingI#staticInitialiseVariables()
	 */
	@Override
	public void			staticInitialiseVariables()
	{
		((VariablesSharingI)this.simulatedModel).staticInitialiseVariables() ;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		super.showCurrentStateContent(indent, elapsedTime) ;
		// TODO
	}
}
// -----------------------------------------------------------------------------
