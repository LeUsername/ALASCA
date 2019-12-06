package wattwattReborn.interfaces.sources.aleatoire.eolienne;

import wattwattReborn.interfaces.sources.ISources;

public interface IEolienne extends ISources {

	public void On() throws Exception;

	public void Off() throws Exception;

	public boolean isOn() throws Exception;

}
