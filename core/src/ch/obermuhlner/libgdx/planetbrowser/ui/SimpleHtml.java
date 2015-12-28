package ch.obermuhlner.libgdx.planetbrowser.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHtml {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
	
	public static String moleculeToHtml(String molecule) {
		StringBuilder html = new StringBuilder();
		
		String string = molecule;
		
		int pos = 0;
		Matcher matcher = NUMBER_PATTERN.matcher(string);
		while(matcher.find()) {
			html.append(string.substring(pos, matcher.start()));
			
			html.append("<sub>");
			html.append(matcher.group(0));
			html.append("</sub>");
			
			pos = matcher.end();
		}
		html.append(string.substring(pos));
		
		return html.toString();
	}

}
