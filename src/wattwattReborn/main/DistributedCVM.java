package wattwattReborn.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import wattwattReborn.composants.Compteur;
import wattwattReborn.composants.Controleur;
import wattwattReborn.composants.appareils.incontrolable.sechecheveux.SecheCheveux;
import wattwattReborn.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwattReborn.connecteurs.CompteurConnector;
import wattwattReborn.connecteurs.appareils.incontrolable.sechecheveux.SecheCheveuxConnector;
import wattwattReborn.connecteurs.appareils.suspensibles.refrigerateur.RefrigerateurConnector;
import wattwattReborn.tools.URIS;

public class DistributedCVM extends AbstractDistributedCVM {

	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;
	protected String secheUri;

	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLEUR_URI)) {

			this.controleurUri = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
					new Object[] { URIS.CONTROLLEUR_URI, URIS.COMPTEUR_IN_URI, URIS.COMPTEUR_OUT_URI,
							URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI });
			assert this.isDeployedComponent(this.controleurUri);

			this.toggleTracing(this.controleurUri);
			this.toggleLogging(this.controleurUri);

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {

			this.compteurUri = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
					new Object[] { URIS.COMPTEUR_URI, URIS.COMPTEUR_IN_URI });
			assert this.isDeployedComponent(this.compteurUri);
			this.toggleTracing(this.compteurUri);
			this.toggleLogging(this.compteurUri);

		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {

			this.refriUri = AbstractComponent.createComponent(Refrigerateur.class.getCanonicalName(),
					new Object[] { URIS.REFRIGERATEUR_URI, URIS.REFRIGERATEUR_IN_URI });
			assert this.isDeployedComponent(this.refriUri);
			assert this.isDeployedComponent(this.refriUri);
			this.toggleTracing(this.refriUri);
			this.toggleLogging(this.refriUri);

		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {

			this.refriUri = AbstractComponent.createComponent(SecheCheveux.class.getCanonicalName(),
					new Object[] { URIS.SECHECHEVEUX_URI, URIS.SECHECHEVEUX_IN_URI });
			assert this.isDeployedComponent(this.secheUri);
			assert this.isDeployedComponent(this.secheUri);
			this.toggleTracing(this.secheUri);
			this.toggleLogging(this.secheUri);

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLEUR_URI)) {

			this.doPortConnection(this.controleurUri, URIS.COMPTEUR_OUT_URI, URIS.COMPTEUR_IN_URI,
					CompteurConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI, URIS.REFRIGERATEUR_IN_URI,
					RefrigerateurConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.SECHECHEVEUX_OUT_URI, URIS.SECHECHEVEUX_IN_URI,
					SecheCheveuxConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {
		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI);
		}

		super.interconnect();
	}

	@Override
	public void finalise() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLEUR_URI)) {
			this.doPortDisconnection(this.controleurUri, URIS.COMPTEUR_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.REFRIGERATEUR_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.SECHECHEVEUX_OUT_URI);

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {
		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI);
		}

		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {

		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {

		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI);
		}

		super.shutdown();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM da = new DistributedCVM(args);
			da.startStandardLifeCycle(50000L);
			Thread.sleep(50000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
