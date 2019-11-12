package tests;

import java.util.Vector;

import composants.Batterie;
import composants.Compteur;
import composants.Controleur;
import composants.Eolienne;
import composants.LaveLinge;
import composants.SecheCheveux;
import connecteurs.StringDataConnector;
import fr.sorbonne_u.components.AbstractComponent;
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
//		uris.add(EOLIENNE_URI);
//		uris.add(LAVE_LINGE_URI);
//		uris.add(SECHE_CHEVEUX_URI);
//		uris.add(BATTERIE_URI);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		

		
		if (thisJVMURI.equals(CONTROLLEUR_URI)) {
			// A first player that initially has the service.
			String pp1URI = AbstractComponent.createComponent(Controleur.class.getCanonicalName(),
					new Object[] { CONTROLLEUR_URI,1, 0,
							uris });
			this.toggleTracing(pp1URI);
		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			// A second player that is initially passive.
			String pp2URI = AbstractComponent.createComponent(Compteur.class.getCanonicalName(),
					new Object[] { COMPTEUR_URI, 1,0 });
			this.toggleTracing(pp2URI);
		} else {
			throw new RuntimeException("Unknown JVM URI: " + thisJVMURI);
		}
		super.instantiateAndPublish();
	}
	
	/**
	 * interconnect the components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#interconnect()
	 */
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.isIntantiatedAndPublished() ;

		if (thisJVMURI.equals(CONTROLLEUR_URI)) {
			this.doPortConnection(CONTROLLEUR_URI, this.cont.stringDataInPort.get(COMPTEUR_URI).getPortURI(),
					this.cpt.stringDataOutPort.getPortURI(), StringDataConnector.class.getCanonicalName());
			

		} else if (thisJVMURI.equals(COMPTEUR_URI)) {
			this.doPortConnection(COMPTEUR_URI, this.cpt.stringDataInPort.getPortURI(),
					this.cont.stringDataOutPort.get(COMPTEUR_URI).getPortURI(),
					StringDataConnector.class.getCanonicalName());
		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.interconnect();
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#finalise()
	 */
//	@Override
//	public void			finalise() throws Exception
//	{
//		// Port disconnections can be done here for static architectures
//		// otherwise, they can be done in the finalise methods of components.
//
//		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {
//
//			assert	this.uriConsumerURI == null && this.uriProviderURI != null ;
//			// nothing to be done on the provider side
//
//		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
//
//			assert	this.uriConsumerURI != null && this.uriProviderURI == null ;
//			this.doPortDisconnection(this.uriConsumerURI, URIGetterOutboundPortURI) ;
//
//		} else {
//
//			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
//
//		}
//
//		super.finalise() ;
//	}

//	/**
//	 * @see fr.sorbonne_u.components.cvm.AbstractDistributedCVM#shutdown()
//	 */
//	@Override
//	public void			shutdown() throws Exception
//	{
//		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {
//
//			assert	this.uriConsumerURI == null && this.uriProviderURI != null ;
//			// any disconnection not done yet can be performed here
//
//		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
//
//			assert	this.uriConsumerURI != null && this.uriProviderURI == null ;
//			// any disconnection not done yet can be performed here
//
//		} else {
//
//			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
//
//		}
//
//		super.shutdown();
//	}

	public static void	main(String[] args)
	{
		String[] a = new String[3];
		a[0] = "controleur";
		a[2] = "compteur";
		a[1] = "src/tests/config.xml";
		try {
			DistributedCVM da  = new DistributedCVM(a, 2, 5) ;
			da.startStandardLifeCycle(50000L) ;
			Thread.sleep(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
