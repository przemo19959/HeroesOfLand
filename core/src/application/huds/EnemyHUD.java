package application.huds;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import application.entities.concrete.entities.Character;

public class EnemyHUD implements Screen{
	private final Stage stage;
	private final Viewport viewport;
	private final EnemyUI enemyUI;
	
	public EnemyHUD(Camera camera) {
		viewport=new ScreenViewport(camera);
		stage=new Stage(viewport);
		
		enemyUI=new EnemyUI();
		stage.addActor(enemyUI);
	}
	
	public void setValues(Character enemy) {
		enemyUI.setEnemyValues(enemy);
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
