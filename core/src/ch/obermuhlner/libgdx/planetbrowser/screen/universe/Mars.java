package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Mars extends AbstractRockyPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = true;
		planetData.radius = random.nextDouble(Units.EARTH_RADIUS * 0.2, Units.EARTH_RADIUS * 2.5);
		planetData.period = random.nextDouble(Units.SECONDS_PER_HOUR * 5, Units.SECONDS_PER_HOUR * 40);
		planetData.temperature = random.nextDouble(250, 300);
		
		return planetData;
	}
}
