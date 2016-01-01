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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.model.MeshPartBuilder;
import ch.obermuhlner.libgdx.planetbrowser.model.ModelBuilder;
import ch.obermuhlner.libgdx.planetbrowser.render.PlanetUberShaderProvider;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.ModelInstanceFactory;
import ch.obermuhlner.libgdx.planetbrowser.screen.universe.PlanetData;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class FlyPlanetScreen extends AbstractScreen {

	private final ModelInstanceFactory factory;
	private final long randomSeed;

	public final Environment environment = new Environment();
	private Stage stage;
	private CameraInputController cameraInputController;
	private ModelBatch modelBatch;
	private PerspectiveCamera camera;
	private ModelInstance surface;
	private PlanetData planetData;

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
		camera.near = 0.001f;
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
		
		cameraInputController = new CameraInputController(camera);
		cameraInputController.forwardTarget = false;
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraInputController));

		// create planet surface

		planetData = factory.createPlanetData(new Random(randomSeed));
		long textureTypes = TextureAttribute.Bump | TextureAttribute.Diffuse | TextureAttribute.Normal | TextureAttribute.Specular;
		float xFrom = 0.5f;
		float xTo = 0.6f;
		float yFrom = 0.5f;
		float yTo = 0.6f;
		
		Map<Long, Texture> textures = factory.createTextures(planetData, new Random(randomSeed), xFrom, xTo, yFrom, yTo, textureTypes, 2048);

		Array<Attribute> materialAttributes = new Array<Attribute>();
//		materialAttributes.add(new TextureAttribute(TextureAttribute.Bump, textures.get(TextureAttribute.Bump)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Diffuse, textures.get(TextureAttribute.Diffuse)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Normal, textures.get(TextureAttribute.Normal)));
		materialAttributes.add(new TextureAttribute(TextureAttribute.Specular, textures.get(TextureAttribute.Specular)));
		Material material = new Material(materialAttributes);
		
		final int rectSize = 100;
		ModelBuilder modelBuilder = new ModelBuilder();
		final Material material1 = material;
		modelBuilder.begin();
		int divisions = 100;
		MeshPartBuilder part = modelBuilder.part("rect", GL20.GL_TRIANGLES, (long) (Usage.Position | Usage.Normal | Usage.Tangent | Usage.TextureCoordinates), material1);
		part.patch((float) rectSize, 0f, (float) -rectSize, (float) -rectSize, 0f, (float) -rectSize, (float) -rectSize, 0f, (float) rectSize, (float) rectSize, 0f, (float) rectSize, (float) 0, (float) 1, (float) 0, divisions, divisions);
		Model model = modelBuilder.end();
		surface = new ModelInstance(model);
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

		stage.act(delta);
		cameraInputController.update();

		modelBatch.begin(camera);
		modelBatch.render(surface, environment);
		modelBatch.end();
		stage.draw();
	}
}
