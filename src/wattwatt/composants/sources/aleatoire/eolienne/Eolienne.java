package wattwatt.composants.sources.aleatoire.eolienne;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.interfaces.sources.aleatoire.eolienne.IEolienne;
import wattwatt.ports.sources.aleatoire.eolienne.EolienneInPort;
import wattwatt.tools.eolienne.EolienneReglage;

@OfferedInterfaces(offered = IEolienne.class)
@RequiredInterfaces(required = IControleur.class)
public class Eolienne extends AbstractComponent {

	protected EolienneInPort eoin;

	protected boolean isOn;
	protected int production;

	protected Eolienne(String uri, String eoIn) throws Exception {
		super(uri, 1, 1);

		this.eoin = new EolienneInPort(eoIn, this);
		this.eoin.publishPort();

		this.tracer.setRelativePosition(2, 0);
	}

	public void behave() {
		// production should depend on the power of the wind
		if (this.isOn) {
			this.production += EolienneReglage.PROD_THR;
		} else {

			if (this.production - EolienneReglage.PROD_THR <= 0) {
				this.production = 0;
			} else {

				this.production -= EolienneReglage.PROD_THR;
			}

		}
	}

	public int getEnergie() {
		return this.production;
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

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Eolienne starting");
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
						((Eolienne) this.getTaskOwner()).behave();
						((Eolienne) this.getTaskOwner())
								.logMessage("Production : [" + ((Eolienne) this.getTaskOwner()).production + "]");
						Thread.sleep(EolienneReglage.REGUL_RATE);
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
			this.eoin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

}
