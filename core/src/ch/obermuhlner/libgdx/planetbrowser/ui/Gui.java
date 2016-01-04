package ch.obermuhlner.libgdx.planetbrowser.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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

	public ScrollPane scrollPane(Actor content) {
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setOverscroll(false, false);
		return scrollPane;
	}

	public void pack(Window window) {
		window.setSize(
				Math.min(window.getMaxWidth(), window.getPrefWidth()),
				Math.min(window.getMaxHeight(), window.getPrefHeight()));
		window.validate();
		if (window.needsLayout()) {
			window.setSize(
					Math.min(window.getMaxWidth(), window.getPrefWidth()),
					Math.min(window.getMaxHeight(), window.getPrefHeight()));
			window.validate();
		}
	}
	
	private static Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-z]*)>");
	public Table htmlLabel(String htmlText) {
		Table table = new Table(skin);
		
		int sub = 0;
		int sup = 0;
		
		Matcher matcher = TAG_PATTERN.matcher(htmlText);
		int pos = 0;
		while (matcher.find()) {
			String text = htmlText.substring(pos, matcher.start());
			addRichLabel(table, text, sub > 0, sup > 0);
			
			String tag = matcher.group(2);
			boolean tagStart = matcher.group(1).length() == 0;
			
			if (tag.equals("sub")) {
				sub += tagStart ? 1 : -1;
			} else if (tag.equals("sup")) {
				sup += tagStart ? 1 : -1;
			}
			
			pos = matcher.end();
		}
		
		String text = htmlText.substring(pos);
		addRichLabel(table, text, sub > 0, sup > 0);
		return table;
	}

	private void addRichLabel(Table table, String text, boolean sub, boolean sup) {
		if (sub) {
			table.add(text, "small").bottom().spaceLeft(1).spaceRight(2);
		} else if (sup) {
			table.add(text, "small").top().spaceLeft(1).spaceRight(2);
		} else {
			table.add(text);
		}
	}
	
	public static String firstToUppercase(String string) {
		if (string.length() == 0) {
			return "";
		}
		
		return string.substring(0, 1).toUpperCase() + string.substring(1);
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
