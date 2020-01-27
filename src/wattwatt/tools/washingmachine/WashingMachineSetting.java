package wattwatt.tools.washingmachine;

/**
 * The class <code>ControllerSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the washing machine component.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class WashingMachineSetting {
	
	/**
	 * Duration of a washing on eco mode
	 */
	public static final int DURATION_ECO_MODE = 3000;
	
	/**
	 * Duration of a washing on premium mode
	 */
	public static final int DURATION_PREMIUM_MODE = 5000;
	
	/**
	 * Energy consumption on eco mode
	 */
	public static final int CONSO_ECO_MODE = 30;
	
	/**
	 * Energy consumption on premium mode
	 */
	public static final int CONSO_PREMIUM_MODE = 50;
	
	/**
	 * The Rate at wich the Washing machine will send his energy consumption.
	 */
	public static final int UPDATE_RATE = 1000; 
	
	/**
	 * Energy consumption on eco mode in the simulation
	 */
	public static final double CONSO_ECO_MODE_SIM = 30;
	
	/**
	 * Energy consumption on premium mode in the simulation
	 */
	public static final double CONSO_PREMIUM_MODE_SIM = 50;
	
	/**
	 * The Rate at wich the Washing machine will send his energy consumption in the simulation.
	 */
	public static final double UPDATE_RATE_SIM = 1000;
	
	/**
	 * End before time
	 */
	public static final int END = 25000; // finis avant ca 
	
	/**
	 * Start at time
	 */
	public static final int START = 1000;
	
	/**
	 * Interval between two washing
	 */
	public static final int BIG_TIME = 300; 
	public static final int MEDIUM_TIME = 200; 
	public static final int SMALL_TIME = 100;

}
