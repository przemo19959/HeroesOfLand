package application.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import application.entities.Entity;
import application.entities.concrete.entities.Character;
import application.entities.concrete.entities.Character.Direction;
import application.game.Utility;
import application.maps.MapManager;

/**
 * Przyjmuje siê, ¿e tekstury s¹ skierowane pierwotnie zawsze w prawo (tak ¿e dla tego kierunku, nie jest konieczna rotacja).
 */
class AnimationEntity {
	private static final String TAG = AnimationEntity.class.getSimpleName();

	private Vector2 currentAnimationPosition;
	private Direction direction;
	private int width;
	private int height;
	private Animation<TextureRegion> animation;
	private float animationRotation;

	private float frameTime;

	AnimationEntity(String animationSpritePath, Entity caster, int width, int height, int numberOfFrames, boolean noRotation) {
		Utility.loadAssetOfGivenType(animationSpritePath, Texture.class);
		currentAnimationPosition = new Vector2(caster.getCurrentEntityPosition());

		if(caster instanceof Character) {
			direction = ((Character) caster).getCurrentEntityDirection();
			setPositionAccordingToDirection(caster.getCurrentEntityPosition(), noRotation);
		} else {
			direction = Direction.RIGHT;
			currentAnimationPosition.set(caster.getCurrentEntityPosition().sub(1f, 1f));
		}

		this.width = width;
		this.height = height;

		animation = Utility.loadAndGetAnimations(animationSpritePath, 1, numberOfFrames, 0.05f, PlayMode.NORMAL).get(0);
		if(noRotation == false)
			animationRotation = getRotationForGivenDirection(direction);
	}

	private void setPositionAccordingToDirection(Vector2 casterPosition, boolean noRotation) {
		if(noRotation) {
			switch (direction) {//@formatter:off
				case RIGHT :currentAnimationPosition.set(casterPosition.add(0.5f,-0.5f));break;
				case UP: currentAnimationPosition.set(casterPosition.add(-0.5f, 0.5f));break;
				case LEFT:currentAnimationPosition.set(casterPosition.add(-1.5f,-0.5f));break;
				case DOWN:currentAnimationPosition.set(casterPosition.add(-0.5f,-1.5f));break;
			}//@formatter:on
		} else {
			switch (direction) {//@formatter:off
				case RIGHT: currentAnimationPosition.set(casterPosition.add(0.5f, -0.5f));break;
				case UP: currentAnimationPosition.set(casterPosition.add(0.5f, 0.5f));break;
				case LEFT:currentAnimationPosition.set(casterPosition.add(-0.5f,0.5f));break;
				case DOWN:currentAnimationPosition.set(casterPosition.add(-0.5f,-0.5f));break;
			}//@formatter:on
		}
	}

	boolean update(Batch batch, float delta) {
		frameTime = (frameTime + delta) % 5;
		batch.draw(animation.getKeyFrame(frameTime), currentAnimationPosition.x, currentAnimationPosition.y, //
			0, 0, width * MapManager.UNIT_SCALE, height * MapManager.UNIT_SCALE, 1, 1, animationRotation);
		return animation.isAnimationFinished(frameTime);
	}

	private float getRotationForGivenDirection(Direction direction) {
		switch (direction) {//@formatter:off
			case UP : return 90f;
			case LEFT : return 180f;
			case DOWN : return 270f;
			case RIGHT :
			default : return 0f;
		}//@formatter:on
	}
}
