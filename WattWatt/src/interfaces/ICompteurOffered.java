package interfaces;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

/**
 * L'interface <code>ICompteurOffered</code> qui permet à un composant
 * Compteur d'envoyer des messages
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */

public interface ICompteurOffered extends DataOfferedI {

	/**
	 * Methode qui sert a l'envoie d'un message StringData
	 * 
	 * @param uri : URI du composant destinataire
	 * @return un StringData
	 * @throws Exception
	 */
	public StringData sendMessage(String uri) throws Exception;
	
	/**
	 * Methode qui sert a l'envoie d'un message CompteurData
	 * 
	 * @param uri : URI du composant destinataire
	 * @return un CompteurData
	 * @throws Exception
	 */
	public CompteurData sendCompteurData(String uri) throws Exception;

}
