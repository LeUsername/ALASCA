package wattwattReborn.connecteurs.appareils.incontrolable.sechecheveux;

import wattwattReborn.connecteurs.appareils.AppareilConnector;
import wattwattReborn.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;

public class SecheCheveuxConnector extends AppareilConnector implements ISecheCheveux {

	@Override
	public void switchMode() throws Exception {
		((ISecheCheveux) this.offering).switchMode();

	}

	@Override
	public void increasePower() throws Exception {
		((ISecheCheveux) this.offering).increasePower();

	}

	@Override
	public void decreasePower() throws Exception {
		((ISecheCheveux) this.offering).decreasePower();

	}

}
