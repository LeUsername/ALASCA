package wattwatt.interfaces.electricmeter;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IElectricMeter extends OfferedI, RequiredI{

	public int getAllConso() throws Exception;
}
