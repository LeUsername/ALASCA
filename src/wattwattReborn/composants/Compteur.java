package wattwattReborn.composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.ports.compteur.CompteurInPort;
import wattwattReborn.ports.compteur.CompteurOutPort;

@OfferedInterfaces(offered = ICompteur.class)
public class Compteur extends AbstractComponent {

	protected String COMPTEUR_URI;

	protected int consomation = 150;
	
	protected CompteurInPort cptin;

	public Compteur(String uri, String compteurIn) throws Exception {
		super(uri, 1, 1);
		COMPTEUR_URI = uri;

		cptin = new CompteurInPort(compteurIn, this);
		cptin.publishPort();

		this.tracer.setRelativePosition(0, 1);
	}

	public String getCOMPTEUR_URI() {
		return COMPTEUR_URI;
	}

	
	public int giveConso() throws Exception {
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
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Compteur shutdown");
		// unpublish les ports
		try {
			this.cptin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		// unpublish les ports
		try {
			this.cptin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

}
