package wattwattReborn.interfaces.appareils.suspensible.refrigerateur;

import wattwattReborn.interfaces.appareils.suspensible.ISuspensible;

public interface IRefrigerateur extends ISuspensible {
	
	public double getTempH() throws Exception;
	public double getTempB() throws Exception;

}
