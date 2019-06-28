package application.components;

import com.badlogic.gdx.math.MathUtils;

import application.entity.Entity.Direction;
import application.entity.Entity.State;

public class EnemyInputComponent extends InputComponent {
	private float frameTime;

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
		frameTime+=delta;
		if(frameTime > MathUtils.random(1, 5)) {
			entity.setCurrentEntityDirection(Direction.getRandomDirection());
			entity.setState(State.getRandomState());
			frameTime = 0.0f;
		}
		moveEntity(delta, entity.getCurrentEntityDirection(), entity.getEntityState());
	}

}
