package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class OptionsScreen extends AbstractStageScreen {

	private static final int COLUMNS = 2;
	
	private SelectBox<Integer> selectGeneratedTexturesSize;
	private SelectBox<Integer> selectSphereDivisions;

	protected void prepareStage(Stage stage, Gui gui) {
		Table rootTable = gui.rootTable();
		stage.addActor(rootTable);		
		
		rootTable.row();
		Table table = gui.table();
		rootTable.add(table);
		
		table.row();
		table.add(gui.title("Graphics Quality")).colspan(COLUMNS);
		
		IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer);
		int maxTextureSize = intBuffer.get();
		Array<Integer> textureSizeArray = new Array<Integer>();
		for (int textureSize = 512; textureSize <= maxTextureSize; textureSize*=2) {
			textureSizeArray.add(textureSize);
		}
		textureSizeArray.reverse();
		
		table.row();
		table.add(gui.label("Generated Texture Size"));		
		selectGeneratedTexturesSize = new SelectBox<Integer>(gui.skin);
		table.add(selectGeneratedTexturesSize);
		selectGeneratedTexturesSize.setItems(textureSizeArray);
		
		table.row();
		table.add(gui.label("Sphere Divisions"));		
		selectSphereDivisions = new SelectBox<Integer>(gui.skin);
		table.add(selectSphereDivisions);
		selectSphereDivisions.setItems(80, 70, 60, 50, 40, 30, 20);
		
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
