package application.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class EntityFactory {
//	private static final String TAG = EntityFactory.class.getSimpleName();
	private List<Entity> entities;
	private MapManager mapManager;

	// sprites paths
	private static final String CHARACTER_FOLDER = "sprites/characters";
	private static final String MAGE_PATH = "/Mage.png";


	public EntityFactory(MapManager mapManager) {
		entities = new ArrayList<>();
		this.mapManager=mapManager;
	}

	public void initAllEntities() {
		Map<String, List<Vector2>> locations = mapManager.getEnemySpawnLocationMap();
		for (String enemyName : locations.keySet()) {
			switch (enemyName) {
				case MapManager.MAGE: {
					for(Vector2 position:locations.get(enemyName))
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
		entities.add(entity);
		entity.init(position, true);
		return entity;
	}

	private Entity getEntity(String entityType) {
		switch (entityType) { //@formatter:off
			case MapManager.PLAYER: return new Entity("");
			case MapManager.MAGE:return new Entity(CHARACTER_FOLDER + MAGE_PATH);
			default:return new Entity("");
		} //@formatter:on
	}
}
