package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.AtmosphereAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.ColorArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetShader;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class Moon extends AbstractPlanet {

	private static final Color[][] MOON_COLORS_VARIANTS = new Color[][] {
		{
			// Moon
			new Color(0.6f, 0.6f, 0.6f, 1.0f),
			new Color(0.7f, 0.7f, 0.7f, 1.0f),
			new Color(0.8f, 0.8f, 0.8f, 1.0f),
		},
		{
			// Mars
			new Color(0xca8c64ff),
			new Color(0x975036ff),
			new Color(0x533032ff),
			//new Color(0x26283aff),
		}
		
		/*,
		{
			// Io
			new Color(0.8f, 0.5f, 0.4f, 1.0f),
			new Color(0.8f, 0.7f, 0.4f, 1.0f),
			new Color(0.9f, 0.8f, 0.4f, 1.0f),
			new Color(0.8f, 0.9f, 0.6f, 1.0f),
			new Color(0.5f, 0.5f, 0.5f, 1.0f),
		},
		*/
	};

	@Override
	protected PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = random.nextBoolean(0.1);
		
		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> attributes = new Array<Attribute>();
		Texture textureDiffuse = renderTextureDiffuse(random);
		Texture textureNormal = renderTextureNormalsCraters(random, planetData);
		
		attributes.add(TextureAttribute.createDiffuse(textureDiffuse));
		attributes.add(TextureAttribute.createNormal(textureNormal));
		
		return new Material(attributes);
	}
	
	public Texture renderTextureDiffuse(Random random) {
		Array<Attribute> materialAttributes = new Array<Attribute>();

		Color[] colors = random.next(MOON_COLORS_VARIANTS);
		materialAttributes.add(new ColorArrayAttribute(ColorArrayAttribute.PlanetColors, randomPlanetColors(random, colors, 0.02f)));

		materialAttributes.add(TerrestrialPlanetFloatAttribute.createHeightFrequency(random.nextInt(2, 4)));

		materialAttributes.add(createRandomFloatArrayAttribute(random));

		{
			Material material = new Material(materialAttributes);
			materialAttributes.clear();

			Texture textureDiffuse = renderTextureDiffuse(material, new TerrestrialPlanetShader.Provider());
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textureDiffuse));

			return textureDiffuse;
		}
	}
	
	public Texture renderTextureNormalsCraters(Random random, PlanetData planetData) {
		final int targetTextureWidth = 2048;
		final int targetTextureHeight = 1024;
		
		int water = 0;
		
		int areaCount = 100;
		int craterCount = random.nextInt(100, 20000);
		boolean fillWithCraters = craterCount > 10000;
		float hugeCraterProbability = random.nextBoolean(0.6f) ? 2 : random.nextFloat(0, 100); 
		float bigCraterProbability = random.nextBoolean(0.6f) ? 20 : random.nextFloat(0, 100); 
		float mediumCraterProbability = random.nextBoolean(0.6f) ? 100 : random.nextFloat(0, 200); 
		float vulcanoProbability = random.nextBoolean(0.9f) ? 0 : random.nextFloat(0, 2);
		int softCount = 0;
		boolean hasAtmosphere = planetData.hasAtmosphere;
		if (hasAtmosphere ) {
			softCount = random.nextInt(100, 500);
			softCount += water;
		}

		//System.out.println("Generating Normals craters=" + craterCount + " craterFill=" + fillWithCraters + " probHuge=" + hugeCraterProbability + " probBig=" + bigCraterProbability + " probMed=" + mediumCraterProbability +" vulcanoProb=" + vulcanoProbability + " softCount=" + softCount);
		
		FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, targetTextureWidth, targetTextureHeight, false);
		frameBuffer.begin();

		Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	       
		SpriteBatch spriteBatch = new SpriteBatch();
		spriteBatch.begin();
		
		Texture area1 = PlanetBrowser.getTexture("normals_area1.png");
		Texture area2 = PlanetBrowser.getTexture("normals_area2.png");
		Texture area3 = PlanetBrowser.getTexture("normals_area3.png");
		Texture craterArea1 = PlanetBrowser.getTexture("normals_crater_area1.png");
		Texture craterArea2 = PlanetBrowser.getTexture("normals_crater_area2.png");
		Texture craterArea3 = PlanetBrowser.getTexture("normals_crater_area3.png");
		Texture craterHuge1 = PlanetBrowser.getTexture("normals_crater_huge1.png");
		Texture craterHuge2 = PlanetBrowser.getTexture("normals_crater_huge2.png");
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
		Texture craterTiny3 = PlanetBrowser.getTexture("normals_crater_tiny3.png");
		Texture mountain1 = PlanetBrowser.getTexture("normals_mountain1.png");
		Texture mountain2 = PlanetBrowser.getTexture("normals_mountain2.png");
		Texture vulcanoHuge1 = PlanetBrowser.getTexture("normals_vulcano_huge1.png");
		Texture vulcanoBig1 = PlanetBrowser.getTexture("normals_vulcano_big1.png");
		Texture vulcanoBig2 = PlanetBrowser.getTexture("normals_vulcano_big2.png");
		Texture vulcanoBig3 = PlanetBrowser.getTexture("normals_vulcano_big3.png");
		Texture vulcanoMedium1 = PlanetBrowser.getTexture("normals_vulcano_medium1.png");
		Texture vulcanoMedium2 = PlanetBrowser.getTexture("normals_vulcano_medium2.png");
		Texture vulcanoMedium3 = PlanetBrowser.getTexture("normals_vulcano_medium3.png");
		Texture vulcanoMedium4 = PlanetBrowser.getTexture("normals_vulcano_medium4.png");
		Texture soft1 = PlanetBrowser.getTexture("normals_soft1.png");

		if (fillWithCraters) {
			{
				Texture texture = craterArea1;
				
				int nx = targetTextureWidth / texture.getWidth() * 2;
				int ny = targetTextureHeight / texture.getHeight() * 2;
				float stepx = (float)targetTextureWidth / nx;
				float stepy = (float)targetTextureHeight / ny;
				for (int iy = 0; iy < ny; iy++) {
					for (int ix = 0; ix < nx; ix++) {
						float x = ix * stepx + texture.getWidth() * random.nextFloat(-0.25f, 0.25f);
						float y = iy * stepy + texture.getHeight() * random.nextFloat(-0.25f, 0.25f);;
						spriteBatch.draw(texture, x, y);
					}
				}
			}
			for (int i = 0; i < areaCount; i++) {
				@SuppressWarnings("unchecked")
				Texture texture = random.nextProbability(
						p(10, craterArea2),
						p(10, craterArea3));
				float x = random.nextFloat(0, targetTextureWidth - texture.getWidth());
				float y = random.nextFloat(0, targetTextureHeight - texture.getHeight());
				spriteBatch.draw(texture, x, y);
			}
		} else {
			for (int i = 0; i < areaCount; i++) {
				@SuppressWarnings("unchecked")
				Texture texture = random.nextProbability(
						p(5, area1),
						p(10, area2),
						p(10, area3));
				float x = random.nextFloat(0, targetTextureWidth - texture.getWidth());
				float y = random.nextFloat(0, targetTextureHeight - texture.getHeight());
				spriteBatch.draw(texture, x, y);
			}
		}

		for (int i = 0; i < craterCount; i++) {
			@SuppressWarnings("unchecked")
			Texture texture = random.nextProbability(
					p(hugeCraterProbability, craterHuge1),
					p(hugeCraterProbability, craterHuge2),
					p(bigCraterProbability, craterBig1),
					p(bigCraterProbability, craterBig2),
					p(mediumCraterProbability, craterMedium1),
					p(mediumCraterProbability, craterMedium2),
					p(mediumCraterProbability, craterMedium3),
					p(300, craterSmall1),
					p(300, craterSmall2),
					p(300, craterSmall3),
					p(300, craterSmall4),
					p(300, craterSmall5),
					p(2000, craterTiny1),
					p(3000, craterTiny2),
					p(3000, craterTiny3),
					p(500, mountain1),
					p(100, mountain2),
					p(vulcanoProbability * 1, vulcanoHuge1),
					p(vulcanoProbability * 5, vulcanoBig1),
					p(vulcanoProbability * 5, vulcanoBig2),
					p(vulcanoProbability * 5, vulcanoBig3),
					p(vulcanoProbability * 10, vulcanoMedium1),
					p(vulcanoProbability * 10, vulcanoMedium2),
					p(vulcanoProbability * 10, vulcanoMedium3),
					p(vulcanoProbability * 10, vulcanoMedium4));
			float x = random.nextFloat(0, targetTextureWidth - texture.getWidth());
			float y = random.nextFloat(0, targetTextureHeight - texture.getHeight());
			spriteBatch.draw(texture, x, y);
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
		float atmosphereEnd = MathUtil.transform(1.0f, 1.1f, 0.8f, 0.3f, atmosphereSize);
		float centerAlpha = random.nextFloat(0.0f, 0.1f);
		float horizonAlpha = random.nextFloat(0.1f, 0.8f);
		float refractionFactor = random.nextFloat(0.3f, 0.7f);
		return new AtmosphereAttribute(
				new Color(0.8f, 0.6f, 0.6f, 1.0f),
				centerAlpha,
				horizonAlpha,
				new Color(0.8f, 0.8f, 1.0f, 1.0f),
				refractionFactor,
				atmosphereEnd);
	}
}
