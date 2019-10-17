package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

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
public interface IControleur extends DataOfferedI {

	// public void gestionRefigerateur() throws Exception;
	//
	// public void gestionLaveLinge() throws Exception;
	//
	// public void gestionBatterie() throws Exception;
	//
	// public void gestionEolienne() throws Exception;

	public DataOfferedI.DataI getData(String uri) throws Exception;

	public void sendData(StringData msg) throws Exception;
}
