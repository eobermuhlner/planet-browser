package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialAttribute.FractalFunction;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public abstract class AbstractRockyPlanet extends AbstractPlanet {

	private static final Color[][] MOON_COLORS_VARIANTS = new Color[][] {
		{
			// Moon
			new Color(0x444444ff),
			new Color(0x555555ff),
			new Color(0x666666ff),
			new Color(0x777777ff),
			new Color(0x888888ff),
			new Color(0x999999ff),
			new Color(0xaaaaaaff),
			new Color(0xbbbbbbff),
			new Color(0xccccccff),
			new Color(0xddddddff),
			new Color(0xeeeeeeff),
		},
		{
			// Moon
			new Color(0x888888ff),
			new Color(0xaaaaaaff),
			new Color(0xccccccff),
		},
		{
			// Moon
			new Color(0x333333ff),
			new Color(0xeeeeeeff),
		},
		{
			// Europa
			new Color(0xb5a27aff), // light brown
			new Color(0x957a5dff), // medium brown
			new Color(0xe2dcccff), // light yellow
			new Color(0xb5b1a5ff), // light gray
			new Color(0xbab58fff), // light greenish
			new Color(0xf9f9efff), // white
		},
		{
			// Mars
			new Color(0xca8c64ff), // light brown
			new Color(0x975036ff), // medium brown
			new Color(0x533032ff), // dark brown
			new Color(0xcc9268ff), // light brown 2
			new Color(0xb87650ff), // medium brown 2
			new Color(0x453234ff), // almost black
			new Color(0xf2f2f2ff), // white
		},
		{
			// Io
			new Color(0x5d4627ff), // brown
			new Color(0xe9dda3ff), // almost white yellow
			new Color(0xfeed9bff), // bright yellow
			new Color(0x5d4627ff), // brown
			new Color(0x989a51ff), // green yellow
			new Color(0x96582fff), // reddish brown
			new Color(0xf2f2f2ff), // white
			new Color(0x453234ff), // almost black
		},
		{
			// Callisto
			new Color(0x806e56ff), // medium brown
			new Color(0x5a4d3aff), // dark brown
			new Color(0xb4a683ff), // light brown
			new Color(0x8b7a5cff), // medium brown 2
			new Color(0x796a53ff), // medium brown 3
			new Color(0x4c4332ff), // dark brown 2
			new Color(0xefe4c4ff), // light yellow
		}
	};
	
	protected static final Color[] ATMOSPHERE_COLORS = {
			new Color(0xffffffff), // white
			new Color(0xffccccff), // light red
			new Color(0xccffccff), // light green
			new Color(0xccccffff), // light blue
			new Color(0xccffffff), // light cyan
			new Color(0xffccffff), // light magenta
			new Color(0xffffccff), // light yellow

//			new Color(0xd2b782ff), // venus - medium yellow
//			new Color(0xe7d7b6ff), // venus - light yellow
//			new Color(0xf1e8d7ff), // venus - very light yellow
//			
//			new Color(0xdec863ff), // titan - medium yellow
	};

	@Override
	protected long getTextureTypes(PlanetData planetData) {
		return TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
	}
	
	@Override
	public Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize, DisposableContainer disposables) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Color[] colors = random.next(MOON_COLORS_VARIANTS);
		float heightMin = 0.1f;
		float heightMax = 0.8f;
		int heightFrequency = random.nextInt(3, 5);
		float heightFunctionValue = random.nextFloat(0.9f, 1.5f);
		String heightFunction = TerrestrialAttribute.POWER;
		@SuppressWarnings("unchecked")
		FractalFunction fractalFunction = random.nextProbability(
				p(2, TerrestrialAttribute.FractalFunction.SignalDependentWeight),
				p(10, TerrestrialAttribute.FractalFunction.SignalDependentWeightRidged),
				p(1, TerrestrialAttribute.FractalFunction.SimpleWeight),
				p(4, TerrestrialAttribute.FractalFunction.SimpleWeightRidged)
				);
		Color[] randomColors = ColorUtil.randomColors(random, 6, colors, 0.01f, 0.1f);
		for (int i = 0; i < randomColors.length; i++) {
			// specular color encoded in alpha (grayscale only)
			randomColors[i].a = random.nextBoolean(0.1) ? random.nextFloat(0.5f, 1.0f) : random.nextFloat(0.0f, 0.3f);
		}
		
		TerrestrialAttribute terrestrialAttribute = TerrestrialAttribute.createTerrestrial(random);
		terrestrialAttribute.fractalFunction = fractalFunction;
		//terrestrialAttribute.heightWater = 0.45f;
		terrestrialAttribute.heightMin = heightMin;
		terrestrialAttribute.heightMax = heightMax;
		terrestrialAttribute.heightFrequency = heightFrequency;
		terrestrialAttribute.heightFunction = heightFunction;
		terrestrialAttribute.heightFunctionValue = heightFunctionValue;
		terrestrialAttribute.planetColors = randomColors;
		terrestrialAttribute.planetColorFrequencies = createPlanetColorFrequencies(random);
		
		if (planetData.atmosphere != null) {
			if (random.nextBoolean(0.6)) {
				terrestrialAttribute.craterBaseGrid = random.nextInt(5, 15);
				terrestrialAttribute.craterProbability = random.nextFloat(0.0f, 0.5f);
			}
		} else {
			terrestrialAttribute.craterBaseGrid = random.nextInt(5, 15);
			if (random.nextBoolean(0.8)) {
				terrestrialAttribute.craterProbability = random.nextFloat(0.7f, 1.0f);
			} else {
				terrestrialAttribute.craterProbability = random.nextFloat(0.0f, 0.7f);
			}
		}

		materialAttributes.add(terrestrialAttribute);

		Material material = new Material(materialAttributes);

		return createTextures(disposables, material, TerrestrialPlanetShader.PROVIDER, textureTypes, textureSize, xFrom, xTo, yFrom, yTo);
	}
}
