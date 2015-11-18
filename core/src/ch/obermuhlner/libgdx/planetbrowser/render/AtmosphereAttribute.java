package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;

public class AtmosphereAttribute extends Attribute {
	public static final String AtmosphereAlias = "heightFunction";
	public static final long Atmosphere = register(AtmosphereAlias);
	
	public final Color centerColor;
	public final Color horizonColor;

	public AtmosphereAttribute(Color centerColor, Color horizonColor) {
		super(Atmosphere);
		
		this.centerColor = centerColor;
		this.horizonColor = horizonColor;
	}

	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		AtmosphereAttribute other = (AtmosphereAttribute) o;
		
		int cmp = other.centerColor.toIntBits() - centerColor.toIntBits();
		if (cmp == 0) {
			cmp = other.horizonColor.toIntBits() - horizonColor.toIntBits();
		}

		return cmp;
	}

	@Override
	public Attribute copy() {
		return new AtmosphereAttribute(centerColor, horizonColor);
	}

}
