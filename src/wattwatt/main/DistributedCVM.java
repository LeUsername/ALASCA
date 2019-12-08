package wattwatt.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import wattwatt.composants.Compteur;
import wattwatt.composants.Controleur;
import wattwatt.composants.appareils.incontrolable.sechecheveux.SecheCheveux;
import wattwatt.composants.appareils.planifiable.lavelinge.LaveLinge;
import wattwatt.composants.appareils.suspensible.refrigerateur.Refrigerateur;
import wattwatt.composants.sources.aleatoire.eolienne.Eolienne;
import wattwatt.composants.sources.intermittent.groupeelectrogene.GroupeElectrogene;
import wattwatt.connecteurs.CompteurConnector;
import wattwatt.connecteurs.appareils.incontrolable.sechecheveux.SecheCheveuxConnector;
import wattwatt.connecteurs.appareils.planifiable.lavelinge.LaveLingeConnector;
import wattwatt.connecteurs.appareils.suspensibles.refrigerateur.RefrigerateurConnector;
import wattwatt.connecteurs.sources.aleatoire.eolienne.EolienneConnector;
import wattwatt.connecteurs.sources.intermittent.groupeelectrogene.GroupeElectrogeneConnector;
import wattwatt.tools.URIS;

public class DistributedCVM extends AbstractDistributedCVM {

	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;
	protected String secheUri;
	protected String eolUri;
	protected String laveUri;
	protected String groupeUri;

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
							URIS.REFRIGERATEUR_IN_URI, URIS.REFRIGERATEUR_OUT_URI, URIS.SECHECHEVEUX_IN_URI,
							URIS.SECHECHEVEUX_OUT_URI, URIS.EOLIENNE_IN_URI, URIS.EOLIENNE_OUT_URI,
							URIS.LAVELINGE_IN_URI, URIS.LAVELINGE_OUT_URI, URIS.GROUPEELECTRO_IN_URI,
							URIS.GROUPEELECTRO_OUT_URI });
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

			this.secheUri = AbstractComponent.createComponent(SecheCheveux.class.getCanonicalName(),
					new Object[] { URIS.SECHECHEVEUX_URI, URIS.SECHECHEVEUX_IN_URI });
			assert this.isDeployedComponent(this.secheUri);
			assert this.isDeployedComponent(this.secheUri);
			this.toggleTracing(this.secheUri);
			this.toggleLogging(this.secheUri);

		} else if (thisJVMURI.equals(URIS.EOLIENNE_URI)) {

			this.eolUri = AbstractComponent.createComponent(Eolienne.class.getCanonicalName(),
					new Object[] { URIS.EOLIENNE_URI, URIS.EOLIENNE_IN_URI });
			assert this.isDeployedComponent(this.eolUri);
			assert this.isDeployedComponent(this.eolUri);
			this.toggleTracing(this.eolUri);
			this.toggleLogging(this.eolUri);

		} else if (thisJVMURI.equals(URIS.LAVELINGE_URI)) {

			this.laveUri = AbstractComponent.createComponent(LaveLinge.class.getCanonicalName(),
					new Object[] { URIS.LAVELINGE_URI, URIS.LAVELINGE_IN_URI });
			assert this.isDeployedComponent(this.laveUri);
			assert this.isDeployedComponent(this.laveUri);
			this.toggleTracing(this.laveUri);
			this.toggleLogging(this.laveUri);

		} else if (thisJVMURI.equals(URIS.GROUPEELECTRO_URI)) {

			this.groupeUri = AbstractComponent.createComponent(GroupeElectrogene.class.getCanonicalName(),
					new Object[] { URIS.GROUPEELECTRO_URI, URIS.GROUPEELECTRO_IN_URI });
			assert this.isDeployedComponent(this.groupeUri);
			assert this.isDeployedComponent(this.groupeUri);
			this.toggleTracing(this.groupeUri);
			this.toggleLogging(this.groupeUri);

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

			this.doPortConnection(this.controleurUri, URIS.EOLIENNE_OUT_URI, URIS.EOLIENNE_IN_URI,
					EolienneConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.LAVELINGE_OUT_URI, URIS.LAVELINGE_IN_URI,
					LaveLingeConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.GROUPEELECTRO_OUT_URI, URIS.GROUPEELECTRO_IN_URI,
					GroupeElectrogeneConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {
		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {
		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {
		} else if (thisJVMURI.equals(URIS.LAVELINGE_URI)) {
		} else if (thisJVMURI.equals(URIS.GROUPEELECTRO_URI)) {
		} else if (thisJVMURI.equals(URIS.EOLIENNE_URI)) {
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
			this.doPortDisconnection(this.controleurUri, URIS.EOLIENNE_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.LAVELINGE_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.GROUPEELECTRO_OUT_URI);

		} else if (thisJVMURI.equals(URIS.COMPTEUR_URI)) {
		} else if (thisJVMURI.equals(URIS.REFRIGERATEUR_URI)) {
		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {
		} else if (thisJVMURI.equals(URIS.LAVELINGE_URI)) {
		} else if (thisJVMURI.equals(URIS.GROUPEELECTRO_URI)) {
		} else if (thisJVMURI.equals(URIS.EOLIENNE_URI)) {
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
		} else if (thisJVMURI.equals(URIS.SECHECHEVEUX_URI)) {
		} else if (thisJVMURI.equals(URIS.LAVELINGE_URI)) {
		} else if (thisJVMURI.equals(URIS.GROUPEELECTRO_URI)) {
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
