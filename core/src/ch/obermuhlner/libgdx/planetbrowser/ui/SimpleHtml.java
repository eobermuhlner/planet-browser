package ch.obermuhlner.libgdx.planetbrowser.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHtml {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

	private static final Pattern EXPONENT_PATTERN = Pattern.compile("\\^([0-9]+)");

	private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("([+-]?[0-9]+.[0-9]+)E[+]?([-]?[0-9]+)");

	private static final String MULTIPLY_SYMBOL = "\u00D7";
	
	/**
	 * Converts a molecule formula into a simple html text.
	 * 
	 * Example: "C2H6" is converted into "C<sub>2</sub>H<sub>6</sub>".
	 * 
	 * @param string the molecule formula
	 * @return the simple html
	 */
	public static String moleculeToHtml(String string) {
		StringBuilder html = new StringBuilder();
		
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

	public static String unitsToHtml(String string) {
		StringBuilder html = new StringBuilder();
		
		int pos = 0;
		Matcher matcher = EXPONENT_PATTERN.matcher(string);
		while(matcher.find()) {
			html.append(string.substring(pos, matcher.start()));
			
			html.append("<sup>");
			html.append(matcher.group(1));
			html.append("</sup>");
			
			pos = matcher.end();
		}
		html.append(string.substring(pos));
		
		return html.toString();
	}

	public static String scientificNumbersToHtml(String string) {
		StringBuilder html = new StringBuilder();
		
		int pos = 0;
		Matcher matcher = SCIENTIFIC_NOTATION_PATTERN.matcher(string);
		while(matcher.find()) {
			html.append(string.substring(pos, matcher.start()));
			
			html.append(matcher.group(1));
			html.append(MULTIPLY_SYMBOL);
			html.append("10");
			html.append("<sup>");
			html.append(matcher.group(2));
			html.append("</sup>");
			
			pos = matcher.end();
		}
		html.append(string.substring(pos));
		
		return html.toString();
	}

	public static String scientificUnitsToHtml(String string) {
		return scientificNumbersToHtml(unitsToHtml(string));
	}
}
