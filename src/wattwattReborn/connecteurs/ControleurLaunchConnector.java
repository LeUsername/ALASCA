package wattwattReborn.connecteurs;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.controleur.IControleurLaunch;

public class ControleurLaunchConnector extends AbstractConnector implements IControleurLaunch {

	@Override
	public void printConso() throws Exception {
		((IControleurLaunch)this.offering).printConso();
	}

}
