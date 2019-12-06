package wattwattReborn.composants;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.appareils.incontrolable.sechecheveux.ISecheCheveux;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.interfaces.sources.aleatoire.eolienne.IEolienne;
import wattwattReborn.ports.appareils.incontrolable.sechecheveux.SecheCheveuxOutPort;
import wattwattReborn.ports.appareils.suspensible.refrigerateur.RefrigerateurOutPort;
import wattwattReborn.ports.compteur.CompteurOutPort;
import wattwattReborn.ports.sources.aleatoire.eolienne.EolienneOutPort;
import wattwattReborn.tools.controleur.ControleurReglage;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = { ICompteur.class, IRefrigerateur.class, ISecheCheveux.class, IEolienne.class})
public class Controleur extends AbstractComponent {
	protected String cptin;
	protected String refrin;
	protected String sechin;
	protected String eoin;

	protected CompteurOutPort cptout;
	protected RefrigerateurOutPort refriout;
	protected SecheCheveuxOutPort sechout;
	protected EolienneOutPort eoout;

	protected int allCons;

	public Controleur(String uri, String compteurIn, String compteurOut, String refriIn, String refriOut, String sechin,
			String sechOut, String eoIn, String eoOut) throws Exception {
		super(uri, 1, 4);

		this.cptin = compteurIn;
		this.refrin = refriIn;
		this.sechin = sechin;
		this.eoin = eoIn;

		this.cptout = new CompteurOutPort(compteurOut, this);
		this.cptout.publishPort();

		this.refriout = new RefrigerateurOutPort(refriOut, this);
		this.refriout.publishPort();

		this.sechout = new SecheCheveuxOutPort(sechOut, this);
		this.sechout.publishPort();

		this.eoout = new EolienneOutPort(eoOut, this);
		this.eoout.publishPort();

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
						if (((Controleur) this.getTaskOwner()).refriout.isOn() && ((Controleur) this.getTaskOwner()).refriout.isWorking() ) {
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
						}
						else {
							((Controleur) this.getTaskOwner()).logMessage("Eolienne>> OFF Prod : [ "
									+ ((Controleur) this.getTaskOwner()).eoout.getEnergie() + " ] : ");
						}
						if( rand.nextInt(100)>60) {
							
							if(((Controleur) this.getTaskOwner()).eoout.isOn()) {
								((Controleur) this.getTaskOwner()).eoout.Off();
							}	
							else {
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
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		try {
			this.cptout.unpublishPort();
			this.refriout.unpublishPort();
			this.sechout.unpublishPort();
			this.eoout.unpublishPort();
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
