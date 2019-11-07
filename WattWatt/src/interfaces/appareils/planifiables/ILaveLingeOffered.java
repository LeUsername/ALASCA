package interfaces.appareils.planifiables;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

/**
 * L'interface <code>ILaveLingeOffered</code> qui permet à un composant
 * LaveLinge d'envoyer des messages
 * 
 * <p>
 * Created on : 2019-11-07
 * </p>
 * 
 * @author 3408625
 *
 */
public interface ILaveLingeOffered extends DataOfferedI {

	/**
	 * Methode qui sert a l'envoi d'un message StringData
	 * 
	 * @param uri : URI du composant destinataire
	 * @return un StringData
	 * @throws Exception
	 */
	public StringData sendMessage(String uri) throws Exception;
}

