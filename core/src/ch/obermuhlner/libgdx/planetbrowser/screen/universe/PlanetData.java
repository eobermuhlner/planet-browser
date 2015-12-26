package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class PlanetData {

	public boolean hasAtmosphere;
	
	public double period; // sec
	public double radius; // m
	public double density; // kg/m^3
	public double mass; // kg
	public double temperature; // K
	
	public void fillStandardValues(Random random) {
		if (radius == 0) {
			radius = Units.EARTH_RADIUS * random.nextDouble(0.1, 2.5);
		}
		
		if (period == 0) {
			period = Units.SECONDS_PER_HOUR * random.nextDouble(5, 40);
		}

		if (density == 0) {
			density = random.nextGaussian(Units.EARTH_DENSITY);
		}

		if (mass == 0) {
			mass = density * Units.volumeSphere(radius);
		}
		
		if (temperature == 0) {
			temperature = random.nextDouble(100, 280);
		}
	}
}
