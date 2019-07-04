package application.huds;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CharacterHUD implements Screen {
	public Stage stage;
	private Viewport viewport;
	private MainBarUI statusUI;
	
	public CharacterHUD(Camera camera) {
		viewport=new ScreenViewport(camera);
		stage=new Stage(viewport);
		
		statusUI=new MainBarUI();
		stage.addActor(statusUI);
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
