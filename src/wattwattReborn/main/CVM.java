package wattwattReborn.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import wattwattReborn.composants.Compteur;
import wattwattReborn.composants.Controleur;
import wattwattReborn.composants.appareils.incontrolable.sechecheveux.SecheCheveux;
import wattwattReborn.composants.appareils.planifiable.lavelinge.LaveLinge;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwattReborn.composants.sources.aleatoire.eolienne.Eolienne;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.appareils.incontrolable.sechecheveux.SecheCheveuxConnector;
import wattwattReborn.connecteurs.appareils.planifiable.lavelinge.LaveLingeConnector;
import wattwattReborn.connecteurs.appareils.suspensibles.refrigerateur.RefrigerateurConnector;
import wattwattReborn.connecteurs.sources.aleatoire.eolienne.EolienneConnector;
import wattwattReborn.tools.URIS;

public class CVM extends AbstractCVM {

	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;
	protected String secheUri;
	protected String eolUri;
	protected String laveUri;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {

		assert !this.deploymentDone();

		this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { URIS.CONTROLLEUR_URI, URIS.COMPTEUR_IN_URI, URIS.COMPTEUR_OUT_URI,
						URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI, URIS.SECHECHEVEUX_IN_URI,
						URIS.SECHECHEVEUX_OUT_URI, URIS.EOLIENNE_IN_URI, URIS.EOLIENNE_OUT_URI, URIS.LAVELINGE_IN_URI,
						URIS.LAVELINGE_OUT_URI });
		assert this.isDeployedComponent(this.controleurUri);

		this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { URIS.COMPTEUR_URI, URIS.COMPTEUR_IN_URI });
		assert this.isDeployedComponent(this.compteurUri);

		this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
				new Object[] { URIS.REFRIGERATEUR_URI, URIS.REFRIGERATEUR_IN_URI });
		assert this.isDeployedComponent(this.refriUri);

		this.secheUri = AbstractComponent.createComponent(SecheCheveux.class.getCanonicalName(),
				new Object[] { URIS.SECHECHEVEUX_URI, URIS.SECHECHEVEUX_IN_URI });
		assert this.isDeployedComponent(this.secheUri);

		this.eolUri = AbstractComponent.createComponent(Eolienne.class.getCanonicalName(),
				new Object[] { URIS.EOLIENNE_URI, URIS.EOLIENNE_IN_URI });
		assert this.isDeployedComponent(this.eolUri);
		
		this.laveUri = AbstractComponent.createComponent(LaveLinge.class.getCanonicalName(),
				new Object[] { URIS.LAVELINGE_URI, URIS.LAVELINGE_IN_URI });
		assert this.isDeployedComponent(this.eolUri);

		this.toggleLogging(this.controleurUri);
		this.toggleTracing(this.controleurUri);

		this.toggleLogging(this.compteurUri);
		this.toggleTracing(this.compteurUri);

		this.toggleLogging(this.refriUri);
		this.toggleTracing(this.refriUri);

		this.toggleLogging(this.secheUri);
		this.toggleTracing(this.secheUri);

		this.toggleLogging(this.eolUri);
		this.toggleTracing(this.eolUri);
		
		this.toggleLogging(this.laveUri);
		this.toggleTracing(this.laveUri);

		this.doPortConnection(this.controleurUri, URIS.COMPTEUR_OUT_URI, URIS.COMPTEUR_IN_URI,
				CompteurConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI, URIS.REFRIGERATEUR_IN_URI,
				RefrigerateurConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.SECHECHEVEUX_OUT_URI, URIS.SECHECHEVEUX_IN_URI,
				SecheCheveuxConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.EOLIENNE_OUT_URI, URIS.EOLIENNE_IN_URI,
				EolienneConnector.class.getCanonicalName());
		
		this.doPortConnection(this.controleurUri, URIS.LAVELINGE_OUT_URI, URIS.LAVELINGE_IN_URI,
				LaveLingeConnector.class.getCanonicalName());

		super.deploy();
		assert this.deploymentDone();
	}

	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.controleurUri, URIS.COMPTEUR_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.SECHECHEVEUX_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.EOLIENNE_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.LAVELINGE_OUT_URI);
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
