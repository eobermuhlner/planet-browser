package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class Lava extends AbstractPlanet {

	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = false;
		planetData.temperature = random.nextDouble(Units.celsiusToKelvin(500), Units.celsiusToKelvin(1200)); // lava liquid 700 - 1200 C

		planetData.fillStandardValues(random);
		
		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
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

		if (true) {
			materialAttributes.clear();

			Array<Texture> textures = renderTextures(material, new TerrestrialPlanetShader.Provider(), true, true, false, true);
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(0)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(1)));
			//materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(2)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Emissive, textures.get(2)));

			float emissive = 0.5f;
			materialAttributes.add(new ColorAttribute(ColorAttribute.Emissive, emissive, emissive, emissive, 1.0f));
		
			material = new Material(materialAttributes);
		}

		return material;
	}
}
