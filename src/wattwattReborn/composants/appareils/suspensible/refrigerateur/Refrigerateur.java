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

@OfferedInterfaces(offered = IRefrigerateur.class)
@RequiredInterfaces(required = IControleur.class)
public class Refrigerateur extends AbstractComponent {

	protected final String REFRIGERATEUR_URI;
	protected RefrigerateurInPort refrin;
	
	protected final double TEMP_H_MIN = 2;
	protected final double TEMP_B_MIN = 8;
	protected final double TEMP_H_MAX = 6;
	protected final double TEMP_B_MAX = 12;

	protected double tempH;
	protected double tempB;

	protected boolean isOn;
	protected int conso;
	protected boolean isWorking;

	protected Refrigerateur(String uri, String refriIn) throws Exception {
		super(uri, 1, 1);

		REFRIGERATEUR_URI = uri;

		refrin = new RefrigerateurInPort(refriIn, this);
		refrin.publishPort();

		this.tempH = 3.0;
		this.tempB = 8.0;

		this.tracer.setRelativePosition(1, 0);
	}

	public double getTempHaut() {
		return tempH;
	}

	public double getTempBas() {
		return tempB;
	}

	public void suspend() {
		if (isOn) {
			isWorking = false;
		}

	}

	public void resume() {
		if (isOn) {
			isWorking = true;
		}

	}

	public void on() {
		isOn = true;
		isWorking = true;
	}

	public void off() {
		isOn = false;
		isWorking = false;
	}

	public void regule() {
		if (this.isOn) {
			if (this.isWorking) {

				if (this.tempH > this.TEMP_H_MIN) {
					this.tempH--;
				}
				if (this.tempB > this.TEMP_B_MIN) {
					this.tempB--;
				}
				this.conso += 100; // a enlever ca
			} else {
				if (this.tempH < this.TEMP_H_MAX) {
					this.tempH++;
				}
				if (this.tempB < this.TEMP_B_MAX) {
					this.tempB++;
				}
				if (this.conso - 50 <= 0) {
					this.conso = 0;
				} else {
					this.conso -= 50; // a enlever ca
				}

			}
		} else {
			if (this.conso - 50 <= 0) {
				this.conso = 0;
			} else {
				this.conso -= 50; // a enlever ca
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
						Thread.sleep(500);
						if (rand.nextInt(100) > 90) {
							((Refrigerateur) this.getTaskOwner()).off();
							for(int tick = 0; tick<5; tick++) {
								Thread.sleep(500 + rand.nextInt(1000));
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
