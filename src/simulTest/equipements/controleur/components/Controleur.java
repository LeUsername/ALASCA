package simulTest.equipements.controleur.components;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;

public class Controleur extends AbstractCyPhyComponent implements EmbeddingComponentStateAccessI {

	protected ControleurSimulatorPlugin asp;

	protected Controleur() throws Exception {
		super(1, 0);
		this.initialise();
	}

	protected Controleur(String reflectionInboundPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return null;
	}

	public void initialise() throws Exception {
		Architecture localArchitecture = this.createLocalArchitecture(null);
		this.asp = new ControleurSimulatorPlugin();
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		this.asp.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.asp);
		this.toggleLogging();
	}
}
