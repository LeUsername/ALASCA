package wattwatt.connecteurs.appareils.suspensibles.refrigerateur;

import wattwatt.connecteurs.appareils.suspensibles.SuspensibleConnector;
import wattwatt.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

public class RefrigerateurConnector extends SuspensibleConnector implements IRefrigerateur {

	@Override
	public double getTempH() throws Exception {
		return ((IRefrigerateur) this.offering).getTempH();
	}

	@Override
	public double getTempB() throws Exception {
		return ((IRefrigerateur) this.offering).getTempB();
	}
}
