package enigma.engine.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import enigma.engine.Game;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration((int) (1920 * 0.75), (int) (1080 * 0.75));
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new Game();
	}
}