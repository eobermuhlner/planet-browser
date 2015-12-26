package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Moon extends AbstractRockyPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = false;
		planetData.temperature = random.nextDouble(20, 200);

		planetData.fillStandardValues(random);
		
		return planetData;
	}
}
