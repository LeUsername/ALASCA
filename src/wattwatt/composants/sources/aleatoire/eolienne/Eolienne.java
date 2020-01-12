package wattwatt.composants.sources.aleatoire.eolienne;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.Duree;
import simulation.equipements.eolienne.components.EolienneSimulatorPlugin;
import simulation.equipements.eolienne.models.EolienneCoupledModel;
import simulation.equipements.eolienne.models.EolienneModel;
import simulation.equipements.lavelinge.models.LaveLingeModel;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.interfaces.sources.aleatoire.eolienne.IEolienne;
import wattwatt.ports.sources.aleatoire.eolienne.EolienneInPort;
import wattwatt.tools.eolienne.EolienneReglage;

@OfferedInterfaces(offered = IEolienne.class)
@RequiredInterfaces(required = IControleur.class)
public class Eolienne extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected EolienneInPort eoin;

	protected boolean isOn;
	protected int production;
	
	protected boolean isOnSim;
	protected double productionSim;
	
	protected EolienneSimulatorPlugin asp;
	

	protected Eolienne(String uri, String eoIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();

		this.eoin = new EolienneInPort(eoIn, this);
		this.eoin.publishPort();
		
		this.isOnSim = (boolean)this.asp.getModelStateValue(EolienneModel.URI, "isOn");
		this.productionSim = (double)this.asp.getModelStateValue(EolienneModel.URI, "production");

		this.tracer.setRelativePosition(2, 0);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new EolienneSimulatorPlugin();
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
	
	public void behave() {
		// production should depend on the power of the wind
		if (this.isOn) {
			this.production += EolienneReglage.PROD_THR;
		} else {

			if (this.production - EolienneReglage.PROD_THR <= 0) {
				this.production = 0;
			} else {

				this.production -= EolienneReglage.PROD_THR;
			}

		}
	}

	public int getEnergie() {
		return this.production;
	}

	public void On() {
		this.isOn = true;
	}

	public void Off() {
		this.isOn = false;
	}

	public boolean isOn() {

		return this.isOn;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Eolienne starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put("componentRef", this);
		
		this.asp.setSimulationRunParameters(simParams);
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					asp.doStandAloneSimulation(0.0, Duree.DUREE_SEMAINE);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Eolienne) this.getTaskOwner()).isOnSim = 
								(((boolean) asp.getModelStateValue(LaveLingeModel.URI, "isOn")));
						((Eolienne) this.getTaskOwner()).productionSim = 
								(((double) asp.getModelStateValue(LaveLingeModel.URI, "production")));
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		/*
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Eolienne) this.getTaskOwner()).behave();
						((Eolienne) this.getTaskOwner())
								.logMessage("Production : [" + ((Eolienne) this.getTaskOwner()).production + "]");
						Thread.sleep(EolienneReglage.REGUL_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);*/
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Eolienne shutdown");
		try {
			this.eoin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return EolienneCoupledModel.build();
	}

}
