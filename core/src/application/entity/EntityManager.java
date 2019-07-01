package application.entity;

import com.badlogic.gdx.utils.Array;

public class EntityManager {
//	private static final String TAG = EntityManager.class.getSimpleName();
	private Array<Entity> entities;
	
	public EntityManager() {
		entities = new Array<>(100);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	public void removeEntity(Entity entity) {
		entities.removeValue(entity, true);
	}
	
	public Array<Entity> getEntities() {
		return entities;
	}

	public void updateAllEntites(float delta) {
		for (Entity entity : entities)
			entity.update(delta);
	}	
}
