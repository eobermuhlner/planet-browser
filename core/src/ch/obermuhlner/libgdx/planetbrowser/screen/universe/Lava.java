package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Lava extends AbstractPlanet {

	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.temperature = random.nextDouble(Units.celsiusToKelvin(500), Units.celsiusToKelvin(1200)); // lava liquid 700 - 1200 C

		planetData.atmosphere = random.nextProbabilityMap(
				p(random.nextGaussian(50), Molecule.N2),
				p(random.nextGaussian(20), Molecule.CO2),
				p(random.nextGaussian(10.0), Molecule.SO2),
				p(random.nextGaussian(8.0), Molecule.NH3),
				p(random.nextGaussian(8.0), Molecule.CH4),
				p(random.nextGaussian(5.0), Molecule.CO),
				p(random.nextGaussian(2.0), Molecule.S2),
				p(random.nextGaussian(2.0), Molecule.H2O),
				p(random.nextGaussian(1.0), Molecule.Cl2),
				p(random.nextGaussian(0.2), Molecule.He),
				p(random.nextGaussian(0.1), Molecule.H2),
				p(random.nextGaussian(0.01), Molecule.Ar)
				);

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Emissive;
		int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		Map<Long, Texture> textures = createTextures(planetData, random, 0, 1, 0, 1, textureTypes, textureSize);

		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Emissive, textures.get(TextureAttribute.Emissive)));

		float emissive = 0.5f;
		materialAttributes.add(new ColorAttribute(ColorAttribute.Emissive, emissive, emissive, emissive, 1.0f));

		return new Material(materialAttributes);
	}
	
	@Override
	public Map<Long, Texture> createTextures(PlanetData planetData, Random random, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		float temperatureAsPower = (float)MathUtil.transform(
				Units.celsiusToKelvin(500), Units.celsiusToKelvin(1200),
				-1.0, 1.0,
				planetData.temperature);
		float heightPower = MathUtil.pow(10, temperatureAsPower);
		String heightFunction = TerrestrialHeightShaderFunctionAttribute.functionPowerMid0(heightPower);
		
		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, new Color[] {
				new Color(0xff0000ff), // red
				new Color(0xee2200ff), // red-orange
				new Color(0xff5500ff), // orange
				new Color(0.30f, 0.30f, 0.30f, 1.0f),
				new Color(0.20f, 0.20f, 0.20f, 1.0f),
				new Color(0.15f, 0.15f, 0.15f, 1.0f),
		}));

		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(random.nextInt(2, 4)));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(heightFunction));
		
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		Map<Long, Texture> texturesMap = new HashMap<Long, Texture>();
		// FIXME only calculate asked textures
		Array<Texture> textures = renderTextures(material, TerrestrialPlanetShader.PROVIDER, textureSize, xFrom, xTo, yFrom, yTo, true, true, true, false, true);
		texturesMap.put(TextureAttribute.Bump, textures.get(0));
		texturesMap.put(TextureAttribute.Diffuse, textures.get(1));
		texturesMap.put(TextureAttribute.Normal, textures.get(2));
		texturesMap.put(TextureAttribute.Emissive, textures.get(3));
		return texturesMap;
	}
}
