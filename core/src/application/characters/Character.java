package application.characters;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.components.InputComponent;
import application.components.PlayerInputComponent;
import application.entity.Entity;
import application.game.Utility;
import application.maps.MapManager;

public class Character extends Entity {
	private static final String TAG = Character.class.getSimpleName();

	private Direction currentEntityDirection = Direction.LEFT;
	private Animation<TextureRegion> walkLeftAnimation;
	private Animation<TextureRegion> walkRightAnimation;
	private Animation<TextureRegion> walkUpAnimation;
	private Animation<TextureRegion> walkDownAnimation;

	private Array<TextureRegion> walkLeftFrames;
	private Array<TextureRegion> walkRightFrames;
	private Array<TextureRegion> walkUpFrames;
	private Array<TextureRegion> walkDownFrames;
	private InputComponent inputComponent;
	public final static Comparator<Character> yComparator = ((entity1, entity2) -> Float.compare(entity1.currentEntityPosition.y, entity2.currentEntityPosition.y));
	private Vector2 moveDirection;
	
	private int maxHealthPoints;
	private int currentHealthPoints;

	public Vector2 getNextPlayerPosition() {
		return nextEntityPosition;
	}

	public enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}

	Character(String entitySpritePath, Vector2 startPosition, InputComponent inputComponent, int healthPoints) {
		super(entitySpritePath, startPosition);
		maxHealthPoints=currentHealthPoints=healthPoints;
		entityVelocity = new Vector2(2f, 2f);
		loadAllAnimations();
		initHitBoxSize(0.1f, 0.1f, 0f, 0f);

		this.inputComponent = inputComponent;
		this.inputComponent.setEntity(this);
		if(inputComponent instanceof PlayerInputComponent)
			Gdx.input.setInputProcessor(inputComponent);
		moveDirection = new Vector2();
	}
	
	public void addHelthPoints(int healthPoints) {
		currentHealthPoints+=healthPoints;
	}
	
	public int getHealthPoints() {
		return currentHealthPoints;
	}
	
	public int getMaxHealthPoints() {
		return maxHealthPoints;
	}

	public InputComponent getInputComponent() {
		return inputComponent;
	}
	
	public Direction getCurrentEntityDirection() {
		return currentEntityDirection;
	}

	public Character stopMovingAndAttack() {
		if(inputComponent instanceof PlayerInputComponent)
			return ((PlayerInputComponent) inputComponent).stopMovingAndAttack();
		return null;
	}

	/**
	 * Ta metoda aktualizuje czas ramki, tak aby odtwarzany by³ poprawny region tekstury encji. Nastêpnie aktualizowany jest hitbox encji (aktualizacja odbywa siê na podstawie zmiennych nastêpnych
	 * pozycji postaci).
	 * 
	 * @param delta - czas delta klatki
	 */
	public void update(float delta) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition(0f, 0f);
	}

	void init(Vector2 position, boolean scaled) {
		currentEntityPosition.x = (scaled) ? position.x * MapManager.UNIT_SCALE : position.x;
		currentEntityPosition.y = (scaled) ? position.y * MapManager.UNIT_SCALE : position.y;
		currentEntityPosition.add(0.5f, 0.5f);
		nextEntityPosition.set(currentEntityPosition);
	}

	private void loadAllAnimations() {
		Texture texture = Utility.getAssetOfGivenType(entitySpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

		walkDownFrames = new Array<TextureRegion>(4);
		walkLeftFrames = new Array<TextureRegion>(4);
		walkRightFrames = new Array<TextureRegion>(4);
		walkUpFrames = new Array<TextureRegion>(4);

		for(int i = 0;i < 4;i++) {
			for(int j = 0;j < 4;j++) {
				TextureRegion region = textureFrames[i][j];
				if(region == null)
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
		if(Utility.isAssetLoaded(entitySpritePath))
			Utility.unloadAsset(entitySpritePath);
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

	@Override
	public void onNoCollision(float delta) {
		setCurrentPosition(nextEntityPosition);
	}

	public void updateInputComponent(float delta) {
		if(inputComponent != null)
			inputComponent.update(delta);
	}

	public void calculateNextPositionToward(Vector2 endPosition, float deltaTime) {
		Vector2 tmp = new Vector2(currentEntityPosition);
		entityVelocity.scl(deltaTime);
		moveDirection.set(endPosition.sub(currentEntityPosition).nor());
		changeDirectionFrame(moveDirection.angle());
		tmp.add(moveDirection.x * entityVelocity.x, moveDirection.y * entityVelocity.y);
		nextEntityPosition.set(tmp);
		entityVelocity.scl(1 / deltaTime);
	}

	private void changeDirectionFrame(float directionAngle) { //@formatter:off
		if((directionAngle>0 && directionAngle<45) || (directionAngle>315 && directionAngle<360)) setDirection(Direction.RIGHT);
		else if(directionAngle>45 && directionAngle<135) setDirection(Direction.UP);
		else if(directionAngle>135 && directionAngle<225) setDirection(Direction.LEFT);
		else if(directionAngle>225 && directionAngle<315) setDirection(Direction.DOWN);
	}//@formatter:on
}
