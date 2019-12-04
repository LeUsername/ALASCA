package wattwattReborn.composants;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.appareils.suspensibles.refrigerateur.RefrigerateurConnector;
import wattwattReborn.interfaces.appareils.suspensible.refrigerateur.IRefrigerateur;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.appareils.suspensible.refrigerateur.RefrigerateurOutPort;
import wattwattReborn.ports.compteur.CompteurOutPort;
import wattwattReborn.tools.controleur.ControleurReglage;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = { ICompteur.class, IRefrigerateur.class })
public class Controleur extends AbstractComponent {
	protected String cptin;
	protected String refrin;

	protected CompteurOutPort cptout;
	protected RefrigerateurOutPort refriout;

	public Controleur(String uri, String compteurIn, String compteurOut, String refriIn, String refriOut)
			throws Exception {
		super(uri, 1, 1);

		this.cptin = compteurIn;
		this.refrin = refriIn;

		this.cptout = new CompteurOutPort(compteurOut, this);
		this.cptout.publishPort();

		this.refriout = new RefrigerateurOutPort(refriOut, this);
		this.refriout.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");

		try {
			this.doPortConnection(this.cptout.getPortURI(), this.cptin, CompteurConnector.class.getCanonicalName());

			this.doPortConnection(this.refriout.getPortURI(), this.refrin,
					RefrigerateurConnector.class.getCanonicalName());

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Thread.sleep(100);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		try {
			this.doPortDisconnection(this.cptout.getPortURI()) ;
			this.doPortDisconnection(this.refriout.getPortURI()) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

}
