package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.Config;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.AtmosphereAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.FloatArrayAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.TerrestrialPlanetFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.UberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.util.MathUtil;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public abstract class AbstractPlanet implements ModelInstanceFactory {

	private static final int DIVISIONS = 40;
	
	protected ModelBuilder modelBuilder = new ModelBuilder();

	@Override
	public Array<ModelInstance> createModelInstance(Random random) {
		Array<ModelInstance> modelInstances = new Array<ModelInstance>();

		PlanetData planetData = createPlanetData(random);
		
		float size = (float) random.nextGaussian(getPlanetRadius());

		{
			Material material = createPlanetMaterial(random, planetData);
			Model model = createSphere(size, material);
			modelInstances.add(new ModelInstance(model));
		}
		
		{
			float atmosphereSize = getAtmosphereSize(random, planetData);
			Material material = createAtmosphereMaterial(random, planetData, atmosphereSize);
			if (material != null) {
				Model model = createSphere(size * atmosphereSize, material);
				modelInstances.add(new ModelInstance(model));
			}
		}
		
		return modelInstances;
	}

	private Model createSphere(float size, Material material) {
		long attributes = Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates;
		Model model = modelBuilder.createSphere(size, size, size, DIVISIONS, DIVISIONS, material, attributes);
		return model;
	}

	protected abstract PlanetData createPlanetData(Random random);
	
	protected abstract Material createPlanetMaterial(Random random, PlanetData planetData);

	protected AtmosphereAttribute getAtmosphereAttribute(Random random, PlanetData planetData, float atmosphereSize) {
		return null;
	}
	
	protected float getAtmosphereSize(Random random, PlanetData planetData) {
		return random.nextFloat(1.01f, 1.1f);
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

	protected float getPlanetRadius() {
		return 3.0f;
	}

	public Color[] randomColors(Random random, int colorCount, Color[] colors, float deltaColor, float deltaLuminance) {
		Color[] result = new Color[colorCount];
		for (int i = 0; i < result.length; i++) {
			result[i] = randomColor(random, colors, deltaColor, deltaLuminance);
		}
		return result;
	}

	public Color randomColor(Random random, Color[] colors, float deltaColor, float deltaLuminance) {
		return randomDeviation(random, colors[random.nextInt(colors.length)], deltaColor, deltaLuminance);
	}
	
	private Color randomDeviation(Random random, Color color, float deltaColor, float deltaLuminance) {
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
	
	public Texture renderTextureSpecular (Material material, ShaderProvider shaderProvider) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateSpecular()); // FIXME just adding attribute is wrong, modifies the material
		return renderTextureDiffuse(material, shaderProvider);
	}
	
	public Texture renderTextureNormal (Material material, ShaderProvider shaderProvider) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateNormal()); // FIXME just adding attribute is wrong, modifies the material
		return renderTextureDiffuse(material, shaderProvider);
	}
	
	public Texture renderTextureEmissive (Material material, ShaderProvider shaderProvider) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateEmissive()); // FIXME just adding attribute is wrong, modifies the material
		return renderTextureDiffuse(material, shaderProvider);
	}
	
	public Texture renderTextureDiffuse (Material material, ShaderProvider shaderProvider) {
		final int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		
		final int rectSize = 1;
		Model model;
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createRect(
			rectSize, 0f, -rectSize,
			-rectSize, 0f, -rectSize,
			-rectSize, 0f, rectSize,
			rectSize, 0f, rectSize,
			0, 1, 0,
			material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);

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
		
		Texture texture = frameBuffer.getColorBufferTexture();

		model.dispose();
		modelBatch.dispose();
		//frameBuffer.dispose(); // FIXME memory leak
		
		return texture;
	}

	public FrameBuffer renderFrameBufferNormal (Material material, ShaderProvider shaderProvider) {
		material.set(TerrestrialPlanetFloatAttribute.createCreateNormal()); // FIXME just adding attribute is wrong, modifies the material
		return renderFrameBuffer(material, shaderProvider);
	}

	public FrameBuffer renderFrameBuffer (Material material, ShaderProvider shaderProvider) {
		final int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		
		final int rectSize = 1;
		Model model;
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createRect(
			rectSize, 0f, -rectSize,
			-rectSize, 0f, -rectSize,
			-rectSize, 0f, rectSize,
			rectSize, 0f, rectSize,
			0, 1, 0,
			material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);

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
}
