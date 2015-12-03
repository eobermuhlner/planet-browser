package ch.obermuhlner.libgdx.planetbrowser;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import ch.obermuhlner.libgdx.planetbrowser.screen.PlanetScreen;

public class PlanetBrowser extends Game {
	
	public static final AssetManager assetManager = new AssetManager();

	@Override
	public void create() {
		{
			TextureParameter textureParameter = new TextureParameter();
			textureParameter.genMipMaps = true;
			textureParameter.minFilter = TextureFilter.MipMapLinearLinear;
			textureParameter.magFilter = TextureFilter.MipMapLinearLinear;
			for (String textureName : new String[] {
					"earth.jpg", "earth_normals.jpg"
			}) {
				assetManager.load(getTexturePath(textureName), Texture.class, textureParameter);
			}
		}

		{
			TextureParameter textureParameter = new TextureParameter();
			textureParameter.minFilter = TextureFilter.Linear;
			textureParameter.magFilter = TextureFilter.Linear;
			for (String textureName : new String[] {
				//"skybox_neg_x.png", "skybox_pos_x.png", "skybox_neg_y.png", "skybox_pos_y.png", "skybox_neg_z.png", "skybox_pos_z.png",  
				"terrestrial_colors.png", "mars_colors.png", "moon_colors.png", "lava_colors.png",
				"normals_crater_huge1.png",
				"normals_crater_big1.png", "normals_crater_big2.png",
				"normals_crater_medium1.png", "normals_crater_medium2.png", "normals_crater_medium3.png",
				"normals_crater_small1.png", "normals_crater_small2.png", "normals_crater_small3.png", "normals_crater_small4.png", "normals_crater_small5.png",
				"normals_crater_tiny1.png", "normals_crater_tiny2.png",
				"normals_soft1.png",
				}) {
				assetManager.load(getTexturePath(textureName), Texture.class);
			}
		}

		assetManager.finishLoading();
		
		setScreen(new PlanetScreen(this));
	}

		@Override
	public void dispose () {
		super.dispose();
		
		assetManager.dispose();
	}

	public static Texture getTexture(String textureName) {
		return assetManager.get(getTexturePath(textureName), Texture.class);
	}
		
	public static String getTexturePath (String textureName) {
		return "data/textures/" + textureName;
	}
}
