package fr.sorbonne_u.devs_simulation.architectures;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>Architecture</code> describes a DEVS simulation architecture
 * with a set of the atomic model descriptors, a set of the coupled model
 * descriptors and the URI of the root model.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true		// TODO
 * </pre>
 * 
 * <p>Created on : 2017-10-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Architecture
implements	ArchitectureI
{
	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	private static final long 				serialVersionUID = 1L ;
	public static final boolean				DEBUG = false ;

	/** the URI of the root model; if the model is composed of only one
	 *  atomic model, its URI is the root model URI, but if the model
	 *  contains at least one coupled model, then the URI of the root
	 *  model is a coupled model URI.										*/
	protected String						rootModelURI ;
	/** Creation descriptors for atomic models.						 		*/
	protected final Map<String,AbstractAtomicModelDescriptor>
											atomicModelDescriptors ;
	/** Creation descriptors for coupled models.						 	*/
	protected final Map<String,CoupledModelDescriptor>
											coupledModelDescriptors ;
	/** Map from model URIs to their parent model URI.						*/
	protected final Map<String,String>		modelsParent ;
	/** Simulation time unit used in this simulation model architecture.	*/
	protected TimeUnit						simulationTimeUnit ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	rootModelURI != null
	 * pre	atomicModelDescriptors != null
	 * pre	coupledModelDescriptors != null
	 * pre	simulationTimeUnit != null
	 * post	checkCompleteArchitectureInvariant(this)
	 * </pre>
	 *
	 * @param rootModelURI				URI of the root model in thia architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 * @throws Exception				<i>TODO</i>
	 */
	public				Architecture(
		String rootModelURI,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		) throws Exception
	{
		assert	rootModelURI != null ;
		assert	atomicModelDescriptors != null ;
		assert	coupledModelDescriptors != null ;
		assert	simulationTimeUnit != null ;

		this.rootModelURI = rootModelURI ;
		this.modelsParent = new HashMap<String,String>() ;
		this.atomicModelDescriptors = atomicModelDescriptors ;
		this.coupledModelDescriptors = coupledModelDescriptors ;
		for (String modelURI : coupledModelDescriptors.keySet()) {
			for (String childURI : coupledModelDescriptors.
											get(modelURI).submodelURIs) {
				this.modelsParent.put(childURI, modelURI) ;
			}
		}
		this.simulationTimeUnit = simulationTimeUnit ;

		// Post-condition
		assert	!this.isComplete() ||
					Architecture.checkCompleteArchitectureInvariant(this) ;
	}

	/**
	 * check the invariant for a complete model architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	ma != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ma	instance to be checked.
	 * @return		true if the invariant is satisfied.
	 */
	public static boolean	checkCompleteArchitectureInvariant(
		Architecture ma
		)
	{
		assert	ma != null ;

		if (DEBUG) {
			System.out.println(
				"ModelArchitecture#checkCompleteArchitectureInvariant") ;
		}
		boolean invariant = true ;

		// There is a root model in the architecture
		invariant &= ma.rootModelURI != null ;
		assert	invariant ;
		invariant &= ma.modelsParent != null ;
		assert	invariant ;
		invariant &= !ma.modelsParent.containsKey(ma.rootModelURI) ;
		assert	invariant ;
		invariant &= ma.atomicModelDescriptors.size() > 1 ||
									ma.coupledModelDescriptors.isEmpty() ;
		assert	invariant ;
		invariant &= ma.atomicModelDescriptors.size() > 1 ||
						ma.atomicModelDescriptors.keySet().
											contains(ma.rootModelURI) ;
		assert	invariant ;
		invariant &= ma.atomicModelDescriptors.size() == 1 ||
						ma.coupledModelDescriptors.containsKey(
														ma.rootModelURI) ;
		assert	invariant ;
		invariant &= !(ma.atomicModelDescriptors.containsKey(ma.rootModelURI)
					 && ma.coupledModelDescriptors.containsKey(
							 							ma.rootModelURI)) ;
		assert	invariant ;
		for (Entry<String,CoupledModelDescriptor> entry :
									ma.coupledModelDescriptors.entrySet()) {
			invariant &= entry.getValue() != null ;
			assert	invariant ;
			for (String childURI : entry.getValue().submodelURIs) {
				invariant &=
						ma.atomicModelDescriptors.containsKey(childURI) ||
						ma.coupledModelDescriptors.containsKey(childURI) ;
				assert	invariant ;
				invariant &=	
					(ma.isEngineCreationMode(childURI,
									SimulationEngineCreationMode.NO_ENGINE) ?
						ma.isEngineCreationMode(
								ma.modelsParent.get(childURI),
								SimulationEngineCreationMode.NO_ENGINE)
						||
						ma.isEngineCreationMode(
								ma.modelsParent.get(childURI),
								SimulationEngineCreationMode.ATOMIC_ENGINE)
					:	true
					) ;
				assert	invariant ;
				invariant &=	
					(!ma.isEngineCreationMode(childURI,
									SimulationEngineCreationMode.NO_ENGINE) ?
							ma.isEngineCreationMode(
									ma.modelsParent.get(childURI),
									SimulationEngineCreationMode.
													COORDINATION_ENGINE)
					:	true
					) ;
				assert	invariant ;
			}
		}
		invariant &= allModelsUnique(ma.atomicModelDescriptors.keySet(),
									ma.coupledModelDescriptors.keySet()) ;
		assert	invariant ;
		invariant &= ma.atomicModelDescriptors.size() == 1 ||
							architectureIsATree(
										ma.atomicModelDescriptors.keySet(),
										ma.rootModelURI,
										ma.coupledModelDescriptors) ;
		assert	invariant ;
		invariant &= ma.modelsParent.size() + 1 ==
							(ma.atomicModelDescriptors.size() +
									ma.coupledModelDescriptors.size()) ;
		assert	invariant ;

		return invariant ;
	}

	// -------------------------------------------------------------------------
	// Correctness tests for the representation of the architecture.
	// -------------------------------------------------------------------------

	/**
	 * return true if all of the model URIs are uniquely defined in the
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	atomicModelsURIs != null
	 * pre	atomicModelsURIs.size() == 1 || coupledModels != null
	 * pre	{@code atomicModelsURIs.size() > 1 || coupledModels == null}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelsURIs	set of URIs of the atomic models in the architecture.
	 * @param coupledModelURIs	set of URIs of the coupled models in the architecture.
	 * @return					true if all of the models a unique in the architecture.
	 */
	protected static boolean	allModelsUnique(
		Set<String> atomicModelsURIs,
		Set<String> coupledModelURIs
		)
	{
		assert	atomicModelsURIs != null ;
		assert	coupledModelURIs != null ;
		assert	atomicModelsURIs.size() == 1 || !coupledModelURIs.isEmpty() ;
		assert	atomicModelsURIs.size() > 1 || coupledModelURIs.isEmpty() ;

		boolean allUnique = true ;
		if (atomicModelsURIs.size() > 1) {
			for (String uri : atomicModelsURIs) {
				allUnique &= !coupledModelURIs.contains(uri) ;
			}
			for (String uri : coupledModelURIs) {
				allUnique &= !atomicModelsURIs.contains(uri) ;
			}
		}
		return allUnique ;
	}

	/**
	 * return true if the model architecture is a tree i.e., every model,
	 * atomic or coupled, appears once and only once in the coupled models,
	 * except for the root coupled model. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelsURIs			set of URIs of the atomic models in the architecture.
	 * @param rootModelURI				URI of the root model in the architecture.
	 * @param coupledModelDescriptors	map of the URIs of the coupled models to the set of URIs of their composed models.
	 * @return							true if the architecture is a model tree.
	 */
	protected static boolean	architectureIsATree(
		Set<String> atomicModelsURIs,
		String rootModelURI,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		boolean ret = true ;	
		if (atomicModelsURIs.size() > 1) {
			int count ;
			for (String atomicModelURI : atomicModelsURIs) {
				count = 0 ;
				for (String coupledModelURI : coupledModelDescriptors.keySet()) {
					if (coupledModelDescriptors.get(coupledModelURI).
									submodelURIs.contains(atomicModelURI)) {
						count++ ;
					}
				}
				ret &= (count == 1) ;
			}
			for (String coupledModelURI : coupledModelDescriptors.keySet()) {
				count = 0 ;
				for (String curi : coupledModelDescriptors.keySet()) {
					if (coupledModelDescriptors.get(curi).
									submodelURIs.contains(coupledModelURI)) {
						count++ ;
					}
				}
				if (rootModelURI.equals(coupledModelURI)) {
					ret &= (count == 0) ;
				} else {
					ret &= (count == 1) ;
				}
			}
		}
		return ret ;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isMonoModel()
	 */
	@Override
	public boolean		isMonoModel()
	{
		assert	this.isComplete() ;

		return this.atomicModelDescriptors.size() == 1 &&
					this.coupledModelDescriptors.size() == 0 &&
						this.atomicModelDescriptors.keySet().
												contains(this.rootModelURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isAtomicModel(java.lang.String)
	 */
	@Override
	public boolean		isAtomicModel(String uri)
	{
		assert	uri != null ;

		return this.atomicModelDescriptors.keySet().contains(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isCoupledModel(java.lang.String)
	 */
	@Override
	public boolean		isCoupledModel(String uri)
	{
		assert	uri != null ;

		return this.coupledModelDescriptors.keySet().contains(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isModel(java.lang.String)
	 */
	@Override
	public boolean		isModel(String uri)
	{
		assert	uri != null ;

		return this.isAtomicModel(uri) || this.isCoupledModel(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getModelDescriptor(java.lang.String)
	 */
	@Override
	public ModelDescriptorI	getModelDescriptor(String uri)
	{
		if (this.isCoupledModel(uri)) {
			return this.coupledModelDescriptors.get(uri) ;
		} else {
			return this.atomicModelDescriptors.get(uri) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isSimulationTimeUnitSet()
	 */
	@Override
	public boolean		isSimulationTimeUnitSet()
	{
		return this.simulationTimeUnit != null ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getSimulationTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulationTimeUnit()
	{
		return this.simulationTimeUnit ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isRootModel(java.lang.String)
	 */
	@Override
	public boolean		isRootModel(String uri)
	{
		assert	uri != null ;

		return this.rootModelURI != null && this.rootModelURI.equals(uri) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isChildModelOf(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean		isChildModelOf(
		String childURI,
		String parentURI
		) throws Exception
	{
		assert	childURI != null && parentURI != null ;
		assert	!childURI.equals(parentURI) ;
		assert	this.isModel(childURI) && this.isCoupledModel(parentURI) ;

		return this.modelsParent.get(childURI).equals(parentURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isDescendant(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean		isDescendant(
		String descendantURI,
		String ancestorURI
		) throws Exception
	{
		assert	descendantURI != null && ancestorURI != null ;
		assert	!descendantURI.equals(ancestorURI) ;
		assert	this.isModel(descendantURI) &&
									this.isCoupledModel(ancestorURI) ;

		boolean ret = false ;
		String uri = descendantURI ;
		while (!ret && uri != null) {
			ret = this.isChildModelOf(uri, ancestorURI) ;
			if (!ret) {
				uri = this.getParent(uri) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getRootModel()
	 */
	@Override
	public String		getRootModel()
	{
		return this.rootModelURI ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getAllModels()
	 */
	@Override
	public Set<String>	getAllModels()
	{
		Set<String> ret = new HashSet<String>() ;
		for (String uri : this.atomicModelDescriptors.keySet()) {
			ret.add(uri) ;
		}
		for (String uri : this.coupledModelDescriptors.keySet()) {
			ret.add(uri) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getParent(java.lang.String)
	 */
	@Override
	public String		getParent(String modelURI)
	{
		assert	modelURI != null ;

		String ret = null ;
		if (this.modelsParent.containsKey(modelURI)) {
			ret = this.modelsParent.get(modelURI) ;
		}
		assert	!this.isComplete() ||
					(ret != null || modelURI.equals(this.getRootModel())) ;
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getChildrenModels(java.lang.String)
	 */
	@Override
	public Set<String>	getChildrenModels(String uri)
	{
		assert	uri != null ;
		assert	this.isCoupledModel(uri) ;

		Set<String> ret = new HashSet<String>() ;
		if (this.coupledModelDescriptors.containsKey(uri)) {
			for (String child :  this.coupledModelDescriptors.
												get(uri).submodelURIs) {
				ret.add(child) ;
			}
		}
		return ret ;
	}

	
	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getDescendantModels(java.lang.String)
	 */
	@Override
	public Set<String>	getDescendantModels(String uri)
	{
		assert	uri != null ;

		HashSet<String> ret = new HashSet<String>() ;
		if (this.coupledModelDescriptors.containsKey(uri)) {
			for (String childURI : this.getChildrenModels(uri)) {
				ret.add(childURI) ;
				ret.addAll(this.getDescendantModels(childURI)) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#setSimulationTimeUnit(java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			setSimulationTimeUnit(TimeUnit tu)
	{
		assert	!this.isSimulationTimeUnitSet()  ;
		assert	tu != null ;

		this.simulationTimeUnit = tu ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addAtomicModel(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor)
	 */
	@Override
	public void			addAtomicModel(
		String modelURI,
		AtomicModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !this.isModel(modelURI) ;
		assert	descriptor != null ;

		this.atomicModelDescriptors.put(modelURI, descriptor) ;

		assert	this.isAtomicModel(modelURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addAtomicModelAsRoot(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor)
	 */
	@Override
	public void			addAtomicModelAsRoot(
		String modelURI,
		AtomicModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !this.isModel(modelURI) ;
		assert	this.getAllModels().size() == 0 ;
		assert	descriptor != null ;

		this.addAtomicModel(modelURI, descriptor) ;
		this.rootModelURI = modelURI ;

		assert	this.isAtomicModel(modelURI) ;
		assert	this.isRootModel(modelURI) ;
		assert	this.isMonoModel() ;
		assert	this.isComplete() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addCoupledModel(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor)
	 */
	@Override
	public void			addCoupledModel(
		String modelURI,
		CoupledModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !this.isModel(modelURI) ;
		assert	descriptor != null ;

		this.coupledModelDescriptors.put(modelURI, descriptor) ;

		assert	this.isCoupledModel(modelURI) ;
		assert	!this.isMonoModel() ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addCoupledModelAsRoot(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor)
	 */
	@Override
	public void			addCoupledModelAsRoot(
		String modelURI,
		CoupledModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !this.isModel(modelURI) ;
		assert	this.getRootModel() == null ;
		assert	descriptor != null ;

		this.addCoupledModel(modelURI, descriptor) ;
		this.rootModelURI = modelURI ;

		assert	this.isCoupledModel(modelURI) ;
		assert	!this.isMonoModel() ;
		assert	this.isRootModel(modelURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isComplete()
	 */
	@Override
	public boolean		isComplete()
	{
		boolean ret = this.rootModelURI != null &&
										this.isSimulationTimeUnitSet() ;
		if (ret) {
			if (this.atomicModelDescriptors.containsKey(this.rootModelURI)) {
				// implies that the architecture is mono-model
				ret = true ;
			} else {
				// implies that the architecture is not mono-model hence the
				// root must appear in the coupled model descriptors and all
				// of it descendent models must be a model in the architecture.
				assert	this.coupledModelDescriptors.containsKey(rootModelURI) ;
				for (String d : this.getDescendantModels(rootModelURI)) {
					this.isModel(d) ;
				}
			}
		}

		if (ret) {
			assert	Architecture.architectureIsATree(
										this.atomicModelDescriptors.keySet(),
										this.rootModelURI,
										this.coupledModelDescriptors) ;
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isEngineCreationMode(java.lang.String, fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode)
	 */
	@Override
	public boolean		isEngineCreationMode(
		String modelURI,
		SimulationEngineCreationMode engineCreationMode
		)
	{
		return this.getEngineCreationMode(modelURI) == engineCreationMode ;
	}

	protected SimulationEngineCreationMode	 getEngineCreationMode(
		String modelURI
		)
	{
		if (this.atomicModelDescriptors.containsKey(modelURI)) {
			return this.atomicModelDescriptors.get(modelURI).
													engineCreationMode ;
		} else {
			return this.coupledModelDescriptors.get(modelURI).
													engineCreationMode ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#topologicalSort()
	 */
	@Override
	public List<String>	topologicalSort()
	{
		assert	this.isComplete() ;
		assert	Architecture.architectureIsATree(
									this.atomicModelDescriptors.keySet(),
									this.rootModelURI,
									this.coupledModelDescriptors) ;

		List<String> ret = this.depthFirstTraversal(this.rootModelURI) ;

		assert	ret.size() == this.atomicModelDescriptors.size() +
								this.coupledModelDescriptors.size() ;
		assert	ret.containsAll(this.atomicModelDescriptors.keySet()) ;
		assert	ret.containsAll(this.coupledModelDescriptors.keySet()) ;

		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#topologicalSort(java.lang.String)
	 */
	@Override
	public List<String>	topologicalSort(String modelURI)
	{
		assert	modelURI != null && this.isModel(modelURI) ;

		assert	this.isComplete() ;
		assert	Architecture.architectureIsATree(
									this.atomicModelDescriptors.keySet(),
									this.rootModelURI,
									this.coupledModelDescriptors) ;

		return this.depthFirstTraversal(modelURI) ;
	}

	/**
	 * return a depth-first traversal of the subtree as a list of model URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and this.isModel(modelURI)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of a model root of a subtree in the architecture.
	 * @return			a depth-first traversal of the subtree as a list of model URIs.
	 */
	protected List<String>	depthFirstTraversal(String modelURI)
	{
		assert	modelURI != null && this.isModel(modelURI) ;

		ArrayList<String> ret = null ;
		if (this.atomicModelDescriptors.containsKey(modelURI)) {
			ret = new ArrayList<String>(1) ;
			ret.add(modelURI) ;
		} else {
			ret = new ArrayList<String>() ;
			Stack<String> traversed = new Stack<String>() ;
			traversed.push(modelURI) ;
			Stack<List<String>> toBeProcessed = new Stack<List<String>>() ;
			ArrayList<String> hs = new ArrayList<String>() ;
			Set<String> children =
				this.coupledModelDescriptors.get(modelURI).submodelURIs ;
			hs = new ArrayList<String>(children.size()) ;
			hs.addAll(children) ;
			toBeProcessed.push(hs) ;
			while (!toBeProcessed.empty()) {
				List<String> top = toBeProcessed.peek() ;
				if (top.isEmpty()) {
					toBeProcessed.pop() ;
					ret.add(traversed.pop()) ;
				} else {
					int last = top.size() - 1 ;
					String uri = top.remove(last) ;
					if (this.atomicModelDescriptors.containsKey(uri)) {
						ret.add(uri) ;
					} else {
						traversed.push(uri) ;
						children =
							this.coupledModelDescriptors.get(uri).submodelURIs ;
						hs = new ArrayList<String>(children.size()) ;
						hs.addAll(children) ;
						toBeProcessed.push(hs) ;
					}
				}
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#extractModelsWithEngine(java.util.List)
	 */
	@Override
	public List<String>	extractModelsWithEngine(List<String> models)
	{
		assert	models != null ;
		for (String m : models) {
			assert	this.isModel(m) ;
		}

		ArrayList<String> ret = new ArrayList<String>(models.size()) ;
		for (int i = 0 ; i < models.size() ; i++) {
			if (!this.isEngineCreationMode(models.get(i), SimulationEngineCreationMode.NO_ENGINE)) {
				ret.add(models.get(i)) ;
			}
		}
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#constructSimulator()
	 */
	@Override
	public SimulationEngine	constructSimulator() throws Exception
	{
		if (DEBUG) {
			System.out.println("ModelArchitectureI#constructSimulator(): "
													+ this.rootModelURI) ;
		}

		assert	this.isComplete() ;

		Iterator<String> iter = this.topologicalSort().iterator() ;
		Map<String,ModelDescriptionI> createdModels =
								new HashMap<String,ModelDescriptionI>() ;
		while (iter.hasNext()) {
			String uri = iter.next() ;
			ModelDescriptionI m = null ;
			if (this.atomicModelDescriptors.containsKey(uri)) {
				m = this.atomicModelDescriptors.get(uri).connectAtomicModel() ;
			} else {
				assert	this.coupledModelDescriptors.containsKey(uri) ;
				ModelDescriptionI[] models =
					new ModelDescriptionI[
					        this.coupledModelDescriptors.get(uri).
					        						submodelURIs.size()] ;
				int i = 0 ;
				for (String childURI :
						this.coupledModelDescriptors.get(uri).submodelURIs) {
					models[i++] = createdModels.get(childURI) ;
				}
				m = this.coupledModelDescriptors.get(uri).
												createCoupledModel(models) ;
			}
			createdModels.put(uri, m) ;
		}
		return (SimulationEngine)createdModels.get(this.rootModelURI) ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#constructSimulator(java.lang.String)
	 */
	@Override
	public SimulationEngine	constructSimulator(String modelURI)
	throws Exception
	{
		if (DEBUG) {
			System.out.println(
				"ModelArchitectureI#constructSimulator(String) 1: "
															+ modelURI) ;
		}

		assert	modelURI != null && this.isModel(modelURI) ;
		assert	!this.isEngineCreationMode(
						modelURI,
						SimulationEngineCreationMode.NO_ENGINE) ;

		Iterator<String> iter = this.topologicalSort(modelURI).iterator() ;
		Map<String,ModelDescriptionI> createdModels =
								new HashMap<String,ModelDescriptionI>() ;
		while (iter.hasNext()) {
			String uri = iter.next() ;
			ModelDescriptionI m = null ;
			if (this.atomicModelDescriptors.containsKey(uri)) {
				m = this.atomicModelDescriptors.get(uri).connectAtomicModel() ;
			} else {
				assert	this.coupledModelDescriptors.containsKey(uri) ;
				ModelDescriptionI[] models =
					new ModelDescriptionI[
					        this.coupledModelDescriptors.get(uri).
					        						submodelURIs.size()] ;
				int i = 0 ;
				for (String childURI :
						this.coupledModelDescriptors.get(uri).submodelURIs) {
					models[i++] = createdModels.get(childURI) ;
				}
				m = this.coupledModelDescriptors.get(uri).
												createCoupledModel(models) ;
			}
			if (DEBUG) {
				System.out.println(
					"ModelArchitectureI#constructSimulator(String): 2\n" +
													m.modelAsString("")) ;
			}
			createdModels.put(uri, m) ;
		}
		return (SimulationEngine)createdModels.get(modelURI) ;
	}
}
// -----------------------------------------------------------------------------

