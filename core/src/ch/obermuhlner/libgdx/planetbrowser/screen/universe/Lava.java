package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute.MID_0;
import static ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute.SQRT_MID_0;
import static ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute.POWER_2_MID_0;

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
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Lava extends AbstractPlanet {

	@Override
	protected Material createPlanetMaterial(Random random) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

//		Texture texture = PlanetBrowser.getTexture("lava_colors.png");
//		materialAttributes.add(TextureAttribute.createDiffuse(texture));
		
		String heightFunction = random.next(MID_0, SQRT_MID_0, POWER_2_MID_0);
		
		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, new Color[] {
				Color.RED,
				new Color(0.20f, 0.20f, 0.20f, 1.0f),
				new Color(0.15f, 0.15f, 0.15f, 1.0f),
		}));

		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(random.nextInt(2, 4)));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(heightFunction));
		
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		if (true) {
			materialAttributes.clear();

			Texture textureDiffuse = renderTextureDiffuse(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textureDiffuse));
			
			Texture textureNormal = renderTextureNormal(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textureNormal));

			float emissive = 0.4f;
			materialAttributes.add(new ColorAttribute(ColorAttribute.Emissive, emissive, emissive, emissive, 1.0f));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Emissive, textureDiffuse));
		
			material = new Material(materialAttributes);
		}

		return new Material(materialAttributes);
	}

}
