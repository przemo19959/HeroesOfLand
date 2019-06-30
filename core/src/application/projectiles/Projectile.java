package application.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entity.Entity;
import application.game.Utility;
import application.maps.MapManager;

public class Projectile {
	private static final String TAG = Projectile.class.getSimpleName();

	private String projectileSpritePath;
	private String explosionSpritePath;
	private Vector2 projectileVelocity;

	private Animation<TextureRegion> moveAnimation;
	private Animation<TextureRegion> explosionAnimation;

	private Vector2 nextProjectilePosition;
	private Vector2 currentProjectilePosition;

	private float frameTime = 0f;
	private Sprite projectileSprite = null;
	private TextureRegion projectileTextureRegion = null;

	public final int FRAME_WIDTH = 16;
	public final int FRAME_HEIGHT = 16;
	public Rectangle projectileHitBox;

	private Vector2 fireDirection;
	private Vector2 finishPosition;
	private final Entity caster;
	
	private boolean zeroTime=true;

	public Entity getCaster() {
		return caster;
	}
	
	public float getRotationAngle() {
		return fireDirection.angle();
	}

	public Projectile setProjectileSpritePath(String projectileSpritePath) {
		this.projectileSpritePath = projectileSpritePath;
		if(!Utility.isAssetLoaded(projectileSpritePath))
			Utility.loadAssetOfGivenType(this.projectileSpritePath, Texture.class);
		return this;
	}
	
	public Projectile setExplosionSpritePath(String explosionSpritePath) {
		this.explosionSpritePath = explosionSpritePath;
		if(!Utility.isAssetLoaded(this.explosionSpritePath))
			Utility.loadAssetOfGivenType(this.explosionSpritePath, Texture.class);
		return this;
	}
	
	public Projectile setStartPosition(Vector2 startPosition) {
		currentProjectilePosition = new Vector2(startPosition);
		nextProjectilePosition = new Vector2(currentProjectilePosition);
		return this;
	}

	public Projectile setEndPosition(Vector2 endPosition) {
		//Dziêki odjêciu, œrodek pocisku trafia centralnie w kursor myszy
		finishPosition = new Vector2(endPosition.sub(8*MapManager.UNIT_SCALE, 8*MapManager.UNIT_SCALE));
		return this;
	}

	public Projectile(Entity caster) {
		this.caster = caster;
		projectileHitBox = new Rectangle();
		projectileVelocity = new Vector2(4f, 4f);
	}
	
	/**
	 * Zawsze musi byæ wywo³ana na koniec ³añcucha buildera
	 */
	public Projectile build() {
		fireDirection = new Vector2(finishPosition.sub(currentProjectilePosition).nor());
		loadDefaultSprite();
		moveAnimation=loadAnimation(projectileSpritePath,5);
		explosionAnimation=loadAnimation(explosionSpritePath, 7);
		initHitBoxSize(0.5f, 0.5f);
		return this;
	}

	public Sprite getProjectileSprite() {
		return projectileSprite;
	}

	public TextureRegion getProjectileTextureRegion() {
		return projectileTextureRegion;
	}

	public Rectangle getProjectileHitBox() {
		return projectileHitBox;
	}

	public void update(float delta) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition();
	}

	private void updateHitBoxPosition() {
		float minX;
		float minY;
		if(MapManager.UNIT_SCALE > 0) {
			minX = nextProjectilePosition.x / MapManager.UNIT_SCALE;
			minY = nextProjectilePosition.y / MapManager.UNIT_SCALE;
		}
		projectileHitBox.setPosition(minX+projectileHitBox.width/2, minY+projectileHitBox.height/2);
	}

	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(projectileSpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		projectileSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		projectileTextureRegion = textureFrames[0][0];
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

	private void initHitBoxSize(float percentageWidthReduced, float percentageHeightReduced) {
		float widthReductionAmount = 1.0f - percentageWidthReduced; // .8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; // .8f for 20% (1 - .20)
		//@formatter:off
		float width = (widthReductionAmount > 0 && widthReductionAmount < 1) ? FRAME_WIDTH * widthReductionAmount: FRAME_WIDTH;
		float height = (heightReductionAmount > 0 && heightReductionAmount < 1) ? FRAME_HEIGHT * heightReductionAmount: FRAME_HEIGHT; //@formatter:on
		if(width == 0 || height == 0)
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
		float minX;
		float minY;
		if(MapManager.UNIT_SCALE > 0) {
			minX = nextProjectilePosition.x / MapManager.UNIT_SCALE;
			minY = nextProjectilePosition.y / MapManager.UNIT_SCALE;
		}
		projectileHitBox.set(minX+width/2, minY+height/2, width, height);
	}

	public void calculateNextPosition(float deltaTime) {
		Vector2 tmp = new Vector2(currentProjectilePosition);
		projectileVelocity.scl(deltaTime);
		tmp.add(fireDirection.x * projectileVelocity.x, fireDirection.y * projectileVelocity.y);
		nextProjectilePosition.set(tmp);
		projectileVelocity.scl(1 / deltaTime);
	}

	private void setCurrentPosition(float currentPositionX, float currentPositionY) {
		projectileSprite.setX(currentPositionX);
		projectileSprite.setY(currentPositionY);
		currentProjectilePosition.set(currentPositionX, currentPositionY);
	}

	public void onNoCollision(float deltaTime) {
		setCurrentPosition(nextProjectilePosition.x, nextProjectilePosition.y);
		calculateNextPosition(deltaTime);
		projectileTextureRegion = moveAnimation.getKeyFrame(frameTime);
	}

//	public void updateAfterCollisionTest(float deltaTime) {
//		calculateNextPosition(deltaTime);
//		projectileTextureRegion = moveAnimation.getKeyFrame(frameTime);
//	}

	public boolean onCollision() {
		if(zeroTime) {
			frameTime=0f;
			zeroTime=false;
		}
		projectileTextureRegion=explosionAnimation.getKeyFrame(frameTime);
		return explosionAnimation.isAnimationFinished(frameTime);
	}
}
