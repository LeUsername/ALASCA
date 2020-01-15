package wattwatt.connectors.energyproviders;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.energyproviders.IEnergyProviders;

public abstract class EnergyProviderConnector extends AbstractConnector implements IEnergyProviders {

	@Override
	public int getEnergy() throws Exception {
		return ((IEnergyProviders) this.offering).getEnergy();
	}

}
