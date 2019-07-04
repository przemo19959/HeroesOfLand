package application.game;

import com.badlogic.gdx.Game;

public class MyGdxGame extends Game {
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
	}	
}
