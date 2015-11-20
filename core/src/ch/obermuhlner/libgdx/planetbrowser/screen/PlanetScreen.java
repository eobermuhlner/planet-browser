package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.render.UberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Earth;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.IceMoon;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Jupiter;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Lava;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Moon;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Neptune;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.TexturePlanet;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class PlanetScreen extends AbstractScreen {

	private final ModelInstanceFactory[] planetFactories = new ModelInstanceFactory[] {
		//new Jupiter(),
		//new Neptune(),
		new Earth(),
		//new Moon(),
		//new IceMoon(),
		//new Lava(),
		//new TexturePlanet("earth.jpg", "earth_normals.jpg"),
	};

	private final long randomSeed;
	
	public final Environment environment = new Environment();
	private ModelBatch modelBatch;
	private Camera camera;
	
	private Vector3 target = new Vector3();
	protected float autoRotateAngle = 5.0f;

	private Stage stage;
	private CameraInputController cameraInputController;

	private final Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	private PlanetBrowser planetBrowser;
	
	public PlanetScreen(PlanetBrowser planetBrowser) {
		this(planetBrowser, 1);
	}

	public PlanetScreen(PlanetBrowser planetBrowser, long randomSeed) {
		this.planetBrowser = planetBrowser;
		this.randomSeed = randomSeed;
	}

	@Override
	public void show() {
		stage = new Stage();
		
		prepareStage();
		
		modelBatch = new ModelBatch(new PlanetUberShaderProvider());
		
		camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.001f;
		camera.far = 400f;
		camera.position.set(2, 1, 2);
		camera.lookAt(0, 0, 0);
		camera.update(true);
		
//		float ambientLight = 0.1f;
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1f));

		PointLight light = new PointLight();
		light.set(Color.WHITE, 0f, 0f, 50f, 1.0f);
		environment.add(light);
		
		cameraInputController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraInputController));

		Random random = new Random(randomSeed);
		ModelInstanceFactory modelInstanceFactory = random.next(planetFactories);
		modelInstances.addAll(modelInstanceFactory.createModelInstance(random));
	}
	
	private void prepareStage() {
		Gui gui = new Gui();
		Table rootTable = gui.rootTable();
		
		rootTable.row();
		Table table = gui.table();
		rootTable.add(table);
		table.row();
		
		table.add(gui.button("Previous", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new PlanetScreen(planetBrowser, randomSeed - 1));
			}
		}));
		table.add(gui.label(String.valueOf(randomSeed)));
		table.add(gui.button("Next", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new PlanetScreen(planetBrowser, randomSeed + 1));
			}
		}));
		table.add(gui.button("Random", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new PlanetScreen(planetBrowser, new Random(randomSeed).nextLong()));
			}
		}));
		
		stage.addActor(rootTable);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.rotateAround(target, Vector3.Y, delta * autoRotateAngle);
		camera.update();

		cameraInputController.update();
		stage.act(delta);
		
		modelBatch.begin(camera);
		
		modelBatch.render(modelInstances, environment);
		
		modelBatch.end();
		
		stage.draw();
	}
}
