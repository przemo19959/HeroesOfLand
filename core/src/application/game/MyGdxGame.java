package application.game;

import com.badlogic.gdx.Game;

public class MyGdxGame extends Game {		
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
