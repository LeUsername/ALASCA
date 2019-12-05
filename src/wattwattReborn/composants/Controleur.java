package wattwattReborn.composants;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.appareils.incontrolable.sechecheveux.SecheCheveuxOutPort;
import wattwattReborn.ports.appareils.suspensible.refrigerateur.RefrigerateurOutPort;
import wattwattReborn.ports.compteur.CompteurOutPort;
import wattwattReborn.tools.controleur.ControleurReglage;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = { ICompteur.class, IRefrigerateur.class })
public class Controleur extends AbstractComponent {
	protected String cptin;
	protected String refrin;
	protected String sechin;

	protected CompteurOutPort cptout;
	protected RefrigerateurOutPort refriout;
	protected SecheCheveuxOutPort sechout;

	public Controleur(String uri, String compteurIn, String compteurOut, String refriIn, String refriOut, String sechin, String sechOut)
			throws Exception {
		super(uri, 1, 1);

		this.cptin = compteurIn;
		this.refrin = refriIn;
		this.sechin = sechin;

		this.cptout = new CompteurOutPort(compteurOut, this);
		this.cptout.publishPort();

		this.refriout = new RefrigerateurOutPort(refriOut, this);
		this.refriout.publishPort();
		
		this.sechout = new SecheCheveuxOutPort(sechOut, this);
		this.sechout.publishPort();

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
				int cons = 0;
				try {
					((Controleur) this.getTaskOwner()).refriout.On();
					while (true) {
						cons = ((Controleur) this.getTaskOwner()).cptout.getAllConso();
						((Controleur) this.getTaskOwner()).logMessage("Consomation : " + cons);
						if (cons > 1220 && ((Controleur) this.getTaskOwner()).refriout.isWorking()) {
							((Controleur) this.getTaskOwner()).refriout.suspend();
							((Controleur) this.getTaskOwner()).logMessage("Refri>> SUSPEND : Temp en Haut : ["
									+ ((Controleur) this.getTaskOwner()).refriout.getTempHaut() + " ] Temp en Bas : ["
									+ ((Controleur) this.getTaskOwner()).refriout.getTempBas()
									+ " ] Conso depuis le debut : ["
									+ ((Controleur) this.getTaskOwner()).refriout.getConso() + " ]");
						} else {
							if (!((Controleur) this.getTaskOwner()).refriout.isWorking()) {
								((Controleur) this.getTaskOwner()).refriout.resume();
								((Controleur) this.getTaskOwner()).logMessage("Refri>> RESUME : Temp en Haut : ["
										+ ((Controleur) this.getTaskOwner()).refriout.getTempHaut()
										+ " ] Temp en Bas : ["
										+ ((Controleur) this.getTaskOwner()).refriout.getTempBas()
										+ " ] Conso depuis le debut : ["
										+ ((Controleur) this.getTaskOwner()).refriout.getConso() + " ]");
							}
						}
						if(((Controleur) this.getTaskOwner()).sechout.isOn()) {
							((Controleur) this.getTaskOwner()).logMessage("SecheCheveux>> ON Conso : [ "+((Controleur) this.getTaskOwner()).sechout.getConso()+" ] : ");
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
