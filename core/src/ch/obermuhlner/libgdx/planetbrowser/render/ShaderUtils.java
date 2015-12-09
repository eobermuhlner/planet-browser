package ch.obermuhlner.libgdx.planetbrowser.render;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderUtils {

	private static final Pattern ERROR_PATTERN = Pattern.compile("([0-9]+):([0-9]+):");

	public static String createErrorMessage(ShaderProgram program, String vertexSource, String fragmentSource) {
		StringBuilder message = new StringBuilder();
		message.append("Shader compilation errors:\n");
		
		String log = program.getLog();
		
		String logLines[] = log.split("\\r?\\n");
		for (int i = 0; i < logLines.length; i++) {
			Matcher matcher = ERROR_PATTERN.matcher(logLines[i]);
			if (matcher.find()) {
				int fileNumber = Integer.parseInt(matcher.group(1));
				int lineNumber = Integer.parseInt(matcher.group(2));
				String line = null;
				if (fileNumber == 1) {
					line = getLine(vertexSource, lineNumber);
				} else if (fileNumber == 2) {
					line = getLine(fragmentSource, lineNumber);
				}
				if (line != null) {
					message.append(line);
					message.append("\n");
				}
			}

			message.append(logLines[i]);
			message.append("\n");
		}
		
		return message.toString();
	}

	private static String getLine(String source, int lineNumber) {
		String lines[] = source.split("\\r?\\n");
		if (lineNumber >= 0 && lineNumber < lines.length) {
			return lines[lineNumber];
		}
		return null;
	}
}
