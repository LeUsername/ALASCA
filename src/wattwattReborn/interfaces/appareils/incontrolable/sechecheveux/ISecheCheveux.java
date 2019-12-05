package wattwattReborn.interfaces.appareils.incontrolable.sechecheveux;

import wattwattReborn.interfaces.appareils.IAppareil;

public interface ISecheCheveux extends IAppareil {
	
	public void switchMode() throws Exception;
	public void increasePower() throws Exception;
	public void decreasePower() throws Exception;

}
