package wattwattReborn.connecteurs;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;

public class ControleurCompteurConnector extends AbstractConnector implements IControleur {

	@Override
	public int getAllConso() throws Exception {
		return ((ICompteur)this.offering).giveAllConso();
	}

}
