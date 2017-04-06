package enigma.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {
	public static ArrayList<Texture> textures = new ArrayList<Texture>();
	public static TextureRegion[][] playerTexture;
	public static Texture characterSpriteSheet;
	public static Texture grass;
	public static TextureRegion wrappedGrass;
	public static Texture light;
	public static Texture flashLight;
	public static Texture blueOverLay;

	public static void initTextures() {
		// A sprite sheet for a character.
		characterSpriteSheet = new Texture("Character16x16packed.png");
		playerTexture = TextureRegion.split(characterSpriteSheet, 16, 16);
		textures.add(characterSpriteSheet);

		// A tile-able grass texture
		grass = new Texture("Grass2.png");
		grass.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		wrappedGrass = new TextureRegion(grass);
		textures.add(grass);

		// a circular light texture
		light = new Texture("light.png");
		textures.add(light);

		// An elongated light texture
		flashLight = new Texture("StretchedLight.png");
		textures.add(flashLight);
		
		// A blue overlay for non-shader testing.
		blueOverLay = new Texture("BlueOverlay.png");
		textures.add(blueOverLay);

	}

	public static void dispose() {
		// Dispose of all textures that have been created.
		for (Texture texture : textures) {
			if (texture != null) texture.dispose();
		}
	}
}
