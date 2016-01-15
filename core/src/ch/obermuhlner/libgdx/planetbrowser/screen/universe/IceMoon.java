package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
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
	public Array<Attribute> createMaterialAttributes(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
		Map<Long, Texture> textures = createTextures(random, planetData, xFrom, xTo, yFrom, yTo, textureTypes, textureSize);

		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));
	
		return materialAttributes;
	}
	
	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Color[] colors = ICEMOON_COLORS;
		
		Color[] randomColors = randomColors(random, 6, colors, 0.02f, 0.1f);

		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, randomColors));

		float heightPower = random.nextFloat(0.8f, 1.5f);
		String heightFunction = TerrestrialHeightShaderFunctionAttribute.functionPowerMid0(heightPower);

		float heightMin = 0.3f;
		float heightMax = random.nextFloat(0.5f, 0.7f);
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(random.nextInt(3, 3)));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMin(heightMin));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMax(heightMax));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(heightFunction));
		
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);
		
		return createTextures(material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
