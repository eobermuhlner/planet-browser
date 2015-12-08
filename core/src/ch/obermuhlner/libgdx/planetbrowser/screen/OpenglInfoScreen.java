package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.ui.Gui;

public class OpenglInfoScreen extends AbstractStageScreen {

	private static final int COLUMNS = 2;
	
	protected void prepareStage(Stage stage, Gui gui) {
		Table rootTable = gui.rootTable();
		stage.addActor(rootTable);		
		
		rootTable.row();
		Table table = gui.table();
		rootTable.add(table);
		
		table.row();
		table.add(gui.title("Open GL")).colspan(COLUMNS);
		
		addInfoRow(gui, table, "Max Texture Size", GL20.GL_MAX_TEXTURE_SIZE);
		addInfoRow(gui, table, "Max Texture Units", GL20.GL_MAX_TEXTURE_UNITS);
		addInfoRow(gui, table, "Max Fragment Uniform Vectors", GL20.GL_MAX_FRAGMENT_UNIFORM_VECTORS);		
		addInfoRow(gui, table, "Max Varying Vectors", GL20.GL_MAX_VARYING_VECTORS);		
		
		// button row
		
		table.row();
		table.add(gui.button("OK", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new WelcomeScreen());
			}
		}));
	}

	private void addInfoRow(Gui gui, Table table, String label, int glConst) {
		IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
		
		Gdx.gl.glGetIntegerv(glConst, intBuffer);
		table.row();
		table.add(gui.label(label));		
		table.add(gui.label(String.valueOf(intBuffer.get())));
	}
}
