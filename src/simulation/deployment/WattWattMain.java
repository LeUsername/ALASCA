package simulation.deployment;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.SimulationMain;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.utils.PlotterDescription;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEvent;
import simulation.events.enginegenerator.StopEvent;
import simulation.events.hairdryer.SwitchModeEvent;
import simulation.events.hairdryer.SwitchOffEvent;
import simulation.events.hairdryer.SwitchOnEvent;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.enginegenerator.EngineGeneratorModel;
import simulation.models.enginegenerator.EngineGeneratorUserModel;
import simulation.models.sechecheveux.HairDryerCoupledModel;
import simulation.models.sechecheveux.HairDryerModel;
import simulation.models.sechecheveux.HairDryerUserModel;
import simulation.models.wattwatt.WattWattModel;
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;

public class WattWattMain {
	public static final String MOLENE_MODEL_URI = "WattWattModel";
	public static int ORIGIN_X = 100;
	public static int ORIGIN_Y = 0;

	public static int getPlotterWidth() {
		int ret = Integer.MAX_VALUE;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			int width = dm.getWidth();
			if (width < ret) {
				ret = width;
			}
		}
		return (int) (0.24 * ret);
	}

	public static int getPlotterHeight() {
		int ret = Integer.MAX_VALUE;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			int height = dm.getHeight();
			if (height < ret) {
				ret = height;
			}
		}
		return (int) (0.18 * ret);
	}

	public static void main(String[] args) {
		try {
			// ----------------------------------------------------------------
			// Seche cheveux
			// ----------------------------------------------------------------
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			atomicModelDescriptors.put(HairDryerModel.URI, AtomicHIOA_Descriptor.create(HairDryerModel.class,
					HairDryerModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(HairDryerUserModel.URI,
					AtomicModelDescriptor.create(HairDryerUserModel.class, HairDryerUserModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(
					TicModel.URI + "-1",
					AtomicModelDescriptor.create(
							TicModel.class,
							TicModel.URI + "-1",
							TimeUnit.SECONDS,
							null,
							SimulationEngineCreationMode.ATOMIC_ENGINE)) ;

			Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(HairDryerModel.URI);
			submodels.add(HairDryerUserModel.URI);
			submodels.add(TicModel.URI + "-1") ;

			Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
			EventSource from1 = new EventSource(HairDryerUserModel.URI, SwitchOnEvent.class);
			EventSink[] to1 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchOnEvent.class) };
			connections.put(from1, to1);
			EventSource from2 = new EventSource(HairDryerUserModel.URI, SwitchOffEvent.class);
			EventSink[] to2 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchOffEvent.class) };
			connections.put(from2, to2);
			EventSource from3 = new EventSource(HairDryerUserModel.URI, SwitchModeEvent.class);
			EventSink[] to3 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchModeEvent.class) };
			connections.put(from3, to3);
			EventSource from4 =
					new EventSource(TicModel.URI + "-1",
									TicEvent.class) ;
			EventSink[] to4 =
					new EventSink[] {
						new EventSink(HairDryerModel.URI,
									  TicEvent.class)} ;
			connections.put(from4, to4);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported.put(
					ConsumptionEvent.class,
					new ReexportedEvent(HairDryerModel.URI,
							ConsumptionEvent.class)) ;

			coupledModelDescriptors.put(HairDryerCoupledModel.URI,
					new CoupledHIOA_Descriptor(HairDryerCoupledModel.class, HairDryerCoupledModel.URI, submodels,
							null, reexported, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));
			
			// ----------------------------------------------------------------
			// GroupeElectrogene
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(EngineGeneratorModel.URI,
					AtomicHIOA_Descriptor.create(EngineGeneratorModel.class, EngineGeneratorModel.URI, TimeUnit.SECONDS,
							null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(EngineGeneratorUserModel.URI,
					AtomicModelDescriptor.create(EngineGeneratorUserModel.class, EngineGeneratorUserModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(TicModel.URI + "-3", AtomicModelDescriptor.create(TicModel.class,
					TicModel.URI + "-3", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels3 = new HashSet<String>();
			submodels3.add(EngineGeneratorModel.URI);
			submodels3.add(EngineGeneratorUserModel.URI);
			submodels3.add(TicModel.URI + "-3");
			
			Map<EventSource, EventSink[]> connections3 = new HashMap<EventSource, EventSink[]>();
			EventSource from31 = new EventSource(EngineGeneratorUserModel.URI, StartEvent.class);
			EventSink[] to31 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, StartEvent.class) };
			connections3.put(from31, to31);
			EventSource from32 = new EventSource(EngineGeneratorUserModel.URI, StopEvent.class);
			EventSink[] to32 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, StopEvent.class) };
			connections3.put(from32, to32);
			EventSource from33 = new EventSource(EngineGeneratorUserModel.URI, RefillEvent.class);
			EventSink[] to33 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, RefillEvent.class) };
			connections3.put(from33, to33);
			
			EventSource from5 = new EventSource(TicModel.URI + "-3", TicEvent.class);
			EventSink[] to5 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, TicEvent.class) };
			connections3.put(from5, to5);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported2 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported2.put(
					EngineGeneratorProductionEvent.class,
					new ReexportedEvent(EngineGeneratorModel.URI,
							EngineGeneratorProductionEvent.class)) ;
			
			coupledModelDescriptors.put(EngineGeneratorCoupledModel.URI,
					new CoupledHIOA_Descriptor(EngineGeneratorCoupledModel.class, EngineGeneratorCoupledModel.URI, submodels3,
							null, reexported2, connections3, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));
			
//			// ----------------------------------------------------------------
//			// Lave linge
//			// ----------------------------------------------------------------
//			
//			atomicModelDescriptors.put(LaveLingeModel.URI,
//					AtomicHIOA_Descriptor.create(LaveLingeModel.class, LaveLingeModel.URI, TimeUnit.SECONDS,
//							null, SimulationEngineCreationMode.ATOMIC_ENGINE));
//
//			atomicModelDescriptors.put(LaveLingeUserModel.URI,
//					AtomicModelDescriptor.create(LaveLingeUserModel.class, LaveLingeUserModel.URI,
//							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
//			
//			atomicModelDescriptors.put(TicModel.URI + "-3", AtomicModelDescriptor.create(TicModel.class,
//					TicModel.URI + "-3", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
//
//			Set<String> submodels = new HashSet<String>();
//			submodels.add(LaveLingeModel.URI);
//			submodels.add(LaveLingeUserModel.URI);
//			submodels.add(TicModel.URI + "-3");
//
//			Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
//			EventSource from1 = new EventSource(LaveLingeUserModel.URI, StartAtEvent.class);
//			EventSink[] to1 = new EventSink[] { new EventSink(LaveLingeModel.URI, StartAtEvent.class) };
//			connections.put(from1, to1);
//			EventSource from2 = new EventSource(LaveLingeUserModel.URI, EcoLavageEvent.class);
//			EventSink[] to2 = new EventSink[] { new EventSink(LaveLingeModel.URI, EcoLavageEvent.class) };
//			connections.put(from2, to2);
//			EventSource from3 = new EventSource(LaveLingeUserModel.URI, PremiumLavageEvent.class);
//			EventSink[] to3 = new EventSink[] { new EventSink(LaveLingeModel.URI, PremiumLavageEvent.class) };
//			connections.put(from3, to3);
//			
//			EventSource from5 = new EventSource(TicModel.URI + "-3", TicEvent.class);
//			EventSink[] to5 = new EventSink[] { new EventSink(LaveLingeModel.URI, TicEvent.class) };
//			connections.put(from5, to5);
//			
//			/*Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
//			VariableSource source = new VariableSource("fuelCapacity", Double.class, GroupeElectrogeneModel.URI);
//			VariableSink[] sinks = new VariableSink[] {
//					new VariableSink("fuelCapacity", Double.class, GroupeElectrogeneUserModel.URI) };
//			bindings.put(source, sinks);*/
//
//			coupledModelDescriptors.put(LaveLingeCoupledModel.URI,
//					new CoupledHIOA_Descriptor(LaveLingeCoupledModel.class, LaveLingeCoupledModel.URI, submodels,
//							null, null, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
//							null));
			
			// ----------------------------------------------------------------
			// Compteur
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(ElectricMeterModel.URI,
					AtomicHIOA_Descriptor.create(ElectricMeterModel.class,
					ElectricMeterModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			// ----------------------------------------------------------------
			// Assemblage
			// ----------------------------------------------------------------
			
			Set<String> submodels2 = new HashSet<String>();
			submodels2.add(HairDryerCoupledModel.URI);
			submodels2.add(EngineGeneratorCoupledModel.URI);
			submodels2.add(ElectricMeterModel.URI);

			Map<EventSource, EventSink[]> connections2 = new HashMap<EventSource, EventSink[]>();

			EventSource from21 = new EventSource(HairDryerCoupledModel.URI, ConsumptionEvent.class);
			EventSink[] to21 = new EventSink[] {
					new EventSink(ElectricMeterModel.URI, ConsumptionEvent.class) };
			connections2.put(from21, to21);
			
			EventSource from22 = new EventSource(EngineGeneratorCoupledModel.URI, EngineGeneratorProductionEvent.class);
			EventSink[] to22 = new EventSink[] {
					new EventSink(ElectricMeterModel.URI, EngineGeneratorProductionEvent.class) };
			connections2.put(from22, to22);
			
			coupledModelDescriptors.put(
					WattWattModel.URI,
					new CoupledModelDescriptor(
							WattWattModel.class,
							WattWattModel.URI,
							submodels2,
							null,
							null,
							connections2,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE)) ;
			
			ArchitectureI architecture =
					new Architecture(
							WattWattModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
			
			// ----------------------------------------------------------------
			// Parametres de simulation
			// ----------------------------------------------------------------
			
			Map<String, Object> simParams = new HashMap<String, Object>() ;

			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTBU,
					EngineGeneratorUserBehaviour.MEAN_TIME_BETWEEN_USAGES);
			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTW,
					EngineGeneratorUserBehaviour.MEAN_TIME_WORKING);
			simParams.put(EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.MTR,
					EngineGeneratorUserBehaviour.MEAN_TIME_AT_REFILL);
			
			simParams.put(
					EngineGeneratorUserModel.URI + ":" + EngineGeneratorUserModel.ACTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneUserModel", "Time (sec)", "User actions",
							WattWattMain.ORIGIN_X, WattWattMain.ORIGIN_Y, WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));

			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.PRODUCTION + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Watt", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			simParams.put(
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (sec)", "Liter", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			
			String modelURI = TicModel.URI  + "-1" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			
			modelURI = TicModel.URI  + "-3" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;

			
			modelURI = HairDryerCoupledModel.URI ;
			simParams.put(
					modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"SecheCheveuxCoupledModel",
							"Time (sec)",
							"Bandwidth (Mbps)",
							SimulationMain.ORIGIN_X,
							SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight())) ;

			modelURI = ElectricMeterModel.URI ;
			simParams.put(
					modelURI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"CompteurModel",
							"Time (sec)",
							"Bandwidth (Mbps)",
							SimulationMain.ORIGIN_X,
							SimulationMain.ORIGIN_Y +
								2*SimulationMain.getPlotterHeight(),
							SimulationMain.getPlotterWidth(),
							SimulationMain.getPlotterHeight())) ;
			
			

			SimulationEngine se = architecture.constructSimulator() ;
			se.setDebugLevel(0);
			
			se.setSimulationRunParameters(simParams) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 10000.0) ;
			long end = System.currentTimeMillis() ;
			System.out.println(se.getFinalReport()) ;
			System.out.println("Simulation ends. " + (end - start)) ;
			Thread.sleep(1000000L);
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
