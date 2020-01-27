package wattwatt.ports.devices.schedulable.washingmachine;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import wattwatt.interfaces.devices.schedulable.washingmachine.IWashingMachine;

/**
 * The class <code>WashingMachineOutPort</code>
 *
 * <p><strong>Description</strong></p>
 *  The OutBound port of the washing machine component
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class WashingMachineOutPort extends AbstractOutboundPort implements IWashingMachine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WashingMachineOutPort(String uri, ComponentI owner) throws Exception {
		super(uri, IWashingMachine.class, owner);
	}

	public boolean isWorking() throws Exception {
		return ((IWashingMachine) this.connector).isWorking();
	}

	@Override
	public boolean canDelay(int delay) throws Exception {
		return ((IWashingMachine) this.connector).canDelay(delay);
	}

	@Override
	public boolean canAdvance(int advance) throws Exception {
		return ((IWashingMachine) this.connector).canAdvance(advance);
	}

	@Override
	public int durationWork() throws Exception {
		return ((IWashingMachine) this.connector).durationWork();
	}

	@Override
	public int startingTime() throws Exception {
		return ((IWashingMachine) this.connector).startingTime();
	}

	@Override
	public int endingTime() throws Exception {
		return ((IWashingMachine) this.connector).endingTime();
	}

	@Override
	public void endBefore(int end) throws Exception {
		((IWashingMachine) this.connector).endBefore(end);

	}

	@Override
	public void startAt(int debut) throws Exception {
		((IWashingMachine) this.connector).startAt(debut);

	}

	@Override
	public void late(int delay) throws Exception {
		((IWashingMachine) this.connector).late(delay);

	}

	@Override
	public void advance(int advance) throws Exception {
		((IWashingMachine) this.connector).advance(advance);

	}

	@Override
	public void On() throws Exception {
		((IWashingMachine) this.connector).On();

	}

	@Override
	public void Off() throws Exception {
		((IWashingMachine) this.connector).Off();

	}

	@Override
	public int getConso() throws Exception {
		return ((IWashingMachine) this.connector).getConso();
	}

	@Override
	public boolean isOn() throws Exception {
		return ((IWashingMachine) this.connector).isOn();
	}

	@Override
	public void ecoWashing() throws Exception {
		((IWashingMachine) this.connector).ecoWashing();

	}

	@Override
	public void premiumWashing() throws Exception {
		((IWashingMachine) this.connector).premiumWashing();

	}

}
