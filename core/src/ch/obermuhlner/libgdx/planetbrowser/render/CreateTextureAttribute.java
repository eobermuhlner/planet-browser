package ch.obermuhlner.libgdx.planetbrowser.render;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;

public class CreateTextureAttribute extends IntAttribute {

	public static final String CreateTextureAlias = "createTexture";
	public static final long CreateTexture = register(CreateTextureAlias);

	public static final int CREATE_BUMP_TEXTURE = 1;
	public static final int CREATE_DIFFUSE_TEXTURE = 2;
	public static final int CREATE_NORMAL_TEXTURE = 4;
	public static final int CREATE_SPECULAR_TEXTURE = 8;
	public static final int CREATE_EMISSIVE_TEXTURE = 16;

	protected CreateTextureAttribute(long type, int value) {
		super(type, value);
	}
	
	@Override
	public Attribute copy() {
		return new CreateTextureAttribute(CreateTexture, value);
	}

	public static CreateTextureAttribute createCreateBump () {
		return new CreateTextureAttribute(CreateTexture, CREATE_BUMP_TEXTURE);
	}

	public static CreateTextureAttribute createCreateDiffuse () {
		return new CreateTextureAttribute(CreateTexture, CREATE_DIFFUSE_TEXTURE);
	}

	public static CreateTextureAttribute createCreateNormal () {
		return new CreateTextureAttribute(CreateTexture, CREATE_NORMAL_TEXTURE);
	}
	
	public static CreateTextureAttribute createCreateSpecular () {
		return new CreateTextureAttribute(CreateTexture, CREATE_SPECULAR_TEXTURE);
	}

	public static CreateTextureAttribute createCreateEmissive () {
		return new CreateTextureAttribute(CreateTexture, CREATE_EMISSIVE_TEXTURE);
	}

	public static CreateTextureAttribute createTextures(boolean bump, boolean diffuse, boolean normal, boolean specular, boolean emissive) {
		int value = 0;
		
		if (bump) {
			value |= CREATE_BUMP_TEXTURE;
		}
		if (diffuse) {
			value |= CREATE_DIFFUSE_TEXTURE;
		}
		if (normal) {
			value |= CREATE_NORMAL_TEXTURE;
		}
		if (specular) {
			value |= CREATE_SPECULAR_TEXTURE;
		}
		if (emissive) {
			value |= CREATE_EMISSIVE_TEXTURE;
		}
		
		return new CreateTextureAttribute(CreateTexture, value);
	}

}
