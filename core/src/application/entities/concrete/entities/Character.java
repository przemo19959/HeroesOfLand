package application.entities.concrete.entities;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.components.InputComponent;
import application.components.concrete.PlayerInputComponent;
import application.entities.Entity;
import application.entities.EntityManager;
import application.entities.concrete.dtos.CharacterDTO;
import application.maps.MapManager;

public class Character extends Entity {
	private static final String TAG = Character.class.getSimpleName();

	public final static Comparator<Character> yComparator = ((entity1, entity2) -> Float.compare(entity1.currentEntityPosition.y, entity2.currentEntityPosition.y));
	
	private final CharacterDTO characterDTO;
	private Array<Animation<TextureRegion>> animations;
	private InputComponent inputComponent;
	private Vector2 moveDirection;

	private final int maxHealthPoints;
	private int currentHealthPoints;

	public enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
	private Direction currentEntityDirection = Direction.LEFT;

	public Character(CharacterDTO characterDTO) {
		super(characterDTO);
		this.characterDTO=characterDTO;

		animations = loadAndGetAnimations(4, 0.25f, PlayMode.LOOP);
		inputComponent = characterDTO.getInputComponent();
		moveDirection = new Vector2();

		entityVelocity = new Vector2(2f, 2f);
		maxHealthPoints = currentHealthPoints = characterDTO.getHealthPoints();
		initHitBoxSize(0.1f, 0.1f, 0f, 0f);
		inputComponent.setEntity(this);
		if(inputComponent instanceof PlayerInputComponent)
			Gdx.input.setInputProcessor(inputComponent);
	}

	@Override
	public void update(float delta, EntityManager entityManager) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition(0f, 0f);
		onNoCollision(delta);
	}

	//@formatter:off
	public CharacterDTO getCharacterDTO() {return characterDTO;}
	public Vector2 getNextPlayerPosition() {return nextEntityPosition;}
	public int getCurrentHealthPoints() {return currentHealthPoints;}
	public int getMaxHealthPoints() {return maxHealthPoints;}
	public InputComponent getInputComponent() {return inputComponent;}
	public Direction getCurrentEntityDirection() {return currentEntityDirection;}
	//@formatter:on

	//@formatter:off
	public void addHealthPoints(int healthPoints) {currentHealthPoints += healthPoints;}
	public void subHealthPoints(int healthPoints) {currentHealthPoints -= healthPoints;}
	//@formatter:on

	public void init(Vector2 position, boolean scaled) {
		currentEntityPosition.x = (scaled) ? position.x * MapManager.UNIT_SCALE : position.x;
		currentEntityPosition.y = (scaled) ? position.y * MapManager.UNIT_SCALE : position.y;
		currentEntityPosition.add(0.5f, 0.5f);
		nextEntityPosition.set(currentEntityPosition);
	}

	@Override
	public void onNoCollision(float delta) {
		setCurrentPosition(nextEntityPosition);
	}

	public void updateInputComponent(float delta) {
		if(inputComponent != null)
			inputComponent.update(delta);
	}

	/**
	 * Same as {@link Projectile#calculateNextPosition}, but here every call calculates new direction vector. It is needed, because character will change his animation, projectile has only one
	 * animation.
	 * 
	 * @param endPosition - end vector, which is used to calculate direction vector
	 * @param deltaTime - change in time
	 */
	public void calculateNextPositionToward(Vector2 endPosition, float deltaTime) {
		Vector2 tmp = new Vector2(currentEntityPosition);
		entityVelocity.scl(deltaTime);

		changeDirectionToward(endPosition, frameTime);

		tmp.add(moveDirection.x * entityVelocity.x, moveDirection.y * entityVelocity.y);
		nextEntityPosition.set(tmp);
		entityVelocity.scl(1 / deltaTime);
	}

	public void changeDirectionToward(Vector2 endPosition, float frameTime) {
		moveDirection = getDirectionVector(endPosition);
		changeDirectionFrame(moveDirection.angle(), frameTime);
	}

	//**************************Change direction frame code******************************
	private void changeDirectionFrame(float directionAngle, float frameTime) { //@formatter:off
		if((directionAngle>=0 && directionAngle<45) ||
				(directionAngle>315 && directionAngle<=360)) setDirection(Direction.RIGHT, frameTime);
		else if(directionAngle>45 && directionAngle<135) setDirection(Direction.UP, frameTime);
		else if(directionAngle>135 && directionAngle<225) setDirection(Direction.LEFT, frameTime);
		else if(directionAngle>225 && directionAngle<315) setDirection(Direction.DOWN, frameTime);
	}//@formatter:on

	private void setDirection(Direction direction, float frameTime) {
		this.currentEntityDirection = direction;
		switch (currentEntityDirection) { //@formatter:off
			//indexes are important, compare png with texture 
			case DOWN:entityTextureRegion = animations.get(0).getKeyFrame(frameTime);break;
			case LEFT:entityTextureRegion = animations.get(1).getKeyFrame(frameTime);break;
			case RIGHT:entityTextureRegion = animations.get(2).getKeyFrame(frameTime);break;
			case UP:entityTextureRegion = animations.get(3).getKeyFrame(frameTime);break;
		}//@formatter:on
	}
	//***********************************************************************************

	@Override
	protected int numberOfAnimations() {
		return 4;
	}
}
