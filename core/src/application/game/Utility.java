package application.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Ta klasa odpowiada za ³adowanie, pobieranie i zwalnianie ró¿nego rodzajów zasobów.
 */
public final class Utility {
	public static final AssetManager assetManager = new AssetManager();
	private static final String TAG = Utility.class.getSimpleName();
	private static InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
	
	public static void unloadAsset(String assetFilenamePath) {
		if(assetManager.isLoaded(assetFilenamePath)) {
			assetManager.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath);
		}
	}

	public static float loadCompleted() {
		return assetManager.getProgress();
	}

	public static int numberAssetsQueued() {
		return assetManager.getQueuedAssets();
	}

	public static boolean updateAssetLoading() {
		return assetManager.update();
	}

	public static boolean isAssetLoaded(String fileName) {
		return assetManager.isLoaded(fileName);

	}
	
	public static <T> void loadAssetOfGivenType(String fileNamePath, Class<T> assetType) {
		if(!isFileNameWrong(fileNamePath) && !isAssetLoaded(fileNamePath)) {
			loadAsset(fileNamePath, assetType);
		}
	}

	private static <T> void loadAsset(String fileNamePath, Class<T> assetType) {
		if(filePathResolver.resolve(fileNamePath).exists()) {
			setAssetLoader(assetType);
			assetManager.load(fileNamePath, assetType);
			assetManager.finishLoadingAsset(fileNamePath);
			Gdx.app.debug(TAG, assetType.getSimpleName()+" loaded!: " + fileNamePath);
		} else {
			Gdx.app.debug(TAG, assetType.getSimpleName()+" doesn't exist!: " + fileNamePath);
		}
	}
	
	private static void setAssetLoader(Class<?> assetType) {
		switch (assetType.getSimpleName()) { //@formatter:off
			case "TiledMap":assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));break;
			case "Texture":assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));break;
			case "Sound":assetManager.setLoader(Sound.class, new SoundLoader(filePathResolver));break;
			case "Music":assetManager.setLoader(Music.class, new MusicLoader(filePathResolver));break;
		}//@formatter:on
	}

	private static boolean isFileNameWrong(String fileName) {
		return (fileName == null || fileName.isEmpty()) ? true : false;
	}

	public static <T> T getAssetOfGivenType(String fileNamePath, Class<T> assetType) {
		T asset = (assetManager.isLoaded(fileNamePath)) ? assetManager.get(fileNamePath, assetType) : null;
		if(asset == null)
			Gdx.app.debug(TAG, assetType.getSimpleName() + " is not loaded: " + fileNamePath);
		return asset;
	}
}
