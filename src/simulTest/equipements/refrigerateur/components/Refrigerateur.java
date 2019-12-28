package simulTest.equipements.refrigerateur.components;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulTest.equipements.sechecheveux.models.SecheCheveuxCoupledModel;
import simulTest.equipements.sechecheveux.models.SecheCheveuxModel;

public class Refrigerateur extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected RefrigerateurSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected Refrigerateur() throws Exception {
		// 2 threads to be able to execute tasks and requests while executing
		// the DEVS simulation.
		super(2, 0);
		this.initialise();

	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new RefrigerateurSimulatorPlugin();
		// Set the URI of the plug-in, using the URI of its associated
		// simulation model.
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		// Set the simulation architecture.
		this.asp.setSimulationArchitecture(localArchitecture);
		// Install the plug-in on the component, starting its own life-cycle.
		this.installPlugin(this.asp);

		// Toggle logging on to get a log on the screen.
		this.toggleLogging();
	}
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return SecheCheveuxCoupledModel.build();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put("componentRef", this);
		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					asp.doStandAloneSimulation(0.0, 500.0);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		Thread.sleep(10L);
		// During the simulation, the following lines provide an example how
		// to use the simulation model access facility by the component.
		for (int i = 0; i < 100; i++) {
			this.logMessage("SecheCheveux " + this.asp.getModelStateValue(SecheCheveuxModel.URI, "mode") + " "
					+ this.asp.getModelStateValue(SecheCheveuxModel.URI, "isOn") + " "
					+ this.asp.getModelStateValue(SecheCheveuxModel.URI, "intensity"));
			Thread.sleep(5L);
		}
//		super.execute();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
//		return HairDryerModel.State.LOW;
		if (name.equals("mode")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "mode");
		} else if (name.equals("isOn")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "isOn");
		} else {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "intensity");
		}
	}
}