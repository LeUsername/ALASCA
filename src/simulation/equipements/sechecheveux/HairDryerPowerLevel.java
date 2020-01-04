package simulation.equipements.sechecheveux;

import java.util.Arrays;
import java.util.Optional;


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