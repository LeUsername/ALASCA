package simulation.tools.enginegenerator;

/**
 * The class <code>HairDryerSetting</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define somme variable used to set up the behaviour of a user.
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public class EngineGeneratorUserBehaviour {

	public static final double INITIAL_DELAY = 10.0;
	public static final double INTERDAY_DELAY = 1440.0; // 24 heures * 60 minutes
	public static final double MEAN_TIME_BETWEEN_USAGES = 2880.0;
	public static final double MEAN_TIME_USAGE = 300.0;
	public static final double MEAN_TIME_REFILL = 5.0;
}
