package simulation.tools.hairdryer;

import java.util.Arrays;
import java.util.Optional;

/**
 * The enumeration <code>HairDryerPowerLevel</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * Define the powerlevels of the hair dryer
 * 
 * <p>Created on : 2020-01-27</p>
 * 
 * @author	<p>Bah Thierno, Zheng Pascal</p>
 */
public enum HairDryerPowerLevel {
	LOW(1), MEDIUM(2), HIGH(3);

	private final int value;

	HairDryerPowerLevel(int value) {
		this.value = value;
	}

	public static Optional<HairDryerPowerLevel> valueOf(int value) {
		return Arrays.stream(values()).filter(powerLevel -> powerLevel.value == value).findFirst();
	}
	
	public int getValue() {
		return this.value;
	}
}