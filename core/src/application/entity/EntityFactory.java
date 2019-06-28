package application.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import application.components.EnemyInputComponent;
import application.components.PlayerInputComponent;
import application.maps.MapManager;

public class EntityFactory {
	private static final String TAG = EntityFactory.class.getSimpleName();
	private List<Entity> entities;
	private MapManager mapManager;
	private Entity player;

	// sprites paths
	private static final String CHARACTER_FOLDER = "sprites/characters";
	private static final String MAGE_PATH = "/Mage.png";


	public EntityFactory(MapManager mapManager) {
		entities = new ArrayList<>();
		this.mapManager=mapManager;
	}

	public void initAllEntities() {
		Map<String, List<Vector2>> enemySpawnLocations = mapManager.getEnemySpawnLocationMap();
		for (String enemyName : enemySpawnLocations.keySet()) {
			switch (enemyName) {
				case MapManager.MAGE: {
					for(Vector2 position:enemySpawnLocations.get(enemyName))
						createEntity(MapManager.MAGE, position);
					break;
				}
			}
		}
	}
	
	public List<Entity> getEntities() {
		return entities;
	}

	public void updateAllEntites(float delta) {
		for (Entity entity : entities)
			entity.update(delta);
	}
	
	public Entity createEntity(String entityType, Vector2 position) {
		Entity entity=getEntity(entityType);
		if(entity!=null) {
			entities.add(entity);
			entity.init(position, true);
		}
		return entity;
	}

	private Entity getEntity(String entityType) {
		switch (entityType) { //@formatter:off
			case MapManager.PLAYER:player=new Entity("", new PlayerInputComponent());return player;
			case MapManager.MAGE:return new Entity(CHARACTER_FOLDER + MAGE_PATH, new EnemyInputComponent());
			default: Gdx.app.debug(TAG, "Entity type "+entityType+" not defined!!!");return null;
		} //@formatter:on
	}
}
