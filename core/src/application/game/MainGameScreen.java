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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import application.huds.CharacterHUD;
import application.huds.EnemyHUD;
import application.maps.MapManager;
import application.animation.AnimationEntityObserver;
import application.entities.EntityManager;
import application.entities.concrete.dtos.CharacterDTO;
import application.entities.concrete.entities.Character;
import application.entities.concrete.entities.Projectile;

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
	private final AnimationEntityObserver animationEntityObserver;

	private static boolean drawHealthBar;

	public MainGameScreen() {
		mapManager = new MapManager();
		animationEntityObserver = new AnimationEntityObserver();
	}

	@Override
	public void show() {
		setupViewport(10, 10);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentMap(), MapManager.UNIT_SCALE);
		mapRenderer.setView(camera);

		//must be here in show method, in constructor exception is thrown (Utility class problems)
		entityManager = new EntityManager(mapManager, animationEntityObserver);
		entityManager.initAllCharacters();
		currentPlayerSprite = entityManager.getPlayer().getEntitySprite();

		shapeRenderer = new ShapeRenderer();

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
		characterHUD = new CharacterHUD(hudCamera, ((CharacterDTO) entityManager.getPlayer().getCreateDTO()).getHealthPoints(), 50, 100);

		enemyHUD = new EnemyHUD(hudCamera);

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(characterHUD.stage);
		multiplexer.addProcessor(((CharacterDTO) entityManager.getPlayer().getCreateDTO()).getInputComponent());
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

		// ----------------- blok UPDATE --------------------------//

		entityManager.updateAllEntities(delta);
		for(Character character:entityManager.getEntitiesOfType(Character.class))
			character.updateInputComponent(delta);

		// ----------------- blok UPDATE end-------------------//

		// ----------------- render block ---------------------//
		mapRenderer.setView(camera);
		shapeRenderer.setProjectionMatrix(camera.combined);
		mapRenderer.render();
		mapRenderer.getBatch().begin();
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.GREEN);
		entityManager.getEntitiesOfType(Character.class).sort(Character.yComparator.reversed()); //for what?
		entityManager.getEntitiesOfType(Character.class).forEach(character -> {
			//			mapRenderer.getBatch().draw(character.getEntityTextureRegion(), character.getEntitySprite().getX()-0.5f, character.getEntitySprite().getY()-0.5f, 1, 1);
			mapRenderer.getBatch().draw(character.getEntityTextureRegion(), character.getCurrentEntityPosition().x - 0.5f, character.getCurrentEntityPosition().y - 0.5f, 1, 1);
			shapeRenderer.rect(character.getEntityHitBox().x * MapManager.UNIT_SCALE, character.getEntityHitBox().y * MapManager.UNIT_SCALE, character.getEntityHitBox().width * MapManager.UNIT_SCALE,
				character.getEntityHitBox().height * MapManager.UNIT_SCALE);
			//			 shapeRenderer.rect(character.getEntitySprite().getX(), character.getEntitySprite().getY(), character.getEntitySprite().getWidth(), character.getEntitySprite().getHeight());
		});

		entityManager.getEntitiesOfType(Projectile.class).forEach(projectile -> {
			//			mapRenderer.getBatch().draw(projectile.getEntityTextureRegion(), projectile.getEntitySprite().getX(), projectile.getEntitySprite().getY(),
			//										(projectile.getEntitySprite().getWidth() * MapManager.UNIT_SCALE) / 2, (projectile.getEntitySprite().getHeight() * MapManager.UNIT_SCALE) / 2, 1, 1, 1, 1,
			//										projectile.getRotationAngle());
			mapRenderer.getBatch().draw(projectile.getEntityTextureRegion(), projectile.getCurrentEntityPosition().x - 1, projectile.getCurrentEntityPosition().y - 1, (projectile.getEntitySprite()
				.getWidth() * MapManager.UNIT_SCALE) / 2, (projectile.getEntitySprite().getHeight() * MapManager.UNIT_SCALE) / 2, 1, 1, 1, 1, projectile.getRotationAngle());
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
		if(drawHealthBar)
			enemyHUD.render(delta);
		// ----------------- render block end -------------------//
	}

	public static void drawHealthBar(boolean draw, Character enemy) {
		enemyHUD.setValues(enemy);
		drawHealthBar = draw;
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
		//		for(Entity entity:entityManager.getEntities())
		//			entity.dispose();
		entityManager.disposeAllEntities();
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

		Gdx.app.debug(TAG, "virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")");
		Gdx.app.debug(TAG, "viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")");
		Gdx.app.debug(TAG, "physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")");
	}

	//	private boolean isEntityCollidedWithOtherCharacters(Entity entity) {
	//		for(Character character:characterManager.getCharacters()) {
	//			if(!character.equals(entity) && isCollisionBetweenEntities(entity, character))
	//				return true;
	//		}
	//		return false;
	//	}

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
