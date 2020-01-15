package wattwatt.interfaces.energyproviders.random.windturbine;

import wattwatt.interfaces.energyproviders.IEnergyProviders;

public interface IWindTurbine extends IEnergyProviders {

	public void On() throws Exception;

	public void Off() throws Exception;

	public boolean isOn() throws Exception;

}
