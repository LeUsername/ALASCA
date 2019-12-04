package wattwattReborn.connecteurs.appareils.suspensible.refrigerateur;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwattReborn.interfaces.controleur.IControleur;

public class ControleurRefrigerateurConnector extends AbstractConnector implements IControleur {

	@Override
	public int getAllConso() throws Exception {
		return 0;
	}

	@Override
	public void refriOn() throws Exception {
		((IRefrigerateur)this.offering).On();
		
	}

	@Override
	public void refriOff() throws Exception {
		((IRefrigerateur)this.offering).Off();
		
	}

	@Override
	public void refriSuspend() throws Exception {
		((IRefrigerateur) this.offering).suspend();
		
	}

	@Override
	public void refriResume() throws Exception {
		((IRefrigerateur) this.offering).resume();
		
	}

	@Override
	public double refriTempH() throws Exception {
		return ((IRefrigerateur) this.offering).getTempHaut();
	}

	@Override
	public double refriTempB() throws Exception {
		return ((IRefrigerateur) this.offering).getTempBas();
	}

	@Override
	public int refriConso() throws Exception {
		return ((IRefrigerateur)this.offering).giveConsommation();
	}

}
