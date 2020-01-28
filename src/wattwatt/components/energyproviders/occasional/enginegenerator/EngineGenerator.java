package wattwatt.components.energyproviders.occasional.enginegenerator;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.plugins.EngineGeneratorSimulatorPlugin;
import simulation.tools.TimeScale;
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;
import wattwatt.ports.energyproviders.occasional.enginegenerator.EngineGeneratorInPort;
import wattwatt.tools.URIS;
import wattwatt.tools.EngineGenerator.EngineGeneratorSetting;

//-----------------------------------------------------------------------------
/**
* The class <code>EngineGenerator</code>
*
* <p>
* <strong>Description</strong>
* </p>
* 
* This class implements the engine generator component. The engine generator 
* requires the controller interface because he have to be
* connected to the controller to receive order from him.
* 
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
//The next annotation requires that the referenced interface is added to
//the required interfaces of the component.
@OfferedInterfaces(offered = IEngineGenerator.class)
@RequiredInterfaces(required = IController.class)
public class EngineGenerator  extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/** The inbound port of the engine generator */	
	protected EngineGeneratorInPort groupein;

	/** The state of the fridge */
	protected boolean isOn;
	/** The energy production of the engine generator */
	protected int production;
	/** The fuel quantity of the engine generator */
	protected int fuelQuantity;
	
	/** The state of the fridge on simulation */
	protected boolean isOnSim;
	/** The energy production of the engine generator on simulation*/
	protected double productionSim;
	/** The fuel quantity of the engine generator on simulation */
	protected double fuelQuantitySim;
	
	protected boolean isFull;
	protected boolean isEmpty;
	
	/** the simulation plug-in holding the simulation models. */
	protected EngineGeneratorSimulatorPlugin asp;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a engine generator.
	 * 
	 *
	 * @param uri        URI of the component.
	 * @param groupeIn 	inbound port URI of the engine generator.
	 * @throws Exception <i>todo.</i>
	 */
	protected EngineGenerator(String uri, String groupeIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.groupein = new EngineGeneratorInPort(groupeIn, this);
		this.groupein.publishPort();
		
		this.isOnSim = (Boolean)asp.getModelStateValue(EngineGeneratorModel.URI, "isOn");
		this.isFull = (Boolean)asp.getModelStateValue(EngineGeneratorModel.URI, "isFull");
		this.isEmpty = (Boolean)asp.getModelStateValue(EngineGeneratorModel.URI, "isEmpty");
		
		this.productionSim = (Double)asp.getModelStateValue(EngineGeneratorModel.URI, "production");
		this.fuelQuantitySim = (Double)asp.getModelStateValue(EngineGeneratorModel.URI, "capacity");
		
		this.tracer.setRelativePosition(2, 1);
	}
	
	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new EngineGeneratorSimulatorPlugin();
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

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.fuelQuantity = EngineGeneratorSetting.FUEL_CAPACITY;

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
		simParams.put(URIS.ENGINE_GENERATOR_URI, this);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.INITIAL_DELAY,
				EngineGeneratorUserBehaviour.INITIAL_DELAY);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.INTERDAY_DELAY,
				EngineGeneratorUserBehaviour.INTERDAY_DELAY);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_BETWEEN_USAGES,
				EngineGeneratorUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_USAGE,
				EngineGeneratorUserBehaviour.MEAN_TIME_USAGE);
		simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MEAN_TIME_REFILL,
				EngineGeneratorUserBehaviour.MEAN_TIME_REFILL);
		simParams.put(
				EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneUserModel", "Time (min)", "Start / Stop / Refill",
						2*WattWattMain.getPlotterWidth(),
						0, WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Production (W)", 2*WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY_SERIES + ":"
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
					asp.doStandAloneSimulation(0.0, TimeScale.WEEK);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
						((EngineGenerator) this.getTaskOwner()).isOnSim = ((boolean) asp.getModelStateValue(EngineGeneratorModel.URI, "isOn"));
						((EngineGenerator) this.getTaskOwner()).isEmpty = ((boolean) asp.getModelStateValue(EngineGeneratorModel.URI, "isEmpty"));
						((EngineGenerator) this.getTaskOwner()).isFull = ((boolean) asp.getModelStateValue(EngineGeneratorModel.URI, "isFull"));
						((EngineGenerator) this.getTaskOwner()).fuelQuantitySim = ((double) asp.getModelStateValue(EngineGeneratorModel.URI, "capacity"));
						((EngineGenerator) this.getTaskOwner()).productionSim = ((double) asp.getModelStateValue(EngineGeneratorModel.URI, "production"));
						
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
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
	public void setEmbeddingComponentStateValue(String name, Object value) throws Exception {
		EmbeddingComponentAccessI.super.setEmbeddingComponentStateValue(name, value);
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return EngineGeneratorCoupledModel.build();
	}
	
	public int getEnergie() throws Exception {
		return this.production;
	}

	public boolean fuelIsEmpty() throws Exception {
		return this.fuelQuantity == 0;
	}

	public boolean fuelIsFull() throws Exception {
		return this.fuelQuantity == EngineGeneratorSetting.FUEL_CAPACITY;
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
		if (this.fuelQuantity + quantity >= EngineGeneratorSetting.FUEL_CAPACITY) {
			this.fuelQuantity = EngineGeneratorSetting.FUEL_CAPACITY;
		} else {
			this.fuelQuantity += EngineGeneratorSetting.FUEL_CAPACITY;
		}
	}

	public boolean isOn() {
		return this.isOn;
	}

	public void behave() throws Exception {
		if (this.isOn && !this.fuelIsEmpty()) {
			this.logMessage("Groupe is producing");
			this.production += EngineGeneratorSetting.PROD_THR;
			if (this.fuelQuantity - EngineGeneratorSetting.PROD_THR <= 0) {
				this.fuelQuantity = 0;
			} else {
				this.fuelQuantity -= EngineGeneratorSetting.PROD_THR;
			}
		} else {
			if (this.fuelIsEmpty()) {
				this.off();
				this.logMessage("No more fuel");
			}
		}
	}

}
