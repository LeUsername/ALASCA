package wattwatt.interfaces.appareils.suspensible;

import wattwatt.interfaces.appareils.IAppareil;

public interface ISuspensible extends IAppareil {

	public void suspend() throws Exception;
	public void resume() throws Exception;
	
	public boolean isWorking() throws Exception;
}
