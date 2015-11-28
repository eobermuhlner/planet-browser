package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Earth;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.IceMoon;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Jupiter;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Lava;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Moon;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Neptune;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class PlanetScreen extends AbstractScreen {

	private static final ModelInstanceFactory[] ALL_PLANET_FACTORIES = new ModelInstanceFactory[] {
		new Earth(),
		new Moon(),
		new Lava(),
		new IceMoon(),
		new Jupiter(),
		new Neptune(),
		//new TexturePlanet("earth.jpg", "earth_normals.jpg"),
	};
	private static final Array<String> planetFactoryNames = new Array<String>();
	private static final String RANDOM_FACTORY_NAME = "Random";
	private static String currentPlanetFactoryName = RANDOM_FACTORY_NAME;
	private static final Map<String, ModelInstanceFactory[]> mapPlanetFactories = new HashMap<String, ModelInstanceFactory[]>();
	static {
		planetFactoryNames.add(RANDOM_FACTORY_NAME);
		mapPlanetFactories.put(RANDOM_FACTORY_NAME, ALL_PLANET_FACTORIES);
		
		for (int i = 0; i < ALL_PLANET_FACTORIES.length; i++) {
			String name = ALL_PLANET_FACTORIES[i].getClass().getSimpleName();
			ModelInstanceFactory[] factory = new ModelInstanceFactory[] { ALL_PLANET_FACTORIES[i] };
			planetFactoryNames.add(name);
			mapPlanetFactories.put(name, factory);
		}
	}

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
	
	private Label fpsLabel;
	
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
		ModelInstanceFactory modelInstanceFactory = random.next(mapPlanetFactories.get(currentPlanetFactoryName));
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
		
		final SelectBox<String> selectBox = gui.select(planetFactoryNames);
		table.add(selectBox);
		selectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String selected = selectBox.getSelected();
				if (! currentPlanetFactoryName.equals(selected)) {
					currentPlanetFactoryName = selected;
					planetBrowser.setScreen(new PlanetScreen(planetBrowser, randomSeed));
				}
			}
		});
		selectBox.setSelected(currentPlanetFactoryName);
		
		fpsLabel = gui.label("");
		table.add(fpsLabel).align(Align.right);
		
		//table.add(gui.label("FPS"));
		
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
		
		fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		
		stage.draw();
	}
}
