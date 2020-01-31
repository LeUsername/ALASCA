package wattwatt.tools.EngineGenerator;

/**
 * The class <code>EngineGeneratorSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the engine generator component.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class EngineGeneratorSetting {

	/**
	 * The maximum fuel capacity of the engine generator
	 */
	public static final int FUEL_CAPACITY = 20;
	public static final double FULL_CAPACITY = 20.0;
	
	/**
	 * The value that we will use to compute how much energy is produce depending on how much fuel we burn.
	 */
	public static final int PROD_THR = 5;
	
	/**
	 * The Rate at wich the engine generator will send his production.
	 */
	public static final int UPDATE_RATE = 1000;
}
