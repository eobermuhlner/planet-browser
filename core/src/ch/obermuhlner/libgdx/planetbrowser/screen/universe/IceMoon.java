package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute.FractalFunction;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class IceMoon extends AbstractPlanet {

	private static final Color[][] ICEMOON_COLORS_VARIANTS = new Color[][] {
		{
			// white
			new Color(1.0f, 1.0f, 1.0f, 0.5f),
		},
		{
			// gray - white
			new Color(0.6f, 0.6f, 0.6f, 0.2f),
			new Color(0.8f, 0.8f, 0.8f, 0.5f),
			new Color(1.0f, 1.0f, 1.0f, 0.5f),
		},
		{
			// gray - white
			new Color(0.6f, 0.6f, 0.6f, 0.2f),
			new Color(0.8f, 0.8f, 0.8f, 0.5f),
			new Color(1.0f, 1.0f, 1.0f, 0.5f),
		},
		{
			// light blue - white
			new Color(0.9f, 0.9f, 1.0f, 0.5f),
			new Color(0.9f, 0.9f, 1.0f, 0.4f),
			new Color(0.9f, 0.9f, 1.0f, 0.3f),
			new Color(0.8f, 0.8f, 0.8f, 0.2f),
			new Color(0.9f, 0.9f, 0.9f, 0.4f),
			new Color(1.0f, 1.0f, 1.0f, 0.5f),
		},
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

		Color[] colors = random.next(ICEMOON_COLORS_VARIANTS);
		if (random.nextBoolean(0.3)) {
			colors = ColorUtil.randomColors(random, 6, colors, 0.02f, 0.1f);
		}

		float heightFunctionValue = random.nextFloat(0.7f, 1.5f);
		@SuppressWarnings("unchecked")
		String heightFunction = random.nextProbability(
				p(3, TerrestrialAttribute.POWER),
				p(10, TerrestrialAttribute.POWER_MID_0)
				);
		@SuppressWarnings("unchecked")
		FractalFunction fractalFunction = random.nextProbability(
				p(2, TerrestrialAttribute.FractalFunction.SignalDependentWeight),
				p(10, TerrestrialAttribute.FractalFunction.SignalDependentWeightRidged),
				p(heightFunction.equals(TerrestrialAttribute.POWER_MID_0) ? 40 : 1, TerrestrialAttribute.FractalFunction.SimpleWeight),
				p(4, TerrestrialAttribute.FractalFunction.SimpleWeightRidged)
				);

		float heightMin = 0.3f;
		float heightMax = random.nextFloat(0.5f, 0.7f);

		TerrestrialAttribute terrestrialAttribute = TerrestrialAttribute.createTerrestrial(random);
		terrestrialAttribute.fractalFunction = fractalFunction;
//		terrestrialAttribute.heightMin = heightMin;
//		terrestrialAttribute.heightMax = heightMax;
		terrestrialAttribute.heightFrequency = 3;
		terrestrialAttribute.heightFunction = heightFunction;
		terrestrialAttribute.heightFunctionValue = heightFunctionValue;
		terrestrialAttribute.planetColors = colors;
		//terrestrialAttribute.planetColorFrequencies = createPlanetColorFrequencies(random);

		terrestrialAttribute.craterBaseGrid = random.nextInt(5, 15);
		if (random.nextBoolean(0.6)) {
			terrestrialAttribute.craterProbability = random.nextFloat(0.7f, 1.0f);
		} else {
			terrestrialAttribute.craterProbability = random.nextFloat(0.0f, 0.7f);
		}

		materialAttributes.add(terrestrialAttribute);

		Material material = new Material(materialAttributes);
		
		return createTextures(disposables, material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
