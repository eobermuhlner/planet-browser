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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.control.Player;
import ch.obermuhlner.libgdx.planetbrowser.control.PlayerController;
import ch.obermuhlner.libgdx.planetbrowser.control.Ship;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder.VertexInfo;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetData;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui.TableLayout;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.StopWatch;

public class FlyPlanetScreen extends AbstractScreen {

	private static final boolean SHOW_DEBUG_INFO = true;

	private final ModelInstanceFactory factory;
	private final long randomSeed;

	private final Environment environment = new Environment();
	private Stage stage;
	private ModelBatch modelBatch;
	private PerspectiveCamera camera;
	
	private Label fpsLabel;
	private Label deltaMillisLabel;
	private Label createTimeLabel;

	private PlanetData planetData;
	private Color atmosphereColor;

	private Terrain terrain;

	private Player player;
	private PlayerController playerController;
	
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
		Color fogColor = new Color(0x60a0d0ff);
		
		environment.set(new ColorAttribute(ColorAttribute.Fog, fogColor));
	
		player = new Player(new Ship(), camera);
		playerController = new PlayerController(player);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, playerController));

		// create planet surface

		planetData = factory.createPlanetData(new Random(randomSeed));

		prepareStage();

		TerrainLod[] lod = new TerrainLod[5];
		lod[0] = new TerrainLod(1, 512, 128);
		lod[1] = new TerrainLod(2, 256, 128);
		lod[2] = new TerrainLod(3, 128, 64);
		lod[3] = new TerrainLod(5, 32, 16);
		lod[4] = new TerrainLod(Integer.MAX_VALUE, 8, 8);

//		TerrainLod[] lod = new TerrainLod[2];
//		lod[0] = new TerrainLod(1, 512, 128);
//		lod[1] = new TerrainLod(4, 64, 64);

		terrain = new Terrain(11, lod);
		terrain.planetX = 0.5f;
		terrain.planetY = 0.5f;
		terrain.planetStep = 0.02f;
		terrain.terrainX = 0f;
		terrain.terrainY = 0f;
		terrain.terrainStep = 20f;
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

		{
			// dummy to have the center empty
			rootTable.row().expandY();
			rootTable.add("");
		}

		{
			rootTable.row();
			Table infoPanel = gui.table();
			rootTable.add(infoPanel).align(Align.bottomLeft);
			if (SHOW_DEBUG_INFO) {
				TableLayout tableLayout = gui.tableLayout();
				infoPanel.row();
				infoPanel.add("FPS");
				infoPanel.add(tableLayout.right());
		
				fpsLabel = tableLayout.addNumeric("888");
				tableLayout.add(" (");
				deltaMillisLabel = tableLayout.addNumeric("8888");
				tableLayout.add(" ms)");
			}
			
			if (SHOW_DEBUG_INFO) {
				infoPanel.row();
				infoPanel.add("Planet Creation");
				createTimeLabel = gui.label("");
				infoPanel.add(createTimeLabel);
			}
		}
	}
	
	private ModelInstance createTerrainMesh(Texture bumpTexture, float rectSize, Material material, float uFrom, float uTo, float vFrom, float vTo) {
		final VertexInfo vertTmp1 = new VertexInfo();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder part = modelBuilder.part("terrain", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates, material);

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
		playerController.update(delta);
		player.update(delta);
		
		modelBatch.begin(camera);
		terrain.center(camera.position.x, camera.position.z);
		terrain.render(modelBatch, environment);
		modelBatch.end();
		
		fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		deltaMillisLabel.setText(String.valueOf((int) (Gdx.graphics.getDeltaTime() * 1000)));

		stage.draw();
	}

	private class Terrain {
		private final int chunkCount;
		private TerrainLod lod[];
		
		private float planetX;
		private float planetY;
		private float planetStep;
		
		private float terrainX;
		private float terrainY;
		private float terrainStep;

		
		private TerrainChunk[] terrain;
		private TerrainChunk[] terrainCopy;
		private int[] terrainLodIndex;

		public Terrain(int chunkCount, TerrainLod lod[]) {
			this.chunkCount = chunkCount;
			this.lod = lod;

			terrain = new TerrainChunk[chunkCount * chunkCount];
			terrainCopy = new TerrainChunk[chunkCount * chunkCount];
			terrainLodIndex = new int[chunkCount * chunkCount];

			for (int i = 0; i < terrain.length; i++) {
				terrain[i] = new TerrainChunk(lod.length);
			}

			int centerChunk = chunkCount / 2;
			for (int i = 0; i < terrainLodIndex.length; i++) {
				int chunkX = i % chunkCount;
				int chunkY = i / chunkCount;
				int distSquare = (centerChunk - chunkX) * (centerChunk - chunkX) + (centerChunk - chunkY) * (centerChunk - chunkY);
				terrainLodIndex[i] = lod.length - 1;
				for (int lodIndex = 0; lodIndex < lod.length; lodIndex++) {
					if (distSquare <= lod[lodIndex].chunkDistanceSquare) {
						terrainLodIndex[i] = lodIndex;
						break;
					}
				}
			}
			terrainLodIndex[chunkCount / 2 + (chunkCount / 2 * chunkCount)] = 0;
		}
		
		public void center(float cameraTerrainX, float cameraTerrainY) {
			int x = (int) ((cameraTerrainX - terrainX) / terrainStep + 0.5f);
			int y = (int) ((cameraTerrainY - terrainY) / terrainStep + 0.5f);
			System.out.println("CENTER camera=" + cameraTerrainX + "," + cameraTerrainY + " terrain=" + terrainX + "," + terrainY + " chunk=" + x + "," + y);
			if (x != 0 || y != 0) {
				//System.out.println("MOVE " + x + " " + y);
				moveChunks(x, y);
			}
		}

		private void moveChunks(int moveX, int moveY) {
			for (int i = 0; i < terrain.length; i++) {
				int chunkX = i % chunkCount;
				int chunkY = i / chunkCount;
				
				int moveChunkX = chunkX + moveX;
				int moveChunkY = chunkY + moveY;
				if (moveChunkX >= 0 && moveChunkX < chunkCount && moveChunkY >= 0 && moveChunkY < chunkCount) {
					int moveIndex = moveChunkX + moveChunkY * chunkCount;
					terrainCopy[i] = terrain[moveIndex];
				} else {
					terrainCopy[i] = new TerrainChunk(lod.length);
				}
			}
			TerrainChunk[] tmp = terrain; 
			terrain = terrainCopy;
			terrainCopy = tmp;

			terrainX += moveX * terrainStep;
			terrainY += moveY * terrainStep;
			planetX -= moveX * planetStep;
			planetY += moveY * planetStep;
		}

		public void render(ModelBatch modelBatch, Environment environment) {
			StringBuilder createTimeText = null;
			boolean hasCreated = false;
			
			for (int i = 0; i < terrain.length; i++) {
				int chunkX = i % chunkCount;
				int chunkY = i / chunkCount;
				int lodIndex = terrainLodIndex[i];
						
				if (terrain[i].surface[lodIndex] == null && !hasCreated) {
					hasCreated = true;
					if (createTimeText == null) {
						createTimeText = new StringBuilder();
					} else {
						createTimeText.append(" ");
					}
					StopWatch stopWatch = new StopWatch();
					terrain[i].surface[lodIndex] = createTerrainSurface(chunkX, chunkY, lod[lodIndex]);
					createTimeText.append((int) stopWatch.getElapsedMilliseconds());
				}
				
				while (lodIndex < lod.length-1 && terrain[i].surface[lodIndex] == null) {
					lodIndex++;
				}
				
				if (terrain[i].surface[lodIndex] != null) {
					int centerChunk = chunkCount / 2;
					float chunkTerrainX = terrainX + (chunkX - centerChunk) * terrainStep;
					float chunkTerrainY = terrainY + (chunkY - centerChunk) * terrainStep;
					terrain[i].surface[lodIndex].transform.setTranslation(chunkTerrainX, 0, chunkTerrainY);
					
					modelBatch.render(terrain[i].surface[lodIndex], environment);
				}
			}
			if (createTimeText != null) {
				createTimeText.append(" ms");
				createTimeLabel.setText(createTimeText.toString());
			}
		}

		private ModelInstance createTerrainSurface(int chunkX, int chunkY, TerrainLod lod) {
			float fromX = planetX - planetStep * chunkX;
			float fromY = planetY + planetStep * chunkY;
			return createTerrainSurface(
					fromX, fromX + planetStep, fromY, fromY + planetStep, 
					lod.textureSize, 
					lod.meshDivisions, 
					terrainStep);
		}
		
		private ModelInstance createTerrainSurface(float xFrom, float xTo, float yFrom, float yTo, int textureSize, int meshDivisions, float terrainSize) {
			long textureTypes = TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
			Map<Long, Texture> textures = factory.createTextures(planetData, new Random(randomSeed), xFrom, xTo, yFrom, yTo, textureTypes, textureSize);
			Array<Attribute> materialAttributes = new Array<Attribute>();
			materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
			materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));

			Texture bumpTexture = factory.createTextures(planetData, new Random(randomSeed), xFrom, xTo, yFrom, yTo, TextureAttribute.Bump, meshDivisions).get(TextureAttribute.Bump);
			materialAttributes.add(new TextureAttribute(TextureAttribute.Bump, bumpTexture));
			Material material = new Material(materialAttributes);
			
			ModelInstance modelInstance = createTerrainMesh(bumpTexture, terrainSize / 2, material, 0, 1, 0, 1);
			return modelInstance;
		}
	}
	
	private static class TerrainLod {
		int chunkDistanceSquare;
		int textureSize;
		int meshDivisions;

		public TerrainLod(int chunkDistance, int textureSize, int meshDivisions) {
			this.chunkDistanceSquare = chunkDistance * chunkDistance;
			this.textureSize = textureSize;
			this.meshDivisions = meshDivisions;
		}
	}
	
	private static class TerrainChunk {
		ModelInstance surface[];
		
		public TerrainChunk(int lodLevels) {
			surface = new ModelInstance[lodLevels];
		}
	}
}
