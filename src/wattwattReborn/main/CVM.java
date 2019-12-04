package wattwattReborn.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import wattwattReborn.composants.Compteur;
import wattwattReborn.composants.Controleur;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwattReborn.tools.URIS;

public class CVM extends AbstractCVM {

	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		assert	!this.deploymentDone() ;

		this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { URIS.CONTROLLEUR_URI, URIS.COMPTEUR_IN_URI, URIS.COMPTEUR_OUT_URI,
						URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI });
		assert	this.isDeployedComponent(this.controleurUri) ;
		
		this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { URIS.COMPTEUR_URI, URIS.COMPTEUR_IN_URI });
		assert	this.isDeployedComponent(this.compteurUri) ;

		this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
				new Object[] { URIS.REFRIGERATEUR_URI, URIS.REFRIGERATEUR_IN_URI });
		assert	this.isDeployedComponent(this.refriUri) ;

		this.toggleLogging(URIS.CONTROLLEUR_URI);
		this.toggleTracing(URIS.CONTROLLEUR_URI);

		this.toggleLogging(URIS.COMPTEUR_URI);
		this.toggleTracing(URIS.COMPTEUR_URI);

		this.toggleLogging(URIS.REFRIGERATEUR_URI);
		this.toggleTracing(URIS.REFRIGERATEUR_URI);

		super.deploy();
		assert	this.deploymentDone();
	}
	
	@Override
	public void shutdown() throws Exception  {
		assert this.allFinalised();
		super.shutdown();
		
	}

	public static void main(String[] args) {
		try {
			CVM a = new CVM();
			a.startStandardLifeCycle(50000L);
			Thread.sleep(50000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
