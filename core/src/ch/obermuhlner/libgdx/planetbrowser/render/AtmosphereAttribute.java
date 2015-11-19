package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class AtmosphereAttribute extends Attribute {
	public static final String AtmosphereAlias = "atmosphere";
	public static final long Atmosphere = register(AtmosphereAlias);

	public final Color centerColor;
	public final Color horizonColor;
	public final Color spaceColor;
	
	public AtmosphereAttribute(Color color) {
		this(color, 0.0f, 0.6f, 0.0f);
	}

	public AtmosphereAttribute(Color color, float centerAlpha, float horizonAlpha, float spaceAlpha) {
		this(new Color(color.r, color.g, color.b, centerAlpha),
			new Color(color.r, color.g, color.b, horizonAlpha),
			new Color(color.r, color.g, color.b, spaceAlpha));
	}

	public AtmosphereAttribute(Color centerColor, Color horizonColor, Color spaceColor) {
		super(Atmosphere);
		
		this.centerColor = centerColor;
		this.horizonColor = horizonColor;
		this.spaceColor = spaceColor;
	}

	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		AtmosphereAttribute other = (AtmosphereAttribute) o;
		
		int cmp = other.centerColor.toIntBits() - centerColor.toIntBits();
		if (cmp == 0) {
			cmp = other.horizonColor.toIntBits() - horizonColor.toIntBits();
		}
		if (cmp == 0) {
			cmp = other.spaceColor.toIntBits() - spaceColor.toIntBits();
		}
		return cmp;
	}

	@Override
	public Attribute copy() {
		return new AtmosphereAttribute(centerColor, horizonColor, spaceColor);
	}

}
