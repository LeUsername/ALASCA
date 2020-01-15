package wattwatt.components;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.electricmeter.IElectricMeter;
import wattwatt.ports.electricmeter.ElectricMeterInPort;
import wattwatt.tools.electricmeter.ElectricMeterSetting;

@OfferedInterfaces(offered = IElectricMeter.class)
@RequiredInterfaces(required = IController.class)
public class ElectricMeter extends AbstractComponent {

	protected ElectricMeterInPort cptin;

	protected int consomation;

	protected ElectricMeter(String uri, String compteurIn) throws Exception {
		super(uri, 1, 1);

		this.cptin = new ElectricMeterInPort(compteurIn, this);
		this.cptin.publishPort();

		this.tracer.setRelativePosition(0, 1);
	}

	public int giveConso() throws Exception {
		return consomation;
	}

	public void majConso() {
		Random rand = new Random();
		this.consomation = ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION
				+ rand.nextInt(ElectricMeterSetting.MAX_THR_HOUSE_CONSUMPTION - ElectricMeterSetting.MIN_THR_HOUSE_CONSUMPTION);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Compteur starting");
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
						((ElectricMeter) this.getTaskOwner()).majConso();
						Thread.sleep(ElectricMeterSetting.UPDATE_RATE);
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
			this.cptin.unpublishPort();
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
