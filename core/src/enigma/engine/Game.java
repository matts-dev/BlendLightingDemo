package enigma.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

/**
 * A minimal example of how to use shaders to simulate lights in a 2d game. These classes are the
 * result of the following tutorial. Note: The tutorial wasn't followed exactly.
 * http://www.alcove-games.com/opengl-es-2-tutorials/lightmap-shader-fire-effect-glsl/
 * 
 * Various assets are used from the linked tutorial, which were open source when obtained;
 * information can be found at the URL above.
 * 
 * @author Matt Stone
 * @version 1.0
 *
 */
public class Game extends ApplicationAdapter {
	private SpriteBatch batch;
	private CenteredSprite player;
	private CenteredSprite npc;

	private SimpleLight playerLight;
	private SimpleLight npcLight;
	private SimpleLight mouseLight;

	private OrthographicCamera camera;

	// lighting and shader stuff.
	private FrameBuffer frameBufferObj;
	public static float ambientIntensity = 0.5f;
	public static final Vector3 ambientColor = new Vector3(0.3f, 0.3f, 0.7f);
	private ShaderProgram defaultShader;
	private ShaderProgram finalShader;
	private String vertexShader;
	private String defaultPixelShader;
	private String finalPixelShader;

	// Lighting show case variables.
	private boolean drawLighting = false;
	private boolean drawWithShaders = true;
	private boolean defaultShaderUsedAtLeastOnce = false;

	// Vector used to convert mouse coordinates to game coordinates.
	private Vector3 conversionVector = new Vector3();

	@Override
	public void create() {
		initShaders();
		initCamera();

		// Create the frame buffer to draw light textures upon.
		frameBufferObj = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		// Create the batch for drawing
		batch = new SpriteBatch();

		// Initialize textures within the create() method
		Textures.initTextures();

		// create an actor to control
		player = new CenteredSprite(Textures.playerTexture, 0, 0);
		player.setScale(4.0f);

		npc = new CenteredSprite(Textures.playerTexture, 0, 0);
		npc.setScale(4.0f);
		npc.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

		playerLight = new SimpleLight(0, 0);
		npcLight = new SimpleLight(0, 0);
		mouseLight = new SimpleLight(0, 0);

		// Debug
		DebugGlobalLookup.init();
	}

	private void initCamera() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	}

	private void initShaders() {
		vertexShader = Gdx.files.internal("shaders/vertexShader.glsl").readString();
		defaultPixelShader = Gdx.files.internal("shaders/defaultPixelShader.glsl").readString();
		finalPixelShader = Gdx.files.internal("shaders/pixelShader.glsl").readString();

		ShaderProgram.pedantic = false;
		defaultShader = new ShaderProgram(vertexShader, defaultPixelShader);
		finalShader = new ShaderProgram(vertexShader, finalPixelShader);

		// set up the final shader
		finalShader.begin();
		finalShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		finalShader.setUniformi("u_lightmap", 1);
		finalShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);
		finalShader.end();
	}

	@Override
	public void render() {
		io();
		updateCamera();
		logic();

		if (drawLighting) {
			if (drawWithShaders) {
				defaultShaderUsedAtLeastOnce = true;
				renderWithShaderBasedLights();
			} else {
				renderWithoutShaderBasedLights();
			}
		} else {
			renderNormal();
		}
	}

	private void renderWithShaderBasedLights() {
		// update camera before drawing starts.
		batch.setProjectionMatrix(camera.combined);

		// --------------------------- DRAW LIGHTS --------------------------- // @formatter:off
		batch.setShader(defaultShader); 
		
		//start the frame buffer - this is what is blended into the actual screen image.
		frameBufferObj.begin();
		
			// Clear last frameBuffer Screen after setting shader
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
			// set blending so that lights bend together
			int srcFunction = batch.getBlendSrcFunc();
			int dstFunction = batch.getBlendDstFunc();
			batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_SRC_ALPHA);
			batch.begin();
				// draw light on actor
				playerLight.setCenterPosition(player.getCenterX(), player.getCenterY());
				npcLight.setCenterPosition(npc.getCenterX(), npc.getCenterY());
				
				//All lights are maintained in a common collections to make drawing them at once easier
				SimpleLight.renderAllLights(batch, player.getCenterX(), player.getCenterY());
			batch.end();
		frameBufferObj.end();

		// restore blending (for lighting) before drawing normal textures
		batch.setBlendFunction(srcFunction, dstFunction);

		// --------------------------- DRAW THE NORMAL WORLD ---------------------------//
		batch.setShader(finalShader);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
			// "bind the frame buffer to the second texture unit" [ie 0 = 1st, 1 = 2nd]
			frameBufferObj.getColorBufferTexture().bind(1); // look in final shader (pixelShader)
	
			// Force binding on first texture unit 
			Textures.light.bind(0); // author says we can bind anything
	
			batch.draw(Textures.grass, 0, 0, 0, 0, 23 * Textures.grass.getWidth(), 16 * Textures.grass.getHeight());
			npc.draw(batch);
			player.draw(batch);
		batch.end();
		
		//@formatter:on
	}

	private void renderWithoutShaderBasedLights() {
		// This implementation is based off a comment in the tutorial - haven't got it working yet.
		System.out.println("Render Lights Without Shader Doesn't Work Yet!");
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// if the default shader has been used, then original set up has been lost.
		if (defaultShaderUsedAtLeastOnce) {
			batch.setShader(defaultShader);
		}

		// DRAW WORLD
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(Textures.grass, 0, 0, 0, 0, 23 * Textures.grass.getWidth(), 16 * Textures.grass.getHeight());
		npc.draw(batch);
		player.draw(batch);
		batch.end();

		// DRAW LIGHTS BLENDED
		batch.begin();
		batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA);
		SimpleLight.renderAllLights(batch, player.getCenterX(), player.getCenterY());
		DebugGlobalLookup.blueSprite.draw(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.end();
	}

	private void renderNormal() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		if (defaultShaderUsedAtLeastOnce) {
			batch.setShader(defaultShader);
		}

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.draw(Textures.grass, 0, 0, 0, 0, 23 * Textures.grass.getWidth(), 16 * Textures.grass.getHeight());
		npc.draw(batch);
		player.draw(batch);
		batch.end();
	}

	public void logic() {
		float screenX = Gdx.input.getX();
		float screenY = Gdx.input.getY();
		camera.unproject(conversionVector.set(screenX, screenY, 0));

		mouseLight.setCenterPosition(conversionVector.x, conversionVector.y);
	}

	public void io() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		// currently these do nothing.
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			ambientIntensity += 0.5f;
			updateShaders();
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			ambientIntensity -= 0.5f;
			updateShaders();
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			playerLight.setScaleFactor(playerLight.getScaleFactor() + 0.25f);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			playerLight.setScaleFactor(playerLight.getScaleFactor() - 0.25f);

		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
			this.drawLighting = !this.drawLighting;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)) {
			System.out.println("Toggling between non-shader/shader mode.");
			this.drawWithShaders = !this.drawWithShaders;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			mouseLight.setOnOff(!mouseLight.getOnOff());
		}

		// movement
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			player.moveUp();
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			player.moveDown();
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.moveLeft();
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.moveRight();
		}

	}

	public void updateShaders() {
		if (drawLighting && drawWithShaders) {
			finalShader.begin();
			finalShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			finalShader.setUniformi("u_lightmap", 1); // this is tied to binding in render
			finalShader.setUniformf("ambientColor", ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);
			finalShader.end();
		}
	}

	public void updateCamera() {
		if (player != null) {
			camera.position.set(player.getCenterX(), player.getCenterY(), 0);
			camera.update();
		}
	}

	@Override
	public void resize(int width, int height) {
		updateShaders();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (player != null) {
			updateCamera();
		}

	}

	@Override
	public void dispose() {
		batch.dispose();
		Textures.dispose();
	}
}
