package application.huds;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import application.huds.MainBarUI.Attribiute;

public class CharacterHUD implements Screen {
	public Stage stage;
	private Viewport viewport;
	private MainBarUI statusUI;
	
	public CharacterHUD(Camera camera,int maxHealthPoints, int maxManaPoints,int nextLevelPoints ) {
		viewport=new ScreenViewport(camera);
		stage=new Stage(viewport);
		
		statusUI=new MainBarUI( maxHealthPoints, maxManaPoints,nextLevelPoints );
		stage.addActor(statusUI);
	}
	
	public void setAttribiuteValue(Attribiute attribiute,int newValue) {
		statusUI.setAttribiuteValue(attribiute, newValue);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height,true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
