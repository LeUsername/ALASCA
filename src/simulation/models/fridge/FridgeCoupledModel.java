package simulation.models.fridge;

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
import simulation.events.controller.ResumeFridgeEvent;
import simulation.events.controller.SuspendFridgeEvent;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.FridgeConsumptionEvent;
import simulation.events.fridge.OpenEvent;
import simulation.events.fridge.ResumeEvent;
import simulation.events.fridge.SuspendEvent;
import wattwatt.tools.URIS;

//-----------------------------------------------------------------------------
/**
* The class <code>FridgeCoupledModel</code> implements a coupled model used to gather
* together all of the model representing the fridge in the WattWatt simulation
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
public class FridgeCoupledModel extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = URIS.FRIDGE_COUPLED_MODEL_URI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public FridgeCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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

		atomicModelDescriptors.put(FridgeUserModel.URI,
				AtomicModelDescriptor.create(FridgeUserModel.class, FridgeUserModel.URI, TimeUnit.SECONDS,
						null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(FridgeModel.URI, AtomicHIOA_Descriptor.create(FridgeModel.class,
				FridgeModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(FridgeSensorModel.URI,
				AtomicHIOA_Descriptor.create(FridgeSensorModel.class, FridgeSensorModel.URI,
						TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(TicModel.URI + "-2", AtomicModelDescriptor.create(TicModel.class,
				TicModel.URI + "-2", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(FridgeUserModel.URI);
		submodels.add(FridgeModel.URI);
		submodels.add(TicModel.URI + "-2");
		submodels.add(FridgeSensorModel.URI);

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		EventSource from1 = new EventSource(FridgeUserModel.URI, OpenEvent.class);
		EventSink[] to1 = new EventSink[] { new EventSink(FridgeModel.URI, OpenEvent.class) };
		connections.put(from1, to1);
		EventSource from2 = new EventSource(FridgeUserModel.URI, CloseEvent.class);
		EventSink[] to2 = new EventSink[] { new EventSink(FridgeModel.URI, CloseEvent.class) };
		connections.put(from2, to2);
		EventSource from3 = new EventSource(FridgeSensorModel.URI, ResumeEvent.class);
		EventSink[] to3 = new EventSink[] { new EventSink(FridgeModel.URI, ResumeEvent.class) };
		connections.put(from3, to3);
		EventSource from4 = new EventSource(FridgeSensorModel.URI, SuspendEvent.class);
		EventSink[] to4 = new EventSink[] { new EventSink(FridgeModel.URI, SuspendEvent.class) };
		connections.put(from4, to4);
		EventSource from5 = new EventSource(TicModel.URI + "-2", TicEvent.class);
		EventSink[] to5 = new EventSink[] { new EventSink(FridgeSensorModel.URI, TicEvent.class) };
		connections.put(from5, to5);
		EventSource from6 = new EventSource(TicModel.URI + "-2", TicEvent.class);
		EventSink[] to6 = new EventSink[] { new EventSink(FridgeModel.URI, TicEvent.class) };
		connections.put(from6, to6);

		Map<Class<? extends EventI>,ReexportedEvent> reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>() ;
		reexported.put(
				FridgeConsumptionEvent.class,
				new ReexportedEvent(FridgeModel.URI,
						FridgeConsumptionEvent.class)) ;
		
		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<Class<? extends EventI>, EventSink[]>();
		imported.put(
				SuspendFridgeEvent.class,
				new EventSink[] {
						new EventSink(FridgeModel.URI,
								SuspendFridgeEvent.class)
				}) ;
		imported.put(
				ResumeFridgeEvent.class,
				new EventSink[] {
						new EventSink(FridgeModel.URI,
								ResumeFridgeEvent.class)
				}) ;
		
		Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
		VariableSource source = new VariableSource("temperature", Double.class, FridgeModel.URI);
		VariableSink[] sinks = new VariableSink[] {
				new VariableSink("temperature", Double.class, FridgeSensorModel.URI) };
		bindings.put(source, sinks);

		coupledModelDescriptors.put(FridgeCoupledModel.URI,
				new CoupledHIOA_Descriptor(FridgeCoupledModel.class, FridgeCoupledModel.URI, submodels,
						imported, reexported, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null,
						null, bindings));

		return new Architecture(FridgeCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}
