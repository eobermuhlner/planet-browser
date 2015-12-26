package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Moon extends AbstractRockyPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = false;
		planetData.radius = random.nextDouble(Units.EARTH_RADIUS * 0.1, Units.EARTH_RADIUS * 1.5);
		planetData.period = random.nextDouble(Units.SECONDS_PER_HOUR * 5, Units.SECONDS_PER_HOUR * 40);
		planetData.temperature = random.nextDouble(20, 200);
		
		return planetData;
	}
}
