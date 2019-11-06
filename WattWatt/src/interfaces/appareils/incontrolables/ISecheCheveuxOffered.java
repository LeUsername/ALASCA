package interfaces.appareils.incontrolables;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

/**
 * L'interface <code>ISecheCheveuxOffered</code> qui permet à un composant
 * SecheCheveux d'envoyer des messages
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */

public interface ISecheCheveuxOffered extends DataOfferedI {

	/**
	 * Methode qui sert a l'envoi d'un message StringData
	 * 
	 * @param uri : URI du composant destinataire
	 * @return un StringData
	 * @throws Exception
	 */
	public StringData sendMessage(String uri) throws Exception;
}
