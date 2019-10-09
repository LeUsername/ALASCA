package interfaces.productions.aleatoires;

import interfaces.productions.IProduction;

/**
 * L'interface <code>IAleatoire</code>
 * 
 * <p>
 * Created on : 2019-09-25
 * <p>
 * 
 * @author 3408625
 *
 */
public interface IAleatoire extends IProduction {

	/**
	 * Renvoie le nombre de kWh au moment T
	 * 
	 * @return Nombre de kWh consomme a l'instant T
	 * @throws Exception
	 */
	public int quantiteProduite() throws Exception;
}
