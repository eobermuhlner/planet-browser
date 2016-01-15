package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Earth extends AbstractPlanet {

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.radius = Units.EARTH_RADIUS * random.nextDouble(0.5, 1.5);
		planetData.period = Units.EARTH_PERIOD * random.nextDouble(0.5, 2.5);
		planetData.temperature = random.nextDouble(Units.celsiusToKelvin(-20), Units.celsiusToKelvin(50));
		planetData.liquidSurface = random.nextDouble(0, 1) * MathUtil.transform(Units.celsiusToKelvin(20), Units.celsiusToKelvin(50), 1.0, 0.0, planetData.temperature);
		
		boolean hot = planetData.temperature > Units.celsiusToKelvin(30);
		planetData.hasLife = random.nextBoolean(hot ? 0.5 : 0.95);

		double atmosphereWaterContent = planetData.liquidSurface * 1.5 * MathUtil.transform(Units.celsiusToKelvin(-40), Units.celsiusToKelvin(100), 0.0, 1.0, planetData.temperature);
		
		if (planetData.hasLife) {
			// earth
			planetData.atmosphere = random.nextProbabilityMap(
					p(random.nextGaussian(78), Molecule.N2),
					p(random.nextGaussian(21), Molecule.O2),
					p(random.nextGaussian(0.9), Molecule.Ar),
					p(random.nextGaussian(0.04), Molecule.CO2),
					p(random.nextGaussian(0.0018), Molecule.Ne),
					p(random.nextGaussian(0.0005), Molecule.He),
					p(random.nextGaussian(0.00018), Molecule.CH4),
					p(random.nextGaussian(0.00011), Molecule.Kr),
					// water is highly variable (earth = 0.9% overall)
					p(random.nextGaussian(atmosphereWaterContent), Molecule.H2O)
					);
		} else {
			// estimated earth atmosphere without life
			planetData.atmosphere = random.nextProbabilityMap(
					p(random.nextGaussian(80), Molecule.N2),
					p(random.nextGaussian(10), Molecule.CO2),
					p(random.nextGaussian(3.0), Molecule.SO2),
					p(random.nextGaussian(2.0), Molecule.NH3),
					p(random.nextGaussian(2.0), Molecule.CH4),
					p(random.nextGaussian(1.0), Molecule.CO),
					p(random.nextGaussian(0.5), Molecule.S2),
					p(random.nextGaussian(0.1), Molecule.Cl2),
					p(random.nextGaussian(0.001), Molecule.He),
					p(random.nextGaussian(0.9), Molecule.Ar),
					p(random.nextGaussian(0.0018), Molecule.Ne),
					p(random.nextGaussian(0.00011), Molecule.Kr),
					// water is highly variable (earth = 0.9% overall)
					p(random.nextGaussian(atmosphereWaterContent), Molecule.H2O)
					);
		}
		
		planetData.clouds = random.nextProbabilityMap(
				p(100, Molecule.H2O)
		);
		
		planetData.atmosphereScatterColor = randomColor(random, new Color(0x87cefaff), 0.1f, 0.1f); // new Color(0.8f, 0.8f, 1.0f, 1.0f); 
		planetData.atmospherePassColor = randomColor(random, new Color(0xe56666ff), 0.1f, 0.1f);
		
		planetData.fillStandardValues(random);
		
		return planetData;
	}

	@Override
	public Material createMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
		int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		Map<Long, Texture> textures = createTextures(random, planetData, 0, 1, 0, 1, textureTypes, textureSize);

		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));
		
		return new Material(materialAttributes);
	}
	
	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		boolean hot = planetData.temperature > Units.celsiusToKelvin(30);
		@SuppressWarnings("unchecked")
		String textureName = random.nextProbability(
				p(hot ? 15 :  3, "terrestrial_coastlife"), // life at the coasts
				p(hot ? 10 : 20, "terrestrial_earthlife"), // earth-like life
				p(hot ? 10 : 10, "terrestrial_earthvariantlife"), // earth-life life with more variations
				p(hot ? 20 :  0, "terrestrial_highlife"), // life only in cold climate (high altitude
				p(hot ?  5 :  2, "terrestrial_spotlife"), // life only in some spots
				p(hot ? 10 :  1, "terrestrial_waterlife") // life only in the water
				);
		if (!planetData.hasLife) {
			textureName = "terrestrial_nolife";
		}
		//textureName = "terrestrial_spotlife";
		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, PlanetBrowser.getTexture(textureName + "_diffuse_map.png")));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, PlanetBrowser.getTexture(textureName + "_specular_map.png")));
		
		float water = (float) planetData.liquidSurface;
		float heightMin = MathUtil.transform(0f, 1f, 0.5f, 0.0f, water);
		float heightMax = MathUtil.transform(0f, 1f, 1.0f, 0.7f, water);
		int heightFrequency = random.nextInt(3, 5);
		float iceLevel = (float) MathUtil.transform(Units.celsiusToKelvin(-20), Units.celsiusToKelvin(50), 1f, -1f, planetData.temperature);

//		System.out.println("texture=" + textureName);
//		System.out.println("water=" + water);
//		System.out.println("temperature=" + planetData.temperature);
//		System.out.println("ice=" + iceLevel);
//		System.out.println("height=" + heightMin + " - " + heightMax);
//		System.out.println("heightFrequency=" + heightFrequency);
//		System.out.println();
		
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightWater(0.45f)); // depends on texture
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMin(heightMin));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMax(heightMax));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(heightFrequency));
//		if (heightFrequency < random.nextInt(1, 3)) {
//			materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMountains(random.nextFloat(0.8f, 1.0f)));
//		}
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createIceLevel(iceLevel));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(TerrestrialHeightShaderFunctionAttribute.CONTINENT_POWER_2));

		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		return createTextures(material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
