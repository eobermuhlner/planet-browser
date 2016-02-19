package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import ch.obermuhlner.libgdx.planetbrowser.render.GasPlanetAttribute;
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
		GasPlanetAttribute gasPlanetAttribute = GasPlanetAttribute.createGasPlanet(random);
		float deltaColor = random.nextFloat(0.01f, 0.5f);
		float deltaLuminance = random.nextFloat(0.01f, 0.2f);
		gasPlanetAttribute.planetColors = ColorUtil.randomColors(random, 3, colors, deltaColor, deltaLuminance);

		Material material = new Material(gasPlanetAttribute);

		return createTextures(disposables, material, GasPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}

}
