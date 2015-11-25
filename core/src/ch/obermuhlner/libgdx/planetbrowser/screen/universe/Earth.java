package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute.CONTINENT_POWER_2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.AtmosphereAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Earth extends AbstractPlanet {

	@Override
	protected Material createPlanetMaterial(Random random) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Texture texture = PlanetBrowser.getTexture("terrestrial_colors.png");
		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, texture));
		
		float water = random.nextFloat(0.1f, 0.9f);
		float heightMin = MathUtil.transform(0f, 1f, 0.4f, 0.0f, water);
		float heightMax = MathUtil.transform(0f, 1f, 1.0f, 0.6f, water);
		int heightFrequency = random.nextInt(0, 5);
		float temperature =  random.nextFloat((float)Units.celsiusToKelvin(-50), (float)Units.celsiusToKelvin(50));
		float iceLevel = MathUtil.transform((float)Units.celsiusToKelvin(-50), (float)Units.celsiusToKelvin(50), 1f, -1f, temperature);
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightWater(0.45f)); // depends on texture
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMin(heightMin));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMax(heightMax));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(heightFrequency));
//		if (heightFrequency < random.nextInt(1, 3)) {
//			materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMountains(random.nextFloat(0.8f, 1.0f)));
//		}
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createIceLevel(iceLevel));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(CONTINENT_POWER_2));

		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);
		
		if (true) {
			materialAttributes.clear();

			Texture textureDiffuse = renderTextureDiffuse(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textureDiffuse));
			
			Texture textureSpecular = renderTextureSpecular(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textureSpecular));
			
			Texture textureNormal = renderTextureNormal(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textureNormal));
			
			material = new Material(materialAttributes);
		}
		
		return material;
	}

	@Override
	protected AtmosphereAttribute getAtmosphereAttribute(Random random, float atmosphereSize) {
		return new AtmosphereAttribute(
				new Color(0.8f, 0.8f, 1.0f, 1.0f),
				new Color(0.9f, 0.4f, 0.4f, 1.0f),
				0.5f,
				0.5f);
	}
}
