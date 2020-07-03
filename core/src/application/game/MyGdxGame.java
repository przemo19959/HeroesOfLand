package application.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class MyGdxGame extends Game {
	private static final String TAG = MyGdxGame.class.getSimpleName();
	
	public static final int VIEW_WIDTH=800;
	public static final int VIEW_HEIGHT=600;
	private static final MainGameScreen mainGameScreen=new MainGameScreen();
	
	@Override
	public void create () {
		setScreen(mainGameScreen);
	}
	
	@Override
	public void dispose () {
		mainGameScreen.dispose();
		Gdx.app.debug(TAG, "Screen properly disposed!");
	}
}
