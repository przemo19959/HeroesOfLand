package application.game;

import com.badlogic.gdx.Gdx;
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
import application.entity.EntityFactory;
import application.maps.MapManager;
import application.maps.MapManager.MapLayerName;
import application.projectiles.Projectile;
import application.projectiles.ProjectileManager;

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
//	private ShapeRenderer shapeRenderer;
	
	
	private EntityFactory entityFactory;
	private MapManager mapManager; 
	private ProjectileManager projectileManager;
	
	public static Entity player;

	public MainGameScreen() {
		mapManager=new MapManager();
		entityFactory=new EntityFactory(mapManager);
		projectileManager=new ProjectileManager();
	}

	@Override
	public void show() {
		setupViewport(10, 10);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentMap(), MapManager.UNIT_SCALE);
		mapRenderer.setView(camera);

		player = entityFactory.createEntity(MapManager.PLAYER, mapManager.getPlayerStart());
		entityFactory.initAllEntities();
		
		currentPlayerSprite = player.getFrameSprite();
//		shapeRenderer=new ShapeRenderer();
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
		entityFactory.updateAllEntites(delta); //tutaj zmieniane jest po³o¿enie hitboxa
		projectileManager.updateProjectiles(delta);
//		updatePortalLayerActivation(player.boundingBox);
		
		for(Entity entity:entityFactory.getEntities()) {
			if (!isCollisionWithMapLayer(entity.entityHitBox) && !isCollisionBetweenEntities(player, entity)
					&& !isCollisionPlayerWithEntities(entity)) { //tutaj sprawdzana jest kolizja hitboxa z map¹
				entity.setNextPositionToCurrent();
			}
		}
		
		for(Projectile projectile:projectileManager.getProjectiles()) {
			if(!isCollisionWithMapLayer(projectile.getProjectileHitBox()) && 
					!isCollisionBetweenProjectileAndEntities(projectile)){
					projectile.onNoCollision(delta);
//					projectile.updateAfterCollisionTest(delta);
			}else {
				if(projectile.onCollision())
					projectileManager.removeProjectile(projectile);
				//tutaj, gdy nast¹pi³a kolizja pocisku i encji lub mapy
			}
		}
		
		for(Entity entity:entityFactory.getEntities())
			entity.updateInputComponent(delta);
		
//		for(Projectile projectile:projectileManager.getProjectiles()) {
//			projectile.updateAfterCollisionTest(delta);
//		}

		// -----------------blok UPDATE koniec-------------------//
		mapRenderer.setView(camera);
//		shapeRenderer.setProjectionMatrix(camera.combined);
		mapRenderer.render();
		mapRenderer.getBatch().begin();
//		shapeRenderer.begin(ShapeType.Line);
//		shapeRenderer.setColor(Color.GREEN);
		entityFactory.getEntities().stream().sorted(Entity.yComparator.reversed()).forEach(entity->{
			mapRenderer.getBatch().draw(entity.getFrame(), entity.getFrameSprite().getX(), entity.getFrameSprite().getY(), 1, 1);
			//To rysuje obwód hitboxa encji
//			shapeRenderer.rect(entity.entityHitBox.x*MapManager.UNIT_SCALE, entity.entityHitBox.y*MapManager.UNIT_SCALE
//			                   , entity.entityHitBox.width*MapManager.UNIT_SCALE, entity.entityHitBox.height*MapManager.UNIT_SCALE);
		});
		projectileManager.getProjectiles().forEach(projectile->{
			mapRenderer.getBatch().draw(projectile.getProjectileTextureRegion(), projectile.getProjectileSprite().getX(), projectile.getProjectileSprite().getY(),
			                            (projectile.getProjectileSprite().getWidth()*MapManager.UNIT_SCALE)/2, (projectile.getProjectileSprite().getHeight()*MapManager.UNIT_SCALE)/2,
			                            1, 1, 1, 1, projectile.getRotationAngle());
//			shapeRenderer.rect(projectile.getProjectileHitBox().x*MapManager.UNIT_SCALE, projectile.getProjectileHitBox().y*MapManager.UNIT_SCALE
//			                   , projectile.getProjectileHitBox().width*MapManager.UNIT_SCALE, projectile.getProjectileHitBox().height*MapManager.UNIT_SCALE);
//			shapeRenderer.rect(projectile.getProjectileSprite().getX(), projectile.getProjectileSprite().getY()
//			                   , projectile.getProjectileSprite().getWidth()*MapManager.UNIT_SCALE, projectile.getProjectileSprite().getHeight()*MapManager.UNIT_SCALE);
		});
		mapRenderer.getBatch().end();
		
//		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		for(Entity entity:entityFactory.getEntities())
			entity.dispose();
		Gdx.input.setInputProcessor(null);
		mapRenderer.dispose();
	}

	private void setupViewport(int width, int height) {
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		if (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
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
		if (doesGivenLayerExists(MapLayerName.MAP_COLLISION_LAYER)) {
			Rectangle rectangle = null;
			for (MapObject object : mapManager.getLayer(MapLayerName.MAP_COLLISION_LAYER).getObjects()) {
				if (object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject) object).getRectangle();
					if (hitBox.overlaps(rectangle)) {
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
		return (!entity1.equals(entity2) && entity1.entityHitBox.overlaps(entity2.entityHitBox))?true:false;
	}
	
	private boolean isCollisionBetweenProjectileAndEntities(Projectile projectile) {
		for(Entity entity:entityFactory.getEntities()) {
			if(!projectile.getCaster().equals(entity) && projectile.getProjectileHitBox().overlaps(entity.entityHitBox))
				return true;
		}
		return false;
	}
	
	private boolean isCollisionPlayerWithEntities(Entity playerEntity) {
		if(playerEntity.equals(player)) {
			for(Entity entity:entityFactory.getEntities()) {
				if(isCollisionBetweenEntities(playerEntity, entity))
					return true;
			}
		}
		return false;
	}

//	private boolean updatePortalLayerActivation(Rectangle boundingBox) {
//		if (doesGivenLayerExists(MapLayerName.MAP_PORTAL_LAYER)) {
//			Rectangle rectangle = null;
//
//			for (MapObject object : mapManager.getLayer(MapLayerName.MAP_PORTAL_LAYER).getObjects()) {
//				if (object instanceof RectangleMapObject) {
//					rectangle = ((RectangleMapObject) object).getRectangle();
//					if (boundingBox.overlaps(rectangle)) {
//						String mapName = object.getName();
//						if (mapName == null) {
//							return false;
//						}
//
//						mapManager.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
//						mapManager.loadMap(mapName);
//						player.init(mapManager.getPlayerStartUnitScaled().x, mapManager.getPlayerStartUnitScaled().y);
//						mapRenderer.setMap(mapManager.getCurrentMap());
//						Gdx.app.debug(TAG, "Portal Activated");
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}

}
