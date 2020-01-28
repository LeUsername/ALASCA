package wattwatt.ports.devices.suspendable.fridge;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.components.devices.suspendable.fridge.Fridge;
import wattwatt.interfaces.devices.suspendable.fridge.IFridge;

/**
 * The class <code>FridgeInPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The InBound port of the fridge component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class FridgeInPort extends AbstractInboundPort implements IFridge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FridgeInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IFridge.class, owner);
	}

	@Override
	public void suspend() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).suspend() ;
						return null;
					}
				}) ;
	}

	@Override
	public void resume() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).resume() ;
						return null;
					}
				}) ;
	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).on() ;
						return null;
					}
				}) ;
	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Fridge)this.getServiceOwner()).off() ;
						return null;
					}
				}) ;
	}

	@Override
	public double getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Fridge)owner).giveConso());
	}

	@Override
	public double getTempH() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Fridge)owner).getTempHaut());
	}

	@Override
	public double getTempB() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Fridge)owner).getTempBas());
	}

	@Override
	public boolean isWorking() throws Exception {
		return  this.getOwner().handleRequestSync(owner ->((Fridge)owner).isWorking());
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Fridge)owner).isOn());
	}

}
