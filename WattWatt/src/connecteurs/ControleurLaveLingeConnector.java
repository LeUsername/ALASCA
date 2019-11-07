package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.IControleurOffered;
import interfaces.appareils.planifiables.ILaveLingeRequired;

/**
 * La classe <code>ControleurLaveLingeConnector</code> qui creer une connexion du Controleur au Lave Linge
 * 
 * <p>
 * Created on : 2019-11-07
 * </p>
 * 
 * @author 3408625
 *
 */
public class ControleurLaveLingeConnector extends AbstractDataConnector {

	@Override
	public DataI request() throws Exception {
		return this.offered2required(((IControleurOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((ILaveLingeRequired.PushI) this.requiring).receive(this.offered2required(d));
	}


}
