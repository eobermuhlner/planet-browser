package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.util.DisposableContainer;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public interface PlanetFactory {

	PlanetData createPlanetData(Random random);
	
	Array<Attribute> createMaterialAttributes(Random random, PlanetData planetData, DisposableContainer disposables, float xFrom, float xTo, float yFrom, float yTo, int textureSize);
	
	Map<Long, Texture> createTextures(Random random, PlanetData planetData, float xFrom, float xTo, float yFrom, float yTo, long textureTypes, int textureSize, DisposableContainer disposables);

	Array<ModelInstance> createModelInstance(Random random, PlanetData planetData, Material material);
}
