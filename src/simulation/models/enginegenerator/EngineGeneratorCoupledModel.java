package simulation.models.enginegenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardCoupledModelReport;
import simulation.events.controller.StartEngineGeneratorEvent;
import simulation.events.controller.StopEngineGeneratorEvent;
import simulation.events.enginegenerator.EngineGeneratorProductionEvent;
import simulation.events.enginegenerator.RefillEvent;
import simulation.events.enginegenerator.StartEngineEvent;
import simulation.events.enginegenerator.StopEngineEvent;
import wattwatt.tools.URIS;

//-----------------------------------------------------------------------------
/**
* The class <code>EngineGeneratorCoupledModel</code> implements a coupled model used to gather
* together all of the model representing the engine generator in the WattWatt simulation
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
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
public class EngineGeneratorCoupledModel extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = URIS.ENGINE_GENERATOR_COUPLED_MODEL_URI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public EngineGeneratorCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#getFinalReport()
	 */
	@Override
	public SimulationReportI getFinalReport() throws Exception {
		StandardCoupledModelReport ret = new StandardCoupledModelReport(this.getURI());
		for (int i = 0; i < this.submodels.length; i++) {
			ret.addReport(this.submodels[i].getFinalReport());
		}
		return ret;
	}

	/**
	 * build the simulation architecture corresponding to this coupled model.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return the simulation architecture corresponding to this coupled model.
	 * @throws Exception <i>TO DO.</i>
	 */
	public static Architecture build() throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(EngineGeneratorModel.URI,
				AtomicHIOA_Descriptor.create(EngineGeneratorModel.class, EngineGeneratorModel.URI, TimeUnit.SECONDS,
						null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		atomicModelDescriptors.put(EngineGeneratorUserModel.URI,
				AtomicModelDescriptor.create(EngineGeneratorUserModel.class, EngineGeneratorUserModel.URI,
						TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(TicModel.URI + "-3", AtomicModelDescriptor.create(TicModel.class,
				TicModel.URI + "-3", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(EngineGeneratorModel.URI);
		submodels.add(EngineGeneratorUserModel.URI);
		submodels.add(TicModel.URI + "-3");

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		EventSource from1 = new EventSource(EngineGeneratorUserModel.URI, StartEngineEvent.class);
		EventSink[] to1 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, StartEngineEvent.class) };
		connections.put(from1, to1);
		EventSource from2 = new EventSource(EngineGeneratorUserModel.URI, StopEngineEvent.class);
		EventSink[] to2 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, StopEngineEvent.class) };
		connections.put(from2, to2);
		EventSource from3 = new EventSource(EngineGeneratorUserModel.URI, RefillEvent.class);
		EventSink[] to3 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, RefillEvent.class) };
		connections.put(from3, to3);
		
		EventSource from5 = new EventSource(TicModel.URI + "-3", TicEvent.class);
		EventSink[] to5 = new EventSink[] { new EventSink(EngineGeneratorModel.URI, TicEvent.class) };
		connections.put(from5, to5);
				
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<Class<? extends EventI>, EventSink[]>();
		imported.put(
				StartEngineGeneratorEvent.class,
				new EventSink[] {
						new EventSink(EngineGeneratorModel.URI,
								StartEngineGeneratorEvent.class)
				}) ;
			imported.put(
					StopEngineGeneratorEvent.class,
					new EventSink[] {
							new EventSink(EngineGeneratorModel.URI,
									StopEngineGeneratorEvent.class)
					}) ;
			
		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		reexported.put(
				EngineGeneratorProductionEvent.class,
				new ReexportedEvent(EngineGeneratorModel.URI,
						EngineGeneratorProductionEvent.class)) ;

		coupledModelDescriptors.put(EngineGeneratorCoupledModel.URI,
				new CoupledHIOA_Descriptor(EngineGeneratorCoupledModel.class, EngineGeneratorCoupledModel.URI, submodels,
						imported, reexported, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
						null));

		return new Architecture(EngineGeneratorCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}

}
