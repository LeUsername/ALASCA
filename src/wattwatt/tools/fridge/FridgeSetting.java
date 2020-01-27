package wattwatt.tools.fridge;

/**
 * The class <code>FridgeSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the fridge component.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class FridgeSetting {

	/**
	 * Initial temperature on upper compartement
	 */
	public static final double TEMP_H_INIT = 3.0;
	/**
	 * Initial temperature on lower compartement
	 */
	public static final double TEMP_L_INIT = 10.0;
	
	/**
	 * Minimal temperature on upper compartement
	 */
	public static final double TEMP_H_MIN = 2.0;
	
	/**
	 * Maximal temperature on upper compartement
	 */
	public static final double TEMP_H_MAX = 6.0;
	
	/**
	 * Minimal temperature on lower compartement
	 */
	public static final double TEMP_L_MIN = 8.0;
	
	/**
	 * Maximal temperature on lower compartement
	 */
	public static final double TEMP_L_MAX = 12.0;
	
	/**
	 * Consumption when the fridge is on and not suspended
	 */
	public static final double ACTIVE_CONSUMPTION = 12.0;
	
	/**
	 * Consumption when the fridge is suspended
	 */
	public static final double PASSIVE_CONSUMPTION = 6.0;
	
	public static final double OPENING_ENERGY_CONSUMPTION = 2.0;
	
	/**
	 * The Rate at wich the fridge will send his energy consumption.
	 */
	public static final int UPDATE_RATE = 500; //ms
	
	
	
}
