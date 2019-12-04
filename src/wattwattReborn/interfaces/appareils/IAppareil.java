package wattwattReborn.interfaces.appareils;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface IAppareil extends OfferedI, RequiredI{
	
	public void On() throws Exception;
	public void Off() throws Exception;
	public int getConso() throws Exception;

}
