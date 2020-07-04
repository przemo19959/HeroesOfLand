package application.entities.concrete.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import application.entities.Entity;
import application.entities.EntityManager;
import application.entities.concrete.dtos.ProjectileDTO;

/**
 * Concrete entity for projectile type entity. Projectile moves with constant velocity and direction given by end position vector. On collision with map collision layer or character it stops and is
 * removed. Also at the collision ending animation is played.
 * 
 * @author hex
 */
public class Projectile extends Entity {
	private static final String TAG = Projectile.class.getSimpleName();

	private final ProjectileDTO projectileDTO;
	private final Animation<TextureRegion> animation;
	private final Vector2 fireDirection;
	private final Character caster;

	public Projectile(ProjectileDTO projectileDTO) {
		super(projectileDTO);
		this.projectileDTO=projectileDTO;

		animation = loadAndGetAnimations(5, 0.1f, PlayMode.NORMAL).get(0);
		//add vector [0.5,0.5] because end position is left bottom corner, and we want center of that position
		Vector2 finishPosition = new Vector2(projectileDTO.getEndPosition()).add(0.5f, 0.5f);
		fireDirection = getDirectionVector(finishPosition);
		caster = projectileDTO.getCaster();

		entityVelocity = new Vector2(4f, 4f);
		initHitBoxSize(0.5f, 0.5f, -0.5f, -0.5f);
	}

	@Override
	public void update(float delta, EntityManager entityManager) {
		frameTime = (frameTime + delta) % 5;
		updateHitBoxPosition(-0.5f, -0.5f);

		if(entityManager.isCollisionWithCollisionLayer(this) == false && //
			entityManager.isCollisionBetweenProjectileAndCharacters(this) == false) {
			onNoCollision(delta);
		} else {
			//TODO - 3 lip 2020:tutaj jakoœ uzale¿niæ koñcz¹c¹ animacjê od konkretnego pocisku
			entityManager.getAnimationEntityObserver().addAnimation("sprites/explosions/fire.png", this, 16, 16, 7, false);
			entityManager.removeEntity(this);
			//test if projectiles are removed
			//			Gdx.app.debug(TAG, "properly removed, size: "+entityManager.getEntitiesOfType(Projectile.class).size);
		}
	}

	@Override
	public void onNoCollision(float deltaTime) {
		setCurrentPosition(nextEntityPosition);
		entityTextureRegion = animation.getKeyFrame(frameTime);
		calculateNextPosition(deltaTime);
	}

	//@formatter:off
	public boolean casterEquals(Entity entity) {return caster.equals(entity);}
	public float getRotationAngle() {return fireDirection.angle();}
	public ProjectileDTO getProjectileDTO() {return projectileDTO;}
	//@formatter:on

	/**
	 * Generally we have velocity equation _v=_s/t. For given delta time dt we have position change vector equal _ds=_v*dt. Firstly that position change is calculated. Next to current projectile
	 * position that vector is added _s+_ds. Additionally vector _ds is multiplied by direction vector coordinates. In that process we get _ds' vector which is directed the same way as direction
	 * vector. Finally new position vector _s+_ds' is set at current position. At the end velocity vector is scaled back, so that velocity is constant.
	 * 
	 * @param deltaTime - change in time
	 */
	private void calculateNextPosition(float deltaTime) {
		Vector2 tmp = new Vector2(currentEntityPosition);
		entityVelocity.scl(deltaTime);
		tmp.add(fireDirection.x * entityVelocity.x, fireDirection.y * entityVelocity.y);
		nextEntityPosition.set(tmp);
		entityVelocity.scl(1 / deltaTime);
	}

	@Override
	protected int numberOfAnimations() {
		return 1;
	}
}
