package wattwatt.components.energyproviders.occasional.enginegenerator;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.interfaces.EmbeddingComponentAccessI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import simulation.models.enginegenerator.EngineGeneratorCoupledModel;
import simulation.plugins.EngineGeneratorSimulatorPlugin;
import simulation.tools.enginegenerator.EngineGeneratorState;
import wattwatt.interfaces.controller.IController;
import wattwatt.interfaces.energyproviders.occasional.IEngineGenerator;
import wattwatt.ports.energyproviders.occasional.enginegenerator.EngineGeneratorInPort;
import wattwatt.tools.EngineGenerator.EngineGeneratorSetting;

//-----------------------------------------------------------------------------
/**
* The class <code>EngineGenerator</code>
*
* <p>
* <strong>Description</strong>
* </p>
* 
* This class implements the engine generator component. The engine generator 
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
@OfferedInterfaces(offered = IEngineGenerator.class)
@RequiredInterfaces(required = IController.class)
public class EngineGenerator  extends AbstractCyPhyComponent implements EmbeddingComponentAccessI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/** The inbound port of the engine generator */	
	protected EngineGeneratorInPort groupein;

	/** The state of the fridge */
	protected boolean isOn;
	/** The energy production of the engine generator */
	protected double production;
	/** The fuel quantity of the engine generator */
	protected double fuelQuantity;
	
	
	protected boolean isFull;
	protected boolean isEmpty;
	
	/** the simulation plug-in holding the simulation models. */
	protected EngineGeneratorSimulatorPlugin asp;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a engine generator.
	 * 
	 *
	 * @param uri        URI of the component.
	 * @param groupeIn 	inbound port URI of the engine generator.
	 * @throws Exception <i>todo.</i>
	 */
	protected EngineGenerator(String uri, String groupeIn) throws Exception {
		super(uri, 2, 1);
		this.initialise();
		this.groupein = new EngineGeneratorInPort(groupeIn, this);
		this.groupein.publishPort();
		
		this.isOn = false;
		this.production = 0.0;
		this.fuelQuantity = EngineGeneratorSetting.FUEL_CAPACITY;
		this.isFull = true;
		this.isEmpty = false;
		
		this.tracer.setRelativePosition(2, 1);
	}
	
	protected void initialise() throws Exception {
		// The coupled model has been made able to create the simulation
		// architecture description.
		Architecture localArchitecture = this.createLocalArchitecture(null);
		// Create the appropriate DEVS simulation plug-in.
		this.asp = new EngineGeneratorSimulatorPlugin();
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

		this.logMessage("Groupe Electro starting");
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
			this.groupein.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	@Override
	public Object getEmbeddingComponentStateValue(String name) throws Exception {
		if(name.equals("state")) {
			return this.isOn?EngineGeneratorState.ON:EngineGeneratorState.OFF;
		}
		else if(name.equals("capacity")) {
			return new Double(this.fuelQuantity);
		}
		else {
			assert name.equals("production");
			return new Double(this.production);
		}
	}
	
	@Override
	public void setEmbeddingComponentStateValue(String name, Object value) throws Exception {
		if(name.equals("production")) {
			this.production = (double) value;
		}
		else if(name.equals("state")) {
			this.isOn = (EngineGeneratorState) value == EngineGeneratorState.ON?true:false;
		}
		else if(name.equals("capacity")) {
			this.fuelQuantity = (double) value;
		}
		else if(name.equals("start")) {
			this.on();
		}
		else if(name.equals("stop")) {
			this.off();
		}else {
			assert name.equals("refill");
			this.fuelQuantity = EngineGeneratorSetting.FUEL_CAPACITY;
		}
		
	}

	@Override
	protected Architecture createLocalArchitecture(String architectureURI) throws Exception {
		return EngineGeneratorCoupledModel.build();
	}
	
	public double getEnergie() throws Exception {
		return this.production;
	}

	public boolean fuelIsEmpty() throws Exception {
		return this.fuelQuantity == 0;
	}

	public boolean fuelIsFull() throws Exception {
		return this.fuelQuantity == EngineGeneratorSetting.FUEL_CAPACITY;
	}

	public double fuelQuantity() throws Exception {
		return this.fuelQuantity;
	}

	public void on() throws Exception {
		this.isOn = true;
	}

	public void off() throws Exception {
		this.isOn = false;
	}

	public void addFuel(int quantity) throws Exception {
		if (this.fuelQuantity + quantity >= EngineGeneratorSetting.FUEL_CAPACITY) {
			this.fuelQuantity = EngineGeneratorSetting.FUEL_CAPACITY;
		} else {
			this.fuelQuantity += EngineGeneratorSetting.FUEL_CAPACITY;
		}
	}

	public boolean isOn() {
		return this.isOn;
	}

	public void behave() throws Exception {
		if (this.isOn && !this.fuelIsEmpty()) {
			this.logMessage("Groupe is producing");
			this.production += EngineGeneratorSetting.PROD_THR;
			if (this.fuelQuantity - EngineGeneratorSetting.PROD_THR <= 0) {
				this.fuelQuantity = 0;
			} else {
				this.fuelQuantity -= EngineGeneratorSetting.PROD_THR;
			}
		} else {
			this.off();
		}
	}

}
