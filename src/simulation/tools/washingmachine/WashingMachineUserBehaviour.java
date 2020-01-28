package simulation.tools.washingmachine;

/**
 * The class <code>HairDryerUserBehaviour</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the washing machine user in the simulation.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class WashingMachineUserBehaviour {

	public static final double INITIAL_DELAY = 10.0;
	public static final double INTER_DAY_DELAY = 1440.0; // 24 heures * 60 minutes
	public static final double MEAN_TIME_BETWEEN_USAGES = 2 * INTER_DAY_DELAY;
	public static final double MEAN_TIME_WORKING_ECO = 45.0;
	public static final double MEAN_TIME_WORKING_PREMIUM = 60.0;
}
