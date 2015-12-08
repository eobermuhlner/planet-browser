package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class WelcomeScreen extends AbstractStageScreen {

	private ProgressBar progressBarLoading;

	protected void prepareStage(Stage stage, Gui gui) {
		Table rootTable = gui.rootTableCenter();
		stage.addActor(rootTable);		
		
		rootTable.row();
		Table table = gui.table();
		rootTable.add(table);
		table.defaults().center();
		
		table.row();
		progressBarLoading = new ProgressBar(0, 1, 0.01f, false, gui.skin);
		table.add(progressBarLoading);

		table.row();
		table.add(gui.button("Start", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new PlanetScreen());
			}
		}));

		table.row();
		table.add(gui.button("Options", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new OptionsScreen());
			}
		}));

		table.row();
		table.add(gui.button("Open GL", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new OpenglInfoScreen());
			}
		}));
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		progressBarLoading.setValue(PlanetBrowser.assetManager.getProgress());
	}
}
