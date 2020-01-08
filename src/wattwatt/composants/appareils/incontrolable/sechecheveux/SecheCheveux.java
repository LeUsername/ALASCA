package wattwatt.composants.appareils.incontrolable.sechecheveux;

import java.util.HashMap;
import java.util.Random;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulation.equipements.Duree;
import simulation.equipements.sechecheveux.components.SecheCheveuxSimulatorPlugin;
import simulation.equipements.sechecheveux.models.SecheCheveuxCoupledModel;
import simulation.equipements.sechecheveux.models.SecheCheveuxModel;
import simulation.equipements.sechecheveux.tools.HairDryerPowerLevel;
import wattwatt.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.ports.appareils.incontrolable.sechecheveux.SecheCheveuxInPort;
import wattwatt.tools.sechecheveux.SecheCheveuxMode;
import wattwatt.tools.sechecheveux.SecheCheveuxReglage;

@OfferedInterfaces(offered = ISecheCheveux.class)
@RequiredInterfaces(required = IControleur.class)
public class SecheCheveux extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {
	protected SecheCheveuxMode mode;
	protected HairDryerPowerLevel powerLvl;

	protected boolean isOn;
	protected int conso;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected SecheCheveuxInPort sechin;
	protected SecheCheveuxSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected SecheCheveux(String uri, String sechin) throws Exception {
		super(uri, 2, 0);
		this.initialise();
		this.sechin = new SecheCheveuxInPort(sechin, this);
		this.sechin.publishPort();

		this.mode = SecheCheveuxMode.HOT_AIR;
		this.powerLvl = HairDryerPowerLevel.LOW;
		this.isOn = false;

		this.tracer.setRelativePosition(1, 1);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new SecheCheveuxSimulatorPlugin();
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
		if (this.mode == SecheCheveuxMode.COLD_AIR) {
			this.mode = SecheCheveuxMode.HOT_AIR;
		} else {
			this.mode = SecheCheveuxMode.COLD_AIR;
		}
	}

	protected void setMode(SecheCheveuxMode mode) {
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
			if (this.mode == SecheCheveuxMode.COLD_AIR) {
				this.conso += SecheCheveuxReglage.CONSO_COLD_MODE * this.powerLvl.getValue();
			} else {
				this.conso += SecheCheveuxReglage.CONSO_HOT_MODE * this.powerLvl.getValue();
			}
		} else {
			if (this.mode == SecheCheveuxMode.COLD_AIR) {
				if (this.conso - SecheCheveuxReglage.CONSO_COLD_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= SecheCheveuxReglage.CONSO_COLD_MODE;
				}
			} else {
				if (this.conso - SecheCheveuxReglage.CONSO_HOT_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= SecheCheveuxReglage.CONSO_HOT_MODE;
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
		simParams.put("componentRef", this);
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
						((SecheCheveux) this.getTaskOwner())
								.setOn((boolean) asp.getModelStateValue(SecheCheveuxModel.URI, "isOn"));
						if (isOn) {
							((SecheCheveux) this.getTaskOwner())
									.setMode((SecheCheveuxMode) asp.getModelStateValue(SecheCheveuxModel.URI, "mode"));
							((SecheCheveux) this.getTaskOwner()).setPowerLevel(
									(HairDryerPowerLevel) asp.getModelStateValue(SecheCheveuxModel.URI, "powerLevel"));
						}
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
//		this.scheduleTask(new AbstractComponent.AbstractTask() {
//			@Override
//			public void run() {
//				Random rand = new Random();
//				int useTime = 0;
//				try {
//					while (true) {
//						if (rand.nextInt(100) > 98 && useTime == 0) {
//							((SecheCheveux) this.getTaskOwner()).on();
//							useTime = SecheCheveuxReglage.MIN_USE_TIME + rand.nextInt(SecheCheveuxReglage.MAX_USE_TIME);
//							((SecheCheveux) this.getTaskOwner()).logMessage("seche cheveux ON for : " + useTime);
//						} else {
//							((SecheCheveux) this.getTaskOwner()).behave(rand);
//							if (useTime - 1 <= 0) {
//								useTime = 0;
//							} else {
//								useTime--;
//							}
//							Thread.sleep(SecheCheveuxReglage.REGUL_RATE);
//							if (useTime <= 0) {
//								((SecheCheveux) this.getTaskOwner()).logMessage("seche cheveux OFF");
//								((SecheCheveux) this.getTaskOwner()).off();
//							}
//						}
//
//					}
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}, 100, TimeUnit.MILLISECONDS);
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
		return SecheCheveuxCoupledModel.build();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("mode")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "mode");
		} else if (name.equals("isOn")) {
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "isOn");
		} else {
			assert name.equals("intensity");
			return this.asp.getModelStateValue(SecheCheveuxModel.URI, "intensity");
		}
	}
}
