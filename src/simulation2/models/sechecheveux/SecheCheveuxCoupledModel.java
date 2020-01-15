package simulation2.models.sechecheveux;

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
import simulation2.events.sechecheveux.DecreasePowerEvent;
import simulation2.events.sechecheveux.IncreasePowerEvent;
import simulation2.events.sechecheveux.SwitchModeEvent;
import simulation2.events.sechecheveux.SwitchOffEvent;
import simulation2.events.sechecheveux.SwitchOnEvent;

public class SecheCheveuxCoupledModel extends CoupledModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "SecheCheveuxCoupledModel";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public SecheCheveuxCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
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

		atomicModelDescriptors.put(SecheCheveuxModel.URI, AtomicHIOA_Descriptor.create(SecheCheveuxModel.class,
				SecheCheveuxModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(SecheCheveuxUserModel.URI, AtomicModelDescriptor.create(SecheCheveuxUserModel.class,
				SecheCheveuxUserModel.URI, TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(SecheCheveuxModel.URI);
		submodels.add(SecheCheveuxUserModel.URI);

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
		EventSource from4 = new EventSource(SecheCheveuxUserModel.URI, IncreasePowerEvent.class);
		EventSink[] to4 = new EventSink[] { new EventSink(SecheCheveuxModel.URI, IncreasePowerEvent.class) };
		connections.put(from4, to4);
		EventSource from5 = new EventSource(SecheCheveuxUserModel.URI, DecreasePowerEvent.class);
		EventSink[] to5 = new EventSink[] { new EventSink(SecheCheveuxModel.URI, DecreasePowerEvent.class) };
		connections.put(from5, to5);

		coupledModelDescriptors.put(SecheCheveuxCoupledModel.URI,
				new CoupledHIOA_Descriptor(SecheCheveuxCoupledModel.class, SecheCheveuxCoupledModel.URI, submodels,
						null, null, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
						null));

		return new Architecture(SecheCheveuxCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}
}
