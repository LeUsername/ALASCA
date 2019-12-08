package wattwatt.interfaces.sources;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ISources extends OfferedI, RequiredI {
	public int getEnergie() throws Exception;
}
