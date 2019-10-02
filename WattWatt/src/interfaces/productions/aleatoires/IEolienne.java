package interfaces.productions.aleatoires;

/**
 * L'interface <code>IEolienne</code>
 * 
 * <p>
 * Created on : 2019-09-25
 * <p>
 * 
 * @author 3408625
 *
 */
public interface IEolienne extends IAleatoire {
	/**
	 * Lance les pales de l'eolienne
	 * 
	 * @throws Exception
	 */
	public void allumer() throws Exception;

	/**
	 * Stop les pales de l'eolienne
	 * 
	 * @throws Exception
	 */
	public void eteindre() throws Exception;

}
