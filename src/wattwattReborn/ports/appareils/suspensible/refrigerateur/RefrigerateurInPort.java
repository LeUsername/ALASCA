package wattwattReborn.ports.appareils.suspensible.refrigerateur;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;

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
	public int giveConsommation() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).giveConso());
	}

	@Override
	public double getTempHaut() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).getTempHaut());
	}

	@Override
	public double getTempBas() throws Exception {
		return this.getOwner().handleRequestSync(owner ->((Refrigerateur)owner).getTempBas());
	}

}
