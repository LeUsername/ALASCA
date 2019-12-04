package wattwattReborn.connecteurs.appareils.suspensibles.refrigerateur;

import wattwattReborn.connecteurs.appareils.suspensibles.SuspensibleConnector;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

public class RefrigerateurConnector extends SuspensibleConnector implements IRefrigerateur {

	@Override
	public double getTempHaut() throws Exception {
		return ((IRefrigerateur) this.offering).getTempHaut();
	}

	@Override
	public double getTempBas() throws Exception {
		return ((IRefrigerateur) this.offering).getTempBas();
	}
}
