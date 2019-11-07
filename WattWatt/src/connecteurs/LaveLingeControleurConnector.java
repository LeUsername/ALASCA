package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.IControleurRequired;
import interfaces.appareils.planifiables.ILaveLingeOffered;


/**
 * La classe <code>LaveLingeControleurConnector</code> qui creer une connexion du lave linge au Controleur
 * 
 * <p>
 * Created on : 2019-11-07
 * </p>
 * 
 * @author 3408625
 *
 */

public class LaveLingeControleurConnector extends AbstractDataConnector{
	
	@Override
	public DataI request() throws Exception {
		return this.offered2required(((ILaveLingeOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((IControleurRequired.PushI) this.requiring).receive(this.offered2required(d));
	}

}
