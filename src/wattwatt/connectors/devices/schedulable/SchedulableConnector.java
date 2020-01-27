package wattwatt.connectors.devices.schedulable;

import wattwatt.connectors.devices.DeviceConnector;
import wattwatt.interfaces.devices.schedulable.ISchedulable;

/**
 * The class <code>SchedulableConnector</code> implements a connector between
 * the <code>IController</code> and the <code>ISchedulable</code> interfaces.
 *
 * <p><strong>Description</strong></p>
 * 
 * It implements the required interface <code>IController</code> and in the
 * methods it calls the corresponding offered method
 * <code>ISchedulable</code>.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public abstract class SchedulableConnector extends DeviceConnector implements ISchedulable {

	@Override
	public boolean canDelay(int delay) throws Exception {
		return ((ISchedulable) this.offering).canDelay(delay);
	}

	@Override
	public boolean canAdvance(int advance) throws Exception {
		return ((ISchedulable) this.offering).canAdvance(advance);
	}

	@Override
	public int durationWork() throws Exception {
		return ((ISchedulable) this.offering).durationWork();
	}

	@Override
	public int startingTime() throws Exception {
		return ((ISchedulable) this.offering).startingTime();
	}

	@Override
	public int endingTime() throws Exception {
		return ((ISchedulable) this.offering).endingTime();
	}

	@Override
	public void endBefore(int end) throws Exception {
		((ISchedulable) this.offering).endBefore(end);

	}

	@Override
	public void startAt(int debut) throws Exception {
		((ISchedulable) this.offering).startAt(debut);

	}

	@Override
	public void late(int delay) throws Exception {
		((ISchedulable) this.offering).late(delay);
	}

	@Override
	public void advance(int advance) throws Exception {
		((ISchedulable) this.offering).advance(advance);

	}

}
