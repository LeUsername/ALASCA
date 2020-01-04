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
import simulation.equipements.WattWattModel;
import simulation.equipements.compteur.models.CompteurModel;
import simulation.equipements.compteur.models.events.ConsommationEvent;
import simulation.equipements.sechecheveux.models.SecheCheveuxCoupledModel;
import simulation.equipements.sechecheveux.models.SecheCheveuxModel;
import simulation.equipements.sechecheveux.models.SecheCheveuxUserModel;
import simulation.equipements.sechecheveux.models.events.SwitchModeEvent;
import simulation.equipements.sechecheveux.models.events.SwitchOffEvent;
import simulation.equipements.sechecheveux.models.events.SwitchOnEvent;

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
		return (int) (0.25 * ret);
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
		return (int) (0.2 * ret);
	}

	public static void main(String[] args) {
		try {
			// ----------------------------------------------------------------
			// Seche cheveux
			// ----------------------------------------------------------------
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			atomicModelDescriptors.put(SecheCheveuxModel.URI, AtomicHIOA_Descriptor.create(SecheCheveuxModel.class,
					SecheCheveuxModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
			atomicModelDescriptors.put(SecheCheveuxUserModel.URI,
					AtomicModelDescriptor.create(SecheCheveuxUserModel.class, SecheCheveuxUserModel.URI,
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
			submodels.add(SecheCheveuxModel.URI);
			submodels.add(SecheCheveuxUserModel.URI);
			submodels.add(TicModel.URI + "-1") ;

			Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
			EventSource from1 = new EventSource(SecheCheveuxUserModel.URI, SwitchOnEvent.class);
			EventSink[] to1 = new EventSink[] { new EventSink(SecheCheveuxModel.URI, SwitchOnEvent.class) };
			connections.put(from1, to1);
			EventSource from2 = new EventSource(SecheCheveuxUserModel.URI, SwitchOffEvent.class);
			EventSink[] to2 = new EventSink[] { new EventSink(SecheCheveuxModel.URI, SwitchOffEvent.class) };
			connections.put(from2, to2);
			EventSource from3 = new EventSource(SecheCheveuxUserModel.URI, SwitchModeEvent.class);
			EventSink[] to3 = new EventSink[] { new EventSink(SecheCheveuxModel.URI, SwitchModeEvent.class) };
			connections.put(from3, to3);
			EventSource from4 =
					new EventSource(TicModel.URI + "-1",
									TicEvent.class) ;
			EventSink[] to4 =
					new EventSink[] {
						new EventSink(SecheCheveuxModel.URI,
									  TicEvent.class)} ;
			connections.put(from4, to4);
			
			Map<Class<? extends EventI>,ReexportedEvent> reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
			reexported.put(
					ConsommationEvent.class,
					new ReexportedEvent(SecheCheveuxModel.URI,
							ConsommationEvent.class)) ;

			coupledModelDescriptors.put(SecheCheveuxCoupledModel.URI,
					new CoupledHIOA_Descriptor(SecheCheveuxCoupledModel.class, SecheCheveuxCoupledModel.URI, submodels,
							null, reexported, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
							null));

			// ----------------------------------------------------------------
			// Compteur
			// ----------------------------------------------------------------
			
			atomicModelDescriptors.put(CompteurModel.URI,
					AtomicHIOA_Descriptor.create(CompteurModel.class,
					CompteurModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

			// ----------------------------------------------------------------
			// Assemblage
			// ----------------------------------------------------------------
			
			Set<String> submodels2 = new HashSet<String>();
			submodels2.add(SecheCheveuxCoupledModel.URI);
			submodels2.add(CompteurModel.URI);

			Map<EventSource, EventSink[]> connections2 = new HashMap<EventSource, EventSink[]>();

			EventSource from21 = new EventSource(SecheCheveuxCoupledModel.URI, ConsommationEvent.class);
			EventSink[] to21 = new EventSink[] {
					new EventSink(CompteurModel.URI, ConsommationEvent.class) };
			connections2.put(from21, to21);
			
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
			
			Map<String, Object> simParams = new HashMap<String, Object>() ;
			
			SimulationEngine se = architecture.constructSimulator() ;
			se.setDebugLevel(0);
			
			String modelURI = TicModel.URI  + "-1" ;
			simParams.put(modelURI + ":" + TicModel.DELAY_PARAMETER_NAME,
						  new Duration(10.0, TimeUnit.SECONDS)) ;
			
			modelURI = SecheCheveuxCoupledModel.URI ;
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

			modelURI = CompteurModel.URI ;
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
			
			
			se.setSimulationRunParameters(simParams) ;
			
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L ;
			long start = System.currentTimeMillis() ;
			se.doStandAloneSimulation(0.0, 5000.0) ;
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
