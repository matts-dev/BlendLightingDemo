package enigma.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CenteredSprite {
	private Sprite sprite;
	private int textureRegionRow = 0;
	private int textureRegionCol = 0;
	private float scaleFactor = 1.0f;
	private float moveSpeed = 10.0f;

	public CenteredSprite(TextureRegion[][] region, int row, int col) {
		int maxRow = region.length - 1;
		int maxCol = region[0].length - 1;
		if (region != null && row <= maxRow && col <= maxCol && row >= 0 && col >= 0) {
			sprite = new Sprite(region[row][col]);
			textureRegionCol = col;
			textureRegionRow = row;
		}
	}

	public boolean spriteValid() {
		return sprite != null;
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	public void setScale(float scale) {
		float oriX = sprite.getX();
		float oriY = sprite.getY();

		scaleFactor = scale;
		sprite.setScale(scale);

		sprite.setPosition(0, 0);
		sprite.setOrigin(sprite.getX(), sprite.getY());
		sprite.setPosition(oriX, oriY);
	}

	public void moveUp() {
		int row = sprite.getRegionY() / 16;
		textureRegionCol = 2;
		sprite.setRegion(Textures.playerTexture[row][textureRegionCol]);
		sprite.translateY(moveSpeed);
		updateSpriteImage();
	}

	public void moveDown() {
		int row = sprite.getRegionY() / 16;
		textureRegionCol = 0;
		sprite.setRegion(Textures.playerTexture[row][textureRegionCol]);
		sprite.translateY(-moveSpeed);
		updateSpriteImage();
	}

	public void moveRight() {
		int row = sprite.getRegionY() / 16;
		textureRegionCol = 3;
		sprite.setRegion(Textures.playerTexture[row][textureRegionCol]);

		sprite.translateX(moveSpeed);
		updateSpriteImage();
	}

	public void moveLeft() {
		int row = sprite.getRegionY() / 16;
		textureRegionCol = 1;
		sprite.setRegion(Textures.playerTexture[row][textureRegionCol]);
		sprite.translateX(-moveSpeed);

		updateSpriteImage();
	}

	// there are two images for walking
	int animationImageCount = 2;
	int imageStartOffset = 0;

	private void updateSpriteImage() {
		float xyPos = 0;
		
		//check which dimension to use (x or y) for walking animation.
		if (textureRegionCol == 0 || textureRegionCol == 2) {
			// if sprite is walking up or down
			xyPos = sprite.getY();
		} else if (textureRegionCol == 1 || textureRegionCol == 3) {
			// if sprite is walking left or right
			xyPos = sprite.getX();
		} else {
			System.out.printf("col: %d not implemented inUpdateSpriteImage", textureRegionCol);
		}

		// below: the number of sprite images from 0
		int numSpriteSizesFromZero = (int) Math.abs(xyPos / (scaleFactor * sprite.getRegionHeight()));

		// uses the number of images to derive an index from #
		int imageRow = ((numSpriteSizesFromZero) % animationImageCount) + imageStartOffset;
		if (imageRow != textureRegionRow) {
			sprite.setRegion(Textures.playerTexture[imageRow][textureRegionCol]);
			textureRegionRow = imageRow;
		}
	}

	public float getX() {
		return sprite.getX();
	}

	public float getY() {
		return sprite.getY();
	}

	public float getScaledWidth() {
		return sprite.getWidth() * scaleFactor;
	}

	public float getScaledHeight() {
		return sprite.getHeight() * scaleFactor;
	}

	public void setPosition(float x, float y) {
		sprite.setPosition(x, y);
	}

	public float getCenterX() {
		return sprite.getX() + getScaledWidth() / 2;
	}

	public float getCenterY() {
		return sprite.getY() + getScaledHeight() / 2;
	}
	
	public void setCenterPosition(float x, float y) {
		// Sprite's coordinates are at the bottom left. Offset corrects that.
		float xOffset = sprite.getWidth() * scaleFactor / 2;
		float yOffset = sprite.getHeight() * scaleFactor / 2;
		sprite.setPosition(x - xOffset, y - yOffset);
	}
}
