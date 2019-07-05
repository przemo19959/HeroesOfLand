package application.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import application.game.Utility;
import application.maps.MapManager;

public abstract class Entity {
	private static final String TAG=Entity.class.getSimpleName();
	
	protected String entitySpritePath;
	protected Vector2 entityVelocity;
	protected Vector2 nextEntityPosition;
	protected Vector2 currentEntityPosition;
	protected float frameTime = 0f;
	private Sprite entitySprite;
	protected TextureRegion entityTextureRegion;
	public static final int FRAME_WIDTH = 16;
	public static final int FRAME_HEIGHT = 16;
	private Rectangle entityHitBox;
	
	public Entity(String entitySpritePath, Vector2 startPosition) {
		this.entitySpritePath = entitySpritePath;
		Utility.loadAssetOfGivenType(entitySpritePath, Texture.class);
		
		currentEntityPosition = new Vector2(startPosition).add(0.5f, 0.5f);
		nextEntityPosition = new Vector2(currentEntityPosition);
		
		entityHitBox = new Rectangle();
		loadDefaultSprite();
	}
	
	protected abstract void update(float delta);
	public abstract void onNoCollision(float delta);
	public void dispose() {};
	
	public Vector2 getCurrentEntityPosition() {
		return currentEntityPosition;
	}
	public Sprite getEntitySprite() {
		return entitySprite;
	}
	public TextureRegion getEntityTextureRegion() {
		return entityTextureRegion;
	}
	public Rectangle getEntityHitBox() {
		return entityHitBox;
	}
	
	protected void updateHitBoxPosition(float xOffset, float yOffset) {
		float minX;
		float minY;
		if (MapManager.UNIT_SCALE > 0) {
			minX = (nextEntityPosition.x+xOffset) / MapManager.UNIT_SCALE;
			minY = (nextEntityPosition.y+yOffset) / MapManager.UNIT_SCALE;
		}
		entityHitBox.setCenter(minX, minY);
	}
	
	protected void initHitBoxSize(float percentageWidthReduced, float percentageHeightReduced,
	                              float xOffset, float yOffset) {
		float widthReductionAmount = 1.0f - percentageWidthReduced; // .8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; // .8f for 20% (1 - .20)
		//@formatter:off
		float width = (widthReductionAmount > 0 && widthReductionAmount < 1) ? FRAME_WIDTH * widthReductionAmount: FRAME_WIDTH;
		float height = (heightReductionAmount > 0 && heightReductionAmount < 1) ? FRAME_HEIGHT * heightReductionAmount: FRAME_HEIGHT; //@formatter:on
		if(width == 0 || height == 0)
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
		updateHitBoxPosition(xOffset, yOffset);
		entityHitBox.setWidth(width);
		entityHitBox.setHeight(height);
	}
	
	protected void setCurrentPosition(Vector2 currentPosition) {
		entitySprite.setX(currentPosition.x);
		entitySprite.setY(currentPosition.y);
		currentEntityPosition.set(currentPosition);
	}
	
	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(entitySpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		entitySprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		entityTextureRegion = textureFrames[0][0];
	}
	
	public <T extends Entity> boolean isEntity(Class<T> entityType) {
		return (this.getClass().equals(entityType))?true:false;
	}
}
