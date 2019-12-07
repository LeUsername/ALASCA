package wattwattReborn.composants.sources.intermittent.groupeelectrogene;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.interfaces.sources.intermittent.IGroupeElectrogene;
import wattwattReborn.ports.sources.intermittent.groupeelectrogene.GroupeElectrogeneInPort;
import wattwattReborn.tools.GroupeElectrogene.GroupreElectrogeneReglage;

@OfferedInterfaces(offered = IGroupeElectrogene.class)
@RequiredInterfaces(required = IControleur.class)
public class GroupeElectrogene extends AbstractComponent {

	protected GroupeElectrogeneInPort groupein;

	protected boolean isOn;
	protected int production;
	protected int fuelQuantity;

	protected GroupeElectrogene(String uri, String groupeIn) throws Exception {
		super(uri, 1, 1);

		this.groupein = new GroupeElectrogeneInPort(groupeIn, this);
		this.groupein.publishPort();

		this.tracer.setRelativePosition(2, 1);
	}

	public int getEnergie() throws Exception {
		return this.production;
	}

	public boolean fuelIsEmpty() throws Exception {
		return this.fuelQuantity == 0;
	}

	public boolean fuelIsFull() throws Exception {
		return this.fuelQuantity == GroupreElectrogeneReglage.FUEL_CAPACITY;
	}

	public int fuelQuantity() throws Exception {
		return this.fuelQuantity;
	}

	public void on() throws Exception {
		this.isOn = true;
	}

	public void off() throws Exception {
		this.isOn = false;
	}

	public void addFuel(int quantity) throws Exception {
		if (this.fuelQuantity + quantity >= GroupreElectrogeneReglage.FUEL_CAPACITY) {
			this.fuelQuantity = GroupreElectrogeneReglage.FUEL_CAPACITY;
		} else {
			this.fuelQuantity += GroupreElectrogeneReglage.FUEL_CAPACITY;
		}
	}

	public boolean isOn() {
		return this.isOn;
	}

	public void behave() throws Exception {
		if (this.isOn && !this.fuelIsEmpty()) {
			this.logMessage("Groupe is producing");
			this.production += GroupreElectrogeneReglage.PROD_THR;
			if (this.fuelQuantity - GroupreElectrogeneReglage.PROD_THR <= 0) {
				this.fuelQuantity = 0;
			} else {
				this.fuelQuantity -= GroupreElectrogeneReglage.PROD_THR;
			}
		} else {
			if (this.fuelIsEmpty()) {
				this.off();
				this.logMessage("No more fuel");
			}
		}
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.fuelQuantity = GroupreElectrogeneReglage.FUEL_CAPACITY;

		this.logMessage("Groupe Electro starting");
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
						((GroupeElectrogene) this.getTaskOwner()).behave();
						;
						Thread.sleep(GroupreElectrogeneReglage.REGUL_RATE);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Groupe Electro shutdown");
		try {
			this.groupein.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

}
