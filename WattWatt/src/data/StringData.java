package data;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;

public class StringData implements DataOfferedI.DataI, DataRequiredI.DataI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String message;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String msg) {
		this.message = msg;
	}



}
