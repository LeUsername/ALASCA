package wattwattReborn.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import wattwattReborn.composants.Compteur;
import wattwattReborn.composants.Controleur;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.appareils.suspensibles.refrigerateur.RefrigerateurConnector;
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

		assert !this.deploymentDone();

		this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { URIS.CONTROLLEUR_URI, URIS.COMPTEUR_IN_URI, URIS.COMPTEUR_OUT_URI,
						URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI });
		assert this.isDeployedComponent(this.controleurUri);

		this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { URIS.COMPTEUR_URI, URIS.COMPTEUR_IN_URI });
		assert this.isDeployedComponent(this.compteurUri);

		this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
				new Object[] { URIS.REFRIGERATEUR_URI, URIS.REFRIGERATEUR_IN_URI });
		assert this.isDeployedComponent(this.refriUri);

		this.toggleLogging(controleurUri);
		this.toggleTracing(controleurUri);

		this.toggleLogging(compteurUri);
		this.toggleTracing(compteurUri);

		this.toggleLogging(refriUri);
		this.toggleTracing(refriUri);

		this.doPortConnection(this.controleurUri, URIS.COMPTEUR_OUT_URI, URIS.COMPTEUR_IN_URI,
				CompteurConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI, URIS.REFRIGERATEUR_IN_URI,
				RefrigerateurConnector.class.getCanonicalName());

		super.deploy();
		assert this.deploymentDone();
	}
	
	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.controleurUri, URIS.COMPTEUR_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI);
		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
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
