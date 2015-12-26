package ch.obermuhlner.libgdx.planetbrowser.screen.universe;

import static ch.obermuhlner.libgdx.planetbrowser.util.Random.p;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;

import ch.obermuhlner.libgdx.planetbrowser.PlanetBrowser;
import ch.obermuhlner.libgdx.planetbrowser.util.Molecule;
import ch.obermuhlner.libgdx.planetbrowser.util.Random;
import ch.obermuhlner.libgdx.planetbrowser.util.Units;

public class TexturePlanet extends AbstractPlanet {

	private String diffuseTextureName;
	private String normalTextureName;
	private String emissiveTextureName;

	public TexturePlanet(String diffuseTextureName, String normalTextureName, String emissiveTextureName) {
		this.diffuseTextureName = diffuseTextureName;
		this.normalTextureName = normalTextureName;
		this.emissiveTextureName = emissiveTextureName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PlanetData createPlanetData(Random random) {
		PlanetData planetData = new PlanetData();
		
		planetData.radius = random.nextDouble(Units.EARTH_RADIUS * 0.5, Units.EARTH_RADIUS * 2.5);
		planetData.period = random.nextDouble(Units.SECONDS_PER_HOUR * 5, Units.SECONDS_PER_HOUR * 40);
		planetData.temperature = random.nextDouble(270, 300);
		
		planetData.atmosphere = random.nextProbabilityMap(
				p(random.nextGaussian(75), Molecule.N2),
				p(random.nextGaussian(20), Molecule.O2),
				p(random.nextGaussian(0.4), Molecule.H2O),
				p(random.nextGaussian(0.01), Molecule.Ar),
				p(random.nextGaussian(0.005), Molecule.CO2)
				);

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
