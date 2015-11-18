package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class ShaderFunctionAttribute extends Attribute {

	public final String code;

	public ShaderFunctionAttribute(long type, String code) {
		super(type);
		
		this.code = code;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 31 * hash + code.hashCode();
		return hash;
	}
	
	@Override
	public int compareTo(Attribute o) {
		if (type != o.type) return (int)(type - o.type);
		ShaderFunctionAttribute other = (ShaderFunctionAttribute) o;
		return code.compareTo(other.code);
	}

	@Override
	public Attribute copy() {
		return new ShaderFunctionAttribute(type, code);
	}

}
