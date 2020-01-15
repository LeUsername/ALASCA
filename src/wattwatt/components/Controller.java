package wattwatt.components;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;
import wattwatt.interfaces.devices.suspendable.fridge.IFridge;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;
import wattwatt.interfaces.electricmeter.IElectricMeter;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;
import wattwatt.interfaces.energyproviders.random.windturbine.IWindTurbine;
import wattwatt.ports.devices.schedulable.washingmachine.WashingMachineOutPort;
import wattwatt.ports.devices.suspendable.fridge.FridgeOutPort;
import wattwatt.ports.devices.uncontrollable.hairdryer.HairDryerOutPort;
import wattwatt.ports.electricmeter.ElectricMeterOutPort;
import wattwatt.ports.energyproviders.occasional.enginegenerator.EngineGeneratorOutPort;
import wattwatt.ports.energyproviders.random.windturbine.WindTurbineOutPort;
import wattwatt.tools.EngineGenerator.EngineGeneratorSetting;
import wattwatt.tools.controller.ControllerSetting;

@OfferedInterfaces(offered = IController.class)
@RequiredInterfaces(required = { IElectricMeter.class, IFridge.class, IHairDryer.class, IWindTurbine.class,
		IWashingMachine.class, IEngineGenerator.class })
public class Controller extends AbstractComponent {
	protected String cptin;
	protected String refrin;
	protected String sechin;
	protected String eoin;
	protected String lavein;
	protected String groupein;

	protected ElectricMeterOutPort cptout;
	protected FridgeOutPort refriout;
	protected HairDryerOutPort sechout;
	protected WindTurbineOutPort eoout;
	protected WashingMachineOutPort laveout;
	protected EngineGeneratorOutPort groupeout;

	protected int allCons;

	protected Controller(String uri, String compteurIn, String compteurOut, String refriIn, String refriOut, String sechin,
			String sechOut, String eoIn, String eoOut, String laveIn, String laveOut, String groupeIn, String groupeOut)
			throws Exception {
		super(uri, 1, 6);

		this.cptin = compteurIn;
		this.refrin = refriIn;
		this.sechin = sechin;
		this.eoin = eoIn;
		this.lavein = laveIn;
		this.groupein = groupeIn;

		this.cptout = new ElectricMeterOutPort(compteurOut, this);
		this.cptout.publishPort();

		this.refriout = new FridgeOutPort(refriOut, this);
		this.refriout.publishPort();

		this.sechout = new HairDryerOutPort(sechOut, this);
		this.sechout.publishPort();

		this.eoout = new WindTurbineOutPort(eoOut, this);
		this.eoout.publishPort();

		this.laveout = new WashingMachineOutPort(laveOut, this);
		this.laveout.publishPort();

		this.groupeout = new EngineGeneratorOutPort(groupeOut, this);
		this.groupeout.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {

				try {
					while (true) {
						((Controller) this.getTaskOwner()).allCons = ((Controller) this.getTaskOwner()).cptout
								.getAllConso();
						((Controller) this.getTaskOwner())
								.logMessage("Compteur>> : " + ((Controller) this.getTaskOwner()).allCons);
						Thread.sleep(ControllerSetting.UPDATE_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				int cons = 0;
				try {
					((Controller) this.getTaskOwner()).refriout.On();
					while (true) {
						cons = ((Controller) this.getTaskOwner()).allCons;
						if (cons > 1220 && ((Controller) this.getTaskOwner()).refriout.isWorking()) {
							((Controller) this.getTaskOwner()).refriout.suspend();
						} else {
							if (!((Controller) this.getTaskOwner()).refriout.isWorking()) {
								((Controller) this.getTaskOwner()).refriout.resume();
							}
						}
						if (((Controller) this.getTaskOwner()).refriout.isOn()
								&& ((Controller) this.getTaskOwner()).refriout.isWorking()) {
							((Controller) this.getTaskOwner()).logMessage("Refri>> ON and Working Conso : [ "
									+ ((Controller) this.getTaskOwner()).refriout.getConso() + " ] : ");
						}
						Thread.sleep(ControllerSetting.UPDATE_RATE);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						if (((Controller) this.getTaskOwner()).sechout.isOn()) {
							((Controller) this.getTaskOwner()).logMessage("SecheCheveux>> ON Conso : [ "
									+ ((Controller) this.getTaskOwner()).sechout.getConso() + " ] : ");
						}
						Thread.sleep(ControllerSetting.UPDATE_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 1000, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controller) this.getTaskOwner()).eoout.On();
					Random rand = new Random(); // use to simulate wind condition for now
					while (true) {
						if (((Controller) this.getTaskOwner()).eoout.isOn()) {
							((Controller) this.getTaskOwner()).logMessage("Eolienne>> ON Prod : [ "
									+ ((Controller) this.getTaskOwner()).eoout.getEnergy() + " ] : ");
						} else {
							((Controller) this.getTaskOwner()).logMessage("Eolienne>> OFF Prod : [ "
									+ ((Controller) this.getTaskOwner()).eoout.getEnergy() + " ] : ");
						}
						if (rand.nextInt(100) > 60) {

							if (((Controller) this.getTaskOwner()).eoout.isOn()) {
								((Controller) this.getTaskOwner()).eoout.Off();
							} else {
								((Controller) this.getTaskOwner()).eoout.On();
							}
						}

						Thread.sleep(ControllerSetting.UPDATE_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controller) this.getTaskOwner()).laveout.On();
					// need to adapt to the energy consumption and production => Simulation.jar
					Random rand = new Random();
					while (true) {
						if (!((Controller) this.getTaskOwner()).laveout.isWorking()) {
							boolean changementMode = rand.nextBoolean();
							if (changementMode) {
								((Controller) this.getTaskOwner()).laveout.ecoLavage();
							} else {
								((Controller) this.getTaskOwner()).laveout.premiumLavage();
							}
						}
						((Controller) this.getTaskOwner())
								.logMessage("LaveLinge>> ON: "+ ((Controller) this.getTaskOwner()).laveout.isOn() + " Conso: " + ((Controller) this.getTaskOwner()).laveout.getConso());

						Thread.sleep(ControllerSetting.UPDATE_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Controller) this.getTaskOwner()).groupeout.on();
						while (((Controller) this.getTaskOwner()).groupeout.isOn()) {
							if (((Controller) this.getTaskOwner()).groupeout.isOn()) {
								((Controller) this.getTaskOwner()).logMessage("Groupe Electro>> ON prod : ["
										+ ((Controller) this.getTaskOwner()).groupeout.getEnergy() + "]"
										+ " fuel at : " + ((Controller) this.getTaskOwner()).groupeout.fuelQuantity()
										+ " / " + EngineGeneratorSetting.FUEL_CAPACITY);
							}
							Thread.sleep(ControllerSetting.UPDATE_RATE);
						}
						Thread.sleep(2 * ControllerSetting.UPDATE_RATE);
						((Controller) this.getTaskOwner()).groupeout.addFuel(EngineGeneratorSetting.FUEL_CAPACITY);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 3000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		try {
			this.cptout.unpublishPort();
			this.refriout.unpublishPort();
			this.sechout.unpublishPort();
			this.eoout.unpublishPort();
			this.laveout.unpublishPort();
			this.groupeout.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

}
