package wattwatt.composants;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwatt.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;
import wattwatt.interfaces.appareils.planifiable.lavelinge.ILaveLinge;
import wattwatt.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwatt.interfaces.compteur.ICompteur;
import wattwatt.interfaces.controleur.IControleur;
import wattwatt.interfaces.sources.aleatoire.eolienne.IEolienne;
import wattwatt.interfaces.sources.intermittent.IGroupeElectrogene;
import wattwatt.ports.appareils.incontrolable.sechecheveux.SecheCheveuxOutPort;
import wattwatt.ports.appareils.planifiable.lavelinge.LaveLingeOutPort;
import wattwatt.ports.appareils.suspensible.refrigerateur.RefrigerateurOutPort;
import wattwatt.ports.compteur.CompteurOutPort;
import wattwatt.ports.sources.aleatoire.eolienne.EolienneOutPort;
import wattwatt.ports.sources.intermittent.groupeelectrogene.GroupeElectrogeneOutPort;
import wattwatt.tools.GroupeElectrogene.GroupreElectrogeneReglage;
import wattwatt.tools.controleur.ControleurReglage;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = { ICompteur.class, IRefrigerateur.class, ISecheCheveux.class, IEolienne.class,
		ILaveLinge.class, IGroupeElectrogene.class })
public class Controleur extends AbstractComponent {
	protected String cptin;
	protected String refrin;
	protected String sechin;
	protected String eoin;
	protected String lavein;
	protected String groupein;

	protected CompteurOutPort cptout;
	protected RefrigerateurOutPort refriout;
	protected SecheCheveuxOutPort sechout;
	protected EolienneOutPort eoout;
	protected LaveLingeOutPort laveout;
	protected GroupeElectrogeneOutPort groupeout;

	protected int allCons;

	public Controleur(String uri, String compteurIn, String compteurOut, String refriIn, String refriOut, String sechin,
			String sechOut, String eoIn, String eoOut, String laveIn, String laveOut, String groupeIn, String groupeOut)
			throws Exception {
		super(uri, 1, 6);

		this.cptin = compteurIn;
		this.refrin = refriIn;
		this.sechin = sechin;
		this.eoin = eoIn;
		this.lavein = laveIn;
		this.groupein = groupeIn;

		this.cptout = new CompteurOutPort(compteurOut, this);
		this.cptout.publishPort();

		this.refriout = new RefrigerateurOutPort(refriOut, this);
		this.refriout.publishPort();

		this.sechout = new SecheCheveuxOutPort(sechOut, this);
		this.sechout.publishPort();

		this.eoout = new EolienneOutPort(eoOut, this);
		this.eoout.publishPort();

		this.laveout = new LaveLingeOutPort(laveOut, this);
		this.laveout.publishPort();

		this.groupeout = new GroupeElectrogeneOutPort(groupeOut, this);
		this.groupeout.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
	}

	@Override
	public void execute() throws Exception {
		super.execute();

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {

				try {
					while (true) {
						((Controleur) this.getTaskOwner()).allCons = ((Controleur) this.getTaskOwner()).cptout
								.getAllConso();
						((Controleur) this.getTaskOwner())
								.logMessage("Compteur>> : " + ((Controleur) this.getTaskOwner()).allCons);
						Thread.sleep(ControleurReglage.MAJ_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				int cons = 0;
				try {
					((Controleur) this.getTaskOwner()).refriout.On();
					while (true) {
						cons = ((Controleur) this.getTaskOwner()).allCons;
						if (cons > 1220 && ((Controleur) this.getTaskOwner()).refriout.isWorking()) {
							((Controleur) this.getTaskOwner()).refriout.suspend();
						} else {
							if (!((Controleur) this.getTaskOwner()).refriout.isWorking()) {
								((Controleur) this.getTaskOwner()).refriout.resume();
							}
						}
						if (((Controleur) this.getTaskOwner()).refriout.isOn()
								&& ((Controleur) this.getTaskOwner()).refriout.isWorking()) {
							((Controleur) this.getTaskOwner()).logMessage("Refri>> ON and Working Conso : [ "
									+ ((Controleur) this.getTaskOwner()).refriout.getConso() + " ] : ");
						}
						Thread.sleep(ControleurReglage.MAJ_RATE);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						if (((Controleur) this.getTaskOwner()).sechout.isOn()) {
							((Controleur) this.getTaskOwner()).logMessage("SecheCheveux>> ON Conso : [ "
									+ ((Controleur) this.getTaskOwner()).sechout.getConso() + " ] : ");
						}
						Thread.sleep(ControleurReglage.MAJ_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 1000, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controleur) this.getTaskOwner()).eoout.On();
					Random rand = new Random(); // use to simulate wind condition for now
					while (true) {
						if (((Controleur) this.getTaskOwner()).eoout.isOn()) {
							((Controleur) this.getTaskOwner()).logMessage("Eolienne>> ON Prod : [ "
									+ ((Controleur) this.getTaskOwner()).eoout.getEnergie() + " ] : ");
						} else {
							((Controleur) this.getTaskOwner()).logMessage("Eolienne>> OFF Prod : [ "
									+ ((Controleur) this.getTaskOwner()).eoout.getEnergie() + " ] : ");
						}
						if (rand.nextInt(100) > 60) {

							if (((Controleur) this.getTaskOwner()).eoout.isOn()) {
								((Controleur) this.getTaskOwner()).eoout.Off();
							} else {
								((Controleur) this.getTaskOwner()).eoout.On();
							}
						}

						Thread.sleep(ControleurReglage.MAJ_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					((Controleur) this.getTaskOwner()).laveout.On();
					// need to adapt to the energy consumption and production => Simulation.jar
					Random rand = new Random();
					while (true) {
						if (!((Controleur) this.getTaskOwner()).laveout.isWorking()) {
							boolean changementMode = rand.nextBoolean();
							if (changementMode) {
								((Controleur) this.getTaskOwner()).laveout.ecoLavage();
							} else {
								((Controleur) this.getTaskOwner()).laveout.premiumLavage();
							}
						}
						((Controleur) this.getTaskOwner())
								.logMessage("LaveLinge>> ON: "+ ((Controleur) this.getTaskOwner()).laveout.isOn() + " Conso: " + ((Controleur) this.getTaskOwner()).laveout.getConso());

						Thread.sleep(ControleurReglage.MAJ_RATE);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 100, TimeUnit.MILLISECONDS);

		this.scheduleTask(new AbstractComponent.AbstractTask() {
			@Override
			public void run() {
				try {
					while (true) {
						((Controleur) this.getTaskOwner()).groupeout.on();
						while (((Controleur) this.getTaskOwner()).groupeout.isOn()) {
							if (((Controleur) this.getTaskOwner()).groupeout.isOn()) {
								((Controleur) this.getTaskOwner()).logMessage("Groupe Electro>> ON prod : ["
										+ ((Controleur) this.getTaskOwner()).groupeout.getEnergie() + "]"
										+ " fuel at : " + ((Controleur) this.getTaskOwner()).groupeout.fuelQuantity()
										+ " / " + GroupreElectrogeneReglage.FUEL_CAPACITY);
							}
							Thread.sleep(ControleurReglage.MAJ_RATE);
						}
						Thread.sleep(2 * ControleurReglage.MAJ_RATE);
						((Controleur) this.getTaskOwner()).groupeout.addFuel(GroupreElectrogeneReglage.FUEL_CAPACITY);

					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, 3000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		try {
			this.cptout.unpublishPort();
			this.refriout.unpublishPort();
			this.sechout.unpublishPort();
			this.eoout.unpublishPort();
			this.laveout.unpublishPort();
			this.groupeout.unpublishPort();
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
