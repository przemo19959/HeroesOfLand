package application.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entity.Entity;
import application.game.Utility;
import application.characters.Character;

public class Projectile extends Entity {
	private static final String TAG = Projectile.class.getSimpleName();

	private Animation<TextureRegion> moveAnimation;
	private Vector2 fireDirection;
	private Vector2 finishPosition;
	private final Character caster;

	Projectile(Character caster, String entitySpritePath, Vector2 startPosition, Vector2 endPosition) {
		super(entitySpritePath, startPosition);
		entityVelocity = new Vector2(4f, 4f);
		
		this.caster = caster;		
		finishPosition = new Vector2(endPosition).add(0.5f, 0.5f);
		
		fireDirection = new Vector2(finishPosition.sub(currentEntityPosition).nor());
		moveAnimation=loadAnimation(entitySpritePath,5);
		initHitBoxSize(0.5f, 0.5f,-0.5f, -0.5f);
	}
	
	public void update(float delta) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition(-0.5f,-0.5f);
	}
	
	public Character getCaster() {
		return caster;
	}
	
	public float getRotationAngle() {
		return fireDirection.angle();
	}
	
	private Animation<TextureRegion> loadAnimation(String spritePath, int numberOfFrames) {
		Texture texture = Utility.getAssetOfGivenType(spritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		Array<TextureRegion> frames = new Array<>(numberOfFrames);

		for(int i = 0;i < numberOfFrames;i++) {
			TextureRegion region = textureFrames[0][i];
			if(region == null)
				Gdx.app.debug(TAG, "Got null animation frame " + 0 + "," + i);
			frames.add(region);
		}
		return new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
	}

	private void calculateNextPosition(float deltaTime) {
		Vector2 tmp = new Vector2(currentEntityPosition);
		entityVelocity.scl(deltaTime);
		tmp.add(fireDirection.x * entityVelocity.x, fireDirection.y * entityVelocity.y);
		nextEntityPosition.set(tmp);
		entityVelocity.scl(1 / deltaTime);
	}

	public void onNoCollision(float deltaTime) {
		setCurrentPosition(nextEntityPosition);
		calculateNextPosition(deltaTime);
		entityTextureRegion = moveAnimation.getKeyFrame(frameTime);
	}
}
