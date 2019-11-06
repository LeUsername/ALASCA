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

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelArchitectureI</code> defines the signatures
 * of methods to be handled by a DEVS simulation architecture implementation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete !
 * 
 * <pre>
 * invariant		this.isCoupledModel(uri) implies this.isModel(uri)
 * invariant		this.isAtomicModel(uri) implies this.isModel(uri)
 * invariant		this.isComplete() implies
 * 				    		this.isMonoModel() equiv
 *				        		this.isAtomicModel(this.getRootModel())
 * invariant		this.isComplete() implies
 * 				    !this.isMonoModel() equiv
 *				        		this.isCoupledModel(this.getRootModel())
 * invariant		this.isComplete() implies this.isSimulationTimeUnitSet()
 * </pre>
 * 
 * <p>Created on : 2017-10-31</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ArchitectureI
extends		Serializable
{
	/**
	 * return true if the architecture contains only one (atomic) model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isComplete()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the architecture contains only one (atomic) model.
	 */
	public boolean		isMonoModel() ;

	/**
	 * return true if the given URI is an atomic model URI in the
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI to be tested.
	 * @return				true if the given URI is an atomic model URI in the architecture.
	 * @throws Exception	if <code>uri</code> does not exist in the architecture.
	 */
	public boolean		isAtomicModel(String uri) throws Exception ;

	/**
	 * return true if the given URI is a coupled model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI to be tested.
	 * @return				true if the given URI is a coupled model URI in the architecture.
	 * @throws Exception	if <code>uri</code> does not exist in the architecture.
	 */
	public boolean		isCoupledModel(String uri) throws Exception ;

	/**
	 * return true if the given URI is a model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI to be tested.
	 * @return		true if the given URI is a model URI in the architecture.
	 */
	public boolean		isModel(String uri) ;

	/**
	 * return the model descriptor associated to the given model URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of a model in the architecture.
	 * @return		the model descriptor of the given model.
	 */
	public ModelDescriptorI	getModelDescriptor(String uri) ;

	/**
	 * return true if the simulation time unit has been set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulation time unit has been set.
	 */
	public boolean		isSimulationTimeUnitSet() ;

	/**
	 * return	the simulation time unit associated to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isSimulationTimeUnitSet()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation time unit associated to the architecture.
	 */
	public TimeUnit		getSimulationTimeUnit() ;

	/**
	 * return true if the given URI is the root model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI to be tested.
	 * @return				true if the given URI is the root model URI in the architecture.
	 */
	public boolean		isRootModel(String uri) ;

	/**
	 * return true if the model <code>childURI</code> is a child of the
	 * model <code>parentURI</code> in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	childURI != null and parentURI != null
	 * pre	!childURI.equals(parentURI)
	 * pre	this.isModel(childURI) and this.isCoupledModel(parentURI)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param childURI		URI of the possible child model.
	 * @param parentURI		URI of the possible parent model.
	 * @return				true if the model <code>childURI</code> is a child of the model <code>parentURI</code>
	 * @throws Exception	if one of the URI does not exist in the architecture.
	 */
	public boolean		isChildModelOf(
		String childURI,
		String parentURI
		) throws Exception ;

	/**
	 * return true if the model <code>descendentURI</code> is a descendent of
	 * the model <code>ancestorURI</code> in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	descendantURI != null and ancestorURI != null
	 * pre	!descendantURI.equals(ancestorURI)
	 * pre	{@code this.isModel(descendantURI) && this.isCoupledModel(ancestorURI)}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param descendantURI	URI of the possible descendent model.
	 * @param ancestorURI	URI of the possible ancestor model.
	 * @return				true if the model <code>descendentURI</code> is a descendent of the model <code>ancestorURI</code> in the architecture.
	 * @throws Exception	if one of the URI does not exist in the architecture.
	 */
	public boolean		isDescendant(
		String descendantURI,
		String ancestorURI
		) throws Exception ;

	/**
	 * return the root model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null implies this.isRootModel(ret)
	 * </pre>
	 *
	 * @return	the root model URI in the architecture.
	 */
	public String		getRootModel();

	/**
	 * get a set of all of the current model URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ret != null
	 * </pre>
	 *
	 * @return	a set of all of the current model URIs.
	 */
	public Set<String>	getAllModels() ;

	/**
	 * return the URI of the parent model of <code>modelURI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null
	 * post	return != null implies
	 *            !this.isComplete() or modelURI.equals(this.getRootModel())
	 * </pre>
	 *
	 * @param modelURI	URI of the model which parent is looked for.
	 * @return			the URI of the parent model of <code>modelURI</code>.
	 */
	public String		getParent(String modelURI) ;

	/**
	 * return the URIs of the direct children models of the given coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * pre	this.isCoupledModel(uri)
	 * post	ret != null
	 * post	{@code this.isComplete() implies ret.size() > 0}
	 * </pre>
	 *
	 * @param uri			the URI of a coupled model.
	 * @return				the set of URIs of the direct children models of the given model.
	 */
	public Set<String>	getChildrenModels(String uri) ;

	/**
	 * return the set of all URIs currently known as descendant models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null
	 * post	ret != null
	 * post	{@code this.isComplete() implies ret.size() > 0}
	 * </pre>
	 *
	 * @param uri	uri of a coupled model.
	 * @return		the set of all URIs currently known as descendant models.
	 */
	public Set<String>	getDescendantModels(String uri) ;

	/**
	 * set the simulation time unit of the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.isSimulationTimeUnitSet()
	 * pre	tu != null
	 * post	this.isSimulationTimeUnitSet()
	 * </pre>
	 *
	 * @param tu	the new simulation time unit.
	 */
	public void			setSimulationTimeUnit(TimeUnit tu) ;

	/**
	 * add an atomic model to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and !this.isModel(modelURI)
	 * pre	descriptor != null
	 * post	this.isAtomicModel(modelURI)
	 * </pre>
	 *
	 * @param modelURI		URI of the new model.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addAtomicModel(
		String modelURI,
		AtomicModelDescriptor descriptor
		) ;

	/**
	 * add an atomic model to the architecture as its root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and !this.isModel(modelURI)
	 * pre	this.getAllModels().size() == 0
	 * pre	descriptor != null
	 * post	this.isAtomicModel(modelURI)
	 * post	this.isRootModel(modelURI)
	 * post	this.isMonoModel()
	 * post	this.isComplete()
	 * </pre>
	 *
	 * @param modelURI		URI of the new model.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addAtomicModelAsRoot(
		String modelURI,
		AtomicModelDescriptor descriptor
		) ;

	/**
	 * add a coupled model to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI ! = null and !this.isModel(modelURI)
	 * pre	descriptor != null
	 * post	this.isCoupledModel(modelURI)
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be added.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addCoupledModel(
		String modelURI,
		CoupledModelDescriptor descriptor
		) ;

	/**
	 * add a coupled model to the architecture as its root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI ! = null and !this.isModel(modelURI)
	 * pre	this.getRootModel() == null
	 * pre	descriptor != null
	 * post	this.isCoupledModel(modelURI)
	 * post	!this.isMonoModel()
	 * post	this.isRootModel(modelURI)
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be added.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addCoupledModelAsRoot(
		String modelURI,
		CoupledModelDescriptor descriptor
		) ;

	/**
	 * return true if the model architecture can be interpreted as complete
	 * i.e., all models mentioned as submodels, sources or sinks of events
	 * have descriptors defined in this model architecture.
	 * 
	 * <p>
	 * Detecting that an architecture is complete is complex. A possibility
	 * would be to have a specific method to be called to inform that the
	 * architecture is complete, but in practice, if the architecture is
	 * constructed by gathering model descriptions from several parallel or
	 * distributed processes, it would be extremely difficult (and probably
	 * repetitive in terms of coding) to check that all models have been added
	 * to the architecture. Hence, the basic idea of the test is to check that
	 * the root model has been defined and that no models mention the URI of
	 * another model that has not been added yet.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if the model architecture can be interpreted as complete.
	 */
	public boolean		isComplete() ;

	/**
	 * return true if the simulation engine creation mode of the model is the
	 * given value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and this.isModel(modelURI)
	 * pre	engineCreationMode != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be tested.
	 * @param engineCreationMode	the simulation engine creation mode sought.
	 * @return						true if the simulation engine creation mode of the model is the given value.
	 */
	public boolean	 	isEngineCreationMode(
		String modelURI,
		SimulationEngineCreationMode engineCreationMode
		) ;

	/**
	 * return a topological sort of the models in the architecture
	 * as a list of URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isComplete()
	 * post	ret != null
	 * </pre>
	 *
	 * @return	a topological sort of the models in the architecture as a list of URIs.
	 */
	public List<String>	topologicalSort() ;


	/**
	 * return a topological sort of the models subtree which root is the
	 * given model as a list of URIs; the result is a depth-first traversal
	 * of the architecture seen as a tree.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and this.isModel(modelURI)
	 * pre	this.isComplete()
	 * post	ret != null
	 * </pre>
	 *
	 * @param modelURI	URI of the model at the root of the subtree to be sorted.
	 * @return			a topological sort of the models subtree which root is the given model as a list of URIs.
	 */
	public List<String>	topologicalSort(String modelURI) ;

	/**
	 * return a list of the models which have an simulation engine in the same
	 * order as they appear in <code>models</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	models != null
	 * pre	for (String m : models) { this.isModel(m) }
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param models	a list of model URIs from this architecture.
	 * @return			a list of the models which have an simulation engine.
	 */
	public List<String>	extractModelsWithEngine(List<String> models) ;

	/**
	 * construct a simulator from a complete architecture returning the root
	 * simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.isComplete()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return				the simulation engine of the root model in a simulation architecture for this model architecture.
	 * @throws Exception	<i>TODO</i>.
	 */
	public SimulationEngine	constructSimulator() throws Exception ;

	/**
	 * construct a simulator from a complete subtree of the architecture
	 * returning the simulation engine of the model at the root of this
	 * subtree.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	modelURI != null and this.isModel(modelURI)
	 * pre	this.isComplete()
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the root model.
	 * @return				the simulation engine of the root model in a simulation architecture for this model architecture.
	 * @throws Exception	<i>TODO</i>.
	 */
	public SimulationEngine	constructSimulator(String modelURI)
	throws Exception ;
}
// -----------------------------------------------------------------------------
