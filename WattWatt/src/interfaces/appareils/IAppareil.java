package interfaces.appareils;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface <code>IAppareil</code>
 * 
 * <p>
 * Created on : 2019-10-02
 * </p>
 * 
 * @author 3410456
 *
 */
public interface IAppareil extends OfferedI,RequiredI {
	/**
	 * 
	 * @throws Exception
	 */
	public void allumer() throws Exception;

	/**
	 * 
	 * @throws Exception
	 */
	public void eteindre() throws Exception;

	/**
	 * Renvoie le nombre de kWh consomme a l'instant T
	 * 
	 * @return Nombre de kWh consomme a l'instant T
	 * @throws Exception
	 */
	public int getConsommation() throws Exception;
}
