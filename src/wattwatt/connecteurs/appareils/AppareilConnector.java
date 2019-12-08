package wattwatt.connecteurs.appareils;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.appareils.IAppareil;

public abstract class AppareilConnector extends AbstractConnector implements IAppareil {

	@Override
	public void On() throws Exception {
		((IAppareil) this.offering).On();
	}

	@Override
	public void Off() throws Exception {
		((IAppareil) this.offering).Off();
	}

	@Override
	public int getConso() throws Exception {
		return ((IAppareil) this.offering).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IAppareil) this.offering).isOn();
	}
}
