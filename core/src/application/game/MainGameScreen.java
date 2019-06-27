package application.game;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import application.game.MapManager.MapLayerName;

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

	private PlayerController controller;
	private Sprite currentPlayerSprite;

	private OrthogonalTiledMapRenderer mapRenderer = null;
	private OrthographicCamera camera = null;
	
	private EntityFactory entityFactory;
	private MapManager mapManager; 
	private Entity player;

	public MainGameScreen() {
		mapManager=new MapManager();
		entityFactory=new EntityFactory(mapManager);
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
		
		controller = new PlayerController(player);
		Gdx.input.setInputProcessor(controller);
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

//		updatePortalLayerActivation(player.boundingBox);
		
		for(Entity entity:entityFactory.getEntities()) {
			if (!isCollisionWithMapLayer(entity.entityHitBox) && !isCollisionBetweenEntities(player, entity)
					&& !isCollisionPlayerWithEntities(entity)) { //tutaj sprawdzana jest kolizja hitboxa z map¹
				entity.setNextPositionToCurrent();
			}
		}
		controller.update(delta); //tutaj liczone jest next na podstawie current pozycji

		// -----------------blok UPDATE koniec-------------------//
		mapRenderer.setView(camera);
		mapRenderer.render();
		mapRenderer.getBatch().begin();
		entityFactory.getEntities().stream().sorted(Entity.yComparator.reversed()).forEach(entity->{
			mapRenderer.getBatch().draw(entity.getFrame(), entity.getFrameSprite().getX(), entity.getFrameSprite().getY(), 1, 1);
		});
		
		mapRenderer.getBatch().end();
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
		controller.dispose();
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

	private boolean isCollisionWithMapLayer(Rectangle boundingBox) {
		if (doesGivenLayerExists(MapLayerName.MAP_COLLISION_LAYER)) {
			Rectangle rectangle = null;
			for (MapObject object : mapManager.getLayer(MapLayerName.MAP_COLLISION_LAYER).getObjects()) {
				if (object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject) object).getRectangle();
					if (boundingBox.overlaps(rectangle)) {
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
