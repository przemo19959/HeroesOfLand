package application.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import application.entity.Entity;
import application.entity.EntityManager;
import application.huds.CharacterHUD;
import application.huds.EnemyHUD;
import application.maps.MapManager;
import application.maps.MapManager.MapLayerName;
import application.projectiles.Projectile;
import application.projectiles.ProjectileManager;
import application.animation.AnimationEntityObserver;
import application.characters.Character;
import application.characters.CharacterManager;

public class MainGameScreen implements Screen {
	private static final String TAG = MainGameScreen.class.getSimpleName();

	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	private Sprite currentPlayerSprite;
	private OrthogonalTiledMapRenderer mapRenderer = null;
	public static OrthographicCamera camera = null;
	private ShapeRenderer shapeRenderer;
	
	private OrthographicCamera hudCamera;
	private InputMultiplexer multiplexer;
	private CharacterHUD characterHUD;
	private static EnemyHUD enemyHUD;
	
	private MapManager mapManager;
	private EntityManager entityManager;
	private CharacterManager characterManager;
	private ProjectileManager projectileManager;
	private final AnimationEntityObserver animationEntityObserver;

	public static Character player;
	private static boolean drawHealthBar;

	public MainGameScreen() {
		mapManager = new MapManager();
		entityManager = new EntityManager();
		characterManager = new CharacterManager(mapManager.getEnemySpawnLocationMap(), entityManager);
		projectileManager = new ProjectileManager(entityManager);
		animationEntityObserver=new AnimationEntityObserver();
	}

	@Override
	public void show() {
		setupViewport(10, 10);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentMap(), MapManager.UNIT_SCALE);
		mapRenderer.setView(camera);

		player = characterManager.createCharacter(MapManager.PLAYER, mapManager.getPlayerStart());
		characterManager.initAllCharacters();
		currentPlayerSprite = player.getEntitySprite();
		
		shapeRenderer = new ShapeRenderer();
		
		hudCamera=new OrthographicCamera();
		hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
		characterHUD=new CharacterHUD(hudCamera, player.getHealthPoints(),50,100);
		
		enemyHUD=new EnemyHUD(hudCamera);
		
		multiplexer=new InputMultiplexer();
		multiplexer.addProcessor(characterHUD.stage);
		multiplexer.addProcessor(player.getInputComponent());
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void hide() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.position.set(currentPlayerSprite.getX(), currentPlayerSprite.getY(), 0f);
		camera.update();
		// -----------------blok UPDATE-------------------//
		entityManager.updateAllEntites(delta);

		// updatePortalLayerActivation(player.boundingBox);

		for(Entity entity:entityManager.getEntities()) {
			if(entity.isEntity(Character.class)) {
				if(!isCollisionBetweenEntities(player, entity)) {
					if(!isCollisionPlayerWithCharacters(entity)) {
						entity.onNoCollision(delta);// tutaj sprawdzana jest kolizja hitboxa z map¹
					}else {
						Character attackedCharacter;
						if((attackedCharacter=player.stopMovingAndAttack())!=null) {
							//Atak 1
//							animationEntityObserver.addAnimation("attacks/sword-slice.png", player,16, 16, 10,false);
//							attackedCharacter.addHelthPoints(-10);
							
							//Atak 2
							animationEntityObserver.addAnimation("attacks/sword-drop.png", player,16, 32, 18,true);
							attackedCharacter.addHelthPoints(-30);
							
							enemyHUD.setValues("MAGE", "Demon", attackedCharacter);
						}
					}
				}	
			} else if(entity.isEntity(Projectile.class)) {
				if(!isCollisionWithMapLayer(entity.getEntityHitBox()) && !isCollisionBetweenProjectileAndCharacters((Projectile) entity)) {
					entity.onNoCollision(delta);
				} else {
					animationEntityObserver.addAnimation(ProjectileManager.FIRE_EXPLOSION, entity, 16, 16, 7,false);
					projectileManager.removeProjectile((Projectile)entity);
				}
			}
		}

		for(Character character:characterManager.getCharacters())
			character.updateInputComponent(delta);

		// -----------------blok UPDATE koniec-------------------//
		mapRenderer.setView(camera);
		shapeRenderer.setProjectionMatrix(camera.combined);
		mapRenderer.render();
		mapRenderer.getBatch().begin();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.GREEN);
		characterManager.getCharacters().sort(Character.yComparator.reversed());
		characterManager.getCharacters().forEach(character -> {
//			mapRenderer.getBatch().draw(character.getEntityTextureRegion(), character.getEntitySprite().getX()-0.5f, character.getEntitySprite().getY()-0.5f, 1, 1);
			mapRenderer.getBatch().draw(character.getEntityTextureRegion(), character.getCurrentEntityPosition().x-0.5f, character.getCurrentEntityPosition().y-0.5f, 1, 1);
//			 shapeRenderer.rect(character.getEntityHitBox().x*MapManager.UNIT_SCALE, character.getEntityHitBox().y*MapManager.UNIT_SCALE
//			 , character.getEntityHitBox().width*MapManager.UNIT_SCALE, character.getEntityHitBox().height*MapManager.UNIT_SCALE);
//			 shapeRenderer.rect(character.getEntitySprite().getX(), character.getEntitySprite().getY(), character.getEntitySprite().getWidth(), character.getEntitySprite().getHeight());
		});

		projectileManager.getProjectiles().forEach(projectile -> {
//			mapRenderer.getBatch().draw(projectile.getEntityTextureRegion(), projectile.getEntitySprite().getX(), projectile.getEntitySprite().getY(),
//										(projectile.getEntitySprite().getWidth() * MapManager.UNIT_SCALE) / 2, (projectile.getEntitySprite().getHeight() * MapManager.UNIT_SCALE) / 2, 1, 1, 1, 1,
//										projectile.getRotationAngle());
			mapRenderer.getBatch().draw(projectile.getEntityTextureRegion(), projectile.getCurrentEntityPosition().x-1, projectile.getCurrentEntityPosition().y-1,
										(projectile.getEntitySprite().getWidth() * MapManager.UNIT_SCALE) / 2, (projectile.getEntitySprite().getHeight() * MapManager.UNIT_SCALE) / 2, 1, 1, 1, 1,
										projectile.getRotationAngle());
//			 shapeRenderer.rect(projectile.getEntityHitBox().x*MapManager.UNIT_SCALE, projectile.getEntityHitBox().y*MapManager.UNIT_SCALE
//			 , projectile.getEntityHitBox().width*MapManager.UNIT_SCALE, projectile.getEntityHitBox().height*MapManager.UNIT_SCALE);
			// shapeRenderer.rect(projectile.getProjectileSprite().getX(), projectile.getProjectileSprite().getY()
			// , projectile.getProjectileSprite().getWidth()*MapManager.UNIT_SCALE, projectile.getProjectileSprite().getHeight()*MapManager.UNIT_SCALE);
		});
		animationEntityObserver.updateAllAnimations(mapRenderer.getBatch(), delta);
		mapRenderer.getBatch().end();
		mapRenderer.render(new int[]{6});
		
//		spriteBatch.begin();
		
//		spriteBatch.end();
		
		
		
		mapManager.drawGrid(shapeRenderer);
		shapeRenderer.end();
		
		characterHUD.render(delta);
		if(drawHealthBar) {
			enemyHUD.render(delta);
		}		
	}
	
	public static void drawHealthBar(boolean draw, Character enemy) {
		enemyHUD.setValues("MAGE", "Demon", enemy);
		drawHealthBar=draw;
	}
	
	@Override
	public void resize(int width, int height) {
		characterHUD.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		for(Entity entity:entityManager.getEntities())
			entity.dispose();
		Gdx.input.setInputProcessor(null);
		mapRenderer.dispose();
		characterHUD.dispose();
	}

	private void setupViewport(int width, int height) {
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		if(VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		} else {
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight / VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")");
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")");
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")");
	}

	private boolean isCollisionWithMapLayer(Rectangle hitBox) {
		if(doesGivenLayerExists(MapLayerName.MAP_COLLISION_LAYER)) {
			Rectangle rectangle = null;
			for(MapObject object:mapManager.getLayer(MapLayerName.MAP_COLLISION_LAYER).getObjects()) {
				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject) object).getRectangle();
					if(hitBox.overlaps(rectangle)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean doesGivenLayerExists(MapLayerName layerName) {
		return (mapManager.getLayer(layerName) != null) ? true : false;
	}

	private boolean isCollisionBetweenEntities(Entity entity1, Entity entity2) {
		return (!entity1.equals(entity2) && entity1.getEntityHitBox().overlaps(entity2.getEntityHitBox())) ? true : false;
	}

	private boolean isCollisionBetweenProjectileAndCharacters(Projectile projectile) {
		for(Character character:characterManager.getCharacters()) {
			if(!projectile.getCaster().equals(character) && projectile.getEntityHitBox().overlaps(character.getEntityHitBox()))
				return true;
		}
		return false;
	}

	private boolean isCollisionPlayerWithCharacters(Entity playerEntity) {
		if(playerEntity.equals(player)) {
			for(Character character:characterManager.getCharacters()) {
				if(isCollisionBetweenEntities(playerEntity, character))
					return true;
			}
		}
		return false;
	}

	// private boolean updatePortalLayerActivation(Rectangle boundingBox) {
	// if (doesGivenLayerExists(MapLayerName.MAP_PORTAL_LAYER)) {
	// Rectangle rectangle = null;
	//
	// for (MapObject object : mapManager.getLayer(MapLayerName.MAP_PORTAL_LAYER).getObjects()) {
	// if (object instanceof RectangleMapObject) {
	// rectangle = ((RectangleMapObject) object).getRectangle();
	// if (boundingBox.overlaps(rectangle)) {
	// String mapName = object.getName();
	// if (mapName == null) {
	// return false;
	// }
	//
	// mapManager.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
	// mapManager.loadMap(mapName);
	// player.init(mapManager.getPlayerStartUnitScaled().x, mapManager.getPlayerStartUnitScaled().y);
	// mapRenderer.setMap(mapManager.getCurrentMap());
	// Gdx.app.debug(TAG, "Portal Activated");
	// return true;
	// }
	// }
	// }
	// }
	// return false;
	// }

}
