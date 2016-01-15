package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class IceMoon extends AbstractPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(20, 100);
		
		planetData.fillStandardValues(random);

		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
		int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		Map<Long, Texture> textures = createTextures(planetData, random, 0, 1, 0, 1, textureTypes, textureSize);

		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));
	
		return new Material(materialAttributes);
	}
	
	@Override
	public Map<Long, Texture> createTextures(PlanetData planetData, Random random, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, new Color[] {
				new Color(0.6f, 0.6f, 1.0f, 1.0f),
				new Color(1.0f, 1.0f, 1.0f, 1.0f),
				new Color(1.0f, 1.0f, 1.0f, 1.0f),
		}));

		String heightFunction = TerrestrialHeightShaderFunctionAttribute.functionPowerMid0(0.8f);

		float heightMin = 0.3f;
		float heightMax = random.nextFloat(0.5f, 0.7f);
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(random.nextInt(1, 4)));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMin(heightMin));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMax(heightMax));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(heightFunction));
		
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);
		
		return createTextures(material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
