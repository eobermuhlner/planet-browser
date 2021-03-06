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
import ch.obermuhlner.libgdx.planetbrowser.render.CreateTextureAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.MoreFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.RingAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.UberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.util.ColorUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public abstract class AbstractPlanet implements PlanetFactory {

	protected ModelBuilder modelBuilder = new ModelBuilder();

	@Override
	public Array<Attribute> createMaterialAttributes(Random random, PlanetData planetData, DisposableContainer disposables, float xFrom, float xTo, float yFrom, float yTo, int textureSize) {
		Array<Attribute> materialAttributes = new Array<Attribute>();
		
		long textureTypes = getTextureTypes(planetData);
		Map<Long, Texture> textures = createTextures(random, planetData, xFrom, xTo, yFrom, yTo, textureTypes, textureSize, disposables);

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

		// create planet sphere
		{
			Model model = createSphere(size, material);
			modelInstances.add(new ModelInstance(model));
		}
		
		// create atmosphere
		{
			float atmosphereSize = getAtmosphereSize(random, planetData);
			Material atmosphereMaterial = createAtmosphereMaterial(random, planetData, atmosphereSize);
			if (atmosphereMaterial != null) {
				Model model = createSphere(size * atmosphereSize, atmosphereMaterial);
				modelInstances.add(new ModelInstance(model));
			}
		}
		
		// create ring
		{
			Model model = createRing(random, planetData);
			if (model != null) {
				modelInstances.add(new ModelInstance(model));
			}
		}
		
		return modelInstances;
	}
	
	private Model createRing(Random random, PlanetData planetData) {
		double probability = MathUtil.transform(0.5 * Units.EARTH_MASS, 2.0 * Units.JUPITER_MASS, 0.0, 0.8, planetData.mass);
		if (!random.nextBoolean(probability)) {
			return null;
		}

		float planetRadius = Units.toRenderUnit(planetData.radius); 
		float ringRadius = planetRadius * random.nextFloat(2.0f, 4.0f);
		ringRadius = planetRadius * 4.0f;
		float alpha = random.nextFloat(0.4f, 0.9f);
		Color color = ColorUtil.randomColor(random, Color.WHITE, 0.1f, 0.5f);

		RingAttribute ringAttribute = RingAttribute.createRing(random);
		ringAttribute.color = color;
		ringAttribute.opacity = alpha;
		ringAttribute.innerRadius = random.nextFloat(0.1f, 0.2f);
		ringAttribute.outerRadius = random.nextFloat(0.4f, 0.5f);
		
		Material material = new Material(ringAttribute);
		
		Model model;
		modelBuilder.begin();
		modelBuilder.part("ring", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, material)
		.rect(
			ringRadius, 0f, -ringRadius,
			-ringRadius, 0f, -ringRadius,
			-ringRadius, 0f, ringRadius,	
			ringRadius, 0f, ringRadius,
			0f, 1f, 0f);
		model = modelBuilder.end();
		
		return model;
	}

	private Model createSphere(float size, Material material) {
		long attributes = Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates;
		int divisions = PlanetBrowser.INSTANCE.options.getSphereDivisions();
		Model model = modelBuilder.createSphere(size, size, size, divisions, divisions, material, attributes);
		return model;
	}

	protected Map<Long, Texture> createTextures(DisposableContainer disposables, Material material, ShaderProvider shaderProvider, long textureTypes, int textureSize, float xFrom, float xTo, float yFrom, float yTo) {
		boolean bump = (textureTypes & TextureAttribute.Bump) != 0; 
		boolean diffuse = (textureTypes & TextureAttribute.Diffuse) != 0; 
		boolean normal = (textureTypes & TextureAttribute.Normal) != 0; 
		boolean specular = (textureTypes & TextureAttribute.Specular) != 0; 
		boolean emissive = (textureTypes & TextureAttribute.Emissive) != 0; 
		
		Map<Long, Texture> texturesMap = new HashMap<Long, Texture>();
		Array<Texture> textures = renderTextures(
				disposables, material, shaderProvider,
				textureSize, xFrom, xTo, yFrom,
				yTo, bump, diffuse, normal, specular, emissive);

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

	public int[] createPlanetColorFrequencies(Random random) {
		int intArray[] = new int[4];
		for (int i = 0; i < intArray.length; i++) {
			@SuppressWarnings("unchecked")
			int powerOfTwo = random.nextProbability(
					p(5, 2),
					p(10, 4),
					p(10, 8),
					p(4, 16),
					p(2, 32));
			intArray[i] = powerOfTwo;
		}
		
		return intArray;
	}
	
	public Array<Texture> renderTextures (DisposableContainer disposables, Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo, boolean bump, boolean diffuse, boolean normal, boolean specular, boolean emissive) {
		if (useMultiTextureRendering()) {
			material.set(CreateTextureAttribute.createTextures(bump, diffuse, normal, specular, emissive));
			if (normal) {
				material.set(MoreFloatAttribute.createNormalStep(1f / textureSize * Math.max(Math.abs(xTo-xFrom), Math.abs(yTo-yFrom))));
			}
			
			int textureCount = 0;
			textureCount += bump ? 1 : 0;
			textureCount += diffuse ? 1 : 0;
			textureCount += normal ? 1 : 0;
			textureCount += specular ? 1 : 0;
			textureCount += emissive ? 1 : 0;
			return renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, textureCount);
		} else {
			Array<Texture> textures = new Array<Texture>();
			if (bump) {
				material.set(CreateTextureAttribute.createCreateBump());
				textures.add(renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0));
			}
			if (diffuse) {
				material.set(CreateTextureAttribute.createCreateDiffuse());
				textures.add(renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0));
			}
			if (normal) {
				material.set(CreateTextureAttribute.createCreateNormal());
				material.set(MoreFloatAttribute.createNormalStep(1f / textureSize * Math.max(Math.abs(xTo-xFrom), Math.abs(yTo-yFrom))));
				textures.add(renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0));
			}
			if (specular) {
				material.set(CreateTextureAttribute.createCreateSpecular());
				textures.add(renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0));
			}
			if (emissive) {
				material.set(CreateTextureAttribute.createCreateEmissive());
				textures.add(renderTextures(disposables, material, shaderProvider, textureSize, xFrom, xTo, yFrom, yTo, 1).get(0));
			}
			return textures;
		}
	}

	private final VertexInfo vertTmp1 = new VertexInfo();
	private final VertexInfo vertTmp2 = new VertexInfo();
	private final VertexInfo vertTmp3 = new VertexInfo();
	private final VertexInfo vertTmp4 = new VertexInfo();
	private Array<Texture> renderTextures (DisposableContainer disposables, Material material, ShaderProvider shaderProvider, int textureSize, float xFrom, float xTo, float yFrom, float yTo, int textureCount) {
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
		
		if (multiTextureFrameBuffer != null) {
			disposables.add(multiTextureFrameBuffer);
		}
		if (frameBuffer != null) {
			disposables.add(frameBuffer);
		}
		
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

	private boolean useMultiTextureRendering() {
		return Gdx.graphics.isGL30Available() && PlanetBrowser.INSTANCE.options.getUseMultiTextureRendering();
	}
}
