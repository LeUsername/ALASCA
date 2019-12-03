package wattwattReborn.composants;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.ControleurConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.compteur.CompteurInPort;
import wattwattReborn.ports.compteur.CompteurOutPort;
import wattwattReborn.ports.controleur.ControleurInPort;
import wattwattReborn.ports.controleur.ControleurOutPort;

@OfferedInterfaces(offered = ICompteur.class)
@RequiredInterfaces(required = IControleur.class)
public class Compteur extends AbstractComponent {

	protected String COMPTEUR_URI;

	protected int consomation = 150;
	protected CompteurInPort contin;
	protected CompteurOutPort contout;

	public Compteur(String uri, String compteurIn, String compteurOut) throws Exception {
		super(uri, 1, 1);
		COMPTEUR_URI = uri;

		contin = new CompteurInPort(compteurIn, this);
		contin.publishPort();

		contout = new CompteurOutPort(compteurOut, this);
		contout.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	public String getCOMPTEUR_URI() {
		return COMPTEUR_URI;
	}

	
	public int getConso() throws Exception {
		
		return consomation;
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Compteur starting");
		try {
			this.doPortConnection( this.contout.getPortURI(), this.contin.getPortURI(),ControleurConnector.class.getCanonicalName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		// unpublish les ports
		try {
			this.contin.unpublishPort();
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
			this.contin.unpublishPort();
			this.contout.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

}
