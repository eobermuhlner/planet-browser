package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class WelcomeScreen extends AbstractStageScreen {

	private PlanetBrowser planetBrowser;
	
	private ProgressBar progressBarLoading;

	public WelcomeScreen(PlanetBrowser planetBrowser) {
		this.planetBrowser = planetBrowser;
	}
	
	protected void prepareStage(Gui gui, Table rootTable) {
		Table table = gui.table();
		rootTable.add(table);
		
		table.row();
		progressBarLoading = new ProgressBar(0, 1, 0.01f, false, gui.skin);
		table.add(progressBarLoading);

		table.row();
		table.add(gui.button("Start", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new PlanetScreen(planetBrowser));
			}
		}));
		table.add(gui.button("Options", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new OptionsScreen(planetBrowser));
			}
		}));
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		progressBarLoading.setValue(PlanetBrowser.assetManager.getProgress());
	}
}
