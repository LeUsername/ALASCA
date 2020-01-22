package wattwatt.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import wattwatt.interfaces.electricmeter.IElectricMeter;

public class ElectricMeterConnector extends AbstractConnector implements IElectricMeter {

	@Override
	public double getAllConso() throws Exception {
		return ((IElectricMeter) this.offering).getAllConso();
	}

}
