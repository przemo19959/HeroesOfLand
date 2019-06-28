package application.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;

import application.game.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MapManager {
	private static final String TAG = MapManager.class.getSimpleName();

	// All maps for the game
	private Hashtable<String, String> mapTable;
	private Hashtable<String, Vector2> playerStartLocationTable;

	private Map<String, List<Vector2>> enemySpawnLocationMap;

	// maps
	private final static String BATTLE_FIELD = "BATTLE_FIELD";

	public enum MapLayerName {
		MAP_COLLISION_LAYER, MAP_SPAWNS_LAYER, MAP_PORTAL_LAYER
	}

	// spawns names
	public final static String PLAYER = "PLAYER";
	public final static String PLAYER_START = "PLAYER_START";
	public final static String MAGE = "MAGE";

	private Vector2 playerStart;
	private TiledMap currentMap = null;
	private String currentMapName;
	private MapLayer collisionLayer = null;
	private MapLayer portalLayer = null;
	private MapLayer spawnsLayer = null;

	public final static float UNIT_SCALE = 1 / 16f;

	public Map<String, List<Vector2>> getEnemySpawnLocationMap() {
		return enemySpawnLocationMap;
	}
	
	public Vector2 getPlayerStart() {
		return playerStart;
	}
	
	public MapManager() {
		playerStart = new Vector2(0, 0);
		mapTable = new Hashtable<>();

		mapTable.put(BATTLE_FIELD, "maps/battle_field.tmx");

		playerStartLocationTable = new Hashtable<>();
		playerStartLocationTable.put(BATTLE_FIELD, playerStart.cpy());

		enemySpawnLocationMap = new HashMap<>();
	}

	public void loadMap(String mapName) {
		playerStart.set(0, 0);
		String mapFullPath = mapTable.get(mapName);

		if (mapFullPath == null || mapFullPath.isEmpty()) {
			Gdx.app.debug(TAG, "Map is invalid");
			return;
		}
		if (currentMap != null)
			currentMap.dispose();

		Utility.loadAssetOfGivenType(mapFullPath, TiledMap.class);
		if (Utility.isAssetLoaded(mapFullPath)) {
			currentMap = Utility.getAssetOfGivenType(mapFullPath, TiledMap.class);
			currentMapName = mapName;
		} else {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}
		
		collisionLayer=fun(MapLayerName.MAP_COLLISION_LAYER);
		portalLayer=fun(MapLayerName.MAP_PORTAL_LAYER);		
		spawnsLayer = currentMap.getLayers().get(MapLayerName.MAP_SPAWNS_LAYER.toString());
		//@formatter:off
		if (spawnsLayer == null) Gdx.app.debug(TAG, "No spawn layer!");
		else getSpawnLocationsForEntities(); //@formatter:on

		Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")");
	}
	
	private MapLayer fun(MapLayerName layerName) { //formatter:off
		MapLayer layer = currentMap.getLayers().get(layerName.toString());
		if (layer == null) Gdx.app.debug(TAG, "No collision layer!");
		return layer; //formatter:on
	}

	public TiledMap getCurrentMap() {
		if (currentMap == null) {
			currentMapName = BATTLE_FIELD;
			loadMap(currentMapName);
		}
		return currentMap;
	}

	public MapLayer getLayer(MapLayerName layerName) {
		switch (layerName) {//@formatter:off
			case MAP_COLLISION_LAYER:return collisionLayer;
			case MAP_PORTAL_LAYER:return portalLayer;
			case MAP_SPAWNS_LAYER:return spawnsLayer;
			default: return null;
		}//@formatter:on
	}

	public Vector2 getPlayerStartUnitScaled() {
		return new Vector2(playerStart.x*UNIT_SCALE, playerStart.y*UNIT_SCALE);
	}

	private void getSpawnLocationsForEntities() {
		for (MapObject object : spawnsLayer.getObjects()) {
			Vector2 position = new Vector2();
			position.x=((RectangleMapObject) object).getRectangle().getX();
			position.y=((RectangleMapObject) object).getRectangle().getY();
			switch (object.getName()) {//@formatter:off
				case PLAYER_START:playerStart.set(position);break;
				case MAGE: {
					if (!enemySpawnLocationMap.containsKey(MAGE))
						enemySpawnLocationMap.put(MAGE, new ArrayList<>());
					enemySpawnLocationMap.get(MAGE).add(position);
					break;
				}
			}//@formatter:on
		}
	}
}
