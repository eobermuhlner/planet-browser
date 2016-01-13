package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.HashMap;
import java.util.Map;

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
		
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Emissive;
		int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		Map<Long, Texture> textures = createTextures(planetData, random, 0, 1, 0, 1, textureTypes, textureSize);

		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));

		return new Material(materialAttributes);
	}
	
	@Override
	public Map<Long, Texture> createTextures(PlanetData planetData, Random random, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, randomColors(random, 3, colors, 0.1f, 0.02f)));
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		Map<Long, Texture> texturesMap = new HashMap<Long, Texture>();
		Array<Texture> textures = renderTextures(material, GasPlanetShader.PROVIDER, textureSize, xFrom, xTo, yFrom, yTo, false, true, false, false, false);
		texturesMap.put(TextureAttribute.Diffuse, textures.get(0));
		return texturesMap;
	}

}
