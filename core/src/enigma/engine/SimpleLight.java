package enigma.engine;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SimpleLight {
	private static ArrayList<SimpleLight> allInstances = new ArrayList<SimpleLight>(1024);

	private Sprite lightSprite;
	private float scaleFactor = 1f;
	private boolean active = true;

	public SimpleLight(float x, float y) {
		lightSprite = new Sprite(Textures.light);
		SimpleLight.allInstances.add(this);
	}

	public void updatePosition(float x, float y) {
		lightSprite.setPosition(x, y);
	}

	public void setCenterPosition(float x, float y) {
		// Sprite's coordinates are at the bottom left. Offset corrects that.
		float xOffset = lightSprite.getWidth() * scaleFactor / 2;
		float yOffset = lightSprite.getHeight() * scaleFactor / 2;
		lightSprite.setPosition(x - xOffset, y - yOffset);
	}

	public float getCenterX() {
		// Sprite's coordinates are at the bottom left. Offset corrects that.
		float xOffset = lightSprite.getWidth() * scaleFactor / 2;
		return lightSprite.getX() + xOffset;
	}

	public float getCenterY() {
		// Sprite's coordinates are at the bottom left. Offset corrects that.
		float yOffset = lightSprite.getHeight() * scaleFactor / 2;
		return lightSprite.getY() + yOffset;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
		lightSprite.setScale(scaleFactor);
		
		//save current position for after alignment is corrected
		float oriX = getCenterX();
		float oriY = getCenterY();
		
		//set to 0, 0 to avoid dealing with coordinate issues, restore after.
		lightSprite.setPosition(0, 0);
		lightSprite.setOrigin(lightSprite.getX(), lightSprite.getY());
		setCenterPosition(oriX, oriY);
	}

	public boolean lightInRange(float x, float y, float xRange, float yRange) {
		float spriteCenterX = lightSprite.getX() + scaleFactor * lightSprite.getWidth() / 2;
		float spriteCenterY = lightSprite.getY() + scaleFactor * lightSprite.getHeight() / 2;

		if (spriteCenterX > x - xRange && spriteCenterX < x + xRange) {
			if (spriteCenterY > y - yRange && spriteCenterY < y + yRange) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Draw all lights that are active. This is useful to draw all lights at once to a single buffer
	 * to be blended.
	 * 
	 * @param batch
	 *            the SpriteBatch responsible for drawing
	 */
	public static void renderAllLights(SpriteBatch batch, float xCenter, float yCenter) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		for (int i = 0; i < allInstances.size(); ++i) {
			SimpleLight current = allInstances.get(i);

			// if it exists and is turned on then draw
			if (current != null && current.active && current.lightInRange(xCenter, yCenter, width, height)) {
				current.lightSprite.draw(batch);
			}
		}
	}

	public boolean getOnOff() {
		return active;
	}

	public void setOnOff(boolean onOff) {
		active = onOff;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}
}
