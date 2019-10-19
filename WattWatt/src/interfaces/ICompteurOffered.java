package interfaces;

import data.CompteurData;
import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public interface ICompteurOffered extends DataOfferedI {
	
	/**
	 * Methode a enlever: utilisee uniqueme,t pour tset
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public StringData sendMessage(String uri) throws Exception;
	
	/**
	 * Le vrai a commenter
	 * @param uri
	 * @return
	 * @throws Exception
	 */
	public CompteurData sendCompteurData(String uri) throws Exception;

}
