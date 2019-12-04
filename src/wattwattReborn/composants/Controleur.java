package wattwattReborn.composants;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import wattwattReborn.connecteurs.ControleurCompteurConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.ports.controleur.ControleurOutPort;

@RequiredInterfaces(required = ICompteur.class)
public class Controleur extends AbstractComponent {
	
	protected String CONTROLEUR_URI;
	
	protected String cptin;
	
	protected ControleurOutPort contout;
	
	public Controleur(String uri,String controleurOut, String compteurIn) throws Exception {
		super(uri, 1,1);
		CONTROLEUR_URI = uri;
		
		cptin =  compteurIn;

		contout = new ControleurOutPort(controleurOut, this);
		contout.publishPort();
		
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
			this.doPortConnection( this.contout.getPortURI(), this.cptin,ControleurCompteurConnector.class.getCanonicalName());
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
	public void execute() throws Exception {
		super.execute();
		try {
			Thread.sleep(1000);
			this.logMessage("Consomation : "+this.contout.getAllConso());
		} catch (Exception e) {
			e.printStackTrace();
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
