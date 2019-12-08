package wattwattRenamed.connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import wattwattRenamed.interfaces.IStringDataOffered;
import wattwattRenamed.interfaces.IStringDataRequired;


/**
 * La classe <code>StringDataConnector</code> qui cree une connexion entre deux appareils
 * 
 * <p>
 * Created on : 2019-11-09
 * </p>
 * 
 * @author 3408625
 *
 */

public class StringDataConnector extends AbstractDataConnector{
	
	@Override
	public DataI request() throws Exception {
		return this.offered2required(((IStringDataOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((IStringDataRequired.PushI) this.requiring).receive(this.offered2required(d));
	}

}
