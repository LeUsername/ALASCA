package wattwatt.components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import simulation.models.controller.ControllerModel;
import simulation.plugins.ControllerSimulatorPlugin;
import simulation.tools.enginegenerator.EngineGeneratorState;
import simulation.tools.fridge.FridgeConsumption;
import simulation.tools.washingmachine.WashingMachineState;
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
import wattwatt.tools.URIS;

//-----------------------------------------------------------------------------
/**
 * The class <code>Controller</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * ³ This class implements the controller component that give orders to all the
 * components in our system. The controller is connected to all other device so
 * he require all their interfaces
 * 
 * 
 * <p>
 * Created on : 2020-01-27
 * </p>
 * 
 * @author
 *         <p>
 * 		Bah Thierno, Zheng Pascal
 *         </p>
 */
// The next annotation requires that the referenced interface is added to
// the required interfaces of the component.
@OfferedInterfaces(offered = IController.class)
@RequiredInterfaces(required = { IElectricMeter.class, IFridge.class, IHairDryer.class, IWindTurbine.class,
		IWashingMachine.class, IEngineGenerator.class })
public class Controller extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** The inbound port of the fridge */
	protected String refrin;
	/** The inbound port of the hair dryer */
	protected String sechin;
	/** The inbound port of the wind turbine */
	protected String eoin;
	/** The inbound port of the washing machine */
	protected String lavein;
	/** The inbound port of the engine generator */
	protected String groupein;

	/** the outbound port used to call the electric meter services. */
	protected ElectricMeterOutPort cptout;
	/** the outbound port used to call the fridge services. */
	protected FridgeOutPort refriout;
	/** the outbound port used to call the hair dryer services. */
	protected HairDryerOutPort sechout;
	/** the outbound port used to call the wind turbine services. */
	protected WindTurbineOutPort eoout;
	/** the outbound port used to call the washing machine services. */
	protected WashingMachineOutPort laveout;
	/** the outbound port used to call the engine generator services. */
	protected EngineGeneratorOutPort groupeout;

	/** the variable to keep the overall consommation received by the compteur */
	protected double allCons;

	/** the simulation plug-in holding the simulation models. */
	protected ControllerSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a Controller.
	 * 
	 *
	 * @param uri
	 *            URI of the component.
	 * @param compteurOut
	 *            outbound port URI of the electric meter.
	 * @param refriIn
	 *            inbound port URI of the fridge.
	 * @param refriOut
	 *            outbound port URI of the fridge.
	 * @param sechin
	 *            inbound port URI of the hair dryer.
	 * @param sechOut
	 *            outbound port URI of the hair dryer.
	 * @param eoIn
	 *            inbound port URI of the wind turbine.
	 * @param eoOut
	 *            outbound port URI of the wind turbine.
	 * @param laveIn
	 *            inbound port URI of the washinf machine.
	 * @param laveOut
	 *            outbound port URI of the washing machine.
	 * @param groupeIn
	 *            inbound port URI of the engine generator.
	 * @param groupeOut
	 *            outbound port URI of the engine generator.
	 * @throws Exception
	 *             <i>todo.</i>
	 */
	protected Controller(String uri, String compteurOut, String refriIn, String refriOut, String sechin, String sechOut,
			String eoIn, String eoOut, String laveIn, String laveOut, String groupeIn, String groupeOut)
			throws Exception {
		super(uri, 1, 5);
		this.initialise();
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

	protected void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new ControllerSimulatorPlugin();
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
		this.logMessage("Controleur starting");
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		HashMap<String, Object> simParams = new HashMap<String, Object>();

		simParams.put(URIS.CONTROLLER_URI, this);

		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					// asp.doStandAloneSimulation(0.0, TimeScale.WEEK);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		// Create a scheduleTask to handle each other devices and by using the overall
		// energy consommation and production
		// send order by calling services from these devices
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		//
		// try {
		// ((Controller) this.getTaskOwner()).allCons = ((Controller)
		// this.getTaskOwner()).cptout
		// .getAllConso();
		// ((Controller) this.getTaskOwner())
		// .logMessage("Compteur>> : " + ((Controller) this.getTaskOwner()).allCons);
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
		//
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// double cons = 0;
		// try {
		// ((Controller) this.getTaskOwner()).refriout.On();
		// cons = ((Controller) this.getTaskOwner()).allCons;
		// if (cons > 1220 && ((Controller) this.getTaskOwner()).refriout.isWorking()) {
		// ((Controller) this.getTaskOwner()).refriout.suspend();
		// } else {
		// if (!((Controller) this.getTaskOwner()).refriout.isWorking()) {
		// ((Controller) this.getTaskOwner()).refriout.resume();
		// }
		// }
		// if (((Controller) this.getTaskOwner()).refriout.isOn()
		// && ((Controller) this.getTaskOwner()).refriout.isWorking()) {
		// ((Controller) this.getTaskOwner()).logMessage("Refri>> ON and Working Conso :
		// [ "
		// + ((Controller) this.getTaskOwner()).refriout.getConso() + " ] : ");
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
		//
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// if (((Controller) this.getTaskOwner()).sechout.isOn()) {
		// ((Controller) this.getTaskOwner()).logMessage("SecheCheveux>> ON Conso : [ "
		// + ((Controller) this.getTaskOwner()).sechout.getConso() + " ] : ");
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 1000, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
		//
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// ((Controller) this.getTaskOwner()).eoout.On();
		// Random rand = new Random(); // use to simulate wind condition for now
		// if (((Controller) this.getTaskOwner()).eoout.isOn()) {
		// ((Controller) this.getTaskOwner()).logMessage("Eolienne>> ON Prod : [ "
		// + ((Controller) this.getTaskOwner()).eoout.getEnergy() + " ] : ");
		// } else {
		// ((Controller) this.getTaskOwner()).logMessage("Eolienne>> OFF Prod : [ "
		// + ((Controller) this.getTaskOwner()).eoout.getEnergy() + " ] : ");
		// }
		// if (rand.nextInt(100) > 60) {
		//
		// if (((Controller) this.getTaskOwner()).eoout.isOn()) {
		// ((Controller) this.getTaskOwner()).eoout.Off();
		// } else {
		// ((Controller) this.getTaskOwner()).eoout.On();
		// }
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
		//
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// ((Controller) this.getTaskOwner()).laveout.On();
		// // need to adapt to the energy consumption and production => Simulation.jar
		// Random rand = new Random();
		// if (!((Controller) this.getTaskOwner()).laveout.isWorking()) {
		// boolean changementMode = rand.nextBoolean();
		// if (changementMode) {
		// ((Controller) this.getTaskOwner()).laveout.ecoWashing();
		// } else {
		// ((Controller) this.getTaskOwner()).laveout.premiumWashing();
		// }
		// }
		// ((Controller) this.getTaskOwner())
		// .logMessage("LaveLinge>> ON: "+ ((Controller)
		// this.getTaskOwner()).laveout.isOn() + " Conso: " + ((Controller)
		// this.getTaskOwner()).laveout.getConso());
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 100, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
		//
		// this.scheduleTaskAtFixedRate(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// ((Controller) this.getTaskOwner()).groupeout.on();
		// while (((Controller) this.getTaskOwner()).groupeout.isOn()) {
		// if (((Controller) this.getTaskOwner()).groupeout.isOn()) {
		// ((Controller) this.getTaskOwner()).logMessage("Groupe Electro>> ON prod : ["
		// + ((Controller) this.getTaskOwner()).groupeout.getEnergy() + "]"
		// + " fuel at : " + ((Controller) this.getTaskOwner()).groupeout.fuelQuantity()
		// + " / " + EngineGeneratorSetting.FUEL_CAPACITY);
		// }
		// Thread.sleep(2 * ControllerSetting.UPDATE_RATE);
		// ((Controller)
		// this.getTaskOwner()).groupeout.addFuel(EngineGeneratorSetting.FUEL_CAPACITY);
		//
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 3000, ControllerSetting.UPDATE_RATE, TimeUnit.MILLISECONDS);
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

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("consumption")) {
			return new Double(this.cptout.getAllConso());
		} else if (name.equals("productionEG")) {
			return new Double(this.groupeout.getEnergy());
		} else if (name.equals("productionWT")) {
			return new Double(this.eoout.getEnergy());
		} else if (name.equals("stateEG")) {
			return this.groupeout.isOn()?EngineGeneratorState.ON : EngineGeneratorState.OFF;
		} else if(name.equals("stateFridge")){
			boolean on  = this.refriout.isOn();
			boolean isWorking = this.refriout.isWorking();
			
			if(on && isWorking) {
				return FridgeConsumption.RESUMED;
			}
			else {
				return FridgeConsumption.SUSPENDED;
			}
		}else {
			assert name.equals("stateWM");
			boolean on  = this.laveout.isOn();
			boolean isWorking = this.laveout.isWorking();
			if(on && isWorking) {
				return WashingMachineState.WORKING;
			}
			else {
				if(on) {
					return WashingMachineState.ON;
				}else {
					return WashingMachineState.OFF;
				}
			}
		}
	}

	@Override
	public void setEmbeddingComponentStateValue(String name, Object value) throws Exception {
		if (name.equals("startEngine")) {
			this.groupeout.on();
		} else if (name.equals("stopEngine")) {
			this.groupeout.off();
		} else if (name.equals("suspendFridge")) {
			this.refriout.suspend();
		} else if (name.equals("resumeFridge")) {
			this.refriout.resume();
		} 
		else if (name.equals("startWM")) {
			this.laveout.On(); 
		}
		else {
			assert name.equals("stopWM");
			this.laveout.Off();
		}
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();
		atomicModelDescriptors.put(ControllerModel.URI, AtomicModelDescriptor.create(ControllerModel.class,
				ControllerModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		return new Architecture(ControllerModel.URI, atomicModelDescriptors, coupledModelDescriptors, TimeUnit.SECONDS);
	}

}
