package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

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
public interface IControleur extends DataOfferedI, DataRequiredI {

	// public void gestionRefigerateur() throws Exception;
	//
	// public void gestionLaveLinge() throws Exception;
	//
	// public void gestionBatterie() throws Exception;
	//
	// public void gestionEolienne() throws Exception;

	/**
	 * Envoi du message en tete de liste sur le service reference par l'URI en
	 * parametre
	 * 
	 * @param uri
	 *            service sur lequel envoyer le message
	 * @throws Exception
	 *             todo
	 */
	public DataOfferedI.DataI sendDataOffered(String uri) throws Exception;

	public DataRequiredI.DataI getDataRequired(StringData msg) throws Exception;

}
