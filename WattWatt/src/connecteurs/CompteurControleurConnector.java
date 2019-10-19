package connecteurs;

import fr.sorbonne_u.components.connectors.AbstractDataConnector;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import interfaces.ICompteurOffered;
import interfaces.IControleurRequired;

public class CompteurControleurConnector  extends AbstractDataConnector{
	
	@Override
	public DataI request() throws Exception {
		return this.offered2required(((ICompteurOffered.PullI)this.offering).get());
	}

	@Override
	public void send(DataOfferedI.DataI d) throws Exception {
		((IControleurRequired.PushI) this.requiring).receive(this.offered2required(d));
	}

}
