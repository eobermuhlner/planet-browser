package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Earth;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Jupiter;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Lava;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Mars;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Moon;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.Neptune;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetData;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui.TableLayout;
import ch.obermuhlner.libgdx.planetbrowser.ui.SimpleHtml;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;
import ch.obermuhlner.libgdx.planetbrowser.util.Units.PlanetTime;

public class PlanetScreen extends AbstractScreen {
	
	private static final boolean SHOW_DEBUG_INFO = true;

	private static final ModelInstanceFactory[] ALL_PLANET_FACTORIES = new ModelInstanceFactory[] {
		new Earth(),
		new Moon(),
		new Mars(),
		new Lava(),
		new Jupiter(),
		new Neptune(),
		//new IceMoon(),
		//new Sun(),
		//new TexturePlanet("earth.jpg", "earth_normals.jpg", null),
		//new TexturePlanet("earth.jpg", null, null),
		//new TexturePlanet(null, null, "sun.jpg"),
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

	private PlanetData planetData;

	private Label fpsLabel;
	private Label deltaMillisLabel;
	private Label createTimeLabel;

	private Label planetTimeLabel;

	private Label shipTimeLabel;
	private Label shipDistanceLabel;
	private Label shipEscapeVelocityLabel;
	
	private long shipYearStartMillis;
	private long planetYearStartMillis;
	private long planetDayMillis;
	private PlanetTime planetTime = new PlanetTime();

	private Window atmosphereWindow;
	private Window cloudsWindow;

	private Table infoPanel;

	public PlanetScreen() {
		this(1);
	}

	public PlanetScreen(long randomSeed) {
		this.randomSeed = validRange(randomSeed);
	}

	private long validRange(long seed) {
		while (seed < 0) {
			seed += 1000000;
		}
		return seed % 1000000;
	}

	@Override
	public void show() {
		// create planet
		ModelInstanceFactory modelInstanceFactory = new Random(randomSeed).next(mapPlanetFactories.get(currentPlanetFactoryName));
		long startMillis = System.currentTimeMillis();
		planetData = modelInstanceFactory.createPlanetData(new Random(randomSeed));
		modelInstances.addAll(modelInstanceFactory.createModelInstance(planetData, new Random(randomSeed)));
		long endMillis = System.currentTimeMillis();
		long deltaMillis = endMillis - startMillis;
		
		// setup rendering
		stage = new Stage();
		
		modelBatch = new ModelBatch(new PlanetUberShaderProvider());
		
		float planetUnit = Units.toRenderUnit(Math.max(Units.JUPITER_RADIUS, 1.5 * planetData.radius));
		float sunUnit = Units.toRenderUnit(1000 * Units.EARTH_RADIUS);
		
		camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.001f;
		camera.far = 400f;
		camera.position.set(planetUnit, 0.5f * planetUnit, planetUnit);
		camera.lookAt(0, 0, 0);
		camera.update(true);
		
//		float ambientLight = 0.1f;
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1f));

		PointLight light = new PointLight();
		light.set(Color.WHITE, 10f * sunUnit, 0f * sunUnit, -30f * sunUnit, 1.0f);
		environment.add(light);
		
		cameraInputController = new CameraInputController(camera);
		cameraInputController.forwardTarget = false;
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraInputController));

		prepareStage();
		
		if (SHOW_DEBUG_INFO) {
			createTimeLabel.setText(String.valueOf(deltaMillis));
		}
		
		Random random = new Random(randomSeed);
		planetDayMillis = random.nextInt(7, 40) * 3600 * 1000;
		planetYearStartMillis = endMillis - random.nextInt((int) planetDayMillis);
	}
	
	private void prepareStage() {
		Gui gui = new Gui();
		Table rootTable = gui.rootTable();

		{
			// catalog control panel
			rootTable.row().expandX();
			Table buttonPanel = gui.table();
			buttonPanel.center();
			rootTable.add(buttonPanel);
			buttonPanel.row();
			
			buttonPanel.add(gui.button("Previous", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlanetBrowser.INSTANCE.setScreen(new PlanetScreen(randomSeed - 1));
				}
			}));
			
			final TextField seedTextField = gui.textField(String.valueOf(randomSeed));
			seedTextField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
			seedTextField.setWidth(gui.textWidth("888888") * 1.1f);
			buttonPanel.add(seedTextField);
			buttonPanel.add(gui.button("Go", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					try {
						int seed = Integer.parseInt(seedTextField.getText());
						PlanetBrowser.INSTANCE.setScreen(new PlanetScreen(seed));
					} catch (NumberFormatException ex) {
						seedTextField.setText(String.valueOf(randomSeed));
					}
				}
			}));
			seedTextField.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					
				}
			});
			buttonPanel.add(gui.button("Next", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlanetBrowser.INSTANCE.setScreen(new PlanetScreen(randomSeed + 1));
				}
			}));
			
			final SelectBox<String> selectBox = gui.select(planetFactoryNames);
			buttonPanel.add(selectBox);
			selectBox.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					String selected = selectBox.getSelected();
					if (! currentPlanetFactoryName.equals(selected)) {
						currentPlanetFactoryName = selected;
						PlanetBrowser.INSTANCE.setScreen(new PlanetScreen(randomSeed));
					}
				}
			});
			selectBox.setSelected(currentPlanetFactoryName);

			buttonPanel.add(gui.button("Info", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					toggleInfoWindow();
				}
			}));
			buttonPanel.add(gui.button("Land", new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlanetBrowser.INSTANCE.setScreen(new FlyPlanetScreen(null, randomSeed));
				}
			}));
		}

		{
			// planet info
			rootTable.row();
			infoPanel = gui.table();
			infoPanel.setVisible(false);
			infoPanel.defaults().spaceRight(gui.textWidth("m")).left();
			rootTable.add(infoPanel).left();
			
			{
				infoPanel.row();
				infoPanel.add("Ship Time:");
				shipTimeLabel = gui.label("");
				infoPanel.add(shipTimeLabel);
			}
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0);
				shipYearStartMillis = calendar.getTimeInMillis();
				
				{
					infoPanel.row();
					infoPanel.add("Planet Time:");
					planetTimeLabel = gui.label("");
					infoPanel.add(planetTimeLabel);
				}
			}
			{
				infoPanel.row();
				infoPanel.add("Period:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.secondsToString(planetData.period))));

				infoPanel.row();
				infoPanel.add("Radius:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.meterSizeToString(planetData.radius))));

				infoPanel.row();
				infoPanel.add("Mass:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.kilogramsToString(planetData.mass))));

				infoPanel.row();
				infoPanel.add("Density:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.densityToString(planetData.density))));

				infoPanel.row();
				infoPanel.add("Surface Gravity:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.newtonGravityToString(Units.gravity(planetData.mass, planetData.radius)))));

				infoPanel.row();
				infoPanel.add("Surface Escape Velocity:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.metersPerSecondToString(Units.escapeVelocity(planetData.mass, planetData.radius)))));

				infoPanel.row();
				infoPanel.add("Ship Distance:");
				shipDistanceLabel = gui.label("");
				infoPanel.add(shipDistanceLabel);
				
				infoPanel.row();
				infoPanel.add("Ship Escape Velocity:");
				shipEscapeVelocityLabel = gui.label("");
				infoPanel.add(shipEscapeVelocityLabel);
				
				infoPanel.row();
				infoPanel.add("Temperature:");
				infoPanel.add(gui.htmlLabel(SimpleHtml.scientificUnitsToHtml(Units.kelvinToString(planetData.temperature))));
			}
			if (planetData.atmosphere != null) {
				infoPanel.row();
				infoPanel.add("Atmosphere:");
				infoPanel.add(gui.htmlLabel(toMoleculeOverviewString(planetData.atmosphere)));
				infoPanel.add(gui.button("Details", new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						toggleAtmosphereAnalysisWindow();
					}
				}));
			}
			if (planetData.clouds != null) {
				infoPanel.row();
				infoPanel.add("Clouds:");
				infoPanel.add(gui.htmlLabel(toMoleculeOverviewString(planetData.clouds)));
				infoPanel.add(gui.button("Details", new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						toggleCloudsAnalysisWindow();
					}
				}));
			}
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
				TableLayout tableLayout = gui.tableLayout();
				infoPanel.row();
				infoPanel.add("Planet Creation");
				infoPanel.add(tableLayout).right();
				
				createTimeLabel = tableLayout.addNumeric("8888");
				tableLayout.add(" ms");
			}
		}
		
		stage.addActor(rootTable);
	}

	public static class EntryDoubleValueComparator implements Comparator<Entry<?, Double>> {
		@Override
		public int compare(Entry<?, Double> o1, Entry<?, Double> o2) {
			return -Double.compare(o1.getValue(), o2.getValue());
		}
	}
	
	private String toMoleculeOverviewString(Map<Molecule, Double> molecules) {
		StringBuilder string = new StringBuilder();
		
		boolean first = true;
		
		List<Entry<Molecule, Double>> entries = new ArrayList<Entry<Molecule, Double>>(molecules.entrySet());
		Collections.sort(entries, new EntryDoubleValueComparator());
		for(Map.Entry<Molecule, Double> entry : entries) {
			int percent = (int) (entry.getValue() * 100);
			if (percent >= 2) {
				if (!first) {
					string.append(", ");
				}
				string.append(entry.getKey().getHtmlFormula());
				first = false;
			}
		}
		
		return string.toString();
	}

	private void toggleInfoWindow() {
		infoPanel.setVisible(!infoPanel.isVisible());
	}
	
	private void toggleAtmosphereAnalysisWindow() {
		if (atmosphereWindow == null) {
			atmosphereWindow = showMoleculeAnalysisWindow("Atmosphere", planetData.atmosphere, new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					atmosphereWindow.remove();
					atmosphereWindow = null;
				}
			});
		} else {
			atmosphereWindow.remove();
			atmosphereWindow = null;
		}
	}

	private void toggleCloudsAnalysisWindow() {
		if (cloudsWindow == null) {
			cloudsWindow = showMoleculeAnalysisWindow("Clouds", planetData.clouds, new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					cloudsWindow.remove();
					cloudsWindow = null;
				}
			});
		} else {
			cloudsWindow.remove();
			cloudsWindow = null;
		}
	}

	private Window showMoleculeAnalysisWindow(String name, Map<Molecule, Double> molecules, ChangeListener okPressed) {
		Gui gui = new Gui();

		TableLayout layout = gui.tableLayout();
		layout.defaults().spaceRight(gui.textWidth("M"));
		
		List<Entry<Molecule, Double>> entries = new ArrayList<Entry<Molecule, Double>>(molecules.entrySet());
		Collections.sort(entries, new EntryDoubleValueComparator());
		for(Map.Entry<Molecule, Double> entry : entries) {
			if (entry.getValue().compareTo(0.0) > 0) {
				layout.row();
				layout.add(gui.htmlLabel(entry.getKey().getHtmlFormula())).left();
				layout.add(Gui.firstToUppercase(entry.getKey().getHumanName())).left();
				layout.add(Units.percentToString(entry.getValue())).right();
			}
		}
	
		return showWindow(name, layout, okPressed);
	}

	private Window showWindow(String name, Actor actor, ChangeListener okPressed) {
		Gui gui = new Gui();
		final Window window = new Window(name, gui.skin, "transparent");
		
		window.row();
		window.add(actor);
		
		window.row().center();
		TextButton buttonOk = new TextButton("OK", gui.skin);
		buttonOk.addListener(okPressed);
		window.add(buttonOk);
		
		window.pack();
		window.setPosition(Math.round((stage.getWidth() - window.getWidth()) / 2), Math.round((stage.getHeight() - window.getHeight()) / 2));
		stage.addActor(window);
		
		return window;
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

		for (int i = 0; i < modelInstances.size; i++) {
			ModelInstance modelInstance = modelInstances.get(i);
//			modelInstance.transform.rotate(Vector3.Y, delta * -10);
		}
		
		cameraInputController.update();
		stage.act(delta);
		
		modelBatch.begin(camera);
		modelBatch.render(modelInstances, environment);
		modelBatch.end();

		fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		deltaMillisLabel.setText(String.valueOf((int) (Gdx.graphics.getDeltaTime() * 1000)));
		
		long nowMillis = System.currentTimeMillis();

		{
			long planetTimeMillis = nowMillis - planetYearStartMillis;
			
			Units.millisToPlanetTime(planetTime, planetTimeMillis, planetDayMillis);
			planetTimeLabel.setText(String.format("%02d:%02d:%02d.%d  (%5.4f%%)", planetTime.hours, planetTime.minutes, planetTime.seconds, planetTime.milliseconds / 100, planetTime.dayFraction * 100.0));
		}

		{
			long shipTimeMillis = nowMillis - shipYearStartMillis;
			
			Units.millisToPlanetTime(planetTime, shipTimeMillis, 24 * 3600 * 1000);
			shipTimeLabel.setText(String.format("%02d:%02d:%02d.%d", planetTime.hours, planetTime.minutes, planetTime.seconds, planetTime.milliseconds / 100));
		}

		{
			double shipDistance = Units.toMeter(camera.position.len());
			double shipDistanceToSurface = shipDistance - planetData.radius;
			shipDistanceLabel.setText(Units.meterDistanceToString(shipDistanceToSurface) + "  (" + Units.toString(shipDistanceToSurface / planetData.radius) + " radii)");
			shipEscapeVelocityLabel.setText(Units.metersPerSecondToString(Units.escapeVelocity(planetData.mass, shipDistance)));
		}
		
		stage.draw();
	}
}
