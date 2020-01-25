package wattwatt.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.deployment.WattWattMain;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.plugins.ElectricMeterSimulatorPlugin;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;
import wattwatt.interfaces.devices.suspendable.fridge.IFridge;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;
import wattwatt.interfaces.electricmeter.IElectricMeter;
import wattwatt.ports.devices.schedulable.washingmachine.WashingMachineOutPort;
import wattwatt.ports.devices.suspendable.fridge.FridgeOutPort;
import wattwatt.ports.devices.uncontrollable.hairdryer.HairDryerOutPort;
import wattwatt.ports.electricmeter.ElectricMeterInPort;
import wattwatt.tools.URIS;
import wattwatt.tools.electricmeter.ElectricMeterSetting;

@OfferedInterfaces(offered = IElectricMeter.class)
@RequiredInterfaces(required = { IController.class, IFridge.class, IHairDryer.class, IWashingMachine.class })
public class ElectricMeter extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {

	protected ElectricMeterInPort cptin;

	protected FridgeOutPort refriout;
	protected HairDryerOutPort sechout;
	protected WashingMachineOutPort laveout;

	protected double consomation;
	protected double fridgeConsumption;
	protected double hairDryerConsumption;
	protected double washingMachineConsumption;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	protected double consumptionSim;

	protected ElectricMeterSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected ElectricMeter(String uri, String compteurIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.cptin = new ElectricMeterInPort(compteurIn, this);
		this.cptin.publishPort();

		this.tracer.setRelativePosition(0, 1);
	}

	protected ElectricMeter(String uri, String compteurIn, String refriOut, String sechOut, String laveOut)
			throws Exception {
		super(uri, 2, 3);
		this.initialise();
		this.cptin = new ElectricMeterInPort(compteurIn, this);
		this.cptin.publishPort();

		this.refriout = new FridgeOutPort(refriOut, this);
		this.refriout.publishPort();

		this.laveout = new WashingMachineOutPort(laveOut, this);
		this.laveout.publishPort();

		this.sechout = new HairDryerOutPort(sechOut, this);
		this.sechout.publishPort();

		this.tracer.setRelativePosition(0, 1);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new ElectricMeterSimulatorPlugin();
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

	public double giveConso() throws Exception {
		return consomation;
	}

	public void majConso() {
		Random rand = new Random();
		this.consomation = ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION + rand.nextInt(
				ElectricMeterSetting.MAX_THR_HOUSE_CONSUMPTION - ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Compteur starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		HashMap<String, Object> simParams = new HashMap<String, Object>();
		simParams.put(URIS.ELECTRIC_METER_URI, this);
		simParams.put(
				ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Electric meter model", "Time (min)", "Consumption (Watt)",
						2 * WattWattMain.getPlotterWidth(), 3 * WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(), WattWattMain.getPlotterHeight()));

		this.asp.setSimulationRunParameters(simParams);
		// Start the simulation.
		this.runTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
//					asp.doStandAloneSimulation(0.0, TimeScale.WEEK);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		// this.runTask(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// while (true) {
		// ((ElectricMeter) this.getTaskOwner()).consumptionSim =
		// (((double) asp.getModelStateValue(ElectricMeterModel.URI, "consumption")));
		// Thread.sleep(1000);
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// });
		// this.scheduleTask(new AbstractComponent.AbstractTask() {
		// @Override
		// public void run() {
		// try {
		// while (true) {
		// ((ElectricMeter) this.getTaskOwner()).majConso();
		// Thread.sleep(ElectricMeterSetting.UPDATE_RATE);
		// }
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// }, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		try {
			this.cptin.unpublishPort();
			this.sechout.unpublishPort();
			this.laveout.unpublishPort();
			this.refriout.unpublishPort();
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
		if (name.equals("fridgeConsumption")) {
			return new Double(this.fridgeConsumption);
		} else if (name.equals("hairDryerConsumption")) {
			return new Double(this.hairDryerConsumption);
		} else if (name.equals("washingMachineConsumption")) {
			return new Double(this.washingMachineConsumption);
		} else if (name.equals("totalConsumption")) {
			return new Double(this.consomation);
		} else if (name.equals("setFridgeConsumption")) {
			this.fridgeConsumption = (double) this.asp.getModelStateValue(ElectricMeterModel.URI, "fridgeConsumption");
			return null;
		} else if (name.equals("setHairDryerConsumption")) {
			this.hairDryerConsumption = (double) this.asp.getModelStateValue(ElectricMeterModel.URI,
					"hairDryerConsumption");
			return null;
		} else if (name.equals("setWashingMachineConsumption")) {
			this.washingMachineConsumption = (double) this.asp.getModelStateValue(ElectricMeterModel.URI,
					"washingMachineConsumption");
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public void setEmbeddingComponentStateValue(String name, Object value) throws Exception {
		// TODO Auto-generated method stub
		EmbeddingComponentAccessI.super.setEmbeddingComponentStateValue(name, value);
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		Map<Class<? extends EventI>,EventSink[]> imported =
				new HashMap<Class<? extends EventI>,EventSink[]>() ;
		imported.put(
				HairDryerConsumptionEvent.class, 
				new EventSink[] {
						new EventSink(ElectricMeterModel.URI, HairDryerConsumptionEvent.class)
				}) ;
		
		atomicModelDescriptors.put(ElectricMeterModel.URI, AtomicHIOA_Descriptor.create(ElectricMeterModel.class,
				ElectricMeterModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		
		return new Architecture(ElectricMeterModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}

}
