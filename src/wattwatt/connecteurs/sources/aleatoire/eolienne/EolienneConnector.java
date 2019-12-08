package wattwatt.connecteurs.sources.aleatoire.eolienne;

import wattwatt.connecteurs.sources.SourceConnector;
import wattwatt.interfaces.sources.aleatoire.eolienne.IEolienne;

public class EolienneConnector extends SourceConnector implements IEolienne {

	@Override
	public void On() throws Exception {
		((IEolienne) this.offering).On();

	}

	@Override
	public void Off() throws Exception {
		((IEolienne) this.offering).Off();

	}

	@Override
	public boolean isOn() throws Exception {
		return ((IEolienne) this.offering).isOn();
	}

}
