package wattwattReborn.connecteurs;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

public class RefrigerateurConnector extends AbstractConnector implements IRefrigerateur {

	@Override
	public void suspend() throws Exception {
		((IRefrigerateur)this.offering).suspend();
		
	}

	@Override
	public void resume() throws Exception {
		((IRefrigerateur)this.offering).resume();
	}

	@Override
	public void On() throws Exception {
		((IRefrigerateur)this.offering).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IRefrigerateur)this.offering).Off();
	}

	@Override
	public int getConso() throws Exception {
		return ((IRefrigerateur)this.offering).getConso();
	}

	@Override
	public double getTempHaut() throws Exception {
		return ((IRefrigerateur)this.offering).getTempHaut();
	}

	@Override
	public double getTempBas() throws Exception {
		return ((IRefrigerateur)this.offering).getTempBas();
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((IRefrigerateur)this.offering).isWorking();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IRefrigerateur)this.offering).isOn();
	}

}
