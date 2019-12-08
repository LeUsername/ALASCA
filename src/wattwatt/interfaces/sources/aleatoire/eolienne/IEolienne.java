package wattwatt.interfaces.sources.aleatoire.eolienne;

import wattwatt.interfaces.sources.ISources;

public interface IEolienne extends ISources {

	public void On() throws Exception;

	public void Off() throws Exception;

	public boolean isOn() throws Exception;

}
