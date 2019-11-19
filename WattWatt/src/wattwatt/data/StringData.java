package wattwatt.data;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

/**
 * La classe <code>StringData</code> qui represente les messages transmis entre
 * composants. Ce message est une chaine de caracteres.
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class StringData implements DataOfferedI.DataI, DataRequiredI.DataI {

	private static final long serialVersionUID = 1L;

	protected String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

}
