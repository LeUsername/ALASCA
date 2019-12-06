package wattwattReborn.ports.sources.aleatoire.eolienne;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwattReborn.interfaces.sources.aleatoire.eolienne.IEolienne;

public class EolienneOutPort extends AbstractOutboundPort implements IEolienne{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EolienneOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IEolienne.class, owner);
	}

	@Override
	public int getEnergie() throws Exception {
		return ((IEolienne)this.connector).getEnergie();
	}

	@Override
	public void On() throws Exception {
		((IEolienne)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IEolienne)this.connector).Off();
		
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IEolienne)this.connector).isOn();
	}

}
