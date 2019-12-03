package wattwattReborn.composants;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
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

	public Compteur(String uri, String controleurIn, String controleurOut) throws Exception {
		super(uri, 1, 1);
		COMPTEUR_URI = uri;

		CompteurInPort contin = new CompteurInPort(controleurIn, this);
		contin.publishPort();

		CompteurOutPort contout = new CompteurOutPort(controleurOut, this);
		contout.publishPort();

		this.tracer.setRelativePosition(0, 0);
	}

	public String getCOMPTEUR_URI() {
		return COMPTEUR_URI;
	}

	public int getAllConso() throws Exception {
		return consomation;
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
	public void execute() {
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		// unpublish les ports
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		// unpublish les ports
		super.finalise();
	}

}
