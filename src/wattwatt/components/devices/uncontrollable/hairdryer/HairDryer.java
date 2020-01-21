package wattwatt.components.devices.uncontrollable.hairdryer;

import java.util.HashMap;
import java.util.Random;
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
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.models.hairdryer.HairDryerModel;
import simulation.models.hairdryer.HairDryerUserModel;
import simulation.plugins.HairDryerSimulatorPlugin;
import simulation.tools.TimeScale;
import simulation.tools.hairdryer.HairDryerPowerLevel;
import simulation.tools.hairdryer.HairDryerUserBehaviour;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;
import wattwatt.ports.devices.uncontrollable.hairdryer.HairDryerInPort;
import wattwatt.tools.URIS;
import wattwatt.tools.hairdryer.HairDryerMode;
import wattwatt.tools.hairdryer.HairDryerSetting;

@OfferedInterfaces(offered = IHairDryer.class)
@RequiredInterfaces(required = IController.class)
public class HairDryer extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {
	protected HairDryerMode mode;
	protected HairDryerPowerLevel powerLvl;

	protected boolean isOn;
	protected int conso;


	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected HairDryerInPort sechin;
	protected HairDryerSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected HairDryer(String uri, String sechin) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.sechin = new HairDryerInPort(sechin, this);
		this.sechin.publishPort();

		this.mode = HairDryerMode.HOT_AIR;
		this.powerLvl = HairDryerPowerLevel.LOW;
		this.isOn = false;

		this.tracer.setRelativePosition(1, 1);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new HairDryerSimulatorPlugin();
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

	public void on() {
		this.isOn = true;
	}

	public void off() {
		this.isOn = false;
	}

	protected void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public int giveConso() {
		return conso;
	}

	public boolean isOn() {
		return isOn;
	}

	public void switchMode() {
		if (this.mode == HairDryerMode.COLD_AIR) {
			this.mode = HairDryerMode.HOT_AIR;
		} else {
			this.mode = HairDryerMode.COLD_AIR;
		}
	}

	protected void setMode(HairDryerMode mode) {
		this.mode = mode;
	}

	public void increasePower() {
		if (this.powerLvl == HairDryerPowerLevel.LOW) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else if(this.powerLvl == HairDryerPowerLevel.MEDIUM){
			this.powerLvl = HairDryerPowerLevel.HIGH;
		}

	}

	public void decreasePower() {
		if (this.powerLvl == HairDryerPowerLevel.HIGH) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else if(this.powerLvl == HairDryerPowerLevel.MEDIUM){
			this.powerLvl = HairDryerPowerLevel.LOW;
		}
	}

	protected void setPowerLevel(HairDryerPowerLevel powerLeveLValue) {
		this.powerLvl = powerLeveLValue;
		
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("SecheCheveux starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void behave(Random rand) {
		if (this.isOn) {
			if (rand.nextBoolean()) {
				this.switchMode();
				if (rand.nextBoolean()) {
					this.increasePower();
				} else {
					this.decreasePower();
				}
			}
			if (this.mode == HairDryerMode.COLD_AIR) {
				this.conso += HairDryerSetting.CONSO_COLD_MODE * this.powerLvl.getValue();
			} else {
				this.conso += HairDryerSetting.CONSO_HOT_MODE * this.powerLvl.getValue();
			}
		} else {
			if (this.mode == HairDryerMode.COLD_AIR) {
				if (this.conso - HairDryerSetting.CONSO_COLD_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= HairDryerSetting.CONSO_COLD_MODE;
				}
			} else {
				if (this.conso - HairDryerSetting.CONSO_HOT_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= HairDryerSetting.CONSO_HOT_MODE;
				}
			}
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
		// To give an example of the embedding component access facility, the
		// following lines show how to set the reference to the embedding
		// component or a proxy responding to the access calls.
		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put(URIS.HAIR_DRYER_URI, this);
		simParams.put(
				HairDryerUserModel.URI + ":" + HairDryerUserModel.INITIAL_DELAY,
				HairDryerUserBehaviour.INITIAL_DELAY) ;
		simParams.put(
				HairDryerUserModel.URI + ":" + HairDryerUserModel.INTERDAY_DELAY,
				HairDryerUserBehaviour.INTERDAY_DELAY) ;
		simParams.put(
				HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_BETWEEN_USAGES,
				HairDryerUserBehaviour.MEAN_TIME_BETWEEN_USAGES) ;
		simParams.put(
				HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_AT_HIGH,
				HairDryerUserBehaviour.MEAN_TIME_AT_HIGH) ;
		simParams.put(
				HairDryerUserModel.URI + ":" + HairDryerUserModel.MEAN_TIME_AT_LOW,
				HairDryerUserBehaviour.MEAN_TIME_AT_LOW) ;
		
		simParams.put(
				HairDryerModel.URI + ":" + HairDryerModel.INTENSITY_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Hair dryer model",
						"Time (min)",
						"Intensity (Amp)",
						0,
						0,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		this.asp.setDebugLevel(0);
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
//		this.runTask(new AbstractComponent.AbstractTask() {
//			@Override
//			public void run() {
//				try {
//					while (true) {
//						((HairDryer) this.getTaskOwner())
//								.setOn((boolean) asp.getModelStateValue(HairDryerModel.URI, "isOn"));
//						if (isOn) {
//							((HairDryer) this.getTaskOwner())
//									.setMode((HairDryerMode) asp.getModelStateValue(HairDryerModel.URI, "mode"));
//							((HairDryer) this.getTaskOwner()).setPowerLevel(
//									(HairDryerPowerLevel) asp.getModelStateValue(HairDryerModel.URI, "powerLevel"));
//						}
//						Thread.sleep(1000);
//					}
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
//		});
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				Random rand = new Random();
				int useTime = 0;
				try {
					while (true) {
						if (rand.nextInt(100) > 98 && useTime == 0) {
							((HairDryer) this.getTaskOwner()).on();
							useTime = HairDryerSetting.MIN_USE_TIME + rand.nextInt(HairDryerSetting.MAX_USE_TIME);
							((HairDryer) this.getTaskOwner()).logMessage("seche cheveux ON for : " + useTime);
						} else {
							((HairDryer) this.getTaskOwner()).behave(rand); // 
							if (useTime - 1 <= 0) {
								useTime = 0;
							} else {
								useTime--;
							}
							Thread.sleep(HairDryerSetting.REGUL_RATE);
							if (useTime <= 0) {
								((HairDryer) this.getTaskOwner()).logMessage("seche cheveux OFF");
								((HairDryer) this.getTaskOwner()).off();
							}
						}

					}
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			} }, 10, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Seche cheveux shutdown");
		try {
			this.sechin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return HairDryerCoupledModel.build();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("mode")) {
			return this.mode;
		} else if (name.equals("isOn")) {
			return new Boolean(this.isOn);
		} else {
			return new Double(this.conso);
		}
	}
}
