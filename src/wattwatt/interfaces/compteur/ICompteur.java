package wattwatt.interfaces.compteur;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ICompteur extends OfferedI, RequiredI{

	public int getAllConso() throws Exception;
}