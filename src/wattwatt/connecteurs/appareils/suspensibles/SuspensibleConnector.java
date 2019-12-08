package wattwatt.connecteurs.appareils.suspensibles;

import wattwatt.connecteurs.appareils.AppareilConnector;
import wattwatt.interfaces.appareils.suspensible.ISuspensible;

public abstract class SuspensibleConnector extends AppareilConnector implements ISuspensible {

	@Override
	public void suspend() throws Exception {
		((ISuspensible) this.offering).suspend();

	}

	@Override
	public void resume() throws Exception {
		((ISuspensible) this.offering).resume();
	}

	@Override
	public boolean isWorking() throws Exception {
		return ((ISuspensible) this.offering).isWorking();
	}
}
