package application.entities;

import java.text.MessageFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entities.concrete.entities.Projectile;
import application.game.Utility;
import application.maps.MapManager;

import static application.game.Utility.*;

public abstract class Entity {
	private static final String WIDTH_AND_HEIGHT_ERROR = "Width and Height are 0! {0}:{1}";
	
	private static final String TAG = Entity.class.getSimpleName();

	private final CreateDTO createDTO;

	protected Vector2 entityVelocity;
	protected Vector2 nextEntityPosition;
	protected Vector2 currentEntityPosition;
	protected float frameTime;
	private Sprite entitySprite;
	protected TextureRegion entityTextureRegion;
	private Rectangle entityHitBox;

	public Entity(CreateDTO createDTO) {
		this.createDTO = createDTO;

		Utility.loadAssetOfGivenType(createDTO.getEntitySpritePath(), Texture.class);
		currentEntityPosition = new Vector2(createDTO.getStartPosition()).add(0.5f, 0.5f);
		nextEntityPosition = new Vector2(currentEntityPosition);

		entityHitBox = new Rectangle();
		loadDefaultSprite();
	}

	private void loadDefaultSprite() {
		Texture texture = Utility.getAssetOfGivenType(createDTO.getEntitySpritePath(), Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
		entitySprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		entityTextureRegion = textureFrames[0][0];
	}

	/**
	 * Ta metoda aktualizuje czas ramki, tak aby odtwarzany by³ poprawny region tekstury encji. Nastêpnie aktualizowany jest hitbox encji (aktualizacja odbywa siê na podstawie zmiennych nastêpnych
	 * pozycji postaci). This method generally performs update operations for entity for every game frame.
	 * 
	 * @param delta - czas delta klatki
	 */
	protected abstract void update(float delta, EntityManager entityManager);

	/**
	 * This method specifies what happens with entity, when there was no collision
	 * 
	 * @param delta - change in time
	 */
	protected abstract void onNoCollision(float delta);

	//@formatter:off
	public Vector2 getCurrentEntityPosition() {return currentEntityPosition;}
	public Sprite getEntitySprite() {return entitySprite;}
	public TextureRegion getEntityTextureRegion() {return entityTextureRegion;}
	public Rectangle getEntityHitBox() {return entityHitBox;}
	public CreateDTO getCreateDTO() {return createDTO;}
	//@formatter:on

	//********************************** hitbox and other methods *********************************
	protected void initHitBoxSize(float percentageWidthReduced, float percentageHeightReduced, float xOffset, float yOffset) {
		float widthReductionAmount = 1.0f - percentageWidthReduced; // .8f for 20% (1 - .20)
		float heightReductionAmount = 1.0f - percentageHeightReduced; // .8f for 20% (1 - .20)
		
		float width = (widthReductionAmount > 0 && widthReductionAmount < 1) ? FRAME_WIDTH * widthReductionAmount : FRAME_WIDTH;
		float height = (heightReductionAmount > 0 && heightReductionAmount < 1) ? FRAME_HEIGHT * heightReductionAmount : FRAME_HEIGHT;
		if(width == 0 || height == 0)
			Gdx.app.debug(TAG, MessageFormat.format(WIDTH_AND_HEIGHT_ERROR, width, height));
		
		updateHitBoxPosition(xOffset, yOffset);
		entityHitBox.setWidth(width);
		entityHitBox.setHeight(height);
	}
	
	protected void updateHitBoxPosition(float xOffset, float yOffset) {
		float minX;
		float minY;
		if(MapManager.UNIT_SCALE > 0) {
			minX = (nextEntityPosition.x + xOffset) / MapManager.UNIT_SCALE;
			minY = (nextEntityPosition.y + yOffset) / MapManager.UNIT_SCALE;
		}
		entityHitBox.setCenter(minX, minY);
	}

	protected void setCurrentPosition(Vector2 currentPosition) {
		entitySprite.setX(currentPosition.x);
		entitySprite.setY(currentPosition.y);
		currentEntityPosition.set(currentPosition);
	}
	//*********************************************************************************************

	//********************************** Collision methods ****************************************
	protected boolean isCollisionBetweenEntities(Entity entity1, Entity entity2) {
		return (entity1.equals(entity2) == false && entity1.getEntityHitBox().overlaps(entity2.getEntityHitBox()));
	}

	protected boolean isCollisionBetweenProjectileAndCharacter(Entity projectile) {
		return ((Projectile) projectile).casterEquals(this) == false && //
				projectile.getEntityHitBox().overlaps(getEntityHitBox());
	}
	//*********************************************************************************************

	//********************************** Animation load *******************************************
	protected abstract int numberOfAnimations();
	protected Array<Animation<TextureRegion>> loadAndGetAnimations(int numberOfFrames, float frameDuration, PlayMode playMode) {
		return Utility.loadAndGetAnimations(createDTO.getEntitySpritePath(),//
			numberOfAnimations(), numberOfFrames, frameDuration, playMode);
	}
	//*********************************************************************************************

	/**
	 * This function returns direction vector. Generally we have _a+_b=_c. In this case _a is current projectile position vector, _c is end projectile position vector. To move projectile vector _b is
	 * needed, which defines fire direction. So we have _b=_c-_a. Additionally, because length of _b is not important, we normalize vector.
	 * 
	 * @param finishPosition - vector of end projectile, this is not where projectile will end, its purpose is just to define direction of firing projectile
	 * @return direction vector of projectile
	 */
	protected Vector2 getDirectionVector(Vector2 finishPosition) {
		return new Vector2(finishPosition.sub(currentEntityPosition).nor());
	}

	protected void dispose() {
		Utility.unloadAsset(createDTO.getEntitySpritePath());
	}
}
