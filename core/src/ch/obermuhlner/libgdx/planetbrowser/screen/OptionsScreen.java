package ch.obermuhlner.libgdx.planetbrowser.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class OptionsScreen extends AbstractStageScreen {

	private static final int COLUMNS = 2;
	
	private SelectBox<Integer> selectGeneratedTexturesSize;

	private SelectBox<Integer> selectSphereDivisions;

	protected void prepareStage(Gui gui, Table rootTable) {
		Table table = gui.table();
		rootTable.add(table);
		
		table.row();
		table.add(gui.title("Graphics Quality")).colspan(COLUMNS);
		
		table.row();
		table.add(gui.label("Generated Texture Size"));		
		selectGeneratedTexturesSize = new SelectBox<Integer>(gui.skin);
		table.add(selectGeneratedTexturesSize);
		selectGeneratedTexturesSize.setItems(4096, 2048, 1024, 512);
		
		table.row();
		table.add(gui.label("Sphere Divisions"));		
		selectSphereDivisions = new SelectBox<Integer>(gui.skin);
		table.add(selectSphereDivisions);
		selectSphereDivisions.setItems(80, 60, 40, 20, 10);
		
		// button row
		
		table.row();
		table.add(gui.button("OK", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.options.setGeneratedTexturesSize(selectGeneratedTexturesSize.getSelected());
				PlanetBrowser.INSTANCE.options.setSphereDivisions(selectSphereDivisions.getSelected());
				PlanetBrowser.INSTANCE.setScreen(new WelcomeScreen());
			}
		}));
		table.add(gui.button("Reset", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.options.reset();
				pushOptionsToGui();
			}
		}));
		table.add(gui.button("Cancel", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new WelcomeScreen());
			}
		}));
		
		pushOptionsToGui();
	}
	
	private void pushOptionsToGui() {
		selectGeneratedTexturesSize.setSelected(PlanetBrowser.INSTANCE.options.getGeneratedTexturesSize());
		selectSphereDivisions.setSelected(PlanetBrowser.INSTANCE.options.getSphereDivisions());
	}
}
