package application.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.entity.Entity;

public class ProjectileManager {
	private static Array<Projectile> projectiles;
	public static final String FIRE_BALL="sprites/projectiles/fireball.png";
	
	public ProjectileManager() {
		projectiles=new Array<>(100);
	}
	
	public static void createProjectile(Entity caster, String projectileSpritePath,Vector2 startPosition, Vector2 endPosition) {
		projectiles.add(new Projectile(caster,projectileSpritePath,startPosition, endPosition));
	}
	
	public void removeProjectile(Projectile projectile) {
		projectiles.removeValue(projectile, true);
	}
	
	public Array<Projectile> getProjectiles() {
		return projectiles;
	}

	public void updateProjectiles(float delta) {
		for(Projectile projectile:projectiles)
			projectile.update(delta);
	}
}
