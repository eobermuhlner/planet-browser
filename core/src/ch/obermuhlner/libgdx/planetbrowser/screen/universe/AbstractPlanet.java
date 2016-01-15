package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.graphics.glutils.MultiTextureFrameBuffer;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder.VertexInfo;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.AtmosphereAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.FloatArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.MoreFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.UberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public abstract class AbstractPlanet implements PlanetFactory {

	protected ModelBuilder modelBuilder = new ModelBuilder();

	@Override
	public Array<Attribute> createMaterialAttributes(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = getTextureTypes(planetData);
		Map<Long, Texture> textures = createTextures(random, planetData, xFrom, xTo, yFrom, yTo, textureTypes, textureSize);

		addTexture(materialAttributes, textureTypes, TextureAttribute.Bump, textures);
		addTexture(materialAttributes, textureTypes, TextureAttribute.Diffuse, textures);
		addTexture(materialAttributes, textureTypes, TextureAttribute.Normal, textures);
		addTexture(materialAttributes, textureTypes, TextureAttribute.Specular, textures);
		addTexture(materialAttributes, textureTypes, TextureAttribute.Emissive, textures);
		
		return materialAttributes;
	}

	private void addTexture(Array<Attribute> materialAttributes, long textureTypes, long textureType, Map<Long, Texture> textures) {
		if ((textureTypes & textureType) != 0) {
			materialAttributes.add(new TextureAttribute(textureType, textures.get(textureType)));
		}
	}

	protected abstract long getTextureTypes(PlanetData planetData);
	
	@Override
	public Array<ModelInstance> createModelInstance(Random random, PlanetData planetData, Material material) {
		Array<ModelInstance> modelInstances = new Array<ModelInstance>();

		float size = Units.toRenderUnit(planetData.radius) * 2;

		{
			Model model = createSphere(size, material);
			modelInstances.add(new ModelInstance(model));
		}
		
		{
			float atmosphereSize = getAtmosphereSize(random, planetData);
			Material atmosphereMaterial = createAtmosphereMaterial(random, planetData, atmosphereSize);
			if (atmosphereMaterial != null) {
				Model model = createSphere(size * atmosphereSize, atmosphereMaterial);
				modelInstances.add(new ModelInstance(model));
			}
		}
		
		return modelInstances;
	}
	
	private Model createSphere(float size, Material material) {
		long attributes = Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates;
		int divisions = PlanetBrowser.INSTANCE.options.getSphereDivisions();
		Model model = modelBuilder.createSphere(size, size, size, divisions, divisions, material, attributes);
		return model;
	}

	protected Map<Long, Texture> createTextures(Material material, ShaderProvider shaderProvider, long textureTypes, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		boolean bump = (textureTypes & TextureAttribute.Bump) != 0; 
		boolean diffuse = (textureTypes & TextureAttribute.Diffuse) != 0; 
		boolean normal = (textureTypes & TextureAttribute.Normal) != 0; 
		boolean specular = (textureTypes & TextureAttribute.Specular) != 0; 
		boolean emissive = (textureTypes & TextureAttribute.Emissive) != 0; 
		
		Map<Long, Texture> texturesMap = new HashMap<Long, Texture>();
		Array<Texture> textures = renderTextures(
				material, shaderProvider, textureSize,
				xFrom, xTo, yFrom, yTo,
				bump, diffuse, normal, specular, emissive);

		int index = 0;
		if (bump) {
			texturesMap.put(TextureAttribute.Bump, textures.get(index++));
		}
		if (diffuse) {
			texturesMap.put(TextureAttribute.Diffuse, textures.get(index++));
		}
		if (normal) {
			texturesMap.put(TextureAttribute.Normal, textures.get(index++));
		}
		if (specular) {
			texturesMap.put(TextureAttribute.Specular, textures.get(index++));
		}
		if (emissive) {
			texturesMap.put(TextureAttribute.Emissive, textures.get(index++));
		}
		return texturesMap;
	}

	protected AtmosphereAttribute getAtmosphereAttribute(Random random, PlanetData planetData, float atmosphereSize) {
		if (planetData.atmosphere == null) {
			return null;
		}

		float atmosphereEnd = MathUtil.transform(1.0f, 1.1f, 0.8f, 0.3f, atmosphereSize);
		float centerAlpha = random.nextFloat(0.0f, 0.3f);
		float horizonAlpha = random.nextFloat(centerAlpha, 0.5f);
		float refractionFactor = random.nextFloat(0.1f, 0.7f);
		return new AtmosphereAttribute(
				planetData.atmosphereScatterColor,
				centerAlpha,
				horizonAlpha,
				planetData.atmospherePassColor,
				refractionFactor,
				atmosphereEnd);
	}
	
	protected float getAtmosphereSize(Random random, PlanetData planetData) {
		return random.nextFloat(1.01f, 1.03f);
	}
	
	protected Material createAtmosphereMaterial(Random random, PlanetData planetData, float atmosphereSize) {
		AtmosphereAttribute atmosphereAttribute = getAtmosphereAttribute(random, planetData, atmosphereSize);
		if (atmosphereAttribute == null) {
			return null;
		}
		
		Array<Attribute> materialAttributes = new Array<Attribute>();

		materialAttributes.add(atmosphereAttribute);

		Material material = new Material(materialAttributes);
		return material;
	}

	public Color[] randomColors(Random random, int colorCount, Color[] colors, float deltaColor, float deltaLuminance) {
		Color[] result = new Color[colorCount];
		for (int i = 0; i < result.length; i++) {
			result[i] = randomColor(random, colors, deltaColor, deltaLuminance);
		}
		return result;
	}

	public Color randomColor(Random random, Color[] colors, float deltaColor, float deltaLuminance) {
		return randomColor(random, colors[random.nextInt(colors.length)], deltaColor, deltaLuminance);
	}
	
	public Color randomColor(Random random, Color color, float deltaColor, float deltaLuminance) {
		float randomLuminance = random.nextFloat(1 - deltaLuminance, 1 + deltaLuminance);
		return new Color(
			MathUtil.clamp(color.r * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			MathUtil.clamp(color.g * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			MathUtil.clamp(color.b * random.nextFloat(1 - deltaColor, 1 + deltaColor) * randomLuminance, 0.0f, 1.0f),
			1.0f);
	}

	public FloatArrayAttribute createRandomFloatArrayAttribute(Random random) {
		float floatArray[] = new float[10];
		for (int i = 0; i < floatArray.length; i++) {
			floatArray[i] = random.nextFloat();
		}
		
		return new FloatArrayAttribute(FloatArrayAttribute.RandomFloatArray, floatArray);
	}

	public FloatArrayAttribute createPlanetColorFrequenciesAttribute(Random random) {
		float floatArray[] = new float[4];
		for (int i = 0; i < floatArray.length; i++) {
			@SuppressWarnings("unchecked")
			float powerOfTwo = random.nextProbability(
					p(5, 2f),
					p(10, 4f),
					p(10, 8f),
					p(4, 16f),
					p(2, 32f));
			floatArray[i] = powerOfTwo;
		}
		
		return new FloatArrayAttribute(FloatArrayAttribute.PlanetColorFrequencies, floatArray);
	}
	
	public Texture renderTextureBump (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateBump());
		return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0);
	}

	public Texture renderTextureDiffuse (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateDiffuse());
		return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0);
	}

	public Texture renderTextureNormal (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateNormal());
		material.set(MoreFloatAttribute.createNormalStep(1f / textureSize * Math.max(Math.abs(xTo-xFrom), Math.abs(yTo-yFrom))));
		return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0);
	}
	
	public Texture renderTextureSpecular (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateSpecular());
		return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0);
	}
	
	public Texture renderTextureEmissive (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateEmissive());
		return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0);
	}

	public Array<Texture> renderTextures (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo, boolean bump, boolean diffuse, boolean normal, boolean specular, boolean emissive) {
		if (useMultiTextureRendering()) {
			material.set(TerrestrialPlanetFloatAttribute.createTextures(bump, diffuse, normal, specular, emissive));
			if (normal) {
				material.set(MoreFloatAttribute.createNormalStep(1f / textureSize * Math.max(Math.abs(xTo-xFrom), Math.abs(yTo-yFrom))));
			}
			
			int textureCount = 0;
			textureCount += bump ? 1 : 0;
			textureCount += diffuse ? 1 : 0;
			textureCount += normal ? 1 : 0;
			textureCount += specular ? 1 : 0;
			textureCount += emissive ? 1 : 0;
			return renderTextures(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, textureCount);
		} else {
			Array<Texture> textures = new Array<Texture>();
			if (bump) {
				textures.add(renderTextureBump(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo));
			}
			if (diffuse) {
				textures.add(renderTextureDiffuse(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo));
			}
			if (normal) {
				textures.add(renderTextureNormal(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo));
			}
			if (specular) {
				textures.add(renderTextureSpecular(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo));
			}
			if (emissive) {
				textures.add(renderTextureEmissive(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo));
			}
			return textures;
		}
	}
	
	private final VertexInfo vertTmp1 = new VertexInfo();
	private final VertexInfo vertTmp2 = new VertexInfo();
	private final VertexInfo vertTmp3 = new VertexInfo();
	private final VertexInfo vertTmp4 = new VertexInfo();
	private Array<Texture> renderTextures (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo, int textureCount) {
		final int rectSize = 1;
		Model model = createTerrainMeshModel(material, xFrom, xTo, yFrom, yTo, rectSize);
		ModelInstance instance = new ModelInstance(model);

		ModelBatch modelBatch = new ModelBatch(shaderProvider == null ? UberShaderProvider.DEFAULT : shaderProvider);

		MultiTextureFrameBuffer multiTextureFrameBuffer = null;
		FrameBuffer frameBuffer = null;
		if (useMultiTextureRendering()) {
			multiTextureFrameBuffer = new MultiTextureFrameBuffer(Pixmap.Format.RGB888, textureSize, textureSize, textureCount);
			multiTextureFrameBuffer.begin();			
		} else {
			frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, textureSize, textureSize, false);
			frameBuffer.begin();
		}
		OrthographicCamera camera = new OrthographicCamera(rectSize*2, rectSize*2);
		camera.position.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update();

		modelBatch.begin(camera);
		modelBatch.render(instance);
		modelBatch.end();

		Array<Texture> textures;
		if (multiTextureFrameBuffer != null) {
			multiTextureFrameBuffer.end();
			textures = multiTextureFrameBuffer.getColorBufferTextures();
		} else {
			frameBuffer.end();
			textures = new Array<Texture>();
			textures.add(frameBuffer.getColorBufferTexture());
		}		

		model.dispose();
		modelBatch.dispose();
		//frameBuffer.dispose(); // FIXME memory leak
		
		return textures;
	}

	private Model createTerrainMeshModel(Material material, float xFrom, float xTo, float yFrom, float yTo,
			final int rectSize) {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder part = modelBuilder.part("terrain", GL20.GL_TRIANGLES, (long) (Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates), material);

		float x00 = rectSize;
		float y00 = 0f;
		float z00 = -rectSize;
		float x10 = -rectSize;
		float y10 = 0f;
		float z10 = -rectSize;
		float x11 = -rectSize;
		float y11 = 0f;
		float z11 = rectSize;
		float x01 = rectSize;
		float y01 = 0f;
		float z01 = rectSize;
		float normalX = 0;
		float normalY = 1;
		float normalZ = 0;
		part.rect(
				vertTmp1.set(null).setPos(x00, y00, z00).setNor(normalX, normalY, normalZ).setUV(xFrom, yTo),
				vertTmp2.set(null).setPos(x10, y10, z10).setNor(normalX, normalY, normalZ).setUV(xTo, yTo),
				vertTmp3.set(null).setPos(x11, y11, z11).setNor(normalX, normalY, normalZ).setUV(xTo, yFrom),
				vertTmp4.set(null).setPos(x01, y01, z01).setNor(normalX, normalY, normalZ).setUV(xFrom, yFrom));

		Model model = modelBuilder.end();
		return model;
	}

	public FrameBuffer renderFrameBufferNormal (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateNormal()); // FIXME just adding attribute is wrong, modifies the material
		material.set(MoreFloatAttribute.createNormalStep(1f / textureSize * Math.max(Math.abs(xTo-xFrom), Math.abs(yTo-yFrom))));
		return renderFrameBuffer(material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo);
	}

	public FrameBuffer renderFrameBuffer (Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {		
		final int rectSize = 1;
		Model model = createTerrainMeshModel(material, xFrom, xTo, yFrom, yTo, rectSize);
		ModelInstance instance = new ModelInstance(model);

		ModelBatch modelBatch = new ModelBatch(shaderProvider == null ? UberShaderProvider.DEFAULT : shaderProvider);

		FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, textureSize, textureSize, false);
		frameBuffer.begin();

		OrthographicCamera camera = new OrthographicCamera(rectSize*2, rectSize*2);
		camera.position.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update();

		modelBatch.begin(camera);
		modelBatch.render(instance);
		modelBatch.end();

		frameBuffer.end();
		
		//model.dispose(); //FIXME memory leak
		//modelBatch.dispose(); //FIXME memory leak
				
		return frameBuffer;
	}

	private boolean useMultiTextureRendering() {
		return Gdx.graphics.isGL30Available() && PlanetBrowser.INSTANCE.options.getUseMultiTextureRendering();
	}
}
