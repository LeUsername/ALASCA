package simulation.tools.controller;

/**
 * The enumeration <code>Decision</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define the decision took by the Controller model
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public enum Decision {
	SUSPEND_FRIDGE, 
	RESUME_FRIDGE,
	START_WASHING, 
	STOP_WASHING,
	START_ENGINE, 
	STOP_ENGINE,
	START_TURBINE,
	STOP_WIND_TURBINE;
}
