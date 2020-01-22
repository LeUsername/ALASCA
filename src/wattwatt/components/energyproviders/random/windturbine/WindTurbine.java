package wattwatt.components.energyproviders.random.windturbine;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import simulation.deployment.WattWattMain;
import simulation.models.washingmachine.WashingMachineModel;
import simulation.models.windturbine.WindTurbineCoupledModel;
import simulation.models.windturbine.WindTurbineModel;
import simulation.models.windturbine.WindTurbineSensorModel;
import simulation.plugins.WindTurbineSimulatorPlugin;
import simulation.tools.TimeScale;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.energyproviders.random.windturbine.IWindTurbine;
import wattwatt.ports.energyproviders.random.windturbine.WindTurbineInPort;
import wattwatt.tools.URIS;
import wattwatt.tools.windturbine.WindTurbineSetting;

@OfferedInterfaces(offered = IWindTurbine.class)
@RequiredInterfaces(required = IController.class)
public class WindTurbine extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected WindTurbineInPort eoin;

	protected boolean isOn;
	protected int production;
	
	protected boolean isOnSim;
	protected double productionSim;
	
	protected WindTurbineSimulatorPlugin asp;
	

	protected WindTurbine(String uri, String eoIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();

		this.eoin = new WindTurbineInPort(eoIn, this);
		this.eoin.publishPort();
		
		this.isOnSim = (boolean)this.asp.getModelStateValue(WindTurbineModel.URI, "isOn");
		this.productionSim = (double)this.asp.getModelStateValue(WindTurbineModel.URI, "production");

		this.tracer.setRelativePosition(2, 0);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new WindTurbineSimulatorPlugin();
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
			this.production += WindTurbineSetting.PROD_THR;
		} else {

			if (this.production - WindTurbineSetting.PROD_THR <= 0) {
				this.production = 0;
			} else {

				this.production -= WindTurbineSetting.PROD_THR;
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
		simParams.put(URIS.WIND_TURBINE_URI, this);
		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INITIAL_DELAY,
				10.0) ;
		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INTERDAY_DELAY,
				100.0) ;
		simParams.put(
				WindTurbineModel.URI + ":" + WindTurbineModel.PRODUCTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Production", "Time (min)", "Production (W)",
						3 * WattWattMain.getPlotterWidth(),
						0,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		
		this.asp.setSimulationRunParameters(simParams);
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
						((WindTurbine) this.getTaskOwner()).isOnSim = 
								(((boolean) asp.getModelStateValue(WashingMachineModel.URI, "isOn")));
						((WindTurbine) this.getTaskOwner()).productionSim = 
								(((double) asp.getModelStateValue(WashingMachineModel.URI, "production")));
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
		return WindTurbineCoupledModel.build();
	}

}
