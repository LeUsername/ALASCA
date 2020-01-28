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
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
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
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.electricmeter.ConsumptionEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEvent;
import simulation.events.enginegenerator.StopEvent;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.FridgeConsumptionEvent;
import simulation.events.fridge.OpenEvent;
import simulation.events.fridge.ResumeEvent;
import simulation.events.fridge.SuspendEvent;
import simulation.events.hairdryer.HairDryerConsumptionEvent;
import simulation.events.hairdryer.SwitchModeEvent;
import simulation.events.hairdryer.SwitchOffEvent;
import simulation.events.hairdryer.SwitchOnEvent;
import simulation.events.washingmachine.EcoModeEvent;
import simulation.events.washingmachine.PremiumModeEvent;
import simulation.events.washingmachine.StartAtEvent;
import simulation.events.washingmachine.WashingMachineConsumptionEvent;
import simulation.events.windturbine.WindReadingEvent;
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
import simulation.tools.enginegenerator.EngineGeneratorUserBehaviour;
import simulation.tools.hairdryer.HairDryerUserBehaviour;
import simulation.tools.washingmachine.WashingMachineUserBehaviour;
import wattwatt.tools.washingmachine.WashingMachineSetting;

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
			// Hair dryer
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

			Set<String> submodels1 = new HashSet<String>();
			submodels1.add(HairDryerModel.URI);
			submodels1.add(HairDryerUserModel.URI);
			submodels1.add(TicModel.URI + "-1") ;

			Map<EventSource, EventSink[]> connections1 = new HashMap<EventSource, EventSink[]>();
			EventSource from11 = new EventSource(HairDryerUserModel.URI, SwitchOnEvent.class);
			EventSink[] to11 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchOnEvent.class) };
			connections1.put(from11, to11);
			EventSource from12 = new EventSource(HairDryerUserModel.URI, SwitchOffEvent.class);
			EventSink[] to12 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchOffEvent.class) };
			connections1.put(from12, to12);
			EventSource from13 = new EventSource(HairDryerUserModel.URI, SwitchModeEvent.class);
			EventSink[] to13 = new EventSink[] { new EventSink(HairDryerModel.URI, SwitchModeEvent.class) };
			connections1.put(from13, to13);
			EventSource from14 =
					new EventSource(TicModel.URI + "-1",
									TicEvent.class) ;
			EventSink[] to14 =
					new EventSink[] {
						new EventSink(HairDryerModel.URI,
									  TicEvent.class)} ;
			connections1.put(from14, to14);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported1.put(
					HairDryerConsumptionEvent.class,
					new ReexportedEvent(HairDryerModel.URI,
							HairDryerConsumptionEvent.class)) ;

			coupledModelDescriptors.put(HairDryerCoupledModel.URI,
					new CoupledHIOA_Descriptor(HairDryerCoupledModel.class, HairDryerCoupledModel.URI, submodels1,
							null, reexported1, connections1, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));
			
			// ----------------------------------------------------------------
			// Engine generator
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(EngineGeneratorModel.URI,
					AtomicHIOA_Descriptor.create(EngineGeneratorModel.class, EngineGeneratorModel.URI, TimeUnit.SECONDS,
							null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(EngineGeneratorUserModel.URI,
					AtomicModelDescriptor.create(EngineGeneratorUserModel.class, EngineGeneratorUserModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(TicModel.URI + "-2", AtomicModelDescriptor.create(TicModel.class,
					TicModel.URI + "-2", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			Set<String> submodels3 = new HashSet<String>();
			submodels3.add(EngineGeneratorModel.URI);
			submodels3.add(EngineGeneratorUserModel.URI);
			submodels3.add(TicModel.URI + "-2");
			
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
			EventSource from34 =
					new EventSource(TicModel.URI + "-2",
									TicEvent.class) ;
			EventSink[] to34 =
					new EventSink[] {
						new EventSink(EngineGeneratorModel.URI,
									  TicEvent.class)} ;
			connections3.put(from34, to34);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported2 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported2.put(
					EngineGeneratorProductionEvent.class,
					new ReexportedEvent(EngineGeneratorModel.URI,
							EngineGeneratorProductionEvent.class)) ;
			
			Map<Class<? extends EventI>,EventSink[]> imported2 =
					new HashMap<Class<? extends EventI>,EventSink[]>() ;
			imported2.put(
				StartEngineGeneratorEvent.class,
				new EventSink[] {
						new EventSink(EngineGeneratorModel.URI,
								StartEngineGeneratorEvent.class)
				}) ;
			imported2.put(
					StopEngineGeneratorEvent.class,
					new EventSink[] {
							new EventSink(EngineGeneratorModel.URI,
									StopEngineGeneratorEvent.class)
					}) ;
			
			coupledModelDescriptors.put(EngineGeneratorCoupledModel.URI,
					new CoupledHIOA_Descriptor(EngineGeneratorCoupledModel.class, EngineGeneratorCoupledModel.URI, submodels3,
							imported2, reexported2, connections3, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));
			
			// ----------------------------------------------------------------
			// Wind turbine
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(WindTurbineModel.URI, AtomicHIOA_Descriptor.create(WindTurbineModel.class,
					WindTurbineModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(WindTurbineSensorModel.URI, AtomicModelDescriptor.create(WindTurbineSensorModel.class,
					WindTurbineSensorModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(TicModel.URI + "-3", AtomicModelDescriptor.create(TicModel.class,
					TicModel.URI + "-3", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			Set<String> submodels5 = new HashSet<String>();
			submodels5.add(WindTurbineModel.URI);
			submodels5.add(WindTurbineSensorModel.URI);
			submodels5.add(TicModel.URI + "-3");

			Map<EventSource, EventSink[]> connections5 = new HashMap<EventSource, EventSink[]>();
			EventSource from51 = new EventSource(WindTurbineSensorModel.URI, WindReadingEvent.class);
			EventSink[] to51 = new EventSink[] { new EventSink(WindTurbineModel.URI, WindReadingEvent.class) };
			connections5.put(from51, to51);
			EventSource from52 = new EventSource(WindTurbineSensorModel.URI, simulation.events.windturbine.SwitchOnEvent.class);
			EventSink[] to52 = new EventSink[] { new EventSink(WindTurbineModel.URI, simulation.events.windturbine.SwitchOnEvent.class) };
			connections5.put(from52, to52);
			EventSource from53 = new EventSource(WindTurbineSensorModel.URI, simulation.events.windturbine.SwitchOffEvent.class);
			EventSink[] to53 = new EventSink[] { new EventSink(WindTurbineModel.URI, simulation.events.windturbine.SwitchOffEvent.class) };
			connections5.put(from53, to53);
			EventSource from54 =
					new EventSource(TicModel.URI + "-3",
									TicEvent.class) ;
			EventSink[] to54 =
					new EventSink[] {
						new EventSink(WindTurbineModel.URI,
									  TicEvent.class)} ;
			connections5.put(from54, to54);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported5 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported5.put(
					WindTurbineProductionEvent.class,
					new ReexportedEvent(WindTurbineModel.URI,
							WindTurbineProductionEvent.class)) ;

			coupledModelDescriptors.put(WindTurbineCoupledModel.URI,
					new CoupledHIOA_Descriptor(WindTurbineCoupledModel.class, WindTurbineCoupledModel.URI, submodels5, null, reexported5,
							connections5, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null, null));

			
			// ----------------------------------------------------------------
			// Washing machine
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(WashingMachineModel.URI,
					AtomicHIOA_Descriptor.create(WashingMachineModel.class, WashingMachineModel.URI, TimeUnit.SECONDS,
							null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(WashingMachineUserModel.URI,
					AtomicModelDescriptor.create(WashingMachineUserModel.class, WashingMachineUserModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TicModel.URI + "-4", AtomicModelDescriptor.create(TicModel.class,
					TicModel.URI + "-4", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			Set<String> submodels6 = new HashSet<String>();
			submodels6.add(WashingMachineModel.URI);
			submodels6.add(WashingMachineUserModel.URI);
			submodels6.add(TicModel.URI + "-4");

			Map<EventSource, EventSink[]> connections6 = new HashMap<EventSource, EventSink[]>();
			EventSource from61 = new EventSource(WashingMachineUserModel.URI, StartAtEvent.class);
			EventSink[] to61 = new EventSink[] { new EventSink(WashingMachineModel.URI, StartAtEvent.class) };
			connections6.put(from61, to61);
			EventSource from62 = new EventSource(WashingMachineUserModel.URI, EcoModeEvent.class);
			EventSink[] to62 = new EventSink[] { new EventSink(WashingMachineModel.URI, EcoModeEvent.class) };
			connections6.put(from62, to62);
			EventSource from63 = new EventSource(WashingMachineUserModel.URI, PremiumModeEvent.class);
			EventSink[] to63 = new EventSink[] { new EventSink(WashingMachineModel.URI, PremiumModeEvent.class) };
			connections6.put(from63, to63);
			
			EventSource from65 = new EventSource(TicModel.URI + "-4", TicEvent.class);
			EventSink[] to65 = new EventSink[] { new EventSink(WashingMachineModel.URI, TicEvent.class) };
			connections6.put(from65, to65);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported6 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported6.put(
					WashingMachineConsumptionEvent.class,
					new ReexportedEvent(WashingMachineModel.URI,
							WashingMachineConsumptionEvent.class)) ;

			
//			Map<Class<? extends EventI>,EventSink[]> imported6 =
//					new HashMap<Class<? extends EventI>,EventSink[]>() ;
//			imported6.put(
//				StartEngineGenerator.class,
//				new EventSink[] {
//						new EventSink(EngineGeneratorModel.URI,
//								StartEngineGenerator.class)
//				}) ;


			coupledModelDescriptors.put(WashingMachineCoupledModel.URI,
					new CoupledHIOA_Descriptor(WashingMachineCoupledModel.class, WashingMachineCoupledModel.URI, submodels6,
							null, reexported6, connections6, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));
			
			
			
			// ----------------------------------------------------------------
			// Fridge
			// ----------------------------------------------------------------
						
			atomicModelDescriptors.put(FridgeModel.URI,
					AtomicHIOA_Descriptor.create(FridgeModel.class, FridgeModel.URI, TimeUnit.SECONDS,
							null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			atomicModelDescriptors.put(FridgeUserModel.URI,
					AtomicModelDescriptor.create(FridgeUserModel.class, FridgeUserModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(FridgeSensorModel.URI,
					AtomicHIOA_Descriptor.create(FridgeSensorModel.class, FridgeSensorModel.URI,
							TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			atomicModelDescriptors.put(TicModel.URI + "-5", AtomicModelDescriptor.create(TicModel.class,
					TicModel.URI + "-5", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			Set<String> submodels7 = new HashSet<String>();
			submodels7.add(FridgeModel.URI);
			submodels7.add(FridgeUserModel.URI);
			submodels7.add(FridgeSensorModel.URI);
			submodels7.add(TicModel.URI + "-5");

			Map<EventSource, EventSink[]> connections7 = new HashMap<EventSource, EventSink[]>();
			EventSource from71 = new EventSource(FridgeSensorModel.URI, ResumeEvent.class);
			EventSink[] to71 = new EventSink[] { new EventSink(FridgeModel.URI, ResumeEvent.class) };
			connections7.put(from71, to71);
			EventSource from72 = new EventSource(FridgeSensorModel.URI, SuspendEvent.class);
			EventSink[] to72 = new EventSink[] { new EventSink(FridgeModel.URI, SuspendEvent.class) };
			connections7.put(from72, to72);
			EventSource from73 = new EventSource(FridgeUserModel.URI, OpenEvent.class);
			EventSink[] to73 = new EventSink[] { new EventSink(FridgeModel.URI, OpenEvent.class) };
			connections7.put(from73, to73);
			EventSource from74 = new EventSource(FridgeUserModel.URI, CloseEvent.class);
			EventSink[] to74 = new EventSink[] { new EventSink(FridgeModel.URI, CloseEvent.class) };
			connections7.put(from74, to74);
			
			EventSource from75 = new EventSource(TicModel.URI + "-5", TicEvent.class);
			EventSink[] to75 = new EventSink[] { new EventSink(FridgeModel.URI, TicEvent.class) };
			connections7.put(from75, to75);
			EventSource from76 = new EventSource(TicModel.URI + "-5", TicEvent.class);
			EventSink[] to76 = new EventSink[] { new EventSink(FridgeSensorModel.URI, TicEvent.class) };
			connections7.put(from76, to76);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported7 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported7.put(
					FridgeConsumptionEvent.class,
					new ReexportedEvent(FridgeModel.URI,
							FridgeConsumptionEvent.class)) ;

			
//						Map<Class<? extends EventI>,EventSink[]> imported6 =
//								new HashMap<Class<? extends EventI>,EventSink[]>() ;
//						imported6.put(
//							StartEngineGenerator.class,
//							new EventSink[] {
//									new EventSink(EngineGeneratorModel.URI,
//											StartEngineGenerator.class)
//							}) ;

			Map<VariableSource, VariableSink[]> bindings7 = new HashMap<VariableSource, VariableSink[]>();
			VariableSource source7 = new VariableSource("temperature", Double.class, FridgeModel.URI);
			VariableSink[] sinks7 = new VariableSink[] {
					new VariableSink("temperature", Double.class, FridgeSensorModel.URI) };
			bindings7.put(source7, sinks7);
			

			coupledModelDescriptors.put(FridgeCoupledModel.URI,
					new CoupledHIOA_Descriptor(FridgeCoupledModel.class, FridgeCoupledModel.URI, submodels7,
							null, reexported7, connections7, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							bindings7));
						
			// ----------------------------------------------------------------
			// Controller
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(ControllerModel.URI,
					AtomicModelDescriptor.create(ControllerModel.class, 
							ControllerModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Electric meter
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(ElectricMeterModel.URI,
					AtomicHIOA_Descriptor.create(ElectricMeterModel.class,
							ElectricMeterModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			
			// ----------------------------------------------------------------
			// Full architecture
			// ----------------------------------------------------------------
			
			Set<String> submodels = new HashSet<String>();
			submodels.add(HairDryerCoupledModel.URI);
			submodels.add(EngineGeneratorCoupledModel.URI);
			submodels.add(WindTurbineCoupledModel.URI);
			submodels.add(ElectricMeterModel.URI);
			submodels.add(ControllerModel.URI);
			submodels.add(WashingMachineCoupledModel.URI);
			submodels.add(FridgeCoupledModel.URI);

			Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

			EventSource from1 = new EventSource(HairDryerCoupledModel.URI, HairDryerConsumptionEvent.class);
			EventSink[] to1 = new EventSink[] {
					new EventSink(ElectricMeterModel.URI, HairDryerConsumptionEvent.class) };
			connections.put(from1, to1);
			EventSource from2 = new EventSource(EngineGeneratorCoupledModel.URI, EngineGeneratorProductionEvent.class);
			EventSink[] to2 = new EventSink[] {
					new EventSink(ControllerModel.URI, EngineGeneratorProductionEvent.class) };
			connections.put(from2, to2);
			EventSource from3 = new EventSource(WindTurbineCoupledModel.URI, WindTurbineProductionEvent.class);
			EventSink[] to3 = new EventSink[] {
					new EventSink(ControllerModel.URI, WindTurbineProductionEvent.class) };
			connections.put(from3, to3);
			EventSource from4 = new EventSource(ElectricMeterModel.URI, ConsumptionEvent.class);
			EventSink[] to4 = new EventSink[] {
					new EventSink(ControllerModel.URI, ConsumptionEvent.class) };
			connections.put(from4, to4);
			EventSource from5 = new EventSource(ControllerModel.URI, StartEngineGeneratorEvent.class);
			EventSink[] to5 = new EventSink[] {
					new EventSink(EngineGeneratorCoupledModel.URI, StartEngineGeneratorEvent.class) };
			connections.put(from5, to5);
			EventSource from6 = new EventSource(ControllerModel.URI, StopEngineGeneratorEvent.class);
			EventSink[] to6 = new EventSink[] {
					new EventSink(EngineGeneratorCoupledModel.URI, StopEngineGeneratorEvent.class) };
			connections.put(from6, to6);
			EventSource from7 = new EventSource(WashingMachineCoupledModel.URI, WashingMachineConsumptionEvent.class);
			EventSink[] to7 = new EventSink[] {
					new EventSink(ElectricMeterModel.URI, WashingMachineConsumptionEvent.class) };
			connections.put(from7, to7);
			EventSource from8 = new EventSource(FridgeCoupledModel.URI, FridgeConsumptionEvent.class);
			EventSink[] to8 = new EventSink[] {
					new EventSink(ElectricMeterModel.URI, FridgeConsumptionEvent.class) };
			connections.put(from8, to8);
			
			
			coupledModelDescriptors.put(
					WattWattModel.URI,
					new CoupledModelDescriptor(
							WattWattModel.class,
							WattWattModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							SimulationEngineCreationMode.COORDINATION_ENGINE)) ;
			
			ArchitectureI architecture =
					new Architecture(
							WattWattModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS) ;
			
			// ----------------------------------------------------------------
			// Simulation parameters
			// ----------------------------------------------------------------
			
			Map<String, Object> simParams = new HashMap<String, Object>() ;

			String modelURI = TicModel.URI  + "-1" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			modelURI = TicModel.URI  + "-2" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			modelURI = TicModel.URI  + "-3" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			modelURI = TicModel.URI  + "-4" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			
			
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
			
			
			simParams.put(
					WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INITIAL_DELAY,
					10.0) ;
			simParams.put(
					WindTurbineSensorModel.URI + ":" + WindTurbineSensorModel.INTERDAY_DELAY,
					100.0) ;
			
			
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
			
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MTBI, 200.0) ;
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MID, 10.0) ;
			
			
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MTBI, 200.0) ;
			simParams.put(FridgeUserModel.URI + ":" + FridgeUserModel.MID, 10.0) ;
			simParams.put(
					FridgeModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 5.0) ;
			simParams.put(
					FridgeModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
			simParams.put(FridgeModel.URI + ":" + FridgeModel.INITIAL_TEMP, 3.0) ;
			simParams.put(
					FridgeSensorModel.URI + ":" + FridgeModel.MAX_TEMPERATURE, 2.5) ;
			simParams.put(
					FridgeSensorModel.URI + ":" + FridgeModel.MIN_TEMPERATURE, 1.0) ;
			
			// ----------------------------------------------------------------
			// Plotters parameters
			// ----------------------------------------------------------------
			
			simParams.put(
					HairDryerModel.URI + ":" + HairDryerModel.INTENSITY_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"HairDryerModel",
							"Time (sec)",
							"Intensity (Watt)",
							WattWattMain.ORIGIN_X +
							WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y +
								WattWattMain.getPlotterHeight(),
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
					EngineGeneratorModel.URI + ":" + EngineGeneratorModel.QUANTITY_SERIES + ":"
							+ PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription("GroupeElectrogeneModel", "Time (min)", "Volume (Liters)", WattWattMain.ORIGIN_X,
							WattWattMain.ORIGIN_Y + 2 * WattWattMain.getPlotterHeight(), WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight()));
			
			
			simParams.put(
					WindTurbineModel.URI + ":" + WindTurbineModel.PRODUCTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Wind turbine model",
							"Time (min)",
							"Production (Watt)",
							WattWattMain.ORIGIN_X +
							WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			
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
			
			
			simParams.put(
					ElectricMeterModel.URI + ":" + ElectricMeterModel.CONSUMPTION_SERIES + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"Electric meter model",
							"Time (sec)",
							"Consumption (Watt)",
							WattWattMain.ORIGIN_X +
							WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y +
								2*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			
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
					FridgeModel.URI + ":" + FridgeModel.TEMPERATURE + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurModel",
							"Time (sec)",
							"Temperature (�C)",
							WattWattMain.ORIGIN_X + 4 * WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y +
							WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			simParams.put(
					FridgeModel.URI + ":"  + FridgeModel.INTENSITY + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurModel",
							"Time (min)",
							"Consumption (W)",
							WattWattMain.ORIGIN_X + 4 * WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y +
							2*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;

			simParams.put(
					FridgeSensorModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurSensorModel",
							"Time (min)",
							"Temperature (Celcius)",
							WattWattMain.ORIGIN_X + 4 * WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y +
							3*WattWattMain.getPlotterHeight(),
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
			simParams.put(
					FridgeUserModel.URI + ":" + PlotterDescription.PLOTTING_PARAM_NAME,
					new PlotterDescription(
							"RefrigerateurUserModel",
							"Time (min)",
							"Opened / Closed",
							WattWattMain.ORIGIN_X +  4 * WattWattMain.getPlotterWidth(),
							WattWattMain.ORIGIN_Y,
							WattWattMain.getPlotterWidth(),
							WattWattMain.getPlotterHeight())) ;
			
	
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
