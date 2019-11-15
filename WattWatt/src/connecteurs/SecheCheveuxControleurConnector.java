package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.IControleurRequired;
import interfaces.appareils.incontrolables.ISecheCheveuxOffered;


/**
 * La classe <code>CompteurControleurConnector</code> qui cree une connexion du Compteur au Controleur
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */

public class SecheCheveuxControleurConnector  extends AbstractDataConnector{
	
	@Override
	public DataI request() throws Exception {
		return this.offered2required(((ISecheCheveuxOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((IControleurRequired.PushI) this.requiring).receive(this.offered2required(d));
	}

}
