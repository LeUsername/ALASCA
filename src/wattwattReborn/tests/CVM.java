package wattwattReborn.tests;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import wattwattReborn.composants.Compteur;
import wattwattReborn.composants.Controleur;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;

public class CVM extends AbstractCVM {

	protected final String COMPTEUR_URI = "compteur";
	protected final String CONTROLLEUR_URI = "controleur";
	protected final String REFRIGERATEUR_URI = "refri";

	protected final String COMPTEUR_IN_URI = "compteurIn";
	protected final String CONTROLLEUR_IN_URI = "controleurIn";
	protected final String REFRIGERATEUR_IN_URI = "refriIn";

	protected final String CONTROLLEUR_COMPTEUR_OUT_URI = "controleurOut";
	protected final String CONTROLLEUR_REFRIGERATEUR_OUT_URI = "controleurOut2";

	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;

	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {

		this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
				new Object[] { CONTROLLEUR_URI, CONTROLLEUR_COMPTEUR_OUT_URI, CONTROLLEUR_REFRIGERATEUR_OUT_URI,
						COMPTEUR_IN_URI, REFRIGERATEUR_IN_URI });

		this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
				new Object[] { COMPTEUR_URI, COMPTEUR_IN_URI });

		this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
				new Object[] { REFRIGERATEUR_URI, REFRIGERATEUR_IN_URI });

		this.toggleLogging(CONTROLLEUR_URI);
		this.toggleTracing(CONTROLLEUR_URI);

		this.toggleLogging(COMPTEUR_URI);
		this.toggleTracing(COMPTEUR_URI);

		this.toggleLogging(REFRIGERATEUR_URI);
		this.toggleTracing(REFRIGERATEUR_URI);

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
