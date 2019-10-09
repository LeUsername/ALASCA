package interfaces;

/**
 * L'interface <code>IControleur</code>
 * 
 * <p>
 * Created on : 2019-10-09
 * </p>
 * 
 * @author 3408625
 *
 */
public interface IControleur {

	
	public void gestionRefigerateur() throws Exception;

	public void gestionLaveLinge() throws Exception;

	public void gestionBatterie() throws Exception;

	public void gestionEolienne() throws Exception;
}
