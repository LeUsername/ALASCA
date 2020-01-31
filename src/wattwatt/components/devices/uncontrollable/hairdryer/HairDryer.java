package wattwatt.components.devices.uncontrollable.hairdryer;

import java.util.Random;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import simulation.models.hairdryer.HairDryerCoupledModel;
import simulation.plugins.HairDryerSimulatorPlugin;
import simulation.tools.hairdryer.HairDryerPowerLevel;
import simulation.tools.hairdryer.HairDryerState;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.devices.uncontrollable.hairdryer.IHairDryer;
import wattwatt.ports.devices.uncontrollable.hairdryer.HairDryerInPort;
import wattwatt.tools.hairdryer.HairDryerMode;
import wattwatt.tools.hairdryer.HairDryerSetting;

//-----------------------------------------------------------------------------
/**
* The class <code>HairDryer</code>
*
* <p>
* <strong>Description</strong>
* </p>
* 
* This class implements the hair dryer component. The hair dryer 
* requires the controller interface because he have to be
* connected to the controller to receive order from him.
* 
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
//The next annotation requires that the referenced interface is added to
//the required interfaces of the component.
@OfferedInterfaces(offered = IHairDryer.class)
@RequiredInterfaces(required = IController.class)
public class HairDryer extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {
	
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/** The inbound port of the hair dryer */
	protected HairDryerInPort sechin;
	
	/** The mode of the hair dryer */
	protected HairDryerMode mode;
	/** The power level of the hair dryer */
	protected HairDryerPowerLevel powerLvl;

	/** The state of the hair dryer */
	protected HairDryerState isOn;
	
	/** The energy consumption of the hair dryer */
	protected double conso;

	/** the simulation plug-in holding the simulation models. */
	protected HairDryerSimulatorPlugin asp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	
	/**
	 * Create a hair dryer.
	 * 
	 *
	 * @param uri        URI of the component.
	 * @param sechin 	inbound port URI of the hair dryer.
	 * @throws Exception <i>todo.</i>
	 */
	protected HairDryer(String uri, String sechin) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.sechin = new HairDryerInPort(sechin, this);
		this.sechin.publishPort();

		this.mode = HairDryerMode.HOT_AIR;
		this.powerLvl = HairDryerPowerLevel.LOW;
		this.isOn = HairDryerState.OFF;

		this.tracer.setRelativePosition(1, 1);
	}

	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new HairDryerSimulatorPlugin();
		
		// Set the URI of the plug-in, using the URI of its associated
		// simulation model.
		this.asp.setPluginURI(localArchitecture.getRootModelURI());
		// Set the simulation architecture.
		this.asp.setSimulationArchitecture(localArchitecture);
		// Install the plug-in on the component, starting its own life-cycle.
		this.installPlugin(this.asp);
		
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void execute() throws Exception {
		super.execute();
		
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.sechin.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalArchitecture(java.lang.String)
	 */
	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return HairDryerCoupledModel.build();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentStateAccessI#getEmbeddingComponentStateValue(java.lang.String)
	 */
	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if (name.equals("mode")) {
			return this.mode;
		} else if (name.equals("isOn")) {
			return this.isOn;
		} else if (name.equals("powerLevel")) {
			return this.powerLvl;
		} else {
			assert name.equals("consumption");
			return new Double(this.conso);
		}
	}
	
	@Override
	public void setEmbeddingComponentStateValue(String name, Object value) throws Exception {
		if (name.equals("switchOn")) {
			this.on();
		} else if (name.equals("switchOff")) {
			this.off();
		} else if (name.equals("increasePower")) {
			this.increasePower();
		} else if (name.equals("decreasePower")) {
			this.decreasePower();
		} else{
			assert name.equals("switchMode");
			this.switchMode();
		}
	}
	
	public void behave(Random rand) {
		if (this.isOn == HairDryerState.ON) {
			if (rand.nextBoolean()) {
				this.switchMode();
				if (rand.nextBoolean()) {
					this.increasePower();
				} else {
					this.decreasePower();
				}
			}
			if (this.mode == HairDryerMode.COLD_AIR) {
				this.conso += HairDryerSetting.CONSO_COLD_MODE * this.powerLvl.getValue();
			} else {
				this.conso += HairDryerSetting.CONSO_HOT_MODE * this.powerLvl.getValue();
			}
		} else {
			if (this.mode == HairDryerMode.COLD_AIR) {
				if (this.conso - HairDryerSetting.CONSO_COLD_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= HairDryerSetting.CONSO_COLD_MODE;
				}
			} else {
				if (this.conso - HairDryerSetting.CONSO_HOT_MODE <= 0) {
					this.conso = 0;
				} else {
					this.conso -= HairDryerSetting.CONSO_HOT_MODE;
				}
			}
		}
	}
	
	public void on() {
		this.isOn = HairDryerState.ON;
	}

	public void off() {
		this.isOn = HairDryerState.OFF;
	}

	public double giveConso() {
		return conso;
	}

	public boolean isOn() {
		return this.isOn == HairDryerState.ON;
	}

	public void switchMode() {
		if (this.mode == HairDryerMode.COLD_AIR) {
			this.mode = HairDryerMode.HOT_AIR;
		} else {
			this.mode = HairDryerMode.COLD_AIR;
		}
	}

	protected void setMode(HairDryerMode mode) {
		this.mode = mode;
	}

	public void increasePower() {
		if (this.powerLvl == HairDryerPowerLevel.LOW) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
			this.powerLvl = HairDryerPowerLevel.HIGH;
		}

	}

	public void decreasePower() {
		if (this.powerLvl == HairDryerPowerLevel.HIGH) {
			this.powerLvl = HairDryerPowerLevel.MEDIUM;
		} else if (this.powerLvl == HairDryerPowerLevel.MEDIUM) {
			this.powerLvl = HairDryerPowerLevel.LOW;
		}
	}

	protected void setPowerLevel(HairDryerPowerLevel powerLeveLValue) {
		this.powerLvl = powerLeveLValue;

	}
}
