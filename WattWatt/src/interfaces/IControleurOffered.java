package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

/**
 * L'interface <code>IControleurOffered</code> qui permet à un composant
 * Controleur d'envoyer des messages
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */

public interface IControleurOffered extends DataOfferedI {
	
	/**
	 * Methode qui sert a l'envoi d'un message StringData
	 * 
	 * @param uri : URI du composant destinataire
	 * @return un StringData
	 * @throws Exception
	 */
	public StringData sendMessage(String uri) throws Exception;


	public StringData sendMessage2(String uri) throws Exception;

}
