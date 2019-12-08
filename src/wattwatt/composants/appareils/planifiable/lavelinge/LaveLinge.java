package wattwatt.composants.appareils.planifiable.lavelinge;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.appareils.planifiable.lavelinge.ILaveLinge;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.ports.appareils.planifiable.lavelinge.LaveLingeInPort;
import wattwatt.tools.lavelinge.LaveLingeMode;
import wattwatt.tools.lavelinge.LaveLingeReglage;

@OfferedInterfaces(offered = ILaveLinge.class)
@RequiredInterfaces(required = IControleur.class)
public class LaveLinge extends AbstractComponent {

	protected LaveLingeInPort lavein;

	protected LaveLingeMode mode;
	protected int startingTime;
	protected int durationWork;

	protected boolean isOn;
	protected boolean isWorking;
	protected int conso;

	protected LaveLinge(String uri, String laveIn) throws Exception {
		super(uri, 1, 1);

		this.lavein = new LaveLingeInPort(laveIn, this);
		this.lavein.publishPort();

		ecoLavage();
		this.startingTime = LaveLingeReglage.START;
		this.tracer.setRelativePosition(1, 2);

	}

	public boolean canDelay(int delay) {
		return startingTime + delay < LaveLingeReglage.END;
	}

	public boolean canAdvance(int advance) {
		return startingTime - advance >= LaveLingeReglage.START;
	}

	public int durationWork() {
		return durationWork;
	}

	public int startingTime() {
		return startingTime;
	}

	public int endingTime() {
		return startingTime + durationWork;
	}

	public void endBefore(int end) {
		assert end < LaveLingeReglage.END;
		startingTime = end;
	}

	public void startAt(int debut) {
		assert debut >= LaveLingeReglage.START;
		startingTime = debut;
	}

	public void late(int delay) {
		if (canDelay(delay)) {
			startingTime += delay;
		}
	}

	public void advance(int advance) {
		if (canAdvance(advance)) {
			startingTime -= advance;
		}
	}

	public void On() {
		this.isOn = true;
	}

	public void Off() {
		this.isOn = false;
	}

	public boolean isOn() {
		return this.isOn;
	}

	public boolean isWorking() {
		return this.isWorking;
	}

	public int giveConso() {
		if (isOn()) {
			return conso;
		} else {
			return 0;
		}
	}

	public void ecoLavage() {
		mode = LaveLingeMode.ECO;
		conso = LaveLingeReglage.CONSO_ECO_MODE;
		durationWork = LaveLingeReglage.DURATION_ECO_MODE;
	}

	public void premiumLavage() {
		mode = LaveLingeMode.PREMIUM;
		conso = LaveLingeReglage.CONSO_PREMIUM_MODE;
		durationWork = LaveLingeReglage.CONSO_PREMIUM_MODE;
	}

	public LaveLingeMode getMode() {
		return mode;
	}

	public void behave(Random rand) throws InterruptedException {
		if (this.isOn) {
			this.isWorking = true;
			if (getMode() == LaveLingeMode.ECO) {
				this.logMessage(
						"Washing machine starting eco mode at: " + this.startingTime + " for " + this.durationWork);
				Thread.sleep(this.durationWork / 2);
				this.logMessage("Still working for " + this.durationWork / 2);
				Thread.sleep(this.durationWork / 2);
			} else {
				this.logMessage(
						"Washing machine starting premium mode at: " + this.startingTime + " for " + this.durationWork);
				Thread.sleep(this.durationWork / 2);
				this.logMessage("Still working for " + this.durationWork / 2);
				Thread.sleep(this.durationWork / 2);
			}
			this.isWorking = false;
		}
	}

	public void printState() {
		this.logMessage(">>> isOn : [" + this.isOn + "] Mode : [" + this.getMode()
				+ " ] \n>>> Conso depuis le debut : [" + this.giveConso() + " ]\n");
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("LaveLinge starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				Random rand = new Random();
				try {
					while (true) {
						((LaveLinge) this.getTaskOwner()).behave(rand);
						((LaveLinge) this.getTaskOwner()).printState();
						Thread.sleep(LaveLingeReglage.REGUL_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("LaveLinge shutdown");
		try {
			this.lavein.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

}
