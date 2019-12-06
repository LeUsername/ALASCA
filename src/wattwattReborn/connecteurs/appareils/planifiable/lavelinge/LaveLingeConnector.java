package wattwattReborn.connecteurs.appareils.planifiable.lavelinge;

import wattwattReborn.connecteurs.appareils.planifiable.PlanifiableConnector;
import wattwattReborn.interfaces.appareils.planifiable.lavelinge.ILaveLinge;

public class LaveLingeConnector extends PlanifiableConnector implements ILaveLinge {

	@Override
	public void ecoLavage() throws Exception {
		((ILaveLinge) this.offering).ecoLavage();
		
	}

	@Override
	public void premiumLavage() throws Exception {
		((ILaveLinge) this.offering).premiumLavage();
		
	}

}
