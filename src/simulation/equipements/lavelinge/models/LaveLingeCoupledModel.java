package simulation.equipements.lavelinge.models;

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
import simulation.equipements.lavelinge.models.events.EcoLavageEvent;
import simulation.equipements.lavelinge.models.events.PremiumLavageEvent;
import simulation.equipements.lavelinge.models.events.StartAtEvent;

public class LaveLingeCoupledModel extends CoupledModel {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI of the unique instance of this class (in this example). */
	public static final String URI = "LaveLingeCoupledModel";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public LaveLingeCoupledModel(String uri, TimeUnit simulatedTimeUnit, SimulatorI simulationEngine,
			ModelDescriptionI[] submodels, Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported, Map<EventSource, EventSink[]> connections,
			Map<StaticVariableDescriptor, VariableSink[]> importedVars,
			Map<VariableSource, StaticVariableDescriptor> reexportedVars, Map<VariableSource, VariableSink[]> bindings)
			throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported, reexported, connections, importedVars,
				reexportedVars, bindings);
	}
	
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

	public static Architecture build() throws Exception {
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(LaveLingeModel.URI,
				AtomicHIOA_Descriptor.create(LaveLingeModel.class, LaveLingeModel.URI, TimeUnit.SECONDS,
						null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		atomicModelDescriptors.put(LaveLingeUserModel.URI,
				AtomicModelDescriptor.create(LaveLingeUserModel.class, LaveLingeUserModel.URI,
						TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));
		atomicModelDescriptors.put(TicModel.URI + "-3", AtomicModelDescriptor.create(TicModel.class,
				TicModel.URI + "-3", TimeUnit.SECONDS, null, SimulationEngineCreationMode.ATOMIC_ENGINE));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<String, CoupledModelDescriptor>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(LaveLingeModel.URI);
		submodels.add(LaveLingeUserModel.URI);
		submodels.add(TicModel.URI + "-3");

		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();
		EventSource from1 = new EventSource(LaveLingeUserModel.URI, StartAtEvent.class);
		EventSink[] to1 = new EventSink[] { new EventSink(LaveLingeModel.URI, StartAtEvent.class) };
		connections.put(from1, to1);
		EventSource from2 = new EventSource(LaveLingeUserModel.URI, EcoLavageEvent.class);
		EventSink[] to2 = new EventSink[] { new EventSink(LaveLingeModel.URI, EcoLavageEvent.class) };
		connections.put(from2, to2);
		EventSource from3 = new EventSource(LaveLingeUserModel.URI, PremiumLavageEvent.class);
		EventSink[] to3 = new EventSink[] { new EventSink(LaveLingeModel.URI, PremiumLavageEvent.class) };
		connections.put(from3, to3);
		
		EventSource from5 = new EventSource(TicModel.URI + "-3", TicEvent.class);
		EventSink[] to5 = new EventSink[] { new EventSink(LaveLingeModel.URI, TicEvent.class) };
		connections.put(from5, to5);
		
		/*Map<VariableSource, VariableSink[]> bindings = new HashMap<VariableSource, VariableSink[]>();
		VariableSource source = new VariableSource("fuelCapacity", Double.class, GroupeElectrogeneModel.URI);
		VariableSink[] sinks = new VariableSink[] {
				new VariableSink("fuelCapacity", Double.class, GroupeElectrogeneUserModel.URI) };
		bindings.put(source, sinks);*/

		coupledModelDescriptors.put(LaveLingeCoupledModel.URI,
				new CoupledHIOA_Descriptor(LaveLingeCoupledModel.class, LaveLingeCoupledModel.URI, submodels,
						null, null, connections, null, SimulationEngineCreationMode.COORDINATION_ENGINE, null, null,
						null));

		return new Architecture(LaveLingeCoupledModel.URI, atomicModelDescriptors, coupledModelDescriptors,
				TimeUnit.SECONDS);
	}

}
