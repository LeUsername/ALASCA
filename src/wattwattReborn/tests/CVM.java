package wattwattReborn.tests;

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

		this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { URIS.CONTROLLEUR_URI, URIS.COMPTEUR_IN_URI, URIS.COMPTEUR_OUT_URI,
						URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI });

		this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { URIS.COMPTEUR_URI, URIS.COMPTEUR_IN_URI });

		this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
				new Object[] { URIS.REFRIGERATEUR_URI, URIS.REFRIGERATEUR_IN_URI });

		this.toggleLogging(URIS.CONTROLLEUR_URI);
		this.toggleTracing(URIS.CONTROLLEUR_URI);

		this.toggleLogging(URIS.COMPTEUR_URI);
		this.toggleTracing(URIS.COMPTEUR_URI);

		this.toggleLogging(URIS.REFRIGERATEUR_URI);
		this.toggleTracing(URIS.REFRIGERATEUR_URI);

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM a = new CVM();
			a.startStandardLifeCycle(500000L);
			Thread.sleep(500000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
