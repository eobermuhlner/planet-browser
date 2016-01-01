package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.GasPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public abstract class AbstractGasPlanet extends AbstractPlanet {

	private Color[] colors;
	
	public AbstractGasPlanet(Color[] colors) {
		this.colors = colors;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, randomColors(random, 3, colors, 0.1f, 0.02f)));
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		if (true) {
			materialAttributes.clear();

			int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
			Texture textureDiffuse = renderTextureDiffuse(material, new GasPlanetShader.Provider(), textureSize, 0, 1, 0, 1);
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textureDiffuse));

//			Texture textureNormal = renderTextureNormal(material, new GasPlanetShader.Provider(), 0, 1, 0, 1);
//			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textureNormal));
			
			material = new Material(materialAttributes);
		}

		return material;
	}

}
