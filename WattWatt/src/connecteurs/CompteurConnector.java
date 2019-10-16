package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import interfaces.ICompteur;

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
public class CompteurConnector extends AbstractConnector implements ICompteur {

	@Override
	public int getAllConsommation() throws Exception {
		return ((ICompteur) this.offering).getAllConsommation();
	}

	@Override
	public int getAllProductionsAleatoires() throws Exception {
		return ((ICompteur) this.offering).getAllProductionsAleatoires();
	}

	@Override
	public int getAllProductionsIntermittentes() throws Exception {
		return ((ICompteur) this.offering).getAllProductionsIntermittentes();
	}

	@Override
	public void reset() throws Exception {
		((ICompteur) this.offering).reset();

	}

}
