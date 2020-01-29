package wattwatt.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
import wattwatt.tools.electricmeter.ElectricMeterSetting;

//-----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeter</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * ³ This class implements the electric meter component that give the overall
 * energy consumption of all the components in our system. The electric meter is
 * connected to all device that consume energy so he require all their
 * interfaces
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
@OfferedInterfaces(offered = IElectricMeter.class)
@RequiredInterfaces(required = { IController.class, IFridge.class, IHairDryer.class, IWashingMachine.class })
public class ElectricMeter extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** The inbound port of the electric meter */
	protected ElectricMeterInPort cptin;

	/** the outbound port used to call the fridge services. */
	protected FridgeOutPort refriout;
	/** the outbound port used to call the hair dryer services. */
	protected HairDryerOutPort sechout;
	/** the outbound port used to call the washing machine services */
	protected WashingMachineOutPort laveout;

	/** the overall energy consumption */
	protected double consomation;

	/** the fridge energy consumption */
	protected double fridgeConsumption;
	/** the hair dryer energy consumption */
	protected double hairDryerConsumption;
	/** the washing machine energy consumption */
	protected double washingMachineConsumption;

	/** the simulation plug-in holding the simulation models. */
	protected ElectricMeterSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create an electric meter. This constructor is used in the CVM.
	 * 
	 *
	 * @param uri        URI of the component.
	 * @param compteurIn inbound port URI of the electric meter.
	 * @throws Exception <i>todo.</i>
	 */
	protected ElectricMeter(String uri, String compteurIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();

		this.cptin = new ElectricMeterInPort(compteurIn, this);
		this.cptin.publishPort();
		this.tracer.setRelativePosition(0, 1);
	}

	/**
	 * Create an electric meter. This constructor is used in the Distributed CVM.
	 * 
	 *
	 * @param uri        URI of the component.
	 * @param compteurIn inbound port URI of the electric meter.
	 * @param refriOut   outbound port URI of the fridge.
	 * @param sechOut    outbound port URI of the hair dryer.
	 * @param laveOut    outbound port URI of the washing machine.
	 * @throws Exception <i>todo.</i>
	 */
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

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

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
//
//		HashMap<String, Object> simParams = new HashMap<String, Object>();
//
//		simParams.put(URIS.ELECTRIC_METER_URI, this);
//		simParams.put(
//				ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":"
//						+ PlotterDescription.PLOTTING_PARAM_NAME,
//				new PlotterDescription("Electric meter model", "Time (min)", "Consumption (Watt)",
//						2 * WattWattMain.getPlotterWidth(), 3 * WattWattMain.getPlotterHeight(),
//						WattWattMain.getPlotterWidth(), WattWattMain.getPlotterHeight()));
//
//		this.asp.setSimulationRunParameters(simParams);
//		// Start the simulation.
//		this.runTask(new AbstractComponent.AbstractTask() {
//			@Override
//			public void run() {
//				try {
//					 asp.doStandAloneSimulation(0.0, TimeScale.WEEK);
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
//		});

		// When there is no simulation
		
//		this.scheduleTask(new AbstractComponent.AbstractTask() {
//			@Override
//			public void run() {
//				try {
//					while (true) {
//						((ElectricMeter) this.getTaskOwner()).majConso();
//						Thread.sleep(ElectricMeterSetting.UPDATE_RATE);
//					}
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
//		}, 100, TimeUnit.MILLISECONDS);
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
		EmbeddingComponentAccessI.super.setEmbeddingComponentStateValue(name, value);
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();
		
		atomicModelDescriptors.put(ElectricMeterModel.URI, AtomicHIOA_Descriptor.create(ElectricMeterModel.class,
				ElectricMeterModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		return new Architecture(ElectricMeterModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}

	/**
	 * Get the overall energy consumption
	 * @return	the overall energy consumption
	 * @throws Exception<i>todo.</i>
	 */
	public double giveConso() throws Exception {
		this.consomation = this.fridgeConsumption + this.washingMachineConsumption + this.hairDryerConsumption;
		return consomation;
	}

	/**
	 * Update the overall energy consumption randomly
	 * @return	void
	 * @throws Exception<i>todo.</i>
	 */
	public void majConso() {
		Random rand = new Random();
		this.consomation = ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION + rand.nextInt(
				ElectricMeterSetting.MAX_THR_HOUSE_CONSUMPTION - ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION);
	}

}
