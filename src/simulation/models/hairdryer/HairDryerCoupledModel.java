package simulation.models.hairdryer;

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
import simulation.events.hairdryer.DecreasePowerEvent;
import simulation.events.hairdryer.IncreasePowerEvent;
import simulation.events.hairdryer.SwitchModeEvent;
import simulation.events.hairdryer.SwitchOffEvent;
import simulation.events.hairdryer.SwitchOnEvent;

public class HairDryerCoupledModel extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "HairDryerCoupledModel";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public HairDryerCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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

		atomicModelDescriptors.put(HairDryerModel.URI, AtomicHIOA_Descriptor.create(HairDryerModel.class,
				HairDryerModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(HairDryerUserModel.URI, AtomicModelDescriptor.create(HairDryerUserModel.class,
				HairDryerUserModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(HairDryerModel.URI);
		submodels.add(HairDryerUserModel.URI);

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
		EventSource from4 = new EventSource(HairDryerUserModel.URI, IncreasePowerEvent.class);
		EventSink[] to4 = new EventSink[] { new EventSink(HairDryerModel.URI, IncreasePowerEvent.class) };
		connections.put(from4, to4);
		EventSource from5 = new EventSource(HairDryerUserModel.URI, DecreasePowerEvent.class);
		EventSink[] to5 = new EventSink[] { new EventSink(HairDryerModel.URI, DecreasePowerEvent.class) };
		connections.put(from5, to5);

		coupledModelDescriptors.put(HairDryerCoupledModel.URI,
				new CoupledHIOA_Descriptor(HairDryerCoupledModel.class, HairDryerCoupledModel.URI, submodels,
						null, null, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
						null));

		return new Architecture(HairDryerCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}
