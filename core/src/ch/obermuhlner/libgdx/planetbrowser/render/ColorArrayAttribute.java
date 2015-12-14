package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * Used to specify an array of colors for the shader to use.
 * 
 * How the shader uses the colors depends on the shader implementation.
 * 
 * <h2>jupiter shader</h2>
 * See the file: <code>jupiter.fragment.glsl</code>
 * 
 * The first 3 colors define the color space used for the bands.
 * Alpha values are ignored.
 * 
 * <h2>terrestrial shader</h2>
 * See the file: <code>terrestrial.fragment.glsl</code>
 * 
 * The first 3 colors define the color space used for the minimum height.
 * The first 3 colors define the color space used for the maximum height.
 * Heights between minimum and maximum are interpolated.
 * Alpha values are used to specify the specularity!
 */
public class ColorArrayAttribute extends Attribute {

	public final static String PlanetColorsAlias = "planetColors";
	public final static long PlanetColors = register(PlanetColorsAlias);

	public final Color[] colors;

	public ColorArrayAttribute (long type, Color... colors) {
		super(type);
		this.colors = colors;
	}
	
	@Override
	public Attribute copy () {
		return new ColorArrayAttribute(type, colors);
	}
	
	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		
		ColorArrayAttribute other = (ColorArrayAttribute)o;
		if (other.colors.length != colors.length) {
			return other.colors.length - colors.length;
		}
		for (int i = 0; i < colors.length; i++) {
			int thisColorBits = colors[i].toIntBits();
			int otherColorBits = other.colors[i].toIntBits();
			if (otherColorBits != thisColorBits) {
				return otherColorBits - thisColorBits;
			}
		}
		return 0;
	}
}
