package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public interface ModelInstanceFactory {

	public enum TextureType {
		Bump,
		Diffuse,
		Specular,
		Normal
	};
	
	PlanetData createPlanetData(Random random);
	
	Array<ModelInstance> createModelInstance(PlanetData planetData, Random random);
	
	Map<TextureType, Texture> createTextures(PlanetData planetData, Random random, float xFrom, float xTo, float yFrom, float yTo, Set<TextureType> textureTypes, int textureSize);
}
