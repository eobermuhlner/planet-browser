package ch.obermuhlner.libgdx.planetbrowser.screen;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.BufferUtils;

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
		
		addStringRow(gui, table, "Version", GL20.GL_VERSION);
		addStringRow(gui, table, "Renderer", GL20.GL_RENDERER);
		addStringRow(gui, table, "Vendor", GL20.GL_VENDOR);
		addRow(gui, table, "GL 3.0", String.valueOf(Gdx.graphics.isGL30Available()));
		addIntegerRow(gui, table, "Max Texture Size", GL20.GL_MAX_TEXTURE_SIZE);
		addIntegerRow(gui, table, "Max Texture Units", GL20.GL_MAX_TEXTURE_UNITS);
		addIntegerRow(gui, table, "Max Fragment Uniform Vectors", GL20.GL_MAX_FRAGMENT_UNIFORM_VECTORS);		
		addIntegerRow(gui, table, "Max Varying Vectors", GL20.GL_MAX_VARYING_VECTORS);		
		
		// button row
		
		table.row();
		table.add(gui.button("OK", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlanetBrowser.INSTANCE.setScreen(new WelcomeScreen());
			}
		}));
	}

	private void addStringRow(Gui gui, Table table, String label, int glConst) {
		String string = Gdx.gl.glGetString(glConst);
		addRow(gui, table, label, string);
	}

	private void addIntegerRow(Gui gui, Table table, String label, int glConst) {
		IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
		
		Gdx.gl.glGetIntegerv(glConst, intBuffer);
		addRow(gui, table, label, String.valueOf(intBuffer.get()));
	}

	private void addRow(Gui gui, Table table, String label, String string) {
		table.row();
		table.add(gui.label(label));		
		table.add(gui.label(string));
	}
}
