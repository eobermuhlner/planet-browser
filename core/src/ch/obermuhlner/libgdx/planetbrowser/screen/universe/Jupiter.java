package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Jupiter extends AbstractGasPlanet {

	private static final Color[] GAS_PLANET_COLORS = new Color[] {
			new Color(0.3333f, 0.2222f, 0.1111f, 1.0f),
			new Color(0.8555f, 0.8125f, 0.7422f, 1.0f),
			new Color(0.4588f, 0.4588f, 0.4297f, 1.0f),
			new Color(0.5859f, 0.3906f, 0.2734f, 1.0f),
	};

	public Jupiter() {
		super(GAS_PLANET_COLORS);
	}

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = true;
		planetData.radius = Units.JUPITER_RADIUS * random.nextDouble(0.5, 2.5);
		planetData.density = Units.JUPITER_DENSITY * random.nextDouble(0.8, 1.2);
		planetData.temperature = random.nextDouble(180, 300);

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	protected float getPlanetRadius() {
		return 3.0f;
	}

}
