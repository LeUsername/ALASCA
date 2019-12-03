package wattwattReborn.composants;

import java.util.HashMap;
import java.util.Vector;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.PortI;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.interfaces.compteur.ICompteur;
import wattwattReborn.interfaces.controleur.IControleur;
import wattwattReborn.ports.compteur.CompteurInPort;
import wattwattReborn.ports.compteur.CompteurOutPort;
import wattwattReborn.ports.controleur.ControleurInPort;
import wattwattReborn.ports.controleur.ControleurOutPort;

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = ICompteur.class)
public class Controleur extends AbstractComponent {
	
	protected String CONTROLEUR_URI;
	
	protected CompteurInPort cptin;
	protected CompteurOutPort cptout;
	
	public Controleur(String uri,String compteurIn, String compteurOut) throws Exception {
		super(uri, 1,1);
		CONTROLEUR_URI = uri;
		
		cptin =  new CompteurInPort(compteurIn, this);
		cptin.publishPort();

		cptout = new CompteurOutPort(compteurOut, this);
		cptout.publishPort();
		
		this.tracer.setRelativePosition(0, 1);
	}
	
	
	
	public String getCONTROLEUR_URI() {
		return CONTROLEUR_URI;
	}
	
	public int getAllConso() {
		return 0;
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		this.logMessage("Controleur starting");
		try {
			this.doPortConnection( this.cptout.getPortURI(), this.cptin.getPortURI(),CompteurConnector.class.getCanonicalName());
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
			this.logMessage("consomation : "+this.cptout.getAllConso());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		this.logMessage("Controleur shutdown");
		// unpublish les ports
		try {
			this.cptin.unpublishPort();
			this.cptout.unpublishPort();
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
			this.cptout.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finalise();
	}

}
