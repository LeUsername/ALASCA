package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.IControleurOffered;
import interfaces.appareils.incontrolables.ISecheCheveuxRequired;

/**
 * La classe <code>ControleurSecheCheveuxConnector</code> qui cree une connexion du Controleur au Seche cheveux
 * 
 * <p>
 * Created on : 2019-11-06
 * </p>
 * 
 * @author 3408625
 *
 */
public class ControleurSecheCheveuxConnector extends AbstractDataConnector {

	@Override
	public DataI request() throws Exception {
		return this.offered2required(((IControleurOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((ISecheCheveuxRequired.PushI) this.requiring).receive(this.offered2required(d));
	}


}
