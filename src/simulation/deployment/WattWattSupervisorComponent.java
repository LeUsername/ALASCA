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
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.sechecheveux.HairDryerCoupledModel;
import simulation.models.wattwatt.WattWattModel;

public class WattWattSupervisorComponent extends AbstractComponent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

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
	 * create a supervisor component for the Molene example from the given map
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
	 * create a supervisor component for the Molene example with the given
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
	 * initialise the simulation architecture for the Molene example from the given
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
								ConsumptionEvent.class
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
							ConsumptionEvent.class
						},
						null,
						TimeUnit.SECONDS,
						modelURIs2componentURIs.get(ElectricMeterModel.URI))) ;
		
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
				new HashMap<>() ;
		
		Set<String> submodels = new HashSet<String>() ;
		submodels.add(HairDryerCoupledModel.URI) ;
		submodels.add(ElectricMeterModel.URI) ;
		
		Map<EventSource,EventSink[]> connections =
				new HashMap<EventSource,EventSink[]>() ;
				
		EventSource from1 =
				new EventSource(
						HairDryerCoupledModel.URI,
						ConsumptionEvent.class) ;
		EventSink[] to1=
				new EventSink[] {
						new EventSink(
								ElectricMeterModel.URI,
								ConsumptionEvent.class)} ;
		connections.put(from1, to1) ;
		
		
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

		sp.createSimulator();

		Thread.sleep(1000L);
		this.logMessage("SupervisorComponent#execute 1");

		Map<String, Object> simParams = new HashMap<String, Object>();

		String modelURI = TicModel.URI + "-1";
		simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME, new Duration(10.0, TimeUnit.SECONDS));

		modelURI = HairDryerCoupledModel.URI;
		simParams.put(modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("SecheCheveuxCoupledModel", "Time (sec)", "Bandwidth (Mbps)",
						SimulationMain.ORIGIN_X, SimulationMain.ORIGIN_Y + SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight()));

		modelURI = ElectricMeterModel.URI;
		simParams.put(modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
				new PlotterDescription("CompteurModel", "Time (sec)", "Bandwidth (Mbps)", SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y + 2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(), SimulationMain.getPlotterHeight()));

		this.logMessage("SupervisorComponent#execute 2");

		sp.setSimulationRunParameters(simParams);

		this.logMessage("SupervisorComponent#execute 3");

		this.logMessage("supervisor component begins simulation.");
		long start = System.currentTimeMillis();
		sp.doStandAloneSimulation(0, 5000L);
		long end = System.currentTimeMillis();
		this.logMessage("supervisor component ends simulation. " + (end - start));
	}
}
