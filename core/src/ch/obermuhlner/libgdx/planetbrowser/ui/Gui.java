package ch.obermuhlner.libgdx.planetbrowser.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.Config;

public class Gui {

	public final Skin skin = Config.getSkin();
	
	private static final boolean DEBUG = false;
	
	public Table rootTable() {
		Table table = new Table(skin);
		
		table.top();
		table.left();
		
		table.setFillParent(true);
		table.setDebug(DEBUG);

		return table;
	}
	
	public Table rootTableCenter() {
		Table table = new Table(skin);
		
		table.center();

		table.setFillParent(true);
		table.setDebug(DEBUG);

		return table;
	}
	
	public Table table() {
		Table table = new Table(skin);
		table.setBackground("table");
		
		table.left();
		table.defaults().left();

		table.setDebug(DEBUG);
		
		return table;
	}

	public TableLayout tableLayout() {
		TableLayout table = new TableLayout(skin);
		
		table.left();
		table.defaults().left();

		table.setDebug(DEBUG);
		
		return table;
	}

	public TextButton button(String text, ChangeListener changeListener) {
		TextButton button = new TextButton(text, skin);
		button.addListener(changeListener);
		return button;
	}

	public float textWidth(String text) {
		return label(text).getWidth();
	}
	
	public Label label(String text) {
		return new Label(text, skin);
	}

	public Label title(String text) {
		return new Label(text, skin, "title");
	}
	
	public TextField textField(String text, ChangeListener changeListener) {
		TextField textField = textField(text);
		textField.addListener(changeListener);
		return textField;
	}

	public TextField textField(String text) {
		return new TextField(text, skin);
	}
	
	public SelectBox<String> select(String... items) {
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems(items);
		return selectBox;
	}

	public SelectBox<String> select(Array<String> items) {
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems(items);
		return selectBox;
	}

	public class TableLayout extends Table {
		public TableLayout(Skin skin) {
			super(skin);
		}
		
		public Label addNumeric(String format) {
			Label label = label(format);
			label.setAlignment(Align.right);
			add(label).align(Align.right).width(label.getWidth());
			return label;
		}
	}
}
