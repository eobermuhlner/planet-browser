package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public abstract class AbstractStageScreen extends AbstractScreen {

	private Stage stage;

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		prepareStage(stage, new Gui());
	}

	protected abstract void prepareStage(Stage stage, Gui gui);

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
		
		stage.draw();
	}
}
