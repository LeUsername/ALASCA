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
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.models.controller.ControllerModel;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.models.hairdryer.HairDryerModel;
import simulation.models.washingmachine.WashingMachineCoupledModel;
import simulation.models.washingmachine.WashingMachineModel;
import simulation.models.washingmachine.WashingMachineUserModel;
import simulation.models.wattwatt.WattWattModel;
import simulation.tools.TimeScale;

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
						/*(Class<? extends EventI>[])
							new Class<?>[]{
								SwitchOffEvent.class,
								SwitchOnEvent.class,
								SwitchModeEvent.class
							}*/null,
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
							HairDryerConsumptionEvent.class
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
							ConsumptionEvent.class
						},
						(Class<? extends EventI>[])
						new Class<?>[]{
							StartEngineGeneratorEvent.class,
							StopEngineGeneratorEvent.class
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
						null,
						(Class<? extends EventI>[])
							new Class<?>[]{
							WashingMachineConsumptionEvent.class
							},
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(WashingMachineCoupledModel.URI))) ;
		
		
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
		
		
		simParams.put(
				HairDryerModel.URI + ":" + HairDryerModel.INTENSITY_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Hair dryer model",
						"Time (min)",
						"Intensity (Watt)",
						WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y,
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight())) ;
		
		simParams.put(
				EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneUserModel", "Time (min)", "User actions",
						WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Power (Watt)", WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				WashingMachineUserModel.URI + ":" + WashingMachineUserModel.ACTION + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("LaveLingeUserModel", "Time (min)", "User actions",
						WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y+4*WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		simParams.put(
				WashingMachineModel.URI + ":" + WashingMachineModel.INTENSITY_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("LaveLingeModel", "Time (min)", "Consumption (W)", WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y + 3*WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(
				EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Volume (Liters)", WattWattMain.ORIGIN_X,
						WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));

		simParams.put(
				ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription(
						"Electric meter model",
						"Time (min)",
						"Consumption (Watt)",
						2 * WattWattMain.getPlotterWidth(), 
						3 * WattWattMain.getPlotterHeight(),
						WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		simParams.put(					
				ControllerModel.URI + ":" + ControllerModel.PRODUCTION_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("ControllerModel", "Time (sec)", "W", WattWattMain.ORIGIN_X + 2 * WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y , WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		simParams.put(
				ControllerModel.URI + ":" + ControllerModel.ENGINE_GENERATOR_SERIES + ":"
						+ PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("ControllerModel", "Time (sec)", "EG decision", WattWattMain.ORIGIN_X + 2 * WattWattMain.getPlotterWidth(),
						WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
						WattWattMain.getPlotterHeight()));
		
		

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
