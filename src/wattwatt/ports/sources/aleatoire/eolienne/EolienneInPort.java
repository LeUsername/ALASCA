package wattwatt.ports.sources.aleatoire.eolienne;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import wattwatt.composants.sources.aleatoire.eolienne.Eolienne;
import wattwatt.interfaces.sources.aleatoire.eolienne.IEolienne;

public class EolienneInPort extends AbstractInboundPort implements IEolienne {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EolienneInPort(String uri, ComponentI owner) throws Exception {
		super(uri, IEolienne.class, owner);
	}

	@Override
	public int getEnergie() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Eolienne) owner).getEnergie());
	}

	@Override
	public boolean isOn() throws Exception {
		return this.getOwner().handleRequestSync(owner -> ((Eolienne) owner).isOn());
	}

	@Override
	public void On() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Eolienne) this.getServiceOwner()).On();
				return null;
			}
		});

	}

	@Override
	public void Off() throws Exception {
		this.owner.handleRequestAsync(new AbstractComponent.AbstractService<Void>() {
			@Override
			public Void call() throws Exception {
				((Eolienne) this.getServiceOwner()).Off();
				return null;
			}
		});

	}

}
