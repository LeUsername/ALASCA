package wattwatt.connecteurs.appareils.planifiable;

import wattwatt.connecteurs.appareils.AppareilConnector;
import wattwatt.interfaces.appareils.planifiable.IPlanifiable;

public abstract class PlanifiableConnector extends AppareilConnector implements IPlanifiable {

	@Override
	public boolean canDelay(int delay) throws Exception {
		return ((IPlanifiable) this.offering).canDelay(delay);
	}

	@Override
	public boolean canAdvance(int advance) throws Exception {
		return ((IPlanifiable) this.offering).canAdvance(advance);
	}

	@Override
	public int durationWork() throws Exception {
		return ((IPlanifiable) this.offering).durationWork();
	}

	@Override
	public int startingTime() throws Exception {
		return ((IPlanifiable) this.offering).startingTime();
	}

	@Override
	public int endingTime() throws Exception {
		return ((IPlanifiable) this.offering).endingTime();
	}

	@Override
	public void endBefore(int end) throws Exception {
		((IPlanifiable) this.offering).endBefore(end);

	}

	@Override
	public void startAt(int debut) throws Exception {
		((IPlanifiable) this.offering).startAt(debut);

	}

	@Override
	public void late(int delay) throws Exception {
		((IPlanifiable) this.offering).late(delay);
	}

	@Override
	public void advance(int advance) throws Exception {
		((IPlanifiable) this.offering).advance(advance);

	}

}
