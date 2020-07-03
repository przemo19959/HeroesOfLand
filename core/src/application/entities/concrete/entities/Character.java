package application.entities.concrete.entities;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.components.InputComponent;
import application.components.concrete.EnemyInputComponent;
import application.components.concrete.PlayerInputComponent;
import application.entities.Entity;
import application.entities.EntityManager;
import application.entities.concrete.dtos.CharacterDTO;
import application.maps.MapManager;

public class Character extends Entity {
	private static final String TAG = Character.class.getSimpleName();

	public final static Comparator<Character> yComparator = ((entity1, entity2) -> Float.compare(entity1.currentEntityPosition.y, entity2.currentEntityPosition.y));

	private Array<Animation<TextureRegion>> animations;
	private InputComponent inputComponent;
	private Vector2 moveDirection;

	private int maxHealthPoints;
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
		//TODO - 3 lip 2020:tutaj dokoñczyæ

		//				if(isCollisionBetweenEntities(entityManager.getPlayer(), this)) {
		//					//Wróg wszed³ w kolizjê z graczem - zatrzymaj siê i wykonaj atak
		//		//			((Character)entity).setCollidedWithEntity(true);
		//					setCollidedWithEntity(true);
		////					animationEntityObserver.addAnimation("attacks/sword-slice.png", entity,16, 16, 10,false);
		//		//			player.addHelthPoints(-10);
		//		//			characterHUD.setAttribiuteValue(Attribiute.HEALTH, player.getHealthPoints());
		//					
		//				}else {
		//					//Wróg brak kolizji z graczem - stój lub idz
		//		//			((Character)entity).updateInputComponent(delta);
		//		//			((Character)entity).setCollidedWithEntity(false);
		//					setCollidedWithEntity(false);
		//				}
		//		
		//				if(isCollisionPlayerWithCharacters(entity)) {
		if(isCollisionBetweenEntities(entityManager.getPlayer(), this)) {
			//			((Character)entity).setCollidedWithEntity(true);
			//			setCollidedWithEntity(true);
			//Gracz wszed³ w kolizjê z wrogiem - zatrzymaj siê i czekaj na naciœniêcie przycisku atak
//			Gdx.app.debug(TAG, "Collided!");
			((PlayerInputComponent) ((Character) entityManager.getPlayer()).getInputComponent()).stopMovingAndAttack();
//			((EnemyInputComponent) getInputComponent()).stopMovingAndAttack();
			//			if(this == entityManager.getPlayer()) {
			//				Character attackedCharacter;
			//				if((attackedCharacter = ((PlayerInputComponent) inputComponent).stopMovingAndAttack()) != null) {
			//					//Atak 1
//			entityManager.getAnimationEntityObserver().addAnimation("attacks/sword-slice.png", entityManager.getPlayer(), 16, 16, 10, false);
			//					//				attackedCharacter.addHelthPoints(-10);
			//
			//					//Atak 2
			//					entityManager.getAnimationEntityObserver().addAnimation("attacks/sword-drop.png", entityManager.getPlayer(), 16, 32, 18, true);
			//					//				attackedCharacter.addHelthPoints(-30);
			//
			//					//				enemyHUD.setValues("MAGE", "Demon", attackedCharacter);
			//				}
			//			}
		} else {
			////			entity.onNoCollision(delta);
			////			((Character)entity).setCollidedWithEntity(false);
			onNoCollision(delta);
			setCollidedWithEntity(false);
		}

		//		if(!isCollisionBetweenEntities(player, entity)) {
		//			if(!isCollisionPlayerWithCharacters(entity)) {
		//				entity.onNoCollision(delta);// tutaj sprawdzana jest kolizja hitboxa z map¹
		//			}else {
		//				Character attackedCharacter;
		//				if((attackedCharacter=player.stopMovingAndAttack())!=null) {
		//					//Atak 1
		////					animationEntityObserver.addAnimation("attacks/sword-slice.png", player,16, 16, 10,false);
		////					attackedCharacter.addHelthPoints(-10);
		//					
		//					//Atak 2
		//					animationEntityObserver.addAnimation("attacks/sword-drop.png", player,16, 32, 18,true);
		//					attackedCharacter.addHelthPoints(-30);
		//					
		//					enemyHUD.setValues("MAGE", "Demon", attackedCharacter);
		//				}
		//			}
		//		}else {
		//			
		//		}
	}

	//@formatter:off
	public Vector2 getNextPlayerPosition() {return nextEntityPosition;}
	public int getHealthPoints() {return currentHealthPoints;}
	public int getMaxHealthPoints() {return maxHealthPoints;}
	public InputComponent getInputComponent() {return inputComponent;}
	public Direction getCurrentEntityDirection() {return currentEntityDirection;}
	//@formatter:on

	public Character stopMovingAndAttack() {
		if(inputComponent instanceof PlayerInputComponent)
			return ((PlayerInputComponent) inputComponent).stopMovingAndAttack();
		return null;
	}

	public void addHealthPoints(int healthPoints) {
		currentHealthPoints += healthPoints;
	}

	//	private boolean isCollisionPlayerWithCharacters(Entity playerEntity) {
	//		if(playerEntity.equals(player)) {
	//			for(Character character:characterManager.getCharacters()) {
	//				if(isCollisionBetweenEntities(playerEntity, character))
	//					return true;
	//			}
	//		}
	//		return false;
	//	}

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

	public void setCollidedWithEntity(boolean collidedWithEntity) {
		inputComponent.setCollidedWithEntity(collidedWithEntity);
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

		moveDirection = getDirectionVector(endPosition);
		changeDirectionFrame(moveDirection.angle());

		tmp.add(moveDirection.x * entityVelocity.x, moveDirection.y * entityVelocity.y);
		nextEntityPosition.set(tmp);
		entityVelocity.scl(1 / deltaTime);
	}

	//**************************Change direction frame code******************************
	private void changeDirectionFrame(float directionAngle) { //@formatter:off
		if((directionAngle>=0 && directionAngle<45) ||
				(directionAngle>315 && directionAngle<=360)) setDirection(Direction.RIGHT);
		else if(directionAngle>45 && directionAngle<135) setDirection(Direction.UP);
		else if(directionAngle>135 && directionAngle<225) setDirection(Direction.LEFT);
		else if(directionAngle>225 && directionAngle<315) setDirection(Direction.DOWN);
	}//@formatter:on

	private void setDirection(Direction direction) {
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
