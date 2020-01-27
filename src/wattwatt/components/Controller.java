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

//-----------------------------------------------------------------------------
/**
* The class <code>Controller</code> 
*
* <p><strong>Description</strong></p>
* 
³ This class implements the controller component that give orders
* to all the components in our system.
* The controller is connected to all other device so he require all their interfaces
* 
* 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
//The next annotation requires that the referenced interface is added to
//the required interfaces of the component.
@OfferedInterfaces(offered = IController.class)
@RequiredInterfaces(required = { IElectricMeter.class,
								 IFridge.class, 
								 IHairDryer.class, 
								 IWindTurbine.class,
								 IWashingMachine.class, 
								 IEngineGenerator.class })
public class Controller extends AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	/** The inbound port of the fridge*/
	protected String refrin;
	/** The inbound port of the hair dryer*/
	protected String sechin;
	/** The inbound port of the wind turbine*/
	protected String eoin;
	/** The inbound port of the washing machine*/
	protected String lavein;
	/** The inbound port of the engine generator*/
	protected String groupein;

	/**	the outbound port used to call the electric meter services.*/
	protected ElectricMeterOutPort cptout;
	/**	the outbound port used to call the fridge services.	*/
	protected FridgeOutPort refriout;
	/**	the outbound port used to call the hair dryer services.	*/
	protected HairDryerOutPort sechout;
	/**	the outbound port used to call the wind turbine services.	*/
	protected WindTurbineOutPort eoout;
	/**	the outbound port used to call the washing machine services.	*/
	protected WashingMachineOutPort laveout;
	/**	the outbound port used to call the engine generator services.	*/
	protected EngineGeneratorOutPort groupeout;

	/** the variable to keep the overall consommation received by the compteur*/
	protected double allCons;

	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Controller.
	 * 
	 *
	 * @param uri				URI of the component.
	 * @param compteurOut		outbound port URI of the electric meter.
	 * @param refriIn			inbound port URI of the fridge.
	 * @param refriOut			outbound port URI of the fridge.
	 * @param sechin			inbound port URI of the hair dryer.
	 * @param sechOut			outbound port URI of the hair dryer.
	 * @param eoIn				inbound port URI of the wind turbine.
	 * @param eoOut				outbound port URI of the wind turbine.
	 * @param laveIn			inbound port URI of the washinf machine.
	 * @param laveOut			outbound port URI of the washing machine.
	 * @param groupeIn			inbound port URI of the engine generator.
	 * @param groupeOut			outbound port URI of the engine generator.
	 * @throws Exception		<i>todo.</i>
	 */
	protected Controller(String uri, String compteurOut, String refriIn, String refriOut, String sechin,
			String sechOut, String eoIn, String eoOut, String laveIn, String laveOut, String groupeIn, String groupeOut)
			throws Exception {
		super(uri, 1, 6);

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

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		
		// Create a scheduleTask to handle each other devices and by using the overall energy consommation and production 
		// send order by calling services from these devices
		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {

				try {
						((Controller) this.getTaskOwner()).allCons = ((Controller) this.getTaskOwner()).cptout
								.getAllConso();
						((Controller) this.getTaskOwner())
								.logMessage("Compteur>> : " + ((Controller) this.getTaskOwner()).allCons);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);

		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				double cons = 0;
				try {
					((Controller) this.getTaskOwner()).refriout.On();
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
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);

		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
						if (((Controller) this.getTaskOwner()).sechout.isOn()) {
							((Controller) this.getTaskOwner()).logMessage("SecheCheveux>> ON Conso : [ "
									+ ((Controller) this.getTaskOwner()).sechout.getConso() + " ] : ");
						}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 1000, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);

		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controller) this.getTaskOwner()).eoout.On();
					Random rand = new Random(); // use to simulate wind condition for now
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
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);

		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controller) this.getTaskOwner()).laveout.On();
					// need to adapt to the energy consumption and production => Simulation.jar
					Random rand = new Random();
						if (!((Controller) this.getTaskOwner()).laveout.isWorking()) {
							boolean changementMode = rand.nextBoolean();
							if (changementMode) {
								((Controller) this.getTaskOwner()).laveout.ecoWashing();
							} else {
								((Controller) this.getTaskOwner()).laveout.premiumWashing();
							}
						}
						((Controller) this.getTaskOwner())
								.logMessage("LaveLinge>> ON: "+ ((Controller) this.getTaskOwner()).laveout.isOn() + " Conso: " + ((Controller) this.getTaskOwner()).laveout.getConso());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);

		this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
						((Controller) this.getTaskOwner()).groupeout.on();
						while (((Controller) this.getTaskOwner()).groupeout.isOn()) {
							if (((Controller) this.getTaskOwner()).groupeout.isOn()) {
								((Controller) this.getTaskOwner()).logMessage("Groupe Electro>> ON prod : ["
										+ ((Controller) this.getTaskOwner()).groupeout.getEnergy() + "]"
										+ " fuel at : " + ((Controller) this.getTaskOwner()).groupeout.fuelQuantity()
										+ " / " + EngineGeneratorSetting.FUEL_CAPACITY);
							}
						Thread.sleep(2 * ControllerSetting.UPDATE_RATE);
						((Controller) this.getTaskOwner()).groupeout.addFuel(EngineGeneratorSetting.FUEL_CAPACITY);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 3000, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
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
