package wattwatt.connectors.energyproviders.random.windturbine;

import wattwatt.connectors.energyproviders.EnergyProviderConnector;
import wattwatt.interfaces.energyproviders.random.windturbine.IWindTurbine;


/**
 * The class <code>WindTurbineConnector</code> implements a connector between
 * the <code>IController</code> and the <code>IWindTurbine</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>IWindTurbine</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class WindTurbineConnector extends EnergyProviderConnector implements IWindTurbine {

	@Override
	public void On() throws Exception {
		((IWindTurbine) this.offering).On();

	}

	@Override
	public void Off() throws Exception {
		((IWindTurbine) this.offering).Off();

	}

	@Override
	public boolean isOn() throws Exception {
		return ((IWindTurbine) this.offering).isOn();
	}

}
