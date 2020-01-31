package simulation.deployment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.events.controller.ResumeFridgeEvent;
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StartWashingMachineEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.controller.StopWashingMachineEvent;
import simulation.events.controller.SuspendFridgeEvent;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.fridge.FridgeConsumptionEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.events.windturbine.WindTurbineProductionEvent;
import simulation.models.controller.ControllerModel;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.models.fridge.FridgeCoupledModel;
import simulation.models.fridge.FridgeModel;
import simulation.models.fridge.FridgeSensorModel;
import simulation.models.fridge.FridgeUserModel;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.models.hairdryer.HairDryerModel;
import simulation.models.hairdryer.HairDryerUserModel;
import simulation.models.washingmachine.WashingMachineCoupledModel;
import simulation.models.washingmachine.WashingMachineModel;
import simulation.models.washingmachine.WashingMachineUserModel;
import simulation.models.wattwatt.WattWattModel;
import simulation.models.windturbine.WindTurbineCoupledModel;
import simulation.models.windturbine.WindTurbineModel;
import simulation.models.windturbine.WindTurbineSensorModel;
import simulation.tools.TimeScale;
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;
import simulation.tools.hairdryer.HairDryerUserBehaviour;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;
import wattwatt.tools.washingmachine.WashingMachineSetting;

public class WattWattSupervisorComponent extends AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	
	public static final String		ARCHITECTURE_URI = "WattWattSimArchitecture" ;
	/** the supervisor plug-in attached to the component. */
	protected SupervisorPlugin sp;
	/**
	 * maps from URIs of models to URIs of the reflection inbound ports of the
	 * components that hold them.
	 */
	protected Map<String, String> modelURIs2componentURIs;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor component for WattWatt from the given map
	 * describing the deployment of models on components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURIs2componentURIs map from model URIs to the URIs of the
	 *                                components that hold them.
	 * @throws Exception <i>to do</i>.
	 */
	protected WattWattSupervisorComponent(Map<String, String> modelURIs2componentURIs) throws Exception {
		super(2, 0);

		assert modelURIs2componentURIs != null;
		this.initialise(modelURIs2componentURIs);
	}

	/**
	 * create a supervisor component for the WattWatt example with the given
	 * reflection inbound port URI and from the given map describing the deployment
	 * of models on components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	reflectionInboundPortURI != null
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI URI of the reflection inbound port imposed
	 *                                 for the creation of this component.
	 * @param modelURIs2componentURIs  map from model URIs to the URIs of the
	 *                                 components that hold them.
	 * @throws Exception <i>to do</i>.
	 */
	protected WattWattSupervisorComponent(String reflectionInboundPortURI, Map<String, String> modelURIs2componentURIs)
			throws Exception {
		super(reflectionInboundPortURI, 2, 0);

		this.initialise(modelURIs2componentURIs);
	}

	/**
	 * initialise the simulation architecture for the WattWatt example from the given
	 * map describing the deployment of models on components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	modelURIs2componentURIs != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param modelURIs2componentURIs map from model URIs to the URIs of the
	 *                                components that hold them.
	 * @throws Exception <i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	protected void initialise(Map<String, String> modelURIs2componentURIs) throws Exception {

		this.modelURIs2componentURIs = modelURIs2componentURIs;

		
		this.tracer.setTitle("SupervisorComponent");
		this.tracer.setRelativePosition(0, 4);
		this.toggleTracing();

		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// ----------------------------------------------------------------
		// Seche cheveux
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				HairDryerCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						HairDryerCoupledModel.URI,
						null,
						(Class<? extends EventI>[])
							new Class<?>[]{
							HairDryerConsumptionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(HairDryerCoupledModel.URI))) ;
		
		// ----------------------------------------------------------------
		// Compteur
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				ElectricMeterModel.URI,
				ComponentAtomicModelDescriptor.create(
						ElectricMeterModel.URI,
						(Class<? extends EventI>[])
						new Class<?>[]{
							HairDryerConsumptionEvent.class,
							FridgeConsumptionEvent.class,
							WashingMachineConsumptionEvent.class
						},
						(Class<? extends EventI>[])
						new Class<?>[]{
						ConsumptionEvent.class
						},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(ElectricMeterModel.URI))) ;
		
		// ----------------------------------------------------------------
		// Controller
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				ControllerModel.URI,
				ComponentAtomicModelDescriptor.create(
						ControllerModel.URI,
						(Class<? extends EventI>[])
						new Class<?>[]{
							ConsumptionEvent.class,
							EngineGeneratorProductionEvent.class,
							WindTurbineProductionEvent.class
						},
						(Class<? extends EventI>[])
						new Class<?>[]{
							StartEngineGeneratorEvent.class, 
							StopEngineGeneratorEvent.class, 
							SuspendFridgeEvent.class, 
							ResumeFridgeEvent.class,
							StartWashingMachineEvent.class,
							StopWashingMachineEvent.class
						},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(ControllerModel.URI))) ;
		
		// ----------------------------------------------------------------
		// Engine Generator
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				EngineGeneratorCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						EngineGeneratorCoupledModel.URI,
						(Class<? extends EventI>[])
							new Class<?>[]{
								StartEngineGeneratorEvent.class,
								StopEngineGeneratorEvent.class
							},
						(Class<? extends EventI>[])
							new Class<?>[]{
							EngineGeneratorProductionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(EngineGeneratorCoupledModel.URI))) ;
		
		// ----------------------------------------------------------------
		// Washing Machine
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				WashingMachineCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						WashingMachineCoupledModel.URI,
						(Class<? extends EventI>[])
						new Class<?>[]{
							StartWashingMachineEvent.class,
							StopWashingMachineEvent.class
						},
						(Class<? extends EventI>[])
							new Class<?>[]{
							WashingMachineConsumptionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(WashingMachineCoupledModel.URI))) ;
		
		// ----------------------------------------------------------------
		// Fridge
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				FridgeCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						FridgeCoupledModel.URI,
						(Class<? extends EventI>[])
						new Class<?>[]{
							SuspendFridgeEvent.class,
							ResumeFridgeEvent.class
						},
						(Class<? extends EventI>[])
							new Class<?>[]{
							FridgeConsumptionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(FridgeCoupledModel.URI))) ;
		
		// ----------------------------------------------------------------
		// WindTurbine
		// ----------------------------------------------------------------
		atomicModelDescriptors.put(
				WindTurbineCoupledModel.URI,
				ComponentAtomicModelDescriptor.create(
						WindTurbineCoupledModel.URI,
						null,
						(Class<? extends EventI>[])
							new Class<?>[]{
							WindTurbineProductionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(WindTurbineCoupledModel.URI))) ;
		
		
		// ----------------------------------------------------------------
		// Full architecture
		// ----------------------------------------------------------------
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>() ;
		
		Set<String> submodels = new HashSet<String>() ;
		submodels.add(HairDryerCoupledModel.URI) ;
		submodels.add(ElectricMeterModel.URI) ;
		submodels.add(ControllerModel.URI) ;
		submodels.add(EngineGeneratorCoupledModel.URI) ;
		submodels.add(WashingMachineCoupledModel.URI) ;
		submodels.add(FridgeCoupledModel.URI) ;
		submodels.add(WindTurbineCoupledModel.URI) ;
		
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>() ;
				
		EventSource from1 =
				new EventSource(
						HairDryerCoupledModel.URI,
						HairDryerConsumptionEvent.class) ;
		EventSink[] to1=
				new EventSink[] {
						new EventSink(
								ElectricMeterModel.URI,
								HairDryerConsumptionEvent.class)} ;
		connections.put(from1, to1) ;
		
		
		EventSource from2 =
				new EventSource(
						ElectricMeterModel.URI,
						ConsumptionEvent.class) ;
		EventSink[] to2 =
				new EventSink[] {
						new EventSink(
								ControllerModel.URI,
								ConsumptionEvent.class)} ;
		connections.put(from2, to2) ;
		
		EventSource from3 =
				new EventSource(
						EngineGeneratorCoupledModel.URI,
						EngineGeneratorProductionEvent.class) ;
		EventSink[] to3 =
				new EventSink[] {
						new EventSink(
								ControllerModel.URI,
								EngineGeneratorProductionEvent.class)} ;
		connections.put(from3, to3) ;
		
		EventSource from4 =
				new EventSource(
						ControllerModel.URI,
						StartEngineGeneratorEvent.class) ;
		EventSink[] to4 =
				new EventSink[] {
						new EventSink(
								EngineGeneratorCoupledModel.URI,
								StartEngineGeneratorEvent.class)} ;
		connections.put(from4, to4) ;
		EventSource from5 =
				new EventSource(
						ControllerModel.URI,
						StopEngineGeneratorEvent.class) ;
		EventSink[] to5 =
				new EventSink[] {
						new EventSink(
								EngineGeneratorCoupledModel.URI,
								StopEngineGeneratorEvent.class)} ;
		connections.put(from5, to5) ;
		
		EventSource from6 =
				new EventSource(
						WashingMachineCoupledModel.URI,
						WashingMachineConsumptionEvent.class) ;
		EventSink[] to6 =
				new EventSink[] {
						new EventSink(
								ElectricMeterModel.URI,
								WashingMachineConsumptionEvent.class)} ;
		connections.put(from6, to6) ;
		
		EventSource from7 =
				new EventSource(
						FridgeCoupledModel.URI,
						FridgeConsumptionEvent.class) ;
		EventSink[] to7 =
				new EventSink[] {
						new EventSink(
								ElectricMeterModel.URI,
								FridgeConsumptionEvent.class)} ;
		connections.put(from7, to7) ;
		
		EventSource from8 =
				new EventSource(
						ControllerModel.URI,
						SuspendFridgeEvent.class) ;
		EventSink[] to8 =
				new EventSink[] {
						new EventSink(
								FridgeCoupledModel.URI,
								SuspendFridgeEvent.class)} ;
		connections.put(from8, to8) ;
		EventSource from9 =
				new EventSource(
						ControllerModel.URI,
						ResumeFridgeEvent.class) ;
		EventSink[] to9 =
				new EventSink[] {
						new EventSink(
								FridgeCoupledModel.URI,
								ResumeFridgeEvent.class)} ;
		connections.put(from9, to9) ;
		
		EventSource from10 =
				new EventSource(
						WindTurbineCoupledModel.URI,
						WindTurbineProductionEvent.class) ;
		EventSink[] to10 =
				new EventSink[] {
						new EventSink(
								ControllerModel.URI,
								WindTurbineProductionEvent.class)} ;
		connections.put(from10, to10) ;
		
		EventSource from11 =
				new EventSource(
						ControllerModel.URI,
						StartWashingMachineEvent.class) ;
		EventSink[] to11 =
				new EventSink[] {
						new EventSink(
								WashingMachineCoupledModel.URI,
								StartWashingMachineEvent.class)} ;
		connections.put(from11, to11) ;
		EventSource from12 =
				new EventSource(
						ControllerModel.URI,
						StopWashingMachineEvent.class) ;
		EventSink[] to12 =
				new EventSink[] {
						new EventSink(
								WashingMachineCoupledModel.URI,
								StopWashingMachineEvent.class)} ;
		connections.put(from12, to12) ;
		

		
		
		coupledModelDescriptors.put(
				WattWattModel.URI,
				ComponentCoupledModelDescriptor.create(
						WattWattModel.class,
						WattWattModel.URI,
						submodels,
						null,
						null,
						connections,
						null,
						SimulationEngineCreationMode.COORDINATION_ENGINE,
						modelURIs2componentURIs.get(WattWattModel.URI))) ;

		ComponentModelArchitecture architecture =
				new ComponentModelArchitecture(
						ARCHITECTURE_URI,
						WattWattModel.URI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS) ;

		this.sp = new SupervisorPlugin(architecture) ;
		sp.setPluginURI("supervisor") ;
		this.installPlugin(this.sp) ;
		this.logMessage("Supervisor plug-in installed...") ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		super.execute();

		this.logMessage("supervisor component begins execution.");
		try{
			sp.createSimulator();
		}catch(Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(1000L) ;
		
		this.logMessage("SupervisorComponent#execute 1");

		Map<String, Object> simParams = new HashMap<String, Object>();
		
		
		

		String modelURI = TicModel.URI + "-10";
		simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME, new Duration(10.0, TimeUnit.SECONDS));
		
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
		
		simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MTBI, 200.0) ;
		simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MID, 10.0) ;
		
		
		simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MTBI, 200.0) ;
		simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MID, 10.0) ;
		simParams.put(
				FridgeModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 3.0) ;
		simParams.put(
				FridgeModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
		simParams.put(FridgeModel.URI + ":" + FridgeModel.INITIAL_TEMP, 2.0) ;
		simParams.put(
				FridgeSensorModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 2.5) ;
		simParams.put(
				FridgeSensorModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
		
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
		
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTBU,
				WashingMachineUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWE,
				WashingMachineUserBehaviour.MEAN_TIME_WORKING_ECO);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.MTWP,
				WashingMachineUserBehaviour.MEAN_TIME_WORKING_PREMIUM);
		simParams.put(WashingMachineUserModel.URI + ":" + WashingMachineUserModel.STD,
				10.0);
		
		
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_ECO,
				WashingMachineSetting.CONSO_ECO_MODE_SIM);
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_PREMIUM,
				WashingMachineSetting.CONSO_PREMIUM_MODE_SIM);
		simParams.put(WashingMachineModel.URI + ":" + WashingMachineUserModel.STD,
				10.0);
		
		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INITIAL_DELAY,
				10.0) ;
		simParams.put(
				WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INTERDAY_DELAY,
				200.0) ;
		
		
		simParams.put(
				ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Electric meter model",
						"Time (min)",
						"Consumption (Watt)",
						WattWattMain.ORIGIN_X + WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(					
				ControllerModel.URI + ":" + ControllerModel.PRODUCTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Controller Model", "Time (min)", "W", WattWattMain.ORIGIN_X ,
						WattWattMain.ORIGIN_Y , WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				ControllerModel.URI + ":" + ControllerModel.ENGINE_GENERATOR_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Controller Model", "Time (min)", "EG decision", WattWattMain.ORIGIN_X ,
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				ControllerModel.URI + ":" + ControllerModel.FRIDGE_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Controller Model", "Time (min)", "Fridge decision", WattWattMain.ORIGIN_X ,
						WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				ControllerModel.URI + ":" + ControllerModel.WASHING_MACHINE_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Controller Model", "Time (min)", "WM decision", WattWattMain.ORIGIN_X ,
						WattWattMain.ORIGIN_Y + 3 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				HairDryerModel.URI + ":" + HairDryerModel.CONSUMPTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Hair dryer model",
						"Time (min)",
						"Intensity (Watt)",
						WattWattMain.ORIGIN_X + WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		

		simParams.put(
				WashingMachineModel.URI + ":" + WashingMachineModel.CONSUMPTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Washing Machine Model", "Time (min)", "Consumption (W)",
						WattWattMain.ORIGIN_X + WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + 2*WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				WashingMachineUserModel.URI + ":" + WashingMachineUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Washing Machine User Model", "Time (min)", "User actions",
						WattWattMain.ORIGIN_X + WattWattMain.getPlotterWidth(), 
						WattWattMain.ORIGIN_Y+ 3*WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				WindTurbineModel.URI + ":" + WindTurbineModel.PRODUCTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Wind turbine model",
						"Time (min)",
						"Production (Watt)",
						WattWattMain.ORIGIN_X + 2*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y , 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
	
		
		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.FUEL_QUANTITY_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Engine Generator Model", "Time (min)", "Volume (Liters)", 
						WattWattMain.ORIGIN_X + 2*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		
		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Engine Generator Model", "Time (min)", "Power (Watt)", 
						WattWattMain.ORIGIN_X + 2*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + 2*WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("Engine Generator User Model", "Time (min)", "User actions",
						WattWattMain.ORIGIN_X + 2*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + 3*WattWattMain.getPlotterHeight() ,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		
		simParams.put(
				FridgeModel.URI + ":" + FridgeModel.TEMPERATURE + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Fridge Model",
						"Time (min)",
						"Temperature (Celsius)",
						WattWattMain.ORIGIN_X + 3*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y , 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		simParams.put(
				FridgeModel.URI + ":"  + FridgeModel.CONSUMPTION + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Fridge Model",
						"Time (min)",
						"Consumption (W)",
						WattWattMain.ORIGIN_X + 3*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;

		simParams.put(
				FridgeSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Fridge Sensor Model",
						"Time (min)",
						"Temperature (Celcius)",
						WattWattMain.ORIGIN_X + 3*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + 2*WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		
		simParams.put(
				FridgeUserModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Fridge User Model",
						"Time (min)",
						"Opened / Closed",
						WattWattMain.ORIGIN_X + 3*WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + 3*WattWattMain.getPlotterHeight(), 
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		

		

		this.logMessage("SupervisorComponent#execute 2");

		sp.setSimulationRunParameters(simParams);

		this.logMessage("SupervisorComponent#execute 3");

		this.logMessage("supervisor component begins simulation.");
		long start = System.currentTimeMillis();
		sp.doStandAloneSimulation(0, TimeScale.WEEK);
		long end = System.currentTimeMillis();
		this.logMessage("supervisor component ends simulation. " + (end - start));
	}
}
