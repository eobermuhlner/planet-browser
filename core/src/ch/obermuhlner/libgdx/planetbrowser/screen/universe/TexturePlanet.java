package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;

public class TexturePlanet extends AbstractPlanet {

	private String diffuseTextureName;
	private String normalTextureName;

	public TexturePlanet(String diffuseTextureName, String normalTextureName) {
		this.diffuseTextureName = diffuseTextureName;
		this.normalTextureName = normalTextureName;
	}
	
	@Override
	protected Material createPlanetMaterial(Random random) {
		Array<Attribute> attributes = new Array<Attribute>();
		
		if (diffuseTextureName != null) {
			Texture texture = PlanetBrowser.getTexture(diffuseTextureName);
			attributes.add(TextureAttribute.createDiffuse(texture));
		}
		if (normalTextureName != null) {
			Texture texture = PlanetBrowser.getTexture(normalTextureName);
			attributes.add(TextureAttribute.createNormal(texture));
		}
		
		Material material = new Material(attributes);
		return material;
	}

}
