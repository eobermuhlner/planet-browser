package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import ch.obermuhlner.libgdx.planetbrowser.render.SunFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Sun extends AbstractPlanet {

	public Sun() {
	}

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = false;
		planetData.radius = random.nextDouble(Units.SUN_RADIUS * 0.5, Units.SUN_RADIUS * 5);
		planetData.period = random.nextDouble(Units.SECONDS_PER_HOUR * 5, Units.SECONDS_PER_HOUR * 40);
		planetData.temperature = random.nextDouble(4000, 6000);
		
		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		return new Material(
				new ColorAttribute(ColorAttribute.Diffuse, Color.RED),
				new ColorAttribute(ColorAttribute.Emissive, Color.YELLOW),
				SunFloatAttribute.createSunNoise(0.5f));
	}
}
