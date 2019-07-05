package application.components;

import com.badlogic.gdx.math.Vector2;

import application.characters.Character.Direction;
import application.game.MainGameScreen;

public class EnemyInputComponent extends InputComponent {
//	private float frameTime;
	private float followDistance=10f;
	
	public EnemyInputComponent() {
		
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void update(float delta) {
//		followPlayer(delta, entity.getCurrentEntityDirection(), entity.getEntityState()); //tymczasowo
	}
	
	private void followPlayer(float delta, Direction direction) {
		Vector2 playerPosition=MainGameScreen.player.getCurrentEntityPosition();
		if(playerPosition.dst2(entity.getCurrentEntityPosition())<followDistance) {
			entity.calculateNextPositionToward(playerPosition.cpy(), delta);
			entity.setDirection(direction);
		}
	}
}
