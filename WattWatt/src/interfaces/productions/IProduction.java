package interfaces.productions;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface <code>IProduction</code>
 * 
 * <p>
 * Created on : 2019-10-02
 * <p>
 * 
 * @author 3410456
 *
 */
public interface IProduction extends RequiredI, OfferedI {

	/**
	 * Renvoie le nombre de kWh au moment T
	 * 
	 * @return Nombre de kWh consomme a l'instant T
	 * @throws Exception
	 */
	public int quantiteProduite() throws Exception;
}
