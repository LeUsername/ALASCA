package wattwatt.interfaces;

import fr.sorbonne_u.components.interfaces.DataRequiredI;
import wattwatt.data.StringData;


/**
 * L'interface <code>IStringDataRequired</code> qui permet la reception de messages
 * 
 * <p>
 * Created on : 2019-11-15
 * </p>
 * 
 * @author 3410456
 *
 */

public interface IStringDataRequired extends DataRequiredI {
	/**
	 * Recuperation du message envoye par un autre service (le controleur)
	 * 
	 * @param msg la donnee a stocker dans le Seche cheveux
	 * @throws Exception todo
	 */
	public void getMessage(StringData msg) throws Exception;
}
