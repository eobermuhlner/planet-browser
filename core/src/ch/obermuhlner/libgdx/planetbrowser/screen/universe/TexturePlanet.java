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
	private String emissiveTextureName;

	public TexturePlanet(String diffuseTextureName, String normalTextureName, String emissiveTextureName) {
		this.diffuseTextureName = diffuseTextureName;
		this.normalTextureName = normalTextureName;
		this.emissiveTextureName = emissiveTextureName;
	}
	
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.hasAtmosphere = true;
		planetData.temperature = random.nextDouble(270, 300);
		
		return planetData;
	}

	@Override
	protected Material createPlanetMaterial(Random random, PlanetData planetData) {
		Array<Attribute> attributes = new Array<Attribute>();
		
		if (diffuseTextureName != null) {
			Texture texture = PlanetBrowser.getTexture(diffuseTextureName);
			attributes.add(TextureAttribute.createDiffuse(texture));
		}
		if (normalTextureName != null) {
			Texture texture = PlanetBrowser.getTexture(normalTextureName);
			attributes.add(TextureAttribute.createNormal(texture));
		}
		if (emissiveTextureName != null) {
			Texture texture = PlanetBrowser.getTexture(emissiveTextureName);
			attributes.add(TextureAttribute.createEmissive(texture));
		}
		
		Material material = new Material(attributes);
		return material;
	}

}
