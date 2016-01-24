package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class IceMoon extends AbstractPlanet {

	private static final Color[] ICEMOON_COLORS = new Color[] {
			new Color(0.6f, 0.6f, 1.0f, 0.5f),
			new Color(0.5f, 0.5f, 0.5f, 0.2f),
			new Color(0.8f, 0.8f, 0.8f, 0.5f),
			new Color(0.9f, 0.9f, 0.9f, 0.5f),
			new Color(1.0f, 1.0f, 1.0f, 0.6f),
	};

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(20, 100);
		
		planetData.fillStandardValues(random);

		return planetData;
	}

	@Override
	protected long getTextureTypes(PlanetData planetData) {
		return TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
	}

	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize, DisposableContainer disposables) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Color[] colors = ICEMOON_COLORS;
		
		Color[] randomColors = ColorUtil.randomColors(random, 6, colors, 0.02f, 0.1f);

		String heightFunction = TerrestrialAttribute.functionPowerMid0(1.2f);

		float heightMin = 0.3f;
		float heightMax = random.nextFloat(0.5f, 0.7f);

		TerrestrialAttribute terrestrialAttribute = TerrestrialAttribute.createTerrestrial(random);
		terrestrialAttribute.heightMin = heightMin;
		terrestrialAttribute.heightMax = heightMax;
		terrestrialAttribute.heightFrequency = 3;
		terrestrialAttribute.heightFunction = heightFunction;
		terrestrialAttribute.planetColors = randomColors;
		//terrestrialAttribute.planetColorFrequencies = createPlanetColorFrequencies(random);

		if (random.nextBoolean(0.7)) {
			terrestrialAttribute.craterBaseGrid = random.nextInt(5, 15);
			terrestrialAttribute.craterProbability = random.nextFloat(0.0f, 1.0f);			
		}

		materialAttributes.add(terrestrialAttribute);

		Material material = new Material(materialAttributes);
		
		return createTextures(disposables, material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
