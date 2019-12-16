package simulTest.compteur.components;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.cyphy.examples.sg.equipments.hairdryer.models.HairDryerModel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulTest.compteur.models.CompteurCoupledModel;

public class Compteur extends		AbstractCyPhyComponent
implements	EmbeddingComponentStateAccessI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected CompteurSimulatorPlugin		asp ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			Compteur() throws Exception
	{
		// 2 threads to be able to execute tasks and requests while executing
		// the DEVS simulation.
		super(2, 0) ;
		this.initialise() ;

	}

	protected Compteur(String reflectionInboundPortURI) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		this.initialise() ;
	}

	protected void		initialise() throws Exception
	{
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null) ;
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new CompteurSimulatorPlugin() ;
		// Set the URI of the plug-in, using the URI of its associated
		// simulation model.
		this.asp.setPluginURI(localArchitecture.getRootModelURI()) ;
		// Set the simulation architecture.
		this.asp.setSimulationArchitecture(localArchitecture) ;
		// Install the plug-in on the component, starting its own life-cycle.
		this.installPlugin(this.asp) ;

		// Toggle logging on to get a log on the screen.
		this.toggleLogging() ;
	}
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture	createLocalArchitecture(String architectureURI)
	throws Exception
	{
		return CompteurCoupledModel.build() ;
	}

	/**
	 * @see fr.sorbonne_	u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String,Object> simParams = new HashMap<String,Object>() ;
		simParams.put("componentRef", this) ;
		this.asp.setSimulationRunParameters(simParams) ;
		// Start the simulation.
		this.runTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							asp.doStandAloneSimulation(0.0, 500.0) ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}) ;
		Thread.sleep(10L) ;
		// During the simulation, the following lines provide an example how
		// to use the simulation model access facility by the component.
		for (int i = 0 ; i < 100 ; i++) {
			this.logMessage("HairDryer " +
				this.asp.getModelStateValue(HairDryerModel.URI, "consommation")) ;
			Thread.sleep(5L) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object		getEmbeddingComponentStateValue(String name)
	throws Exception
	{
		// As there is only one value, don't care about the name.
		return 5; // just an example...
		// With this facility, the state of the hair dryer can be set by the
		// component part (e.g., the controller) rather than by the user model
		// included in the hair dryer model in a development process going from
		// a pure MIL simulation to a SIL simulation.
	}
}
