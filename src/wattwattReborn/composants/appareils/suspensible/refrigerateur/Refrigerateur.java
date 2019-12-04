package wattwattReborn.composants.appareils.suspensible.refrigerateur;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.appareils.suspensible.refrigerateur.RefrigerateurInPort;
import wattwattReborn.tools.refrigerateur.RefrigerateurReglage;

@OfferedInterfaces(offered = IRefrigerateur.class)
@RequiredInterfaces(required = IControleur.class)
public class Refrigerateur extends AbstractComponent {

	protected RefrigerateurInPort refrin;

	protected double tempH;
	protected double tempB;

	protected boolean isOn;
	protected boolean isWorking;
	protected int conso;

	protected Refrigerateur(String uri, String refriIn) throws Exception {
		super(uri, 1, 1);

		this.refrin = new RefrigerateurInPort(refriIn, this);
		this.refrin.publishPort();

		this.tempH = RefrigerateurReglage.TEMP_H_INIT;
		this.tempB = RefrigerateurReglage.TEMP_B_INIT;

		this.tracer.setRelativePosition(1, 0);
	}

	public double getTempHaut() {
		return this.tempH;
	}

	public double getTempBas() {
		return this.tempB;
	}

	public void suspend() {
		this.isWorking = false;
	}

	public void resume() {
		if (this.isOn) {
			this.isWorking = true;
		} else {
			this.isWorking = false;
		}

	}

	public void on() {
		this.isOn = true;
		this.isWorking = true;
	}

	public void off() {
		this.isOn = false;
		this.isWorking = false;
	}

	public boolean isWorking() {
		return this.isWorking;
	}

	public boolean isOn() {
		return this.isOn;
	}

	public void regule() {
		if (this.isOn) {
			if (this.isWorking) {

				if (this.tempH > RefrigerateurReglage.TEMP_H_MIN) {
					this.tempH--;
				}
				if (this.tempB > RefrigerateurReglage.TEMP_B_MIN) {
					this.tempB--;
				}
				this.conso += RefrigerateurReglage.CONSOMMATION_ACTIVE;
			} else {
				if (this.tempH < RefrigerateurReglage.TEMP_H_MAX) {
					this.tempH++;
				}
				if (this.tempB < RefrigerateurReglage.TEMP_B_MAX) {
					this.tempB++;
				}
				if (this.conso - RefrigerateurReglage.CONSOMMATION_PASSIVE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= RefrigerateurReglage.CONSOMMATION_PASSIVE;
				}

			}
		} else {
			if (this.conso - RefrigerateurReglage.CONSOMMATION_PASSIVE <= 0) {
				this.conso = 0;
			} else {
				this.conso -= RefrigerateurReglage.CONSOMMATION_PASSIVE;
			}
		}
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Refrigerateur starting");
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
						((Refrigerateur) this.getTaskOwner()).regule();
						Thread.sleep(RefrigerateurReglage.REGUL_RATE);
						if (rand.nextInt(100) > 90) {
							((Refrigerateur) this.getTaskOwner()).off();
							for (int tick = 0; tick < 5; tick++) {
								Thread.sleep(RefrigerateurReglage.REGUL_RATE);
								((Refrigerateur) this.getTaskOwner()).regule();
							}

							((Refrigerateur) this.getTaskOwner()).on();
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
		this.logMessage("Compteur shutdown");
		try {
			this.refrin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		try {
			this.refrin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

	public int giveConso() {
		return conso;
	}

}
