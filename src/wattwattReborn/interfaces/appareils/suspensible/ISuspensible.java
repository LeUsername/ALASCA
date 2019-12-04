package wattwattReborn.interfaces.appareils.suspensible;

import wattwattReborn.interfaces.appareils.IAppareil;

public interface ISuspensible extends IAppareil {

	public void suspend() throws Exception;
	public void resume() throws Exception;
	
	public boolean isWorking() throws Exception;
}
