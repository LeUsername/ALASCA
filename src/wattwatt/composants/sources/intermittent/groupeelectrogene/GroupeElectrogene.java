package wattwatt.composants.sources.intermittent.groupeelectrogene;

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
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.Duree;
import simulation.deployment.WattWattMain;
import simulation.equipements.groupeelectrogene.components.GroupeElectrogeneSimulatorPlugin;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneCoupledModel;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneModel;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneUserModel;
import simulation.equipements.groupeelectrogene.tools.GroupeElectrogeneUserBehaviour;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.interfaces.sources.intermittent.IGroupeElectrogene;
import wattwatt.ports.sources.intermittent.groupeelectrogene.GroupeElectrogeneInPort;
import wattwatt.tools.GroupeElectrogene.GroupreElectrogeneReglage;

@OfferedInterfaces(offered = IGroupeElectrogene.class)
@RequiredInterfaces(required = IControleur.class)
public class GroupeElectrogene  extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected GroupeElectrogeneInPort groupein;

	protected boolean isOn;
	protected int production;
	protected int fuelQuantity;
	
	protected boolean isOnSim;
	protected double productionSim;
	protected double fuelQuantitySim;
	protected boolean isFull;
	protected boolean isEmpty;
	
	protected GroupeElectrogeneSimulatorPlugin asp;
	

	protected GroupeElectrogene(String uri, String groupeIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.groupein = new GroupeElectrogeneInPort(groupeIn, this);
		this.groupein.publishPort();
		
		this.isOnSim = (Boolean)asp.getModelStateValue(GroupeElectrogeneModel.URI, "isOn");
		this.isFull = (Boolean)asp.getModelStateValue(GroupeElectrogeneModel.URI, "isFull");
		this.isEmpty = (Boolean)asp.getModelStateValue(GroupeElectrogeneModel.URI, "isEmpty");
		
		this.productionSim = (Double)asp.getModelStateValue(GroupeElectrogeneModel.URI, "production");
		this.fuelQuantitySim = (Double)asp.getModelStateValue(GroupeElectrogeneModel.URI, "capacity");
		
		this.tracer.setRelativePosition(2, 1);
	}
	
	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new GroupeElectrogeneSimulatorPlugin();
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

	public int getEnergie() throws Exception {
		return this.production;
	}

	public boolean fuelIsEmpty() throws Exception {
		return this.fuelQuantity == 0;
	}

	public boolean fuelIsFull() throws Exception {
		return this.fuelQuantity == GroupreElectrogeneReglage.FUEL_CAPACITY;
	}

	public int fuelQuantity() throws Exception {
		return this.fuelQuantity;
	}

	public void on() throws Exception {
		this.isOn = true;
	}

	public void off() throws Exception {
		this.isOn = false;
	}

	public void addFuel(int quantity) throws Exception {
		if (this.fuelQuantity + quantity >= GroupreElectrogeneReglage.FUEL_CAPACITY) {
			this.fuelQuantity = GroupreElectrogeneReglage.FUEL_CAPACITY;
		} else {
			this.fuelQuantity += GroupreElectrogeneReglage.FUEL_CAPACITY;
		}
	}

	public boolean isOn() {
		return this.isOn;
	}

	public void behave() throws Exception {
		if (this.isOn && !this.fuelIsEmpty()) {
			this.logMessage("Groupe is producing");
			this.production += GroupreElectrogeneReglage.PROD_THR;
			if (this.fuelQuantity - GroupreElectrogeneReglage.PROD_THR <= 0) {
				this.fuelQuantity = 0;
			} else {
				this.fuelQuantity -= GroupreElectrogeneReglage.PROD_THR;
			}
		} else {
			if (this.fuelIsEmpty()) {
				this.off();
				this.logMessage("No more fuel");
			}
		}
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.fuelQuantity = GroupreElectrogeneReglage.FUEL_CAPACITY;

		this.logMessage("Groupe Electro starting");
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
		simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTBU,
				GroupeElectrogeneUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTW,
				GroupeElectrogeneUserBehaviour.MEAN_TIME_WORKING);
		simParams.put(GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.MTR,
				GroupeElectrogeneUserBehaviour.MEAN_TIME_AT_REFILL);
		simParams.put(
				GroupeElectrogeneUserModel.URI + ":" + GroupeElectrogeneUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneUserModel", "Time (min)", "Start / Stop / Refill",
						2*WattWattMain.getPlotterWidth(),
						0, WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		simParams.put(
				GroupeElectrogeneModel.URI + ":" + GroupeElectrogeneModel.PRODUCTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Production (W)", 2*WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				GroupeElectrogeneModel.URI + ":" + GroupeElectrogeneModel.QUANTITY + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Fuel Quantity (L)", 2*WattWattMain.getPlotterWidth(),
						2*WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
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
						((GroupeElectrogene) this.getTaskOwner()).isOnSim = ((boolean) asp.getModelStateValue(GroupeElectrogeneModel.URI, "isOn"));
						((GroupeElectrogene) this.getTaskOwner()).isEmpty = ((boolean) asp.getModelStateValue(GroupeElectrogeneModel.URI, "isEmpty"));
						((GroupeElectrogene) this.getTaskOwner()).isFull = ((boolean) asp.getModelStateValue(GroupeElectrogeneModel.URI, "isFull"));
						((GroupeElectrogene) this.getTaskOwner()).fuelQuantitySim = ((double) asp.getModelStateValue(GroupeElectrogeneModel.URI, "capacity"));
						((GroupeElectrogene) this.getTaskOwner()).productionSim = ((double) asp.getModelStateValue(GroupeElectrogeneModel.URI, "production"));
						
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
						((GroupeElectrogene) this.getTaskOwner()).behave();
						;
						Thread.sleep(GroupreElectrogeneReglage.REGUL_RATE);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);*/
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Groupe Electro shutdown");
		try {
			this.groupein.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		return null;
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return GroupeElectrogeneCoupledModel.build();
	}

}
