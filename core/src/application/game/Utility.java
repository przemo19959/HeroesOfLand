package application.game;

import java.text.MessageFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * Ta klasa odpowiada za ³adowanie, pobieranie i zwalnianie ró¿nego rodzajów zasobów.
 */
public final class Utility {
	private static final String TAG = Utility.class.getSimpleName();
	
	private static final String ASSET_IS_NOT_LOADED_NOTHING_TO_UNLOAD = "Asset is not loaded; Nothing to unload: {0}";
	private static final String ASSET_IS_NOT_LOADED = "{0} is not loaded!: {1}";
	private static final String ASSET_DOESNT_EXIST = "{0} doesn''t exist!: {1}";
	private static final String ASSET_LOADED = "{0} loaded!: {1}";
	
	public static final int FRAME_WIDTH = 16;
	public static final int FRAME_HEIGHT = 16;
	private static final String NUMBER_OF_ANIMATIONS_ERROR = "ERROR number of animations for entity is equal to 0, must be at least 1";
	private static final String NULL_ANIMATION_FRAME_AT = "Got null animation frame at: {0},{1}";
	
	private static final AssetManager ASSET_MANAGER = new AssetManager();
	private static final InternalFileHandleResolver FILE_PATH_RESOLVER = new InternalFileHandleResolver();

	private static final String STATUS_TEXTURE_ATLAS_PATH = "skins/statusui.atlas";
	public static final TextureAtlas STATUSUI_TEXTUREATLAS = new TextureAtlas(STATUS_TEXTURE_ATLAS_PATH);
	private static final String STATUS_SKIN_PATH = "skins/statusui.json";
	public static final Skin STATUSUI_SKIN = new Skin(Gdx.files.internal(STATUS_SKIN_PATH), STATUSUI_TEXTUREATLAS);

	public static void unloadAsset(String assetFilenamePath) {
		if(isAssetLoaded(assetFilenamePath)) {
			ASSET_MANAGER.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, MessageFormat.format(ASSET_IS_NOT_LOADED_NOTHING_TO_UNLOAD, assetFilenamePath));
		}
	}

	// TODO Remove unused code found by UCDetector
	// 	public static float loadCompleted() {
	// 		return assetManager.getProgress();
	// 	}

	// TODO Remove unused code found by UCDetector
	// 	public static int numberAssetsQueued() {
	// 		return assetManager.getQueuedAssets();
	// 	}

	// TODO Remove unused code found by UCDetector
	// 	public static boolean updateAssetLoading() {
	// 		return assetManager.update();
	// 	}

	//@formatter:off
	public static boolean isAssetLoaded(String fileName) {return ASSET_MANAGER.isLoaded(fileName);}
	private static boolean isFileNameWrong(String fileName) {return (fileName == null || fileName.isEmpty());}
	//@formatter:on

	public static <T> void loadAssetOfGivenType(String fileNamePath, Class<T> assetType) {
		if(isFileNameWrong(fileNamePath) == false && isAssetLoaded(fileNamePath) == false) {
			loadAsset(fileNamePath, assetType);
		}
	}

	private static <T> void loadAsset(String fileNamePath, Class<T> assetType) {
		if(FILE_PATH_RESOLVER.resolve(fileNamePath).exists()) {
			setAssetLoader(assetType);
			ASSET_MANAGER.load(fileNamePath, assetType);
			ASSET_MANAGER.finishLoadingAsset(fileNamePath);
			Gdx.app.debug(TAG, MessageFormat.format(ASSET_LOADED, assetType.getSimpleName(), fileNamePath));
		} else {
			Gdx.app.debug(TAG, MessageFormat.format(ASSET_DOESNT_EXIST, assetType.getSimpleName(), fileNamePath));
		}
	}

	private static void setAssetLoader(Class<?> assetType) {
		switch (assetType.getSimpleName()) { //@formatter:off
			case "TiledMap":ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(FILE_PATH_RESOLVER));break;
			case "Texture":ASSET_MANAGER.setLoader(Texture.class, new TextureLoader(FILE_PATH_RESOLVER));break;
			case "Sound":ASSET_MANAGER.setLoader(Sound.class, new SoundLoader(FILE_PATH_RESOLVER));break;
			case "Music":ASSET_MANAGER.setLoader(Music.class, new MusicLoader(FILE_PATH_RESOLVER));break;
		}//@formatter:on
	}

	public static <T> T getAssetOfGivenType(String fileNamePath, Class<T> assetType) {
		T asset = (ASSET_MANAGER.isLoaded(fileNamePath)) ? ASSET_MANAGER.get(fileNamePath, assetType) : null;
		if(asset == null)
			Gdx.app.debug(TAG, MessageFormat.format(ASSET_IS_NOT_LOADED, assetType.getSimpleName(), fileNamePath));
		return asset;
	}
	
	public static Array<Animation<TextureRegion>> loadAndGetAnimations(String spritePath,int numberOfAnimations,int numberOfFrames, float frameDuration, PlayMode playMode, int frameWidth, int frameHeight) {
		Texture texture = Utility.getAssetOfGivenType(spritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, frameWidth, frameHeight);

		int noa = numberOfAnimations;
		if(noa == 0)
			Gdx.app.debug(TAG, NUMBER_OF_ANIMATIONS_ERROR);
		Array<Animation<TextureRegion>> result = new Array<>(noa);

		Array<TextureRegion> frames = new Array<>(numberOfFrames);
		for(int i = 0;i < noa;i++) {
			for(int j = 0;j < numberOfFrames;j++) {
				TextureRegion region = textureFrames[i][j];
				if(region == null)
					Gdx.app.debug(TAG, MessageFormat.format(NULL_ANIMATION_FRAME_AT, i, i));
				frames.add(region);
			}
			result.add(new Animation<>(frameDuration, frames, playMode));
			frames.clear(); //important
		}
		return result;
	}
}
