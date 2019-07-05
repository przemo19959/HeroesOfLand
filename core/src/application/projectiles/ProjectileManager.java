package application.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entity.EntityManager;
import application.characters.Character;

public class ProjectileManager {
	private static EntityManager entityManager;
	private static Array<Projectile> projectiles;
	// projectiles
	public static final String FIRE_BALL = "sprites/projectiles/fireball.png";

	// explosions
	public static final String FIRE_EXPLOSION = "sprites/explosions/fire.png";

	public ProjectileManager(EntityManager entityManager) {
		projectiles = new Array<>(100);
		ProjectileManager.entityManager = entityManager;
	}

	public static void createProjectile(Character caster, String projectileSpritePath, Vector2 startPosition, Vector2 endPosition) {
		Projectile projectile = new Projectile(caster, projectileSpritePath, startPosition, endPosition);
		projectiles.add(projectile);
		entityManager.addEntity(projectile);
	}

	public void removeProjectile(Projectile projectile) {
		boolean result = projectiles.removeValue(projectile, true);
		entityManager.removeEntity(projectile);
		System.out.println(result);
	}

	public Array<Projectile> getProjectiles() {
		return projectiles;
	}
}
