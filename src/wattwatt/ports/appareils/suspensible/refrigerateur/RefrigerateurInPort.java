package wattwatt.ports.appareils.suspensible.refrigerateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwatt.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

public class RefrigerateurInPort extends AbstractInboundPort implements IRefrigerateur {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefrigerateurInPort(String uri, ComponentI owner) throws Exception {
		super(uri, Refrigerateur.class, owner);
	}

	@Override
	public void suspend() throws Exception {
		this.owner.handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((Refrigerateur)this.getServiceOwner()).suspend() ;
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
						((Refrigerateur)this.getServiceOwner()).resume() ;
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
						((Refrigerateur)this.getServiceOwner()).on() ;
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
						((Refrigerateur)this.getServiceOwner()).off() ;
						return null;
					}
				}) ;
	}

	@Override
	public int getConso() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).giveConso());
	}

	@Override
	public double getTempH() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).getTempHaut());
	}

	@Override
	public double getTempB() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).getTempBas());
	}

	@Override
	public boolean isWorking() throws Exception {
		return  this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).isWorking());
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).isOn());
	}

}
