package wattwattReborn.ports.appareils.planifiable.lavelinge;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwattReborn.interfaces.appareils.planifiable.lavelinge.ILaveLinge;

public class LaveLingeOutPort extends AbstractOutboundPort implements ILaveLinge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LaveLingeOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, ILaveLinge.class, owner);
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((ILaveLinge) this.connector).isWorking();
	}

	@Override
	public boolean canDelay(int delay) throws Exception {
		return ((ILaveLinge) this.connector).canDelay(delay);
	}

	@Override
	public int durationWork() throws Exception {
		return ((ILaveLinge) this.connector).durationWork();
	}

	@Override
	public int startingTime() throws Exception {
		return ((ILaveLinge) this.connector).startingTime();
	}

	@Override
	public int endingTime() throws Exception {
		return ((ILaveLinge) this.connector).endingTime();
	}

	@Override
	public void endBefore(int end) throws Exception {
		((ILaveLinge) this.connector).endBefore(end);

	}

	@Override
	public void startAt(int debut) throws Exception {
		((ILaveLinge) this.connector).startAt(debut);

	}

	@Override
	public void late(int delay) throws Exception {
		((ILaveLinge) this.connector).late(delay);

	}

	@Override
	public void advance(int advance) throws Exception {
		((ILaveLinge) this.connector).advance(advance);

	}

	@Override
	public void On() throws Exception {
		((ILaveLinge) this.connector).On();

	}

	@Override
	public void Off() throws Exception {
		((ILaveLinge) this.connector).Off();

	}

	@Override
	public int getConso() throws Exception {
		return ((ILaveLinge) this.connector).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((ILaveLinge) this.connector).isOn();
	}

	@Override
	public void ecoLavage() throws Exception {
		((ILaveLinge) this.connector).ecoLavage();

	}

	@Override
	public void premiumLavage() throws Exception {
		((ILaveLinge) this.connector).premiumLavage();

	}

}
