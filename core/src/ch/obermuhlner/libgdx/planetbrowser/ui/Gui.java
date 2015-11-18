package ch.obermuhlner.libgdx.planetbrowser.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class Gui {

	private Skin skin = GameSkin.getSkin();
	
	private static final boolean DEBUG = false;
	
	public Table rootTable() {
		Table table = new Table(skin);
		
		table.top().left();
		table.defaults().left().space(10);

		table.setFillParent(true);

		table.setDebug(DEBUG);

		return table;
	}
	
	public Table table() {
		Table table = new Table(skin);
		table.setBackground("table");
		
		table.left();
		table.defaults().align(Align.left).spaceRight(50);

		table.setDebug(DEBUG);
		
		return table;
	}

	public TextButton button(String text, ChangeListener changeListener) {
		TextButton button = new TextButton(text, skin);
		button.addListener(changeListener);
		return button;
	}

	public Label label(String text) {
		return new Label(text, skin);
	}

}
