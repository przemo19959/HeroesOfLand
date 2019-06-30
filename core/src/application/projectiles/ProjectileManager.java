package application.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entity.Entity;

public class ProjectileManager {
	private static Array<Projectile> projectiles;
	//projectiles
	public static final String FIRE_BALL = "sprites/projectiles/fireball.png";
	
	//explosions
	public static final String FIRE_EXPLOSION="sprites/explosions/fire.png";

	public ProjectileManager() {
		projectiles = new Array<>(100);
	}

	public static void createProjectile(Entity caster, String projectileSpritePath,String explosionSpritePath, Vector2 startPosition, Vector2 endPosition) {
		projectiles.add(new Projectile(caster).setProjectileSpritePath(projectileSpritePath).setExplosionSpritePath(explosionSpritePath).setStartPosition(startPosition).setEndPosition(endPosition).build());
	}

	public void removeProjectile(Projectile projectile) {
		boolean result=projectiles.removeValue(projectile, true);
		System.out.println(result);
	}

	public Array<Projectile> getProjectiles() {
		return projectiles;
	}

	public void updateProjectiles(float delta) {
		for(Projectile projectile:projectiles)
			projectile.update(delta);
	}
}
