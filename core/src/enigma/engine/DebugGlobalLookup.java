package enigma.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A place where temporary global variables can be made to prevent cluttering the main code.
 * 
 * @author Matt Stone
 *
 */
public class DebugGlobalLookup {
	public static Sprite blueSprite;

	public static void init() {
		blueSprite = new Sprite(Textures.blueOverLay);
		blueSprite.setPosition(100, 150);
	}

}
