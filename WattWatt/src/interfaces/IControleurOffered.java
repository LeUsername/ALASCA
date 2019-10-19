package interfaces;

import data.StringData;
import fr.sorbonne_u.components.interfaces.DataOfferedI;

public interface IControleurOffered extends DataOfferedI {
	
	public StringData sendMessage(String uri) throws Exception;

}
