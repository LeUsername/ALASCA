package wattwatt.main;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import wattwatt.components.ElectricMeter;
import wattwatt.components.Controller;
import wattwatt.components.devices.schedulable.washingmachine.WashingMachine;
import wattwatt.components.devices.suspendable.fridge.Fridge;
import wattwatt.components.devices.uncontrollable.hairdryer.HairDryer;
import wattwatt.components.energyproviders.occasional.enginegenerator.EngineGenerator;
import wattwatt.components.energyproviders.random.windturbine.WindTurbine;
import wattwatt.connectors.ElectricMeterConnector;
import wattwatt.connectors.devices.schedulable.washingmachine.WashingMachineConnector;
import wattwatt.connectors.devices.suspendables.fridge.FridgeConnector;
import wattwatt.connectors.devices.uncontrollable.hairdryer.HairDryerConnector;
import wattwatt.connectors.energyproviders.occasional.enginegenerator.EngineGeneratorConnector;
import wattwatt.connectors.energyproviders.random.windturbine.WindTurbineConnector;
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
		if (thisJVMURI.equals(URIS.CONTROLLER_URI)) {

			this.controleurUri = AbstractComponent.createComponent(Controller.class.getCanonicalName(),
					new Object[] { URIS.CONTROLLER_URI, URIS.ELECTRIC_METER_IN_URI, URIS.ELECTRIC_METER_OUT_URI,
							URIS.FRIDGE_IN_URI, URIS.FRIDGE_OUT_URI, URIS.HAIR_DRYER_IN_URI,
							URIS.HAIR_DRYER_OUT_URI, URIS.WIND_TURBINE_IN_URI, URIS.WIND_TURBINE_OUT_URI,
							URIS.WASHING_MACHINE_IN_URI, URIS.WASHING_MACHINE_OUT_URI, URIS.ENGINE_GENERATOR_IN_URI,
							URIS.ENGINE_GENERATOR_OUT_URI });
			assert this.isDeployedComponent(this.controleurUri);

			this.toggleTracing(this.controleurUri);
			this.toggleLogging(this.controleurUri);

		} else if (thisJVMURI.equals(URIS.ELECTRIC_METER_URI)) {

			this.compteurUri = AbstractComponent.createComponent(ElectricMeter.class.getCanonicalName(),
					new Object[] { URIS.ELECTRIC_METER_URI, URIS.ELECTRIC_METER_IN_URI });
			assert this.isDeployedComponent(this.compteurUri);
			this.toggleTracing(this.compteurUri);
			this.toggleLogging(this.compteurUri);

		} else if (thisJVMURI.equals(URIS.FRIDGE_URI)) {

			this.refriUri = AbstractComponent.createComponent(Fridge.class.getCanonicalName(),
					new Object[] { URIS.FRIDGE_URI, URIS.FRIDGE_IN_URI });
			assert this.isDeployedComponent(this.refriUri);
			assert this.isDeployedComponent(this.refriUri);
			this.toggleTracing(this.refriUri);
			this.toggleLogging(this.refriUri);

		} else if (thisJVMURI.equals(URIS.HAIR_DRYER_URI)) {

			this.secheUri = AbstractComponent.createComponent(HairDryer.class.getCanonicalName(),
					new Object[] { URIS.HAIR_DRYER_URI, URIS.HAIR_DRYER_IN_URI });
			assert this.isDeployedComponent(this.secheUri);
			assert this.isDeployedComponent(this.secheUri);
			this.toggleTracing(this.secheUri);
			this.toggleLogging(this.secheUri);

		} else if (thisJVMURI.equals(URIS.WIND_TURBINE_URI)) {

			this.eolUri = AbstractComponent.createComponent(WindTurbine.class.getCanonicalName(),
					new Object[] { URIS.WIND_TURBINE_URI, URIS.WIND_TURBINE_IN_URI });
			assert this.isDeployedComponent(this.eolUri);
			assert this.isDeployedComponent(this.eolUri);
			this.toggleTracing(this.eolUri);
			this.toggleLogging(this.eolUri);

		} else if (thisJVMURI.equals(URIS.WASHING_MACHINE_URI)) {

			this.laveUri = AbstractComponent.createComponent(WashingMachine.class.getCanonicalName(),
					new Object[] { URIS.WASHING_MACHINE_URI, URIS.WASHING_MACHINE_IN_URI });
			assert this.isDeployedComponent(this.laveUri);
			assert this.isDeployedComponent(this.laveUri);
			this.toggleTracing(this.laveUri);
			this.toggleLogging(this.laveUri);

		} else if (thisJVMURI.equals(URIS.ENGINE_GENERATOR_URI)) {

			this.groupeUri = AbstractComponent.createComponent(EngineGenerator.class.getCanonicalName(),
					new Object[] { URIS.ENGINE_GENERATOR_URI, URIS.ENGINE_GENERATOR_IN_URI });
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
		if (thisJVMURI.equals(URIS.CONTROLLER_URI)) {

			this.doPortConnection(this.controleurUri, URIS.ELECTRIC_METER_OUT_URI, URIS.ELECTRIC_METER_IN_URI,
					ElectricMeterConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.FRIDGE_OUT_URI, URIS.FRIDGE_IN_URI,
					FridgeConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.HAIR_DRYER_OUT_URI, URIS.HAIR_DRYER_IN_URI,
					HairDryerConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.WIND_TURBINE_OUT_URI, URIS.WIND_TURBINE_IN_URI,
					WindTurbineConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.WASHING_MACHINE_OUT_URI, URIS.WASHING_MACHINE_IN_URI,
					WashingMachineConnector.class.getCanonicalName());

			this.doPortConnection(this.controleurUri, URIS.ENGINE_GENERATOR_OUT_URI, URIS.ENGINE_GENERATOR_IN_URI,
					EngineGeneratorConnector.class.getCanonicalName());

		} else if (thisJVMURI.equals(URIS.ELECTRIC_METER_URI)) {
		} else if (thisJVMURI.equals(URIS.FRIDGE_URI)) {
		} else if (thisJVMURI.equals(URIS.HAIR_DRYER_URI)) {
		} else if (thisJVMURI.equals(URIS.WASHING_MACHINE_URI)) {
		} else if (thisJVMURI.equals(URIS.ENGINE_GENERATOR_URI)) {
		} else if (thisJVMURI.equals(URIS.WIND_TURBINE_URI)) {
		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI);
		}

		super.interconnect();
	}

	@Override
	public void finalise() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLER_URI)) {
			this.doPortDisconnection(this.controleurUri, URIS.ELECTRIC_METER_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.FRIDGE_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.HAIR_DRYER_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.WIND_TURBINE_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.WASHING_MACHINE_OUT_URI);
			this.doPortDisconnection(this.controleurUri, URIS.ENGINE_GENERATOR_OUT_URI);

		} else if (thisJVMURI.equals(URIS.ELECTRIC_METER_URI)) {
		} else if (thisJVMURI.equals(URIS.FRIDGE_URI)) {
		} else if (thisJVMURI.equals(URIS.HAIR_DRYER_URI)) {
		} else if (thisJVMURI.equals(URIS.WASHING_MACHINE_URI)) {
		} else if (thisJVMURI.equals(URIS.ENGINE_GENERATOR_URI)) {
		} else if (thisJVMURI.equals(URIS.WIND_TURBINE_URI)) {
		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI);
		}

		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
		if (thisJVMURI.equals(URIS.CONTROLLER_URI)) {
		} else if (thisJVMURI.equals(URIS.ELECTRIC_METER_URI)) {
		} else if (thisJVMURI.equals(URIS.FRIDGE_URI)) {
		} else if (thisJVMURI.equals(URIS.HAIR_DRYER_URI)) {
		} else if (thisJVMURI.equals(URIS.WASHING_MACHINE_URI)) {
		} else if (thisJVMURI.equals(URIS.ENGINE_GENERATOR_URI)) {
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
