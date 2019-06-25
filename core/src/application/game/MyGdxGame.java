package application.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MyGdxGame extends Game {
	private static final String TAG = MyGdxGame.class.getSimpleName();
	private final float UNIT_SCALE = 1 / 16f;
	private static final int FRAME_WIDTH=16;
	private static final int FRAME_HEIGHT=16;
	
	private static final int playerStartX=5;
	private static final int playerStartY=5;
	
	public static class VIEWPORT {
		public static float viewportWidth;
		public static float viewportHeight;
		public static float virtualWidth;
		public static float virtualHeight;
		public static float physicalWidth;
		public static float physicalHeight;
		public static float aspectRatio;
	}
	
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private TiledMap map1;
	
	private TextureRegion player;
	
	private static final String BATTLE_FIELD="maps/battle_field.tmx";
	private static final String PLAYER="sprites/characters/Warrior.png";
	
	@Override
	public void create () {
		setupViewport(10, 10);		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		camera.update();
				
		Utility.loadAssetOfGivenType(BATTLE_FIELD, TiledMap.class);
		
		map1=Utility.getAssetOfGivenType(BATTLE_FIELD, TiledMap.class);
		renderer=new OrthogonalTiledMapRenderer(map1, UNIT_SCALE);
		
		batch=new SpriteBatch();
		font=new BitmapFont();
		
		TextureRegion[][] regions=getTextureRegionsFromTexture(PLAYER);
		player=regions[0][0];
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.position.set(playerStartX*UNIT_SCALE, playerStartY*UNIT_SCALE, 0);
		camera.update();
		renderer.setView(camera);
		renderer.render();
		
		batch.begin();
		batch.draw(player, playerStartX, playerStartY);
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
		batch.end();
	}
	
	@Override
	public void dispose () {
		Utility.unloadAsset(BATTLE_FIELD);
	}
	
	/**
	 * Ta metoda wczytuje zasób tekstury, a nastepnie pobiera z niego dostêpne regiony tekstury dziel¹c j¹ na kafelki o wymiarach takich jak sama encja.
	 * 
	 * @param textureName - œcie¿ka do zasobu
	 * @return tablica dwuwymiarowa regionów tekstur
	 */
	private TextureRegion[][] getTextureRegionsFromTexture(String textureName) {
		Utility.loadAssetOfGivenType(textureName, Texture.class);
		Texture texture = Utility.getAssetOfGivenType(textureName, Texture.class);
		return TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
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
}
