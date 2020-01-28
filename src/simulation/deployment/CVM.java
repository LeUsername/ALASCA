package simulation.deployment;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import simulation.models.controller.ControllerModel;
import simulation.models.electricmeter.ElectricMeterModel;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.models.washingmachine.WashingMachineCoupledModel;
import wattwatt.components.Controller;
import wattwatt.components.ElectricMeter;
import wattwatt.components.devices.schedulable.washingmachine.WashingMachine;
import wattwatt.components.devices.uncontrollable.hairdryer.HairDryer;
import wattwatt.components.energyproviders.occasional.enginegenerator.EngineGenerator;
import wattwatt.connectors.ElectricMeterConnector;
import wattwatt.connectors.devices.schedulable.washingmachine.WashingMachineConnector;
import wattwatt.connectors.devices.uncontrollable.hairdryer.HairDryerConnector;
import wattwatt.connectors.energyproviders.occasional.enginegenerator.EngineGeneratorConnector;
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
		
		this.controleurUri = AbstractComponent.createComponent(Controller.class.getCanonicalName(),
				new Object[] { URIS.CONTROLLER_URI, URIS.ELECTRIC_METER_OUT_URI,
						URIS.FRIDGE_IN_URI, URIS.FRIDGE_OUT_URI, URIS.HAIR_DRYER_IN_URI, URIS.HAIR_DRYER_OUT_URI,
						URIS.WIND_TURBINE_IN_URI, URIS.WIND_TURBINE_OUT_URI, URIS.WASHING_MACHINE_IN_URI,
						URIS.WASHING_MACHINE_OUT_URI, URIS.ENGINE_GENERATOR_IN_URI, URIS.ENGINE_GENERATOR_OUT_URI });
		assert this.isDeployedComponent(this.controleurUri);
		
		hm.put(ControllerModel.URI, this.controleurUri);

		
		this.compteurUri = AbstractComponent.createComponent(ElectricMeter.class.getCanonicalName(),
				new Object[] { URIS.ELECTRIC_METER_URI, URIS.ELECTRIC_METER_IN_URI, URIS.FRIDGE_OUT_URI + "1",
						URIS.HAIR_DRYER_OUT_URI + "1", URIS.WASHING_MACHINE_OUT_URI + "1" });
		assert this.isDeployedComponent(this.compteurUri);
		
		hm.put(ElectricMeterModel.URI, this.compteurUri);


		this.secheUri = AbstractComponent.createComponent(HairDryer.class.getCanonicalName(),
				new Object[] { URIS.HAIR_DRYER_URI, URIS.HAIR_DRYER_IN_URI });
		assert this.isDeployedComponent(this.secheUri);

		hm.put(HairDryerCoupledModel.URI, this.secheUri);
		
		this.groupeUri = AbstractComponent.createComponent(EngineGenerator.class.getCanonicalName(),
				new Object[] { URIS.ENGINE_GENERATOR_URI, URIS.ENGINE_GENERATOR_IN_URI });
		assert this.isDeployedComponent(this.groupeUri);

		hm.put(EngineGeneratorCoupledModel.URI, this.groupeUri);
		
		this.laveUri = AbstractComponent.createComponent(WashingMachine.class.getCanonicalName(),
				new Object[] { URIS.WASHING_MACHINE_URI, URIS.WASHING_MACHINE_IN_URI });
		assert this.isDeployedComponent(this.laveUri);
		hm.put(WashingMachineCoupledModel.URI, this.laveUri);

		
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



		this.doPortConnection(this.controleurUri, URIS.ELECTRIC_METER_OUT_URI, URIS.ELECTRIC_METER_IN_URI,
				ElectricMeterConnector.class.getCanonicalName());

//		this.doPortConnection(this.controleurUri, URIS.FRIDGE_OUT_URI, URIS.FRIDGE_IN_URI,
//				FridgeConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.HAIR_DRYER_OUT_URI, URIS.HAIR_DRYER_IN_URI,
				HairDryerConnector.class.getCanonicalName());

//		this.doPortConnection(this.controleurUri, URIS.WIND_TURBINE_OUT_URI, URIS.WIND_TURBINE_IN_URI,
//				WindTurbineConnector.class.getCanonicalName());

//		this.doPortConnection(this.controleurUri, URIS.WASHING_MACHINE_OUT_URI, URIS.WASHING_MACHINE_IN_URI,
//				WashingMachineConnector.class.getCanonicalName());

		this.doPortConnection(this.controleurUri, URIS.ENGINE_GENERATOR_OUT_URI, URIS.ENGINE_GENERATOR_IN_URI,
				EngineGeneratorConnector.class.getCanonicalName());

		
		this.doPortConnection(this.compteurUri, URIS.HAIR_DRYER_OUT_URI + "1", URIS.HAIR_DRYER_IN_URI, HairDryerConnector.class.getCanonicalName());

//		this.doPortConnection(this.compteurUri, URIS.FRIDGE_OUT_URI + "1", URIS.FRIDGE_IN_URI,
//				FridgeConnector.class.getCanonicalName());

		this.doPortConnection(this.compteurUri, URIS.WASHING_MACHINE_OUT_URI + "1", URIS.WASHING_MACHINE_IN_URI,
				WashingMachineConnector.class.getCanonicalName());

		super.deploy();
		assert this.deploymentDone();
	}

	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.controleurUri, URIS.ELECTRIC_METER_OUT_URI);
//		this.doPortDisconnection(this.controleurUri, URIS.FRIDGE_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.HAIR_DRYER_OUT_URI);
//		this.doPortDisconnection(this.controleurUri, URIS.WIND_TURBINE_OUT_URI);
//		this.doPortDisconnection(this.controleurUri, URIS.WASHING_MACHINE_OUT_URI);
		this.doPortDisconnection(this.controleurUri, URIS.ENGINE_GENERATOR_OUT_URI);

//		this.doPortDisconnection(this.compteurUri, URIS.FRIDGE_OUT_URI + "1");
		this.doPortDisconnection(this.compteurUri, URIS.HAIR_DRYER_OUT_URI + "1");
		this.doPortDisconnection(this.compteurUri, URIS.WASHING_MACHINE_OUT_URI + "1");
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
			vm.startStandardLifeCycle(50000L) ;
			Thread.sleep(30000L) ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
