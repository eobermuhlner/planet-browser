package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.Color;

import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Neptune extends AbstractGasPlanet {

	private static final Color[] ICE_GAS_PLANET_COLORS = new Color[] {
			new Color(0.6094f, 0.6563f, 0.7695f, 1.0f),
			new Color(0.5820f, 0.6406f, 0.6406f, 1.0f),
			new Color(0.2695f, 0.5234f, 0.9102f, 1.0f),
			new Color(0.3672f, 0.4609f, 0.7969f, 1.0f),
			new Color(0.7344f, 0.8594f, 0.9102f, 1.0f),
		};

	public Neptune() {
		super(ICE_GAS_PLANET_COLORS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.radius = Units.NEPTUNE_RADIUS * random.nextDouble(0.5, 2.5);
		planetData.density = Units.NEPTUNE_DENSITY * random.nextDouble(0.8, 1.2);
		planetData.temperature = random.nextDouble(100, 300);

		planetData.atmosphere = random.nextProbabilityMap(
				p(random.nextGaussian(80), Molecule.H2),
				p(random.nextGaussian(19), Molecule.He),
				p(random.nextGaussian(1.5), Molecule.CH4)
				);

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	protected float getPlanetRadius() {
		return 2.0f;
	}
}
