package wattwattReborn.connecteurs;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.compteur.ICompteur;

public class CompteurConnector extends AbstractConnector implements ICompteur {

	@Override
	public int getAllConso() throws Exception {
		
		return ((ICompteur)this.offering).getAllConso();
	}

}
