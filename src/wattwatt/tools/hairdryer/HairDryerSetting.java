package wattwatt.tools.hairdryer;

/**
 * The class <code>HairDryerSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the hair dryer component.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class HairDryerSetting {
	
	/**
	 * The maximum power level
	 */
	public static final int POWER_LVL_MAX = 3; 
	
	/**
	 * The minmum power level
	 */
	public static final int POWER_LVL_MIN = 1; 
	
	/**
	 * Energy consumption when on hot mode
	 */
	public static final double CONSO_HOT_MODE = 800.0;
	
	/**
	 * Energy consumption when on cold mode
	 */
	public static final double CONSO_COLD_MODE = 500.0;
	
	/**
	 * The Rate at wich the hair dryer will send his enery consumption
	 */
	public static final int REGUL_RATE = 10; //ms
	
	/**
	 * Maximum usage time
	 */
	public static final int MAX_USE_TIME = 30; //ms
	
	/**
	 * Minimum usage time
	 */
	public static final int MIN_USE_TIME = 10; // ms

}
