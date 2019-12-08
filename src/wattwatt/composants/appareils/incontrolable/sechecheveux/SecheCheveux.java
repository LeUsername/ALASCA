package wattwatt.composants.appareils.incontrolable.sechecheveux;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.ports.appareils.incontrolable.sechecheveux.SecheCheveuxInPort;
import wattwatt.tools.sechecheveux.SecheCheveuxMode;
import wattwatt.tools.sechecheveux.SecheCheveuxReglage;

@OfferedInterfaces(offered = ISecheCheveux.class)
@RequiredInterfaces(required = IControleur.class)
public class SecheCheveux extends AbstractComponent {

	protected SecheCheveuxInPort sechin;

	protected SecheCheveuxMode mode;
	protected int powerLvl;
	protected boolean isOn;
	protected int conso;

	protected SecheCheveux(String uri, String sechin) throws Exception {
		super(uri, 1, 1);
		this.sechin = new SecheCheveuxInPort(sechin, this);
		this.sechin.publishPort();

		this.mode = SecheCheveuxMode.HOT_AIR;
		this.powerLvl = SecheCheveuxReglage.POWER_LVL_MIN;

		this.tracer.setRelativePosition(1, 1);
	}

	public void on() {
		this.isOn = true;
	}

	public void off() {
		this.isOn = false;
	}

	public int giveConso() {
		return conso;
	}

	public boolean isOn() {
		return isOn;
	}

	public void switchMode() {
		if (this.mode == SecheCheveuxMode.COLD_AIR) {
			this.mode = SecheCheveuxMode.HOT_AIR;
		} else {
			this.mode = SecheCheveuxMode.COLD_AIR;
		}
	}

	public void increasePower() {
		if (this.powerLvl + 1 >= SecheCheveuxReglage.POWER_LVL_MAX) {
			this.powerLvl = SecheCheveuxReglage.POWER_LVL_MAX;
		} else {
			this.powerLvl++;
		}

	}

	public void decreasePower() {
		if (this.powerLvl - 1 <= SecheCheveuxReglage.POWER_LVL_MIN) {
			this.powerLvl = SecheCheveuxReglage.POWER_LVL_MIN;
		} else {
			this.powerLvl--;
		}
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("SecheCheveux starting");
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void behave(Random rand) {
		if (this.isOn) {
			if (rand.nextBoolean()) {
				this.switchMode();
				if (rand.nextBoolean()) {
					this.increasePower();
				} else {
					this.decreasePower();
				}
			}
			if (this.mode == SecheCheveuxMode.COLD_AIR) {
				this.conso += SecheCheveuxReglage.CONSO_COLD_MODE * this.powerLvl;
			} else {
				this.conso += SecheCheveuxReglage.CONSO_HOT_MODE * this.powerLvl;
			}
		} else {
			if (this.mode == SecheCheveuxMode.COLD_AIR) {
				if (this.conso - SecheCheveuxReglage.CONSO_COLD_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= SecheCheveuxReglage.CONSO_COLD_MODE;
				}
			} else {
				if (this.conso - SecheCheveuxReglage.CONSO_HOT_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= SecheCheveuxReglage.CONSO_HOT_MODE;
				}
			}
		}
	}

	@Override
	public void execute() throws Exception {
		super.execute();
		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				Random rand = new Random();
				int useTime = 0;
				try {
					while (true) {
						if (rand.nextInt(100) > 98 && useTime == 0) {
							((SecheCheveux) this.getTaskOwner()).on();
							useTime = SecheCheveuxReglage.MIN_USE_TIME + rand.nextInt(SecheCheveuxReglage.MAX_USE_TIME);
							((SecheCheveux) this.getTaskOwner()).logMessage("seche cheveux ON for : " + useTime);
						} else {
							((SecheCheveux) this.getTaskOwner()).behave(rand);
							if (useTime - 1 <= 0) {
								useTime = 0;
							} else {
								useTime--;
							}
							Thread.sleep(SecheCheveuxReglage.REGUL_RATE);
							if (useTime <= 0) {
								((SecheCheveux) this.getTaskOwner()).logMessage("seche cheveux OFF");
								((SecheCheveux) this.getTaskOwner()).off();
							}
						}

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Eolienne shutdown");
		try {
			this.sechin.unpublishPort();
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
