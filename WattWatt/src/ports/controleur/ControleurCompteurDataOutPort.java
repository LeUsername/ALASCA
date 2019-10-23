package ports.controleur;

import data.CompteurData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import interfaces.IControleurRequired;

/**
 * La classe <code>ControleurStringDataOutPort</code> qui represente le port du
 * Controleur par lequel vont etre recu des message de type CompteurData
 * 
 * <p>
 * Created on : 2019-10-19
 * </p>
 * 
 * @author 3408625
 *
 */
public class ControleurCompteurDataOutPort extends AbstractDataOutboundPort{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurCompteurDataOutPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	@Override
	public void receive(DataI d) throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((IControleurRequired) this.getServiceOwner()).getCompteurData(((CompteurData) d));
				return null;
			}
		});
		
	}

}
