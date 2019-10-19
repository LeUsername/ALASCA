package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.ICompteur;
import interfaces.IControleur;

/**
 * La classe <code>CompteurConnector</code> qui permet de se connecter à un
 * Compteur et d'appeler ses methodes
 * 
 * <p>
 * Created on : 2019-10-17
 * </p>
 * 
 * @author 3408625
 *
 */
public class CompteurConnector extends AbstractDataConnector {

	@Override
	public DataI request() throws Exception {
		return this.offered2required(((IControleur.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((ICompteur.PushI) this.requiring).receive(this.offered2required(d));
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
