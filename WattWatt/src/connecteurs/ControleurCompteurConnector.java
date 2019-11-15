package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.ICompteurRequired;
import interfaces.IControleurOffered;

/**
 * La classe <code>ControleurCompteurConnector</code> qui cree une connexion du Controleur au Compteur
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class ControleurCompteurConnector extends AbstractDataConnector {

	@Override
	public DataI request() throws Exception {
		return this.offered2required(((IControleurOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((ICompteurRequired.PushI) this.requiring).receive(this.offered2required(d));
	}

//	@Override
//	public int getAllConsommation() throws Exception {
//		return ((ICompteur) this.offering).getAllConsommation();
//	}
//
//	@Override
//	public int getAllProductionsAleatoires() throws Exception {
//		return ((ICompteur) this.offering).getAllProductionsAleatoires();
//	}
//
//	@Override
//	public int getAllProductionsIntermittentes() throws Exception {
//		return ((ICompteur) this.offering).getAllProductionsIntermittentes();
//	}
//
//	@Override
//	public void reset() throws Exception {
//		((ICompteur) this.offering).reset();
//
//	}

}
