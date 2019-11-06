package interfaces.appareils.incontrolables;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataRequiredI;


/**
 * L'interface <code>ISecheCheveuxRequired</code> qui permet à un composant Seche cheveux
 * de recuperer des messages
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */
public interface ISecheCheveuxRequired extends DataRequiredI {
	
	/**
	 * Recuperation du message envoye par un autre service (le controleur)
	 * 
	 * @param msg
	 *            la donnee a stocker dans le Seche cheveux
	 * @throws Exception
	 *             todo
	 */
	public void getMessage(StringData msg) throws Exception;

}
