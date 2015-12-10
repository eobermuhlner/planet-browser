package ch.obermuhlner.libgdx.planetbrowser.render;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderUtils {

	private static final Pattern ERROR_PATTERN = Pattern.compile("([0-9]+):([0-9]+):");

	public static String createErrorMessage(ShaderProgram program) {
		return createErrorMessage(program, program.getVertexShaderSource(), program.getFragmentShaderSource());
	}
	
	public static String createErrorMessage(ShaderProgram program, String vertexSource, String fragmentSource) {
		StringBuilder message = new StringBuilder();
		message.append("Shader compilation errors:\n");

		String log = program.getLog();
		
		String logLines[] = log.split("\\r?\\n");
		for (int i = 0; i < logLines.length; i++) {
			message.append(logLines[i]);
			message.append("\n");

			Matcher matcher = ERROR_PATTERN.matcher(logLines[i]);
			if (matcher.find()) {
				int lineNumber = Integer.parseInt(matcher.group(2)) - 1;
				String vertexLine = getLine(vertexSource, lineNumber);
				if (vertexLine != null) {
					message.append("Vertex source at line " + lineNumber + ":\n");
					message.append(vertexLine);
					message.append("\n");
				}

				String fragmentLine = getLine(fragmentSource, lineNumber);
				if (fragmentLine != null) {
					message.append("Fragment source at line " + lineNumber + ":\n");
					message.append(fragmentLine);
					message.append("\n");
				}
				message.append("-----------------------------------------\n");
			}
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
