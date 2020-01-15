package wattwatt.interfaces.energyproviders;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IEnergyProviders extends OfferedI, RequiredI {
	public int getEnergy() throws Exception;
}
