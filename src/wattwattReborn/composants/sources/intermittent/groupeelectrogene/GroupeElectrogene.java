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
	
	protected GroupeElectrogene(String uri, String groupeIn) throws Exception {
		super(uri,1,1);
		
		this.groupein = new GroupeElectrogeneInPort(groupeIn,this);
		this.groupein.publishPort();
		
		this.tracer.setRelativePosition(2, 1);
	}

	public int getEnergie() throws Exception {
		return 0;
	}

	public boolean fuelIsEmpty() throws Exception {
		return false;
	}

	public boolean fuelIsFull() throws Exception {
		return false;
	}

	public int fuelQuantity() throws Exception {
		return 0;
	}

	public void on() throws Exception {
	}

	public void off() throws Exception {
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
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
						((GroupeElectrogene) this.getTaskOwner()).logMessage("groupe is alive");
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
