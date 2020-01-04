package simulation.equipements.refrigerateur.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.SimulationEngineCreationMode;
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
import simulation.equipements.refrigerateur.models.events.CloseEvent;
import simulation.equipements.refrigerateur.models.events.OpenEvent;
import simulation.equipements.refrigerateur.models.events.TemperatureReadingEvent;

public class RefrigerateurCoupledModel extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "RefrigerateurCoupledModel";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public RefrigerateurCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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

		atomicModelDescriptors.put(RefrigerateurUserModel.URI,
				AtomicModelDescriptor.create(RefrigerateurUserModel.class, RefrigerateurUserModel.URI, TimeUnit.SECONDS,
						null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(RefrigerateurModel.URI, AtomicHIOA_Descriptor.create(RefrigerateurModel.class,
				RefrigerateurModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(RefrigerateurSensorModel.URI,
				AtomicHIOA_Descriptor.create(RefrigerateurSensorModel.class, RefrigerateurSensorModel.URI,
						TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(RefrigerateurUserModel.URI);
		submodels.add(RefrigerateurModel.URI);
		submodels.add(RefrigerateurSensorModel.URI);

		Map<Class<? extends EventI>, EventSink[]> imported = new HashMap<Class<? extends EventI>, EventSink[]>();

		Map<Class<? extends EventI>, ReexportedEvent> reexported = new HashMap<Class<? extends EventI>, ReexportedEvent>();
		reexported.put(TemperatureReadingEvent.class,
				new ReexportedEvent(RefrigerateurSensorModel.URI, TemperatureReadingEvent.class));
		reexported.put(OpenEvent.class, new ReexportedEvent(RefrigerateurUserModel.URI, OpenEvent.class));
		reexported.put(CloseEvent.class, new ReexportedEvent(RefrigerateurUserModel.URI, CloseEvent.class));

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		EventSource from1 = new EventSource(RefrigerateurUserModel.URI, OpenEvent.class);
		EventSink[] to1 = new EventSink[] { new EventSink(RefrigerateurModel.URI, OpenEvent.class) };
		connections.put(from1, to1);
		EventSource from2 = new EventSource(RefrigerateurUserModel.URI, CloseEvent.class);
		EventSink[] to2 = new EventSink[] { new EventSink(RefrigerateurModel.URI, CloseEvent.class) };
		connections.put(from2, to2);

		Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
		VariableSource source = new VariableSource("temperature", Double.class, RefrigerateurModel.URI);
		VariableSink[] sinks = new VariableSink[] {
				new VariableSink("temperature", Double.class, RefrigerateurSensorModel.URI) };
		bindings.put(source, sinks);

		coupledModelDescriptors.put(RefrigerateurCoupledModel.URI,
				new CoupledHIOA_Descriptor(RefrigerateurCoupledModel.class, RefrigerateurCoupledModel.URI, submodels,
						imported, reexported, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null,
						null, bindings));

		return new Architecture(RefrigerateurCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}
