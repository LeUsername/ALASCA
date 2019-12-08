package wattwatt.connecteurs.appareils.planifiable.lavelinge;

import wattwatt.connecteurs.appareils.planifiable.PlanifiableConnector;
import wattwatt.interfaces.appareils.planifiable.lavelinge.ILaveLinge;

public class LaveLingeConnector extends PlanifiableConnector implements ILaveLinge {

	@Override
	public void ecoLavage() throws Exception {
		((ILaveLinge) this.offering).ecoLavage();
		
	}

	@Override
	public void premiumLavage() throws Exception {
		((ILaveLinge) this.offering).premiumLavage();
		
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((ILaveLinge) this.offering).isWorking();
	}

}
