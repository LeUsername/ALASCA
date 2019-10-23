package interfaces;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * L'interface <code>IControleurRequired</code> qui permet à un composant
 * Controleur de recuperer des messages
 * <p>
 * Created on : 2019-10-09
 * </p>
 * 
 * @author 3408625
 *
 */
public interface IControleurRequired extends DataRequiredI {

	// public void gestionRefigerateur() throws Exception;
	//
	// public void gestionLaveLinge() throws Exception;
	//
	// public void gestionBatterie() throws Exception;
	//
	// public void gestionEolienne() throws Exception;

	/**
	 * Recuperation du message envoye par un autre service
	 * 
	 * @param msg
	 *            la donnee a stocker dans le compteur
	 * @throws Exception
	 *             todo
	 */
	public void getMessage(StringData msg) throws Exception;

	/**
	 * Recuperation du message envoye par le compteur
	 * 
	 * @param msg
	 *            la donnee a stocker dans le compteur
	 * @throws Exception
	 *             todo
	 */
	public void getCompteurData(CompteurData msg) throws Exception;

}
