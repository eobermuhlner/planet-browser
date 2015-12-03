package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Moon extends AbstractRockyPlanet {

	@Override
	protected PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = false;
		
		return planetData;
	}
}
