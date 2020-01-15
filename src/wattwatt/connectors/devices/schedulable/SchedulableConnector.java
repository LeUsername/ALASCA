package wattwatt.connectors.devices.schedulable;

import wattwatt.connectors.devices.DeviceConnector;
import wattwatt.interfaces.devices.schedulable.ISchedulable;

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
