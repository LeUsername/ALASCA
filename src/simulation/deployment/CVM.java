package simulation.deployment;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.tools.TimeScale;
import wattwatt.components.ElectricMeter;
import wattwatt.components.devices.uncontrollable.hairdryer.HairDryer;
import wattwatt.connectors.devices.uncontrollable.hairdryer.HairDryerConnector;
import wattwatt.tools.URIS;

public class CVM 
extends		AbstractCVM
{
	
	protected String compteurUri;
	protected String controleurUri;
	protected String refriUri;
	protected String secheUri;
	protected String eolUri;
	protected String laveUri;
	protected String groupeUri;
	protected String coordUri;
	protected String supervisorUri;
	
	public				CVM() throws Exception
	{
		super() ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		assert !this.deploymentDone();

		HashMap<String,String> hm = new HashMap<>() ;
		
		this.compteurUri = AbstractComponent.createComponent(ElectricMeter.class.getCanonicalName(),
				new Object[] { URIS.ELECTRIC_METER_URI, URIS.ELECTRIC_METER_IN_URI, URIS.FRIDGE_OUT_URI + "1",
						URIS.HAIR_DRYER_OUT_URI + "1", URIS.WASHING_MACHINE_OUT_URI + "1" });
		assert this.isDeployedComponent(this.compteurUri);
		
		hm.put(ElectricMeterModel.URI, this.compteurUri);


		this.secheUri = AbstractComponent.createComponent(HairDryer.class.getCanonicalName(),
				new Object[] { URIS.HAIR_DRYER_URI, URIS.HAIR_DRYER_IN_URI });
		assert this.isDeployedComponent(this.secheUri);

		hm.put(HairDryerCoupledModel.URI, this.secheUri);
		

		
		String coordURI =
				AbstractComponent.createComponent(
					WattWattCoordinatorComponent.class.getCanonicalName(),
					new Object[]{}) ;
			hm.put(WattWattModel.URI, coordURI) ;

		@SuppressWarnings("unused")
		String supervisorURI =
			AbstractComponent.createComponent(
					WattWattSupervisorComponent.class.getCanonicalName(),
					new Object[]{hm}) ;

//		this.toggleLogging(this.controleurUri);
//		this.toggleTracing(this.controleurUri);
//
//		this.toggleLogging(this.compteurUri);
//		this.toggleTracing(this.compteurUri);
//
//		this.toggleLogging(this.refriUri);
//		this.toggleTracing(this.refriUri);
//
//		this.toggleLogging(this.secheUri);
//		this.toggleTracing(this.secheUri);
//
//		this.toggleLogging(this.eolUri);
//		this.toggleTracing(this.eolUri);
//
//		this.toggleLogging(this.laveUri);
//		this.toggleTracing(this.laveUri);
//
//		this.toggleLogging(this.groupeUri);
//		this.toggleTracing(this.groupeUri);


		this.doPortConnection(this.compteurUri, URIS.HAIR_DRYER_OUT_URI + "1", URIS.HAIR_DRYER_IN_URI, HairDryerConnector.class.getCanonicalName());


		super.deploy();
		assert this.deploymentDone();
	}

	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.compteurUri, URIS.HAIR_DRYER_OUT_URI + "1");
		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
		assert this.allFinalised();
		super.shutdown();

	}

	public static void	main(String[] args)
	{
		try {
			CVM vm = new CVM() ;
			vm.startStandardLifeCycle((long)TimeScale.WEEK) ;
			Thread.sleep(30000L) ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
