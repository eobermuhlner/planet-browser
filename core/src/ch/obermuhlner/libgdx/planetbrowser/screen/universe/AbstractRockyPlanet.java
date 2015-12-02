package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.Config;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.AtmosphereAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialHeightShaderFunctionAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Tuple2;

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
	private static final Color[] ATMOSPHERE_COLORS = {
			new Color(0xdd8888ff), // light red
			new Color(0x88dd88ff), // light green
			new Color(0x8888ddff), // light blue
			new Color(0x88ddddff), // light cyan
			new Color(0xdd88ddff), // light magenta
			new Color(0xdddd88ff), // light yellow
//
//			new Color(0xd2b782ff), // venus - medium yellow
//			new Color(0xe7d7b6ff), // venus - light yellow
//			new Color(0xf1e8d7ff), // venus - very light yellow
//			
//			new Color(0xdec863ff), // titan - medium yellow
	};

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Color[] colors = random.next(MOON_COLORS_VARIANTS);
		float heightMin = 0.3f;
		float heightMax = 0.8f;
		int heightFrequency = random.nextInt(2, 4);
		@SuppressWarnings("unchecked")
		String heightFunction = random.nextProbability(
				p(2, TerrestrialHeightShaderFunctionAttribute.CONTINENT_POWER_2),
				p(3, TerrestrialHeightShaderFunctionAttribute.SMOOTH),
				p(3, TerrestrialHeightShaderFunctionAttribute.SMOOTH + TerrestrialHeightShaderFunctionAttribute.POWER_2),
				p(20, TerrestrialHeightShaderFunctionAttribute.functionPower(random.nextFloat(1.0f, 4.0f)))
				);
		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, randomColors(random, 6, colors, 0.01f, 0.1f)));
		materialAttributes.add(createPlanetColorFrequenciesAttribute(random));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMin(heightMin));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightMax(heightMax));
		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(heightFrequency));
		materialAttributes.add(new TerrestrialHeightShaderFunctionAttribute(heightFunction));
		materialAttributes.add(createRandomFloatArrayAttribute(random));

		Material material = new Material(materialAttributes);
		{
			materialAttributes.clear();

			Texture textureDiffuse = renderTextureDiffuse(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textureDiffuse));

			Texture textureNormal = renderTextureNormalsCraters(random, planetData, material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(TextureAttribute.createNormal(textureNormal));

			material = new Material(materialAttributes);
		}

		return material;
	}
	
	public Texture renderTextureNormalsCraters(Random random, PlanetData planetData, Material material, ShaderProvider shaderProvider) {
		final int targetTextureWidth = Config.textureSize;
		final int targetTextureHeight = Config.textureSize;
		
		int craterFactor = random.nextInt(1, 30);

		boolean favorHugeCraters = random.nextBoolean(0.1);
		int hugeCraterCount = favorHugeCraters ? random.nextInt(5, 20) : random.nextInt(0, 10);
		int bigCraterCount = random.nextInt(5, favorHugeCraters ? 10 : 20);
		int mediumCraterCount = random.nextInt(20, favorHugeCraters ? 40 : 80);
		int smallCraterCount = random.nextInt(50, favorHugeCraters ? 70 : 200);
		int tinyCraterCount = random.nextInt(200, favorHugeCraters ? 300 : 3000);
		
		int softCount = 0;
		if (planetData.hasAtmosphere ) {
			softCount = random.nextInt(1000, 2000);
		}

		int totalCraterCount = craterFactor * (hugeCraterCount + bigCraterCount + mediumCraterCount + smallCraterCount + tinyCraterCount);
		System.out.println("Generating craterFactor=" + craterFactor + " huge=" + hugeCraterCount + " big=" + bigCraterCount + " medium=" + mediumCraterCount + " small=" + smallCraterCount + " tiny=" + tinyCraterCount + " softCount=" + softCount + " total=" + totalCraterCount);

		FrameBuffer frameBuffer = renderFrameBufferNormal(material, new TerrestrialPlanetShader.Provider());

		frameBuffer.begin();

		SpriteBatch spriteBatch = new SpriteBatch();
		spriteBatch.begin();

		Texture craterHuge1 = PlanetBrowser.getTexture("normals_crater_huge1.png");
		Texture craterBig1 = PlanetBrowser.getTexture("normals_crater_big1.png");
		Texture craterBig2 = PlanetBrowser.getTexture("normals_crater_big2.png");
		Texture craterMedium1 = PlanetBrowser.getTexture("normals_crater_medium1.png");
		Texture craterMedium2 = PlanetBrowser.getTexture("normals_crater_medium2.png");
		Texture craterMedium3 = PlanetBrowser.getTexture("normals_crater_medium3.png");
		Texture craterSmall1 = PlanetBrowser.getTexture("normals_crater_small1.png");
		Texture craterSmall2 = PlanetBrowser.getTexture("normals_crater_small2.png");
		Texture craterSmall3 = PlanetBrowser.getTexture("normals_crater_small3.png");
		Texture craterSmall4 = PlanetBrowser.getTexture("normals_crater_small4.png");
		Texture craterSmall5 = PlanetBrowser.getTexture("normals_crater_small5.png");
		Texture craterTiny1 = PlanetBrowser.getTexture("normals_crater_tiny1.png");
		Texture craterTiny2 = PlanetBrowser.getTexture("normals_crater_tiny2.png");
		Texture soft1 = PlanetBrowser.getTexture("normals_soft1.png");

		@SuppressWarnings("unchecked")
		Tuple2<Integer, Texture>[] texturesToDraw = new Tuple2[] {
			new Tuple2<Integer, Texture>(random.nextInt(hugeCraterCount * craterFactor), craterHuge1),
			new Tuple2<Integer, Texture>(random.nextInt(bigCraterCount * craterFactor), craterBig1),
			new Tuple2<Integer, Texture>(random.nextInt(bigCraterCount * craterFactor), craterBig2),
			new Tuple2<Integer, Texture>(random.nextInt(mediumCraterCount * craterFactor), craterMedium1),
			new Tuple2<Integer, Texture>(random.nextInt(mediumCraterCount * craterFactor), craterMedium2),
			new Tuple2<Integer, Texture>(random.nextInt(mediumCraterCount * craterFactor), craterMedium3),
			new Tuple2<Integer, Texture>(random.nextInt(smallCraterCount * craterFactor), craterSmall1),
			new Tuple2<Integer, Texture>(random.nextInt(smallCraterCount * craterFactor), craterSmall2),
			new Tuple2<Integer, Texture>(random.nextInt(smallCraterCount * craterFactor), craterSmall3),
			new Tuple2<Integer, Texture>(random.nextInt(smallCraterCount * craterFactor), craterSmall4),
			new Tuple2<Integer, Texture>(random.nextInt(smallCraterCount * craterFactor), craterSmall5),
			new Tuple2<Integer, Texture>(random.nextInt(tinyCraterCount * craterFactor), craterTiny1),
			new Tuple2<Integer, Texture>(random.nextInt(tinyCraterCount * craterFactor), craterTiny2),
		};
		
		for (int i = 0; i < texturesToDraw.length; i++) {
			int count = texturesToDraw[i].getValue1();
			Texture texture = texturesToDraw[i].getValue2();
			for (int j = 0; j < count; j++) {
				float x = random.nextFloat(0, targetTextureWidth - texture.getWidth());
				float y = random.nextFloat(0, targetTextureHeight - texture.getHeight());
				spriteBatch.draw(texture, x, y);
			}
		}
		
		for (int i = 0; i < softCount; i++) {
			Texture texture = soft1;
			float x = random.nextFloat(0, targetTextureWidth - texture.getWidth());
			float y = random.nextFloat(0, targetTextureHeight - texture.getHeight());
			spriteBatch.draw(texture, x, y);
		}

		spriteBatch.end();

		frameBuffer.end();
		
		Texture texture = frameBuffer.getColorBufferTexture();

		//frameBuffer.dispose(); // FIXME memory leak
		
		return texture;
	}

	@Override
	protected AtmosphereAttribute getAtmosphereAttribute(Random random, PlanetData planetData, float atmosphereSize) {
		if (! planetData.hasAtmosphere) {
			return null;
		}
		
		Color atmosphereColor = randomColor(random, ATMOSPHERE_COLORS, 0.5f, 0.2f);
		Color refractionColor = randomColor(random, ATMOSPHERE_COLORS, 0.5f, 0.2f); // TODO contrary color than atmosphereColor
		
		float atmosphereEnd = MathUtil.transform(1.0f, 1.1f, 0.8f, 0.3f, atmosphereSize);
		float centerAlpha = random.nextFloat(0.0f, 0.3f);
		float horizonAlpha = random.nextFloat(centerAlpha, 0.5f);
		float refractionFactor = random.nextFloat(0.0f, 0.7f);
		return new AtmosphereAttribute(
				atmosphereColor,
				centerAlpha,
				horizonAlpha,
				refractionColor,
				refractionFactor,
				atmosphereEnd);
	}
}
