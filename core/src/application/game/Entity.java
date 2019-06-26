package application.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Entity {
	private static final String TAG = Entity.class.getSimpleName();
	private static final String defaultSpritePath = "sprites/characters/Warrior.png";
	private String characterSpritePath;

	private Vector2 velocity;

	private Direction currentDirection = Direction.LEFT;

	private Animation<TextureRegion> walkLeftAnimation;
	private Animation<TextureRegion> walkRightAnimation;
	private Animation<TextureRegion> walkUpAnimation;
	private Animation<TextureRegion> walkDownAnimation;

	private Array<TextureRegion> walkLeftFrames;
	private Array<TextureRegion> walkRightFrames;
	private Array<TextureRegion> walkUpFrames;
	private Array<TextureRegion> walkDownFrames;

	private Vector2 nextPlayerPosition;
	private Vector2 currentPlayerPosition;
	private State state = State.IDLE;
	private float frameTime = 0f;
	private Sprite frameSprite = null;
	private TextureRegion currentFrame = null;

	public final int FRAME_WIDTH = 16;
	public final int FRAME_HEIGHT = 16;
	public Rectangle boundingBox;
	
	public Vector2 getNextPlayerPosition() {
		return nextPlayerPosition;
	}

	public enum State {
		IDLE, WALKING
	}

	public enum Direction {
		UP, RIGHT, DOWN, LEFT;
	}

	public Entity(String characterSpritePath) {
		this.characterSpritePath = characterSpritePath;
		initEntity();
	}

	private void initEntity() {
		nextPlayerPosition = new Vector2();
		currentPlayerPosition = new Vector2();
		boundingBox = new Rectangle();
		velocity = new Vector2(2f, 2f);

		Utility.loadAssetOfGivenType(getSpritePath(), Texture.class);
		loadDefaultSprite();
		loadAllAnimations();
		initBoundingBoxSize(0, 0.5f);
	}

	/**
	 * Ta metoda aktualizuje czas ramki, tak aby odtwarzany by³ poprawny region tekstury encji. Nastêpnie aktualizowany
	 * jest hitbox encji (aktualizacja odbywa siê na podstawie zmiennych nastêpnych pozycji postaci).
	 * @param delta - czas delta klatki
	 */
	public void update(float delta) {
		frameTime = (frameTime + delta) % 5;
		updateBoundingBox();
	}

	public void init(float startX, float startY) {
		currentPlayerPosition.x = startX;
		currentPlayerPosition.y = startY;

		nextPlayerPosition.x = startX;
		nextPlayerPosition.y = startY;
	}
	
	public void init(Vector2 position) {
		currentPlayerPosition.x = position.x;
		currentPlayerPosition.y = position.y;

		nextPlayerPosition.x = position.x;
		nextPlayerPosition.y = position.y;
	}
	
	private void initBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced) {
		float widthReductionAmount = 1.0f - percentageWidthReduced; // .8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; // .8f for 20% (1 - .20)

		float width=(widthReductionAmount > 0 && widthReductionAmount < 1)?FRAME_WIDTH * widthReductionAmount:FRAME_WIDTH;
		float height=(heightReductionAmount > 0 && heightReductionAmount < 1)?FRAME_HEIGHT * heightReductionAmount:FRAME_HEIGHT;
		if (width == 0 || height == 0)
			Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
		float minX;
		float minY;
		if (MapManager.UNIT_SCALE > 0) {
			minX = nextPlayerPosition.x / MapManager.UNIT_SCALE;
			minY = nextPlayerPosition.y / MapManager.UNIT_SCALE;
		}
		boundingBox.set(minX, minY, width, height);
	}

	private void updateBoundingBox() {	
		float minX;
		float minY;
		if (MapManager.UNIT_SCALE > 0) {
			minX = nextPlayerPosition.x / MapManager.UNIT_SCALE;
			minY = nextPlayerPosition.y / MapManager.UNIT_SCALE;
		}
		boundingBox.setPosition(minX, minY);
	}
	
	private String getSpritePath() {
		return (characterSpritePath==null || characterSpritePath.equals(""))?defaultSpritePath:characterSpritePath;
	}

	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(getSpritePath(), Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		currentFrame = textureFrames[0][0];
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
		Utility.unloadAsset(defaultSpritePath);
	}

	public void setState(State state) {
		this.state = state;
	}

	public Sprite getFrameSprite() {
		return frameSprite;
	}

	public TextureRegion getFrame() {
		return currentFrame;
	}

	public Vector2 getCurrentPosition() {
		return currentPlayerPosition;
	}

	private void setCurrentPosition(float currentPositionX, float currentPositionY) {
		frameSprite.setX(currentPositionX);
		frameSprite.setY(currentPositionY);
		this.currentPlayerPosition.x = currentPositionX;
		this.currentPlayerPosition.y = currentPositionY;
	}

	public void setDirection(Direction direction, float deltaTime) {
		this.currentDirection = direction;
		switch (currentDirection) { //@formatter:off
			case DOWN:currentFrame = walkDownAnimation.getKeyFrame(frameTime);break;
			case LEFT:currentFrame = walkLeftAnimation.getKeyFrame(frameTime);break;
			case UP:currentFrame = walkUpAnimation.getKeyFrame(frameTime);break;
			case RIGHT:currentFrame = walkRightAnimation.getKeyFrame(frameTime);break;
		}//@formatter:on
	}
	
	public void setNextPositionToCurrent() {
		setCurrentPosition(nextPlayerPosition.x, nextPlayerPosition.y);
	}
	
	public void calculateNextPosition(Direction currentDirection, float deltaTime) {
		float testX = currentPlayerPosition.x;
		float testY = currentPlayerPosition.y;
		velocity.scl(deltaTime);
		switch (currentDirection) { //@formatter:off
			case LEFT:testX -= velocity.x;break;
			case RIGHT:testX += velocity.x;break;
			case UP:testY += velocity.y;break;
			case DOWN:testY -= velocity.y;break;
		}//@formatter:on
		nextPlayerPosition.x = testX;
		nextPlayerPosition.y = testY;
		velocity.scl(1 / deltaTime);
	}
}
