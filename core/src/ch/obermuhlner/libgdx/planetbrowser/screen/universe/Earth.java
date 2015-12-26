package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;
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
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = true;
		planetData.temperature = random.nextDouble(Units.celsiusToKelvin(-20), Units.celsiusToKelvin(50));
		
		return planetData;
	}
	
	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		@SuppressWarnings("unchecked")
		String textureName = random.nextProbability(
				p(3, "terrestrial_nolife"), // no life
				p(5, "terrestrial_coastlife"), // life at the coasts
				p(20, "terrestrial_earthlife"), // earth-like life
				p(10, "terrestrial_earthvariantlife"), // earth-life life with more variations
				p(8, "terrestrial_highlife"), // life only in cold climate (high altitude
				p(3, "terrestrial_spotlife"), // life only in some spots
				p(1, "terrestrial_waterlife") // life only in the water
				);
		//textureName = "terrestrial_spotlife";
		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, PlanetBrowser.getTexture(textureName + "_diffuse_map.png")));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, PlanetBrowser.getTexture(textureName + "_specular_map.png")));
		
		float water = random.nextFloat(0.0f, 1.0f) * (float) MathUtil.transform(Units.celsiusToKelvin(20), Units.celsiusToKelvin(50), 1.0f, 0.0f, planetData.temperature);
		float heightMin = MathUtil.transform(0f, 1f, 0.5f, 0.0f, water);
		float heightMax = MathUtil.transform(0f, 1f, 1.0f, 0.7f, water);
		int heightFrequency = random.nextInt(3, 5);
		float iceLevel = (float) MathUtil.transform(Units.celsiusToKelvin(-20), Units.celsiusToKelvin(50), 1f, -1f, planetData.temperature);

		System.out.println();
		System.out.println("texture=" + textureName);
		System.out.println("water=" + water);
		System.out.println("temperature=" + planetData.temperature);
		System.out.println("ice=" + iceLevel);
		System.out.println("height=" + heightMin + " - " + heightMax);
		System.out.println("heightFrequency=" + heightFrequency);
		
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

			Array<Texture> textures = renderTextures(material, new TerrestrialPlanetShader.Provider(), true, true, true, false);
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(0)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(1)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(2)));
			
			material = new Material(materialAttributes);
		}
		
		return material;
	}

	@Override
	protected AtmosphereAttribute getAtmosphereAttribute(Random random, PlanetData planetData, float atmosphereSize) {
		float atmosphereEnd = MathUtil.transform(1.0f, 1.1f, 0.8f, 0.3f, atmosphereSize);
		float centerAlpha = random.nextFloat(0.0f, 0.1f);
		float horizonAlpha = random.nextFloat(0.1f, 0.8f);
		float refractionFactor = random.nextFloat(0.3f, 0.7f);
		return new AtmosphereAttribute(
				new Color(0.8f, 0.8f, 1.0f, 1.0f),
				centerAlpha,
				horizonAlpha,
				new Color(0.9f, 0.4f, 0.4f, 1.0f),
				refractionFactor,
				atmosphereEnd);
	}
}
