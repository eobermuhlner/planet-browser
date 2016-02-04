package ch.obermuhlner.libgdx.planetbrowser.screen;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import ch.obermuhlner.libgdx.planetbrowser.Options.TerrainQuality;
import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.control.Player;
import ch.obermuhlner.libgdx.planetbrowser.control.PlayerController;
import ch.obermuhlner.libgdx.planetbrowser.control.Ship;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder;
import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder.VertexInfo;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.MoreFloatAttribute;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetData;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetFactory;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui.TableLayout;
import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.StopWatch;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class FlyPlanetScreen extends AbstractScreen {

	private static final boolean SHOW_DEBUG_INFO = true;

	private final PlanetFactory factory;
	private final long randomSeed;

	private final Environment environment = new Environment();
	private Stage stage;
	private ModelBatch modelBatch;
	private PerspectiveCamera camera;
	
	private Label positionLabel;

	private Label fpsLabel;
	private Label deltaMillisLabel;
	private Label createTimeLabel;

	private PlanetData planetData;
	private Color atmosphereColor;

	private Terrain terrain;

	private Player player;
	private PlayerController playerController;
	
	public FlyPlanetScreen(PlanetFactory factory, long randomSeed) {
		this.factory = factory;
		this.randomSeed = randomSeed;
	}
	
	@Override
	public void show() {
		planetData = factory.createPlanetData(new Random(randomSeed));
		Random random = new Random(randomSeed);
		
		// setup rendering
		stage = new Stage();
		
		modelBatch = new ModelBatch(new PlanetUberShaderProvider());
		
		camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 10f;
		camera.far = 10000f;
		camera.position.set(0, 800, 0);
		camera.lookAt(1000, 200, 1000);
		camera.update(true);
		
//		float ambientLight = 0.1f;
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1f));

		DirectionalLight light = new DirectionalLight();
		light.set(Color.WHITE, random.nextFloat(-20f, 20f), random.nextFloat(-7f, -10f), random.nextFloat(-20f, 20f));
		environment.add(light);

//		PointLight light = new PointLight();
//		light.set(Color.WHITE, -30f, 10f, 30f, 1.0f);
//		environment.add(light);
				
		atmosphereColor = planetData.atmosphereScatterColor != null ? planetData.atmosphereScatterColor : Color.BLACK;
		if (planetData.atmosphereFogColor != null && planetData.fogLevel != 0.0) {
			environment.set(MoreFloatAttribute.createFogLevel((float) planetData.fogLevel));
			environment.set(new ColorAttribute(ColorAttribute.Fog, planetData.atmosphereFogColor));
		}
	
		player = new Player(new Ship(), camera);
		playerController = new PlayerController(player);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, playerController));

		// create planet surface

		prepareStage();

		TerrainQuality terrainQuality = PlanetBrowser.INSTANCE.options.getTerrainQuality();
		TerrainLod[] lod = toLod(terrainQuality);
		int chunkCount = PlanetBrowser.INSTANCE.options.getTerrainChunks();
		terrain = new Terrain(chunkCount, lod);
		terrain.planetX = 0.5f;
		terrain.planetY = 0.5f;
		terrain.planetStep = 0.01f;
		terrain.terrainX = 0f;
		terrain.terrainY = 0f;
		terrain.terrainStep = 400f;
		terrain.bumpFactor = 400f;
		terrain.surfaceBounds.set(terrain.terrainStep, terrain.terrainStep, terrain.terrainStep);
	}
	
	private TerrainLod[] toLod(TerrainQuality terrainQuality) {
		TerrainLod[] lod;
		
		switch(terrainQuality) {
		case Best:
			lod = new TerrainLod[3];
			lod[0] = new TerrainLod(5, 512, 64);
			lod[1] = new TerrainLod(7, 256, 64);
			lod[2] = new TerrainLod(Integer.MAX_VALUE, 32, 16);
			return lod;
		case VeryGood:
			lod = new TerrainLod[3];
			lod[0] = new TerrainLod(3, 512, 64);
			lod[1] = new TerrainLod(5, 256, 64);
			lod[2] = new TerrainLod(Integer.MAX_VALUE, 32, 16);
			return lod;
		case Good:
			lod = new TerrainLod[3];
			lod[0] = new TerrainLod(5, 256, 64);
			lod[1] = new TerrainLod(7, 128, 64);
			lod[2] = new TerrainLod(Integer.MAX_VALUE, 32, 16);
			return lod;
		case Poor:
			lod = new TerrainLod[3];
			lod[0] = new TerrainLod(5, 128, 64);
			lod[1] = new TerrainLod(7, 64, 64);
			lod[2] = new TerrainLod(Integer.MAX_VALUE, 16, 16);
			return lod;
		case VeryPoor:
			lod = new TerrainLod[2];
			lod[0] = new TerrainLod(5, 128, 64);
			lod[1] = new TerrainLod(Integer.MAX_VALUE, 16, 16);
			return lod;
		case Worst:
			lod = new TerrainLod[2];
			lod[0] = new TerrainLod(3, 128, 64);
			lod[1] = new TerrainLod(Integer.MAX_VALUE, 16, 16);
			return lod;
		case SimpleBest:
			lod = new TerrainLod[1];
			lod[0] = new TerrainLod(Integer.MAX_VALUE, 1024, 64);
			return lod;
		case SimpleGood:
			lod = new TerrainLod[1];
			lod[0] = new TerrainLod(Integer.MAX_VALUE, 512, 64);
			return lod;
		case SimplePoor:
			lod = new TerrainLod[1];
			lod[0] = new TerrainLod(Integer.MAX_VALUE, 256, 64);
			return lod;
		case SimpleWorst:
			lod = new TerrainLod[1];
			lod[0] = new TerrainLod(Integer.MAX_VALUE, 256, 32);
			return lod;
		default:
			break;
		}

		throw new RuntimeException("Unknown: " + terrainQuality);
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
			rootTable.row();
			Table infoPanel = gui.table();
			rootTable.add(infoPanel);
			infoPanel.add("Position");
			positionLabel = gui.label("");
			infoPanel.add(positionLabel);
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
	
	private ModelInstance createTerrainMesh(int meshDivisions, float rectSize, Material material, float uFrom, float uTo, float vFrom, float vTo) {
		final VertexInfo vertTmp1 = new VertexInfo();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder part = modelBuilder.part("terrain", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates, material);

		int divisionsU = meshDivisions;
		int divisionsV = meshDivisions;

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
		modelBatch.dispose();
		terrain.dispose();
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
		
		positionLabel.setText(Units.toString(camera.position.x) + "," + Units.toString(camera.position.y) + "," + Units.toString(camera.position.z));
		
		fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		deltaMillisLabel.setText(String.valueOf((int) (Gdx.graphics.getDeltaTime() * 1000)));

		stage.draw();
	}

	private class Terrain implements Disposable {
		private final int chunkCount;
		private TerrainLod lod[];
				
		private float planetX;
		private float planetY;
		private float planetStep;
		
		private float terrainX;
		private float terrainY;
		private float terrainStep;

		public float bumpFactor;
		
		private TerrainChunk[] terrain;
		private TerrainChunk[] terrainCopy;
		private int[] terrainLodIndex;

		private final Vector3 positionForIsVisible = new Vector3();
		private final Vector3 surfaceBounds = new Vector3();

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
			//System.out.println("CENTER camera=" + cameraTerrainX + "," + cameraTerrainY + " terrain=" + terrainX + "," + terrainY + " chunk=" + x + "," + y);
			if (x != 0 || y != 0) {
				//System.out.println("MOVE " + x + " " + y);
				moveChunks(x, y);
			}
		}

		private void moveChunks(int moveX, int moveY) {
			for (int i = 0; i < terrain.length; i++) {
				int chunkX = i % chunkCount;
				int chunkY = i / chunkCount;

				int targetChunkX = chunkX - moveX;
				int targetChunkY = chunkY - moveY;
				if (targetChunkX >= 0 && targetChunkX < chunkCount && targetChunkY >= 0 && targetChunkY < chunkCount) {
					// do nothing (actual copying is done below with source chunk)
				} else {
					if (terrainCopy[i] != null) {
						terrainCopy[i].dispose();
					}
				}
				
				int sourceChunkX = chunkX + moveX;
				int sourceChunkY = chunkY + moveY;
				if (sourceChunkX >= 0 && sourceChunkX < chunkCount && sourceChunkY >= 0 && sourceChunkY < chunkCount) {
					int sourceIndex = sourceChunkX + sourceChunkY * chunkCount;
					terrainCopy[i] = terrain[sourceIndex];
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
					terrain[i].surface[lodIndex] = createTerrainSurface(terrain[i].disposables, chunkX, chunkY, lod[lodIndex]);
					createTimeText.append((int) stopWatch.getElapsedMilliseconds());
				}
				
				ModelInstance surface = terrain[i].bestSurface(lodIndex);
				
				if (surface != null) {
					int centerChunk = chunkCount / 2;
					float chunkTerrainX = terrainX + (chunkX - centerChunk) * terrainStep;
					float chunkTerrainY = terrainY + (chunkY - centerChunk) * terrainStep;
					surface.transform.setTranslation(chunkTerrainX, 0, chunkTerrainY);
				
					if (isVisible(surface)) {
						modelBatch.render(surface, environment);
					}
				}
			}
			if (createTimeText != null) {
				createTimeText.append(" ms");
				createTimeLabel.setText(createTimeText.toString());
			}
		}

		private boolean isVisible(ModelInstance surface) {
			// FPS 39 when full visible, FPS 53 when black
			surface.transform.getTranslation(positionForIsVisible);
			return camera.frustum.boundsInFrustum(positionForIsVisible, surfaceBounds);
		}

		@Override
		public void dispose() {
			for (int i = 0; i < terrain.length; i++) {
				if (terrain[i] != null) {
					terrain[i].dispose();
				}
			}
		}
		private ModelInstance createTerrainSurface(DisposableContainer disposables, int chunkX, int chunkY, TerrainLod lod) {
			float xFrom = planetX - planetStep * chunkX;
			float yFrom = planetY + planetStep * chunkY;
			return createTerrainSurface(
					disposables, xFrom, yFrom, 
					planetStep, 
					lod.textureSize, 
					lod.meshDivisions, terrainStep);
		}
		
		private ModelInstance createTerrainSurface(DisposableContainer disposables, float xFrom, float yFrom, float xyStep, int textureSize, int meshDivisions, float terrainSize) {
			float xToTextures = xFrom + xyStep;
			float yToTextures = yFrom + xyStep;
			
			Array<Attribute> materialAttributes = factory.createMaterialAttributes(new Random(randomSeed), planetData, disposables, xFrom, xToTextures, yFrom, yToTextures, textureSize);

			float xToBump = xFrom + xyStep / meshDivisions * (meshDivisions + 1);
			float yToBump = yFrom + xyStep / meshDivisions * (meshDivisions + 1);

			Texture bumpTexture = factory.createTextures(new Random(randomSeed), planetData, xFrom, xToBump, yFrom, yToBump, TextureAttribute.Bump, meshDivisions, disposables).get(TextureAttribute.Bump);
			if (bumpTexture != null) {
				materialAttributes.add(new TextureAttribute(TextureAttribute.Bump, bumpTexture));
				materialAttributes.add(MoreFloatAttribute.createBumpFactor(bumpFactor));
			}
			
			Material material = new Material(materialAttributes);
			
			ModelInstance modelInstance = createTerrainMesh(meshDivisions, terrainSize / 2, material, 0, 1, 0, 1);
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
	
	private static class TerrainChunk implements Disposable {
		DisposableContainer disposables = new DisposableContainer();
		ModelInstance surface[];
		
		public TerrainChunk(int lodLevels) {
			surface = new ModelInstance[lodLevels];
		}

		public ModelInstance bestSurface(int lodIndex) {
			if (surface[lodIndex] != null) {
				return surface[lodIndex];
			}
			
			for (int i = 0; i < surface.length; i++) {
				if (surface[i] != null) {
					return surface[i];
				}
			}

			return null;
		}

		@Override
		public void dispose() {
			disposables.dispose();
		}
	}
}
