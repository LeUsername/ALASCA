package tests;

import java.util.Vector;

import composants.Batterie;
import composants.Compteur;
import composants.Controleur;
import composants.Eolienne;
import composants.LaveLinge;
import composants.SecheCheveux;
import connecteurs.StringDataConnector;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {

	/**
	 * URI of the reflection inbound port of the concurrent map component.
	 */
	protected String COMPTEUR_URI = "compteur";
	protected String CONTROLLEUR_URI = "controleur";
	protected String SECHE_CHEVEUX_URI = "secheCheveux";
	protected String LAVE_LINGE_URI = "laveLinge";
	protected String EOLIENNE_URI = "eolienne";
	protected String BATTERIE_URI = "batterie";

	String outportCont = "outPortCont";
	String inportCont = "inPortCont";
	String outportCont2 = "outPortCont2";
	String inportCont2 = "inPortCont2";
	String outportCont3 = "outPortCont3";
	String inportCont3 = "inPortCont3";
	String outportCont4 = "outPortCont4";
	String inportCont4 = "inPortCont4";
	String outportCont5 = "outPortCont5";
	String inportCont5 = "inPortCont5";

	String outportCpt = "outPortCpt";
	String inportCpt = "inPortCpt";
	String outportEol = "outPortEol";
	String inportEol = "inPortEol";
	String outportBat = "outPortBat";
	String inportBat = "inPortBat";
	String outportLav = "outPortLav";
	String inportLav = "inPortLav";
	String outportSec = "outPortSec";
	String inportSec = "inPortSec";

	protected Vector<String> uris = new Vector<>();

	Controleur cont;
	Compteur cpt;
	SecheCheveux secheCheveux;
	LaveLinge laveLinge;
	Eolienne eolienne;
	Batterie batterie;

	int quantiteMaxBatterie = 10;

	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		uris.add(COMPTEUR_URI);
		uris.add(EOLIENNE_URI);
		uris.add(BATTERIE_URI);
//		uris.add(LAVE_LINGE_URI);
//		uris.add(SECHE_CHEVEUX_URI);
	}

	@Override
	public void instantiateAndPublish() throws Exception {

		if (thisJVMURI.equals(CONTROLLEUR_URI)) {
			this.cont = new Controleur(CONTROLLEUR_URI, 1, 0);
			this.cont.plug(COMPTEUR_URI, inportCont, outportCont);
			this.cont.plug(EOLIENNE_URI, inportCont2, outportCont2);
			this.cont.plug(BATTERIE_URI, inportCont3, outportCont3);
//			this.cont.plug(LAVE_LINGE_URI, inportCont4, outportCont4);
//			this.cont.plug(SECHE_CHEVEUX_URI, inportCont5, outportCont5);

			this.addDeployedComponent(CONTROLLEUR_URI, cont);
			this.toggleTracing(CONTROLLEUR_URI);

		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			this.cpt = new Compteur(COMPTEUR_URI, 1, 0);
			this.cpt.plug(CONTROLLEUR_URI,inportCpt, outportCpt);
			this.addDeployedComponent(COMPTEUR_URI, cpt);
			this.toggleTracing(COMPTEUR_URI);

		} else if (thisJVMURI.equals(EOLIENNE_URI)) {
			this.eolienne = new Eolienne(EOLIENNE_URI, 1, 0);
			this.eolienne.plug(CONTROLLEUR_URI, inportEol, outportEol);
			this.addDeployedComponent(EOLIENNE_URI, eolienne);
			this.toggleTracing(EOLIENNE_URI);

		} else if (thisJVMURI.equals(BATTERIE_URI)) {
			this.batterie = new Batterie(BATTERIE_URI, 1, 0, quantiteMaxBatterie);
			this.batterie.plug(CONTROLLEUR_URI, inportBat, outportBat);
			this.addDeployedComponent(BATTERIE_URI, batterie);
			this.toggleTracing(BATTERIE_URI);

		} 
//		else if (thisJVMURI.equals(LAVE_LINGE_URI)) {
//			this.laveLinge = new LaveLinge(LAVE_LINGE_URI, 1, 0, inportLav, outportLav);
//			this.addDeployedComponent(LAVE_LINGE_URI, laveLinge);
//			this.toggleTracing(LAVE_LINGE_URI);
//
//		} else if (thisJVMURI.equals(SECHE_CHEVEUX_URI)) {
//			this.secheCheveux = new SecheCheveux(SECHE_CHEVEUX_URI, 1, 0, inportSec, outportSec);
//			this.addDeployedComponent(SECHE_CHEVEUX_URI, secheCheveux);
//			this.toggleTracing(SECHE_CHEVEUX_URI);
//
//		} 
		else {
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

		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			this.doPortConnection(COMPTEUR_URI, this.cpt.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont,
					StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(EOLIENNE_URI)) {
			this.doPortConnection(EOLIENNE_URI, this.eolienne.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont2,
					StringDataConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(BATTERIE_URI)) {
			this.doPortConnection(BATTERIE_URI, this.batterie.stringDataInPort.get(CONTROLLEUR_URI).getPortURI(), this.outportCont3,
					StringDataConnector.class.getCanonicalName());
			
		} 
//		else if (thisJVMURI.equals(LAVE_LINGE_URI)) {
//			this.doPortConnection(LAVE_LINGE_URI, this.laveLinge.stringDataInPort.getPortURI(), this.outportCont4,
//					StringDataConnector.class.getCanonicalName());
//			
//		}else if (thisJVMURI.equals(SECHE_CHEVEUX_URI)) {
//			this.doPortConnection(SECHE_CHEVEUX_URI, this.secheCheveux.stringDataInPort.getPortURI(), this.outportCont5,
//					StringDataConnector.class.getCanonicalName());
//		}
		else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.interconnect();
	}

	public static void main(String[] args) {

		try {
			DistributedCVM da = new DistributedCVM(args, 2, 5);
			da.startStandardLifeCycle(50000L);
			Thread.sleep(75000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
