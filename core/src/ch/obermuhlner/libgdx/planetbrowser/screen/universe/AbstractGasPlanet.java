package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.GasPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public abstract class AbstractGasPlanet extends AbstractPlanet {

	private Color[] colors;
	
	public AbstractGasPlanet(Color[] colors) {
		this.colors = colors;
	}

	@Override
	protected long getTextureTypes(PlanetData planetData) {
		return TextureAttribute.Diffuse | TextureAttribute.Normal;
	}
	
	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize, DisposableContainer disposables) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, ColorUtil.randomColors(random, 3, colors, 0.1f, 0.02f)));
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);

		return createTextures(disposables, material, GasPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}

}
