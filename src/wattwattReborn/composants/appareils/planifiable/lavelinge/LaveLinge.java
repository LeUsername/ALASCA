package wattwattReborn.composants.appareils.planifiable.lavelinge;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.appareils.planifiable.lavelinge.ILaveLinge;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.appareils.planifiable.lavelinge.LaveLingeInPort;
import wattwattReborn.tools.lavelinge.LaveLingeReglage;

@OfferedInterfaces(offered = ILaveLinge.class)
@RequiredInterfaces(required = IControleur.class)
public class LaveLinge extends AbstractComponent {

	protected LaveLingeInPort lavein;

	protected LaveLinge(String uri, String laveIn) throws Exception {
		super(uri, 1, 1);

		this.lavein = new LaveLingeInPort(laveIn, this);
		this.lavein.publishPort();

		this.tracer.setRelativePosition(1, 2);

	}

	public boolean isWorking() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canDelay(int delay) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public int durationWork() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public int startingTime() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public int endingTime() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public void endBefore(int end) throws Exception {
		// TODO Auto-generated method stub

	}

	public void startAt(int debut) throws Exception {
		// TODO Auto-generated method stub

	}

	public void late(int delay) throws Exception {
		// TODO Auto-generated method stub

	}

	public void advance(int advance) throws Exception {
		// TODO Auto-generated method stub

	}

	public void On() throws Exception {
		// TODO Auto-generated method stub

	}

	public void Off() throws Exception {
		// TODO Auto-generated method stub

	}

	public int getConso() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isOn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void ecoLavage() throws Exception {
		// TODO Auto-generated method stub

	}

	public void premiumLavage() throws Exception {
		// TODO Auto-generated method stub

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
				try {
					while (true) {
						((LaveLinge) this.getTaskOwner()).logMessage("lave linge alive");
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
