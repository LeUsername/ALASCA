package wattwattReborn.connecteurs.sources.aleatoire.eolienne;

import wattwattReborn.connecteurs.sources.SourceConnector;
import wattwattReborn.interfaces.sources.aleatoire.eolienne.IEolienne;

public class EolienneConnector extends SourceConnector implements IEolienne {

	@Override
	public void On() throws Exception {
		((IEolienne)this.offering).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IEolienne)this.offering).Off();
		
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IEolienne)this.offering).isOn();
	}

}
