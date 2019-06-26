package application.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class EntityFactory {
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
					locations.get(MapManager.MAGE).stream().forEach(position -> {
						getEntity(MapManager.MAGE, position);
					});
					break;
				}
			}
		}
	}
	//TODO: inne encje s¹ wyœwietlane w punkcie (0,0)
	
	public List<Entity> getEntities() {
		return entities;
	}

	public void updateAllEntites(float delta) {
		for (Entity entity : entities)
			entity.update(delta);
	}

	public Entity getEntity(String entityType, Vector2 position) {
		Entity entity = null;
		switch (entityType) { //@formatter:off
			case MapManager.PLAYER: entity=new Entity("");break;
			case MapManager.MAGE:entity= new Entity(CHARACTER_FOLDER + MAGE_PATH);break;
			default:entity= new Entity("");break;
		} //@formatter:on
		entities.add(entity);
		entity.init(position);
//		System.out.println(position);
		return entity;
	}
}
