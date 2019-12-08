package wattwatt.ports.appareils.incontrolable.sechecheveux;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;

public class SecheCheveuxOutPort extends AbstractOutboundPort implements ISecheCheveux{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SecheCheveuxOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ISecheCheveux.class, owner);
		
	}

	@Override
	public void On() throws Exception {
		((ISecheCheveux)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((ISecheCheveux)this.connector).Off();
		
	}

	@Override
	public int getConso() throws Exception {
		return ((ISecheCheveux)this.connector).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((ISecheCheveux)this.connector).isOn();
	}

	@Override
	public void switchMode() throws Exception {
		((ISecheCheveux)this.connector).switchMode();
		
	}

	@Override
	public void increasePower() throws Exception {
		((ISecheCheveux)this.connector).increasePower();
		
	}

	@Override
	public void decreasePower() throws Exception {
		((ISecheCheveux)this.connector).decreasePower();
		
	}

}
