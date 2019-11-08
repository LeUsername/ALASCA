package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public interface IStringDataRequired extends DataRequiredI  {
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
