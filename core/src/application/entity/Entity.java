package application.entity;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.components.InputComponent;
import application.components.PlayerInputComponent;
import application.game.Utility;
import application.maps.MapManager;

public class Entity {
	private static final String TAG = Entity.class.getSimpleName();
	private static final String defaultSpritePath = "sprites/characters/Warrior.png";
	private String entitySpritePath;

	private Vector2 entityVelocity;

	private Direction currentEntityDirection = Direction.LEFT;

	private Animation<TextureRegion> walkLeftAnimation;
	private Animation<TextureRegion> walkRightAnimation;
	private Animation<TextureRegion> walkUpAnimation;
	private Animation<TextureRegion> walkDownAnimation;

	private Array<TextureRegion> walkLeftFrames;
	private Array<TextureRegion> walkRightFrames;
	private Array<TextureRegion> walkUpFrames;
	private Array<TextureRegion> walkDownFrames;

	private Vector2 nextEntityPosition;
	private Vector2 currentEntityPosition;
	private State entityState = State.IDLE;
	private float frameTime = 0f;
	private Sprite entitySprite = null;
	private TextureRegion entityTextureRegion = null;

	public final int FRAME_WIDTH = 16;
	public final int FRAME_HEIGHT = 16;
	public Rectangle entityHitBox;

	public final static Comparator<Entity> yComparator = ((entity1, entity2) -> Float
			.compare(entity1.currentEntityPosition.y, entity2.currentEntityPosition.y));
	private InputComponent inputComponent;

	public Vector2 getNextPlayerPosition() {
		return nextEntityPosition;
	}

	public void setCurrentEntityDirection(Direction currentEntityDirection) {
		this.currentEntityDirection = currentEntityDirection;
	}

	public void setEntityState(State entityState) {
		this.entityState = entityState;
	}

	public Direction getCurrentEntityDirection() {
		return currentEntityDirection;
	}

	public State getEntityState() {
		return entityState;
	}

	public enum State {
		IDLE, WALKING;

		public static State getRandomState() {
			return State.values()[MathUtils.random(State.values().length - 1)];
		}
	}

	public enum Direction {
		UP, RIGHT, DOWN, LEFT;

		public static Direction getRandomDirection() {
			return Direction.values()[MathUtils.random(Direction.values().length - 1)];
		}
	}
	
	private boolean up,right,down,left;
	
	public void setDirectionFlag(Direction direction, boolean value) {
		switch(direction) {//@formatter:off
			case UP: up=value;break;
			case RIGHT: right=value;break;
			case DOWN: down=value;break;
			case LEFT: left=value;break;
		}//@formatter:on
	}
	
	public void clearAllDirectionFlags() {
		up=down=right=left=false;
	}

	public Entity(String entitySpritePath, InputComponent inputComponent) {
		this.entitySpritePath = entitySpritePath;
		this.inputComponent = inputComponent;
		this.inputComponent.setEntity(this);
		if (inputComponent instanceof PlayerInputComponent)
			Gdx.input.setInputProcessor(inputComponent);
		initEntity();
	}
	
	private void initEntity() {
		nextEntityPosition = new Vector2();
		currentEntityPosition = new Vector2();
		entityHitBox = new Rectangle();
		entityVelocity = new Vector2(2f, 2f);

		Utility.loadAssetOfGivenType(getSpritePath(), Texture.class);
		loadDefaultSprite();
		loadAllAnimations();
		initHitBoxSize(0.45f, 0.55f);
	}

	/**
	 * Ta metoda aktualizuje czas ramki, tak aby odtwarzany by³ poprawny region
	 * tekstury encji. Nastêpnie aktualizowany jest hitbox encji (aktualizacja
	 * odbywa siê na podstawie zmiennych nastêpnych pozycji postaci).
	 * 
	 * @param delta
	 *            - czas delta klatki
	 */
	public void update(float delta) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition();
	}

	public void updateInputComponent(float delta) {
		if (inputComponent != null)
			inputComponent.update(delta);
	}

	public void init(float startX, float startY) {
		currentEntityPosition.set(startX, startY);
		nextEntityPosition.set(startX,startY);
	}

	public void init(Vector2 position, boolean scaled) {
		currentEntityPosition.x = (scaled) ? position.x * MapManager.UNIT_SCALE : position.x;
		currentEntityPosition.y = (scaled) ? position.y * MapManager.UNIT_SCALE : position.y;
		nextEntityPosition.set(currentEntityPosition);
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
			minX = nextEntityPosition.x / MapManager.UNIT_SCALE;
			minY = nextEntityPosition.y / MapManager.UNIT_SCALE;
		}
		entityHitBox.set(minX+width/2, minY, width, height);
	}

	private void updateHitBoxPosition() {
		float minX;
		float minY;
		if (MapManager.UNIT_SCALE > 0) {
			minX = nextEntityPosition.x / MapManager.UNIT_SCALE;
			minY = nextEntityPosition.y / MapManager.UNIT_SCALE;
		}
		entityHitBox.setPosition(minX+entityHitBox.width/2, minY);
	}

	private String getSpritePath() {
		return (entitySpritePath == null || entitySpritePath.equals("")) ? defaultSpritePath : entitySpritePath;
	}

	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(getSpritePath(), Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		entitySprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		entityTextureRegion = textureFrames[0][0];
	}

	private void loadAllAnimations() {
		Texture texture = Utility.getAssetOfGivenType(getSpritePath(), Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

		walkDownFrames = new Array<TextureRegion>(4);
		walkLeftFrames = new Array<TextureRegion>(4);
		walkRightFrames = new Array<TextureRegion>(4);
		walkUpFrames = new Array<TextureRegion>(4);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				TextureRegion region = textureFrames[i][j];
				if (region == null)
					Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
				switch (i) {//@formatter:off
					case 0:walkDownFrames.insert(j, region);break;
					case 1:walkLeftFrames.insert(j, region);break;
					case 2:walkRightFrames.insert(j, region);break;
					case 3:walkUpFrames.insert(j, region);break;
				}//@formatter:on
			}
		}

		walkDownAnimation = new Animation<>(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
		walkLeftAnimation = new Animation<>(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
		walkRightAnimation = new Animation<>(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
		walkUpAnimation = new Animation<>(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
	}

	public void dispose() {
		if (Utility.isAssetLoaded(getSpritePath()))
			Utility.unloadAsset(getSpritePath());
	}

	public void setState(State state) {
		this.entityState = state;
	}

	public Sprite getFrameSprite() {
		return entitySprite;
	}

	public TextureRegion getFrame() {
		return entityTextureRegion;
	}

	public Vector2 getCurrentPosition() {
		return currentEntityPosition;
	}

	private void setCurrentPosition(float currentPositionX, float currentPositionY) {
		entitySprite.setX(currentPositionX);
		entitySprite.setY(currentPositionY);
		this.currentEntityPosition.x = currentPositionX;
		this.currentEntityPosition.y = currentPositionY;
	}

	public void setDirection(Direction direction) {
		this.currentEntityDirection = direction;
		switch (currentEntityDirection) { //@formatter:off
			case DOWN:entityTextureRegion = walkDownAnimation.getKeyFrame(frameTime);break;
			case LEFT:entityTextureRegion = walkLeftAnimation.getKeyFrame(frameTime);break;
			case UP:entityTextureRegion = walkUpAnimation.getKeyFrame(frameTime);break;
			case RIGHT:entityTextureRegion = walkRightAnimation.getKeyFrame(frameTime);break;
		}//@formatter:on
	}

	public void setNextPositionToCurrent() {
		setCurrentPosition(nextEntityPosition.x, nextEntityPosition.y);
	}
	
	public void calculateNextPosition(float deltaTime) {
		float testX = currentEntityPosition.x;
		float testY = currentEntityPosition.y;
		entityVelocity.scl(deltaTime);
		
		//@formatter:off
		if (left) testX -= entityVelocity.x;
		if (right) testX += entityVelocity.x;
		if (up) testY += entityVelocity.y;
		if (down) testY -= entityVelocity.y;
		//@formatter:on
		
		nextEntityPosition.x = testX;
		nextEntityPosition.y = testY;
		entityVelocity.scl(1 / deltaTime);
	}
	
	public void calculateNextPositionToward(Vector2 endPosition, float deltaTime) {
		float testX = currentEntityPosition.x;
		float testY = currentEntityPosition.y;
		entityVelocity.scl(deltaTime);
		
		Vector2 direction=endPosition.sub(currentEntityPosition).nor();
		testX+=direction.x*entityVelocity.x;
		testY+=direction.y*entityVelocity.y;
		
		nextEntityPosition.x = testX;
		nextEntityPosition.y = testY;
		entityVelocity.scl(1 / deltaTime);
	}	
}
