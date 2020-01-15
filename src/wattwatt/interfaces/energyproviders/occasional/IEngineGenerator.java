package wattwatt.interfaces.energyproviders.occasional;

import wattwatt.interfaces.energyproviders.IEnergyProviders;

public interface IEngineGenerator extends IEnergyProviders {

	public boolean fuelIsEmpty() throws Exception;

	public boolean fuelIsFull() throws Exception;

	public int fuelQuantity() throws Exception;

	public void addFuel(int quantity) throws Exception;

	public void on() throws Exception; // use fuel to prod electricity

	public void off() throws Exception;

	public boolean isOn() throws Exception;

}
