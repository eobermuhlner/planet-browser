package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Mars extends AbstractRockyPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = true;
		planetData.temperature = random.nextDouble(250, 300);

		planetData.fillStandardValues(random);
		
		return planetData;
	}
}
