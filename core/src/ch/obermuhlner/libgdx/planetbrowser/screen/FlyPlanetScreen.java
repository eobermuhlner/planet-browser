package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder.VertexInfo;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetData;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class FlyPlanetScreen extends AbstractScreen {

	private final ModelInstanceFactory factory;
	private final long randomSeed;

	public final Environment environment = new Environment();
	private Stage stage;
	private CameraInputController cameraInputController;
	private ModelBatch modelBatch;
	private PerspectiveCamera camera;
	private PlanetData planetData;
	private Color atmosphereColor;

	private TerrainChunk[] terrain = new TerrainChunk[9];
	
	public FlyPlanetScreen(ModelInstanceFactory factory, long randomSeed) {
		this.factory = factory;
		this.randomSeed = randomSeed;
	}
	
	@Override
	public void show() {
		// setup rendering
		stage = new Stage();
		
		modelBatch = new ModelBatch(new PlanetUberShaderProvider());
		
		camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 400f;
		camera.position.set(3, 3, 3);
		camera.lookAt(0, 0, 0);
		camera.update(true);
		
//		float ambientLight = 0.1f;
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1f));

		DirectionalLight light = new DirectionalLight();
		light.set(Color.WHITE, 0, -10f, -10);
		environment.add(light);

//		PointLight light = new PointLight();
//		light.set(Color.WHITE, -30f, 10f, 30f, 1.0f);
//		environment.add(light);
		
		atmosphereColor = new Color(0x87cefaff);
		
		environment.set(new ColorAttribute(ColorAttribute.Fog, atmosphereColor));
		
		cameraInputController = new CameraInputController(camera);
		cameraInputController.forwardTarget = false;
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraInputController));

		// create planet surface

		planetData = factory.createPlanetData(new Random(randomSeed));

		prepareStage();

		//int textureSize = PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize();
		int textureSize = 64;
		float terrainSize = 5.0f;
		int meshDivisions = 8;
		float stepX = 0.1f;
		float stepY = 0.1f;
		for (int dy = 0; dy < 3; dy++) {
			for (int dx = 0; dx < 3; dx++) {
				boolean best = dx == 0 && dy == 0;
				float fromX = 0.5f - stepX * dx;
				float fromY = 0.5f + stepY * dy;
				float terrainX = dx * terrainSize * 2;
				float terrainY = dy * terrainSize * 2;
				terrain[dy * 3 + dx] = createTerrainChunk(
						fromX, fromX + stepX, fromY, fromY + stepY, 
						best ? 1024 : textureSize, 
						best ? 32 : meshDivisions, 
						terrainX, terrainY, terrainSize);
			}
		}
	}
	
	private TerrainChunk createTerrainChunk(float xFrom, float xTo, float yFrom, float yTo, int textureSize, int meshDivisions, float terrainX, float terrainY, float terrainSize) {
		long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
		Map<Long, Texture> textures = factory.createTextures(planetData, new Random(randomSeed), xFrom, xTo, yFrom, yTo, textureTypes, textureSize);

		Array<Attribute> materialAttributes = new Array<Attribute>();
		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));

		Texture bumpTexture = factory.createTextures(planetData, new Random(randomSeed), xFrom, xTo, yFrom, yTo, TextureAttribute.Bump, meshDivisions).get(TextureAttribute.Bump);
		materialAttributes.add(new TextureAttribute(TextureAttribute.Bump, bumpTexture));
		Material material = new Material(materialAttributes);
		
		ModelInstance modelInstance = createTerrainMesh(bumpTexture, terrainSize, material, 0, 1, 0, 1);
		TerrainChunk chunk = new TerrainChunk();
		chunk.surface = modelInstance;
		chunk.surface.transform.setToTranslation(terrainX, 0, terrainY);
		return chunk;
	}

	private void prepareStage() {
		Gui gui = new Gui();
		Table rootTable = gui.rootTable();
		stage.addActor(rootTable);

		{
			// catalog control panel
			rootTable.row().expandX();
			Table buttonPanel = gui.table();
			buttonPanel.center();
			rootTable.add(buttonPanel);
			buttonPanel.row();
			
			buttonPanel.add(gui.button("Orbit", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlanetBrowser.INSTANCE.setScreen(new PlanetScreen(randomSeed));
				}
			}));
		}
	}
	private ModelInstance createTerrainMesh(Texture bumpTexture, float rectSize, Material material, float uFrom, float uTo, float vFrom, float vTo) {
		final VertexInfo vertTmp1 = new VertexInfo();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder part = modelBuilder.part("terrain", GL20.GL_TRIANGLES, (long) (Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates), material);

		int divisionsU = bumpTexture.getWidth();
		int divisionsV = bumpTexture.getHeight();

		float normalX = 0;
		float normalY = 1;
		float normalZ = 0;

		float vStep = (vTo - vFrom) / divisionsV;
		float uStep = (uTo - uFrom) / divisionsU;
		
		float xStep = 2 * rectSize / divisionsU;
		float zStep = 2 * rectSize / divisionsV;

		for (int indexV = 0; indexV <= divisionsV; indexV++) {
			float v = vFrom + vStep * indexV;
			float z = -rectSize + zStep * indexV;
			for (int indexU = 0; indexU <= divisionsU; indexU++) {
				float u = uFrom + uStep * indexU;
				float x = -rectSize + xStep * indexU;
				float y = 0.1f;
				vertTmp1.set(null).setPos(x, y, z).setNor(normalX, normalY, normalZ).setUV(u, v);
				part.vertex(vertTmp1);
			}
		}

		int index = 0;
		int indexStepV = divisionsU + 1;
		for (int indexV = 0; indexV < divisionsV; indexV++) {
			for (int indexU = 0; indexU < divisionsU; indexU++) {
				part.index((short) (index));
				part.index((short) (index + indexStepV));
				part.index((short) (index + indexStepV + 1));

				part.index((short) (index + indexStepV + 1));
				part.index((short) (index + 1));
				part.index((short) (index));
				index++;
			}
			index++;
		}
		Model model = modelBuilder.end();
		return new ModelInstance(model);
	}

	@Override
	public void hide() {
		super.hide();
		stage.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(atmosphereColor.r, atmosphereColor.g, atmosphereColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.act(delta);
		cameraInputController.update();

		modelBatch.begin(camera);
		for (int i = 0; i < terrain.length; i++) {
			modelBatch.render(terrain[i].surface, environment);
		}
		modelBatch.end();
		stage.draw();
	}
	
	private static class TerrainChunk {
		float xFrom;
		float xTo;
		float yFrom;
		float yTo;
		
		ModelInstance surface;
	}
}
