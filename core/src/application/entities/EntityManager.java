package application.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.animation.AnimationEntityObserver;
import application.components.concrete.EnemyInputComponent;
import application.components.concrete.PlayerInputComponent;
import application.entities.concrete.dtos.CharacterDTO;
import application.entities.concrete.dtos.ProjectileDTO;
import application.entities.concrete.entities.Character;
import application.entities.concrete.entities.Projectile;
import application.maps.MapManager;

public class EntityManager {
	private static final String TAG = EntityManager.class.getSimpleName();

	private static final String CREATE_ENTITY_ERROR = "ERROR: Unknown create DTO, define concrete dto class to create concrete entity!";
	private static final String ERROR_MAP_MANAGER_IS_NULL = "ERROR: Map manager is null!";

	private Array<Entity> entities;
	private final MapManager mapManager;
	private final AnimationEntityObserver animationEntityObserver;
	private final Entity player;

	public EntityManager(MapManager mapManager, AnimationEntityObserver animationEntityObserver) {
		this.mapManager = mapManager;
		this.animationEntityObserver = animationEntityObserver;
		entities = new Array<>(100);
		player=createAndGetEntity(new CharacterDTO(	"sprites/characters/Warrior.png", //
			mapManager.getPlayerStart(),//
			new PlayerInputComponent(this), 200));
	}

	//@formatter:off
	public void addEntity(Entity entity) {entities.add(entity);}
	public void removeEntity(Entity entity) {entities.removeValue(entity, true);}
	public AnimationEntityObserver getAnimationEntityObserver() {return animationEntityObserver;}
	public Entity getPlayer() {return player;}
	//@formatter:on

	public Entity createAndGetEntity(CreateDTO createDTO) {
		Entity result = null;
		if(createDTO instanceof ProjectileDTO) {
			result = new Projectile((ProjectileDTO) createDTO);
			entities.add(result);
		} else if(createDTO instanceof CharacterDTO) {
			result = new Character((CharacterDTO) createDTO);
			entities.add(result);
			((Character) result).init(createDTO.getStartPosition(), true);
		} else {
			Gdx.app.debug(TAG, CREATE_ENTITY_ERROR);
		}
		return result;
	}

	public void initAllCharacters() {
		for(String enemyName:mapManager.getEnemySpawnLocationMap().keySet()) {
			switch (enemyName) {
				case MapManager.MAGE : {
					for(Vector2 position:mapManager.getEnemySpawnLocationMap().get(enemyName))
						//						createCharacter(MapManager.MAGE, position);
						createAndGetEntity(new CharacterDTO("sprites/characters/Mage.png", position, new EnemyInputComponent(this), 80));
					break;
				}
			}
		}
	}

	public boolean isCollisionBetweenProjectileAndCharacters(Entity projectile) {
		if(projectile instanceof Projectile) {
			for(Entity entity:entities) {
				if(entity instanceof Character) {
					if(entity.isCollisionBetweenProjectileAndCharacter(projectile))
						return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends Entity> Array<T> getEntitiesOfType(Class<T> entityType) {
		Array<T> result = new Array<>();
		for(Entity entity:entities) {
			if(entity.getClass().equals(entityType)) {
				result.add((T) entity);
			}
		}
		return result;
	}

	public void disposeAllEntities() {
		for(Entity entity:entities) {
			entity.dispose();
		}
	}

	public void updateAllEntities(float delta) {
		for(int i = 0;i < entities.size;i++) {
			entities.get(i).update(delta, this);
		}
	}

	public boolean isCollisionWithCollisionLayer(Entity entity) {
		if(mapManager != null)
			return mapManager.isCollisionWithCollisionLayer(entity.getEntityHitBox());
		Gdx.app.debug(TAG, ERROR_MAP_MANAGER_IS_NULL);
		return false;
	}
}
