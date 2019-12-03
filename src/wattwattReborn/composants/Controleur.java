package wattwattReborn.composants;

import java.util.HashMap;
import java.util.Vector;

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

@OfferedInterfaces(offered = IControleur.class)
@RequiredInterfaces(required = ICompteur.class)
public class Controleur extends AbstractComponent {
	
	protected String CONTROLEUR_URI;
	
	public Controleur(String uri,String compteurIn, String compteurOut) throws Exception {
		super(uri, 1,1);
		CONTROLEUR_URI = uri;
		
		ControleurInPort cptin =  new ControleurInPort(compteurIn, this);
		cptin.publishPort();

		ControleurOutPort cptout = new ControleurOutPort(compteurOut, this);
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
		this.logMessage("Controleur shutdown");
		// unpublish les ports
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		// unpublish les ports
		super.finalise();
	}

}
