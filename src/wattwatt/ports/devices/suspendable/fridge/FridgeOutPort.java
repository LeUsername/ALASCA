package wattwatt.ports.devices.suspendable.fridge;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.devices.suspendable.fridge.IFridge;

/**
 * The class <code>FridgeOutPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The OutBound port of the fridge component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class FridgeOutPort extends AbstractOutboundPort implements IFridge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FridgeOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IFridge.class, owner);
	}

	@Override
	public void suspend() throws Exception {
		((IFridge)this.connector).suspend();
		
	}

	@Override
	public void resume() throws Exception {
		((IFridge)this.connector).resume();
		
	}

	@Override
	public void On() throws Exception {
		((IFridge)this.connector).On();
		
	}

	@Override
	public void Off() throws Exception {
		((IFridge)this.connector).Off();
		
	}

	@Override
	public int getConso() throws Exception {
		return ((IFridge)this.connector).getConso();
	}

	@Override
	public double getTempH() throws Exception {
		return ((IFridge)this.connector).getTempH();
	}

	@Override
	public double getTempB() throws Exception {
		return ((IFridge)this.connector).getTempB();
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((IFridge)this.connector).isWorking();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IFridge)this.connector).isOn();
	}

}
