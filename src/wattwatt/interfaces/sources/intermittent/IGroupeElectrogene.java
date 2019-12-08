package wattwatt.interfaces.sources.intermittent;

import wattwatt.interfaces.sources.ISources;

public interface IGroupeElectrogene extends ISources {

	public boolean fuelIsEmpty() throws Exception;

	public boolean fuelIsFull() throws Exception;

	public int fuelQuantity() throws Exception;

	public void addFuel(int quantity) throws Exception;

	public void on() throws Exception; // use fuel to prod electricity

	public void off() throws Exception;

	public boolean isOn() throws Exception;

}
