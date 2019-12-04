package wattwattReborn.composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.connecteurs.ControleurConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.controleur.ControleurOutPort;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = ICompteur.class)
public class Controleur extends AbstractComponent {

	protected String CONTROLEUR_URI;

	protected String cptin;
	protected String refrin;

	protected ControleurOutPort contout;
	protected ControleurOutPort contout2;

	public Controleur(String uri, String controleurOut, String controleurOut2, String compteurIn, String refriIn)
			throws Exception {
		super(uri, 1, 1);
		CONTROLEUR_URI = uri;

		cptin = compteurIn;
		refrin = refriIn;

		contout = new ControleurOutPort(controleurOut, this);
		contout.publishPort();

		contout2 = new ControleurOutPort(controleurOut2, this);
		contout2.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	public String getCONTROLEUR_URI() {
		return CONTROLEUR_URI;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
		try {
			this.doPortConnection(this.contout.getPortURI(), this.cptin,
					ControleurConnector.class.getCanonicalName());

			this.doPortConnection(this.contout2.getPortURI(), this.refrin,
					ControleurConnector.class.getCanonicalName());

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
		int cons = 0;
		try {
			this.contout2.refriOn();
			while (true) {
				cons = this.contout.getAllConso();
				this.logMessage("Consomation : " + cons);
				if (cons > 1220) { // a changer la
					this.contout2.refriSuspend();
					this.logMessage("Refri>> suspend : [ .. ] Temp en Haut : [" + this.contout2.refriTempH()
							+ " ] Temp en Bas : [" + this.contout2.refriTempB() + " ] Conso depuis le debut : ["
							+ this.contout2.refriConso() + " ]");
				} else {
					this.contout2.refriResume();
					this.logMessage("Refri>> suspend : [ .. ] Temp en Haut : [" + this.contout2.refriTempH()
							+ " ] Temp en Bas : [" + this.contout2.refriTempB() + " ] Conso depuis le debut : ["
							+ this.contout2.refriConso() + " ]");
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		// unpublish les ports
		try {
			this.contout.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		// unpublish les ports
		try {
			this.contout.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

}
