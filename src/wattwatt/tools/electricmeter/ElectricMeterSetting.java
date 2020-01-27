package wattwatt.tools.electricmeter;

/**
 * The class <code>ElectricMeterSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the electric meter component.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class ElectricMeterSetting {
	
	/**
	 * Those are used only to have a good behaviour of this component, normaly he have to send the energy consumption of all devices
	 */
	public static final int MIN_THR_HOUSE_CONSUMPTION = 1000;
	public static final int MAX_THR_HOUSE_CONSUMPTION = 1300;
	
	/**
	 * The Rate at wich the Controller will send the overall energy consumption.
	 */
	public static final int UPDATE_RATE = 1000; //ms

}
