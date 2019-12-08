package wattwattRenamed.tests;

import java.util.Vector;

import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import wattwattRenamed.composants.Batterie;
import wattwattRenamed.composants.Compteur;
import wattwattRenamed.composants.Controleur;
import wattwattRenamed.composants.Eolienne;
import wattwattRenamed.composants.LaveLinge;
import wattwattRenamed.composants.Refrigerateur;
import wattwattRenamed.composants.SecheCheveux;
import wattwattRenamed.connecteurs.StringDataConnector;

public class DistributedCVM extends AbstractDistributedCVM {

	/**
	 * URI of the reflection inbound port of the concurrent map component.
	 */
	protected String COMPTEUR_URI = "compteur";
	protected String CONTROLLEUR_URI = "controleur";
	protected String LAVE_LINGE_URI = "laveLinge";
	protected String EOLIENNE_URI = "eolienne";
	protected String BATTERIE_URI = "batterie";
	protected String REFRIGERATEUR_URI = "refrigerateur";
	protected String SECHE_CHEVEUX_URI = "secheCheveux";
	protected String APPAREILS_URI = "appareils";

	String outportCont1 = "outPortCont1";
	String inportCont1 = "inPortCont1";

	String outportCont2 = "outPortCont2";
	String inportCont2 = "inPortCont2";

	String outportCont3 = "outPortCont3";
	String inportCont3 = "inPortCont3";

	String outportCont4 = "outPortCont4";
	String inportCont4 = "inPortCont4";

	String outportCont5 = "outPortCont5";
	String inportCont5 = "inPortCont5";

	String outportCont6 = "outPortCont6";
	String inportCont6 = "inPortCont6";

	String outportCpt = "outPortCpt";
	String inportCpt = "inPortCpt";

	String outportEol = "outPortEol";
	String inportEol = "inPortEol";

	String outportBat = "outPortBat";
	String inportBat = "inPortBat";

	String outportLav = "outPortLav";
	String inportLav = "inPortLav";

	String outportRef = "outPortRef";
	String inportRef = "inPortRef";

	String outportSec = "outPortSec";
	String inportSec = "inPortSec";

	protected Vector<String> uris = new Vector<>();

	Controleur cont;
	Compteur cpt;
	SecheCheveux secheCheveux;
	LaveLinge laveLinge;
	Eolienne eolienne;
	Batterie batterie;
	Refrigerateur refrigerateur;

	int quantiteMaxBatterie = 10;

	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		uris.add(COMPTEUR_URI);
		uris.add(EOLIENNE_URI);
		uris.add(BATTERIE_URI);
		uris.add(LAVE_LINGE_URI);
		uris.add(REFRIGERATEUR_URI);
		uris.add(SECHE_CHEVEUX_URI);
	}

	@Override
	public void instantiateAndPublish() throws Exception {

		if (thisJVMURI.equals(CONTROLLEUR_URI)) {
			this.cont = new Controleur(CONTROLLEUR_URI, 1, 0);
			this.cont.plugCompteur(COMPTEUR_URI, inportCont1, outportCont1);
			this.cont.plugEolienne(EOLIENNE_URI, inportCont2, outportCont2);
			this.cont.plugBatterie(BATTERIE_URI, inportCont3, outportCont3);
			this.cont.plugLaveLinge(LAVE_LINGE_URI, inportCont4, outportCont4);
			this.cont.plugRefrigerateur(REFRIGERATEUR_URI, inportCont5, outportCont5);
			this.cont.plugSecheCheveux(SECHE_CHEVEUX_URI, inportCont6, outportCont6);

			this.addDeployedComponent(CONTROLLEUR_URI, cont);
			this.toggleTracing(CONTROLLEUR_URI);

		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			this.cpt = new Compteur(COMPTEUR_URI, 1, 0);
			this.cpt.plugControleur(CONTROLLEUR_URI, inportCpt, outportCpt);
			this.addDeployedComponent(COMPTEUR_URI, cpt);
			this.toggleTracing(COMPTEUR_URI);

		} else if (thisJVMURI.equals(EOLIENNE_URI)) {
			this.eolienne = new Eolienne(EOLIENNE_URI, 1, 0);
			this.eolienne.plugControleur(CONTROLLEUR_URI, inportEol, outportEol);
			this.addDeployedComponent(EOLIENNE_URI, eolienne);
			this.toggleTracing(EOLIENNE_URI);

		} else if (thisJVMURI.equals(BATTERIE_URI)) {
			this.batterie = new Batterie(BATTERIE_URI, 1, 0, quantiteMaxBatterie);
			this.batterie.plugControleur(CONTROLLEUR_URI, inportBat, outportBat);
			this.addDeployedComponent(BATTERIE_URI, batterie);
			this.toggleTracing(BATTERIE_URI);

		} else if (thisJVMURI.equals(LAVE_LINGE_URI)) {
			this.laveLinge = new LaveLinge(LAVE_LINGE_URI, 1, 0);
			this.laveLinge.plugControleur(CONTROLLEUR_URI, inportLav, outportLav);
			this.addDeployedComponent(LAVE_LINGE_URI, laveLinge);
			this.toggleTracing(LAVE_LINGE_URI);

		} else if (thisJVMURI.equals(REFRIGERATEUR_URI)) {
			this.refrigerateur = new Refrigerateur(REFRIGERATEUR_URI, 1, 0);
			this.refrigerateur.plug(CONTROLLEUR_URI, inportRef, outportRef);
			this.addDeployedComponent(REFRIGERATEUR_URI, refrigerateur);
			this.toggleTracing(REFRIGERATEUR_URI);

		} else if (thisJVMURI.equals(SECHE_CHEVEUX_URI)) {
			this.secheCheveux = new SecheCheveux(SECHE_CHEVEUX_URI, 1, 0);
			this.secheCheveux.plugControleur(CONTROLLEUR_URI, inportSec, outportSec);
			this.addDeployedComponent(SECHE_CHEVEUX_URI, secheCheveux);
			this.toggleTracing(SECHE_CHEVEUX_URI);
			// } else if (thisJVMURI.equals(APPAREILS_URI)) {
//
//			this.eolienne = new Eolienne(EOLIENNE_URI, 1, 0);
//			this.eolienne.plug(CONTROLLEUR_URI, inportEol, outportEol);
//			this.addDeployedComponent(EOLIENNE_URI, eolienne);
//			this.toggleTracing(EOLIENNE_URI);
//
//			this.batterie = new Batterie(BATTERIE_URI, 1, 0, quantiteMaxBatterie);
//			this.batterie.plug(CONTROLLEUR_URI, inportBat, outportBat);
//			this.addDeployedComponent(BATTERIE_URI, batterie);
//			this.toggleTracing(BATTERIE_URI);
//
//			this.laveLinge = new LaveLinge(LAVE_LINGE_URI, 1, 0);
//			this.laveLinge.plug(CONTROLLEUR_URI, inportLav, outportLav);
//			this.addDeployedComponent(LAVE_LINGE_URI, laveLinge);
//			this.toggleTracing(LAVE_LINGE_URI);
//
//			this.refrigerateur = new Refrigerateur(REFRIGERATEUR_URI, 1, 0);
//			this.refrigerateur.plug(CONTROLLEUR_URI, inportRef, outportRef);
//			this.addDeployedComponent(REFRIGERATEUR_URI, refrigerateur);
//			this.toggleTracing(REFRIGERATEUR_URI);
//
//			this.secheCheveux = new SecheCheveux(SECHE_CHEVEUX_URI, 1, 0);
//			this.secheCheveux.plug(CONTROLLEUR_URI, inportSec, outportSec);
//			this.addDeployedComponent(SECHE_CHEVEUX_URI, secheCheveux);
//			this.toggleTracing(SECHE_CHEVEUX_URI);

		} else {
			throw new RuntimeException("Unknown JVM URI: " + thisJVMURI);
		}
		super.instantiateAndPublish();
	}

	/**
	 * interconnect the components.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#interconnect()
	 */
	@Override
	public void interconnect() throws Exception {
		assert this.isIntantiatedAndPublished();

		if (thisJVMURI.equals(CONTROLLEUR_URI)) {
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(COMPTEUR_URI).getPortURI(),
					this.outportCpt, StringDataConnector.class.getCanonicalName());
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(EOLIENNE_URI).getPortURI(),
					this.outportEol, StringDataConnector.class.getCanonicalName());
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(BATTERIE_URI).getPortURI(),
					this.outportBat, StringDataConnector.class.getCanonicalName());
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(LAVE_LINGE_URI).getPortURI(),
					this.outportLav, StringDataConnector.class.getCanonicalName());
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(REFRIGERATEUR_URI).getPortURI(),
					this.outportRef, StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			this.doPortConnection(COMPTEUR_URI, this.cpt.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
					this.outportCont1, StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(EOLIENNE_URI)) {
			this.doPortConnection(EOLIENNE_URI, this.eolienne.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
					this.outportCont2, StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(BATTERIE_URI)) {
			this.doPortConnection(BATTERIE_URI, this.batterie.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
					this.outportCont3, StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(LAVE_LINGE_URI)) {
			this.doPortConnection(LAVE_LINGE_URI, this.laveLinge.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
					this.outportCont4, StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(REFRIGERATEUR_URI)) {
			this.doPortConnection(REFRIGERATEUR_URI,
					this.refrigerateur.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont5,
					StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(SECHE_CHEVEUX_URI)) {
			this.doPortConnection(SECHE_CHEVEUX_URI,
					this.secheCheveux.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont6,
					StringDataConnector.class.getCanonicalName());
			// } else if (thisJVMURI.equals(APPAREILS_URI)) {
//
//			this.doPortConnection(EOLIENNE_URI, this.eolienne.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
//					this.outportCont2, StringDataConnector.class.getCanonicalName());
//
//			this.doPortConnection(BATTERIE_URI, this.batterie.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
//					this.outportCont3, StringDataConnector.class.getCanonicalName());
//
//			this.doPortConnection(LAVE_LINGE_URI, this.laveLinge.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(),
//					this.outportCont4, StringDataConnector.class.getCanonicalName());
//
//			this.doPortConnection(REFRIGERATEUR_URI,
//					this.refrigerateur.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont5,
//					StringDataConnector.class.getCanonicalName());
//
//			this.doPortConnection(SECHE_CHEVEUX_URI,
//					this.secheCheveux.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont6,
//					StringDataConnector.class.getCanonicalName());

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.interconnect();
	}

	public static void main(String[] args) {

		try {
			DistributedCVM da = new DistributedCVM(args, 2, 5);
			da.startStandardLifeCycle(500000L);
			Thread.sleep(500000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
