package wattwatt.connectors.energyproviders.random.windturbine;

import wattwatt.connectors.energyproviders.EnergyProviderConnector;
import wattwatt.interfaces.energyproviders.random.windturbine.IWindTurbine;

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
