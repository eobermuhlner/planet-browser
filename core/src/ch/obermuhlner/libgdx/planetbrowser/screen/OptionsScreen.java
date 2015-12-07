package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class OptionsScreen extends AbstractStageScreen {

	private PlanetBrowser planetBrowser;
	
	public OptionsScreen(PlanetBrowser planetBrowser) {
		this.planetBrowser = planetBrowser;
	}
	
	protected void prepareStage(Gui gui, Table rootTable) {
		Table table = gui.table();
		rootTable.add(table);
		
		table.row();
		table.add(gui.label("Graphics Quality"));
		SelectBox<String> selectGraphicsQuality = new SelectBox<String>(gui.skin);
		table.add(selectGraphicsQuality);
		selectGraphicsQuality.setItems("High", "Medium", "Low");
		
		table.row();
		table.add(gui.button("OK", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				planetBrowser.setScreen(new WelcomeScreen(planetBrowser));
			}
		}));
	}
}
