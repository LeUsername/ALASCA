package wattwatt.connectors.energyproviders;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.energyproviders.IEnergyProviders;

/**
 * The class <code>EnergyProviderConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IEnergyProviders</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IEnergyProviders</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public abstract class EnergyProviderConnector extends AbstractConnector implements IEnergyProviders {

	@Override
	public int getEnergy() throws Exception {
		return ((IEnergyProviders) this.offering).getEnergy();
	}

}
