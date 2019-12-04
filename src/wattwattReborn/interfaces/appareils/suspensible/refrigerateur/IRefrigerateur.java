package wattwattReborn.interfaces.appareils.suspensible.refrigerateur;

import wattwattReborn.interfaces.appareils.suspensible.ISuspensible;

public interface IRefrigerateur extends ISuspensible {
	
	public double getTempHaut() throws Exception;
	public double getTempBas() throws Exception;

}
