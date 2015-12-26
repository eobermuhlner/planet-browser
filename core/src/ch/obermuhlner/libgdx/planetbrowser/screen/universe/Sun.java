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
		planetData.radius = Units.SUN_RADIUS * random.nextDouble(0.5, 5);
		planetData.density = Units.SUN_DENSITY * random.nextDouble(0.9, 1.1);
		planetData.temperature = random.nextDouble(4000, 6000);
		
		planetData.fillStandardValues(random);
		
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
