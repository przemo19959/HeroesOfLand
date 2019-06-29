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
	private Vector2 projectileVelocity;

	private Animation<TextureRegion> animation;

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
	
 	public Entity getCaster() {
		return caster;
	}

	private boolean projectileCollided;
	
	public boolean isProjectileCollided() {
		return projectileCollided;
	}

	public void setProjectileCollided(boolean projectileCollided) {
		this.projectileCollided = projectileCollided;
	}

	public Projectile(Entity caster,String projectileSpritePath,Vector2 startPosition, Vector2 endPosition) {
		this.projectileSpritePath = projectileSpritePath;
		this.caster=caster;
		currentProjectilePosition=new Vector2(startPosition);
		nextProjectilePosition=new Vector2(startPosition);
		finishPosition=new Vector2(endPosition);		
		projectileHitBox = new Rectangle();
		projectileVelocity = new Vector2(4f, 4f);
		
		if(!Utility.isAssetLoaded(projectileSpritePath))
			Utility.loadAssetOfGivenType(this.projectileSpritePath, Texture.class);
		
		fireDirection=new Vector2(finishPosition.sub(currentProjectilePosition).nor());
		loadDefaultSprite();
		loadAllAnimations(7);
		initHitBoxSize(0.5f, 0.5f);
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
		if (MapManager.UNIT_SCALE > 0) {
			minX = nextProjectilePosition.x / MapManager.UNIT_SCALE;
			minY = nextProjectilePosition.y / MapManager.UNIT_SCALE;
		}
		projectileHitBox.setPosition(minX,minY);
	}

	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(projectileSpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		projectileSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		projectileTextureRegion = textureFrames[0][0];
	}

	private void loadAllAnimations(int numberOfFrames) {
		Texture texture = Utility.getAssetOfGivenType(projectileSpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

		Array<TextureRegion> frames = new Array<>(numberOfFrames);

		for(int i = 0;i < numberOfFrames;i++) {
			TextureRegion region = textureFrames[0][i];
			if(region == null)
				Gdx.app.debug(TAG, "Got null animation frame " + 0 + "," + i);
			frames.add(region);
		}

		animation = new Animation<>(0.5f, frames, Animation.PlayMode.NORMAL);
	}
	
	private void initHitBoxSize(float percentageWidthReduced, float percentageHeightReduced) {
		float widthReductionAmount = 1.0f - percentageWidthReduced; // .8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; // .8f for 20% (1 - .20)
		//@formatter:off
		float width = (widthReductionAmount > 0 && widthReductionAmount < 1) ? FRAME_WIDTH * widthReductionAmount: FRAME_WIDTH;
		float height = (heightReductionAmount > 0 && heightReductionAmount < 1) ? FRAME_HEIGHT * heightReductionAmount: FRAME_HEIGHT; //@formatter:on
		if (width == 0 || height == 0)
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
		float minX;
		float minY;
		if (MapManager.UNIT_SCALE > 0) {
			minX = nextProjectilePosition.x / MapManager.UNIT_SCALE;
			minY = nextProjectilePosition.y / MapManager.UNIT_SCALE;
		}
		projectileHitBox.set(minX, minY, width, height);
	}
		
	public void calculateNextPosition(float deltaTime) {
		Vector2 tmp=new Vector2(currentProjectilePosition);
		projectileVelocity.scl(deltaTime);
		tmp.add(fireDirection.x*projectileVelocity.x, fireDirection.y*projectileVelocity.y);
		nextProjectilePosition.set(tmp);		
		projectileVelocity.scl(1 / deltaTime);
	}
	
	private void setCurrentPosition(float currentPositionX, float currentPositionY) {
		projectileSprite.setX(currentPositionX);
		projectileSprite.setY(currentPositionY);
		currentProjectilePosition.set(currentPositionX, currentPositionY);
	}
	
	public void onNoCollision() {
		setCurrentPosition(nextProjectilePosition.x, nextProjectilePosition.y);	
	}
	
	public void updateAfterCollisionTest(float deltaTime) {
		calculateNextPosition(deltaTime);
		projectileTextureRegion = animation.getKeyFrame(frameTime);
	}
	
	public void onCollision() {
		
	}
}
