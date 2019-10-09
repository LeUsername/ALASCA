package interfaces.productions.intermittentes;

import interfaces.productions.IProduction;

/**
 * L'interface <code>IRefrigerateur</code>
 * 
 * <p>
 * Created on : 2019-09-25
 * <p>
 * 
 * @author 3408625
 *
 */
public interface IIntermittente extends IProduction{

	/**
	 * Renvoie la quantite de kWh disponible dans la ressource
	 * @return nombre de kWh dans la ressource
	 * @throws Exception
	 */
	public int quantiteDisponible() throws Exception;
	
	/**
	 * Recharge l'unité de stockage de quantite kWh 
	 * @param kWh
	 * @throws Exception
	 */
	public void recharger(int quantite) throws Exception;
	
	/**
	 * Decharge l'unité de stockage de quantité kWh 
	 * @param quantite
	 * @throws Exception
	 */
	public void decharger(int quantite) throws Exception;
}
