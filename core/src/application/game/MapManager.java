package application.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;

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

	private Vector2 playerStartPositionRect;
	private Vector2 closestPlayerStartPosition;
	private Vector2 convertedUnits;

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

		playerStartPositionRect = new Vector2(0, 0);
		closestPlayerStartPosition = new Vector2(0, 0);
		convertedUnits = new Vector2(0, 0);
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

		collisionLayer = currentMap.getLayers().get(MapLayerName.MAP_COLLISION_LAYER.toString());
		if (collisionLayer == null) {
			Gdx.app.debug(TAG, "No collision layer!");
		}

		portalLayer = currentMap.getLayers().get(MapLayerName.MAP_PORTAL_LAYER.toString());
		if (portalLayer == null) {
			Gdx.app.debug(TAG, "No portal layer!");
		}

		spawnsLayer = currentMap.getLayers().get(MapLayerName.MAP_SPAWNS_LAYER.toString());
		if (spawnsLayer == null) {
			Gdx.app.debug(TAG, "No spawn layer!");
		} else {
			getSpawnLocationsForEntities();
			// Vector2 start = playerStartLocationTable.get(currentMapName);
			// if (start.isZero()) {
			// setClosestStartPosition(playerStart);
			// start = playerStartLocationTable.get(currentMapName);
			// }
			// playerStart.set(start.x, start.y);
		}

		Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")");
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
			switch (object.getName()) {
			case PLAYER_START:playerStart.set(position);break;
			case MAGE: {
				if (!enemySpawnLocationMap.containsKey(MAGE))
					enemySpawnLocationMap.put(MAGE, new ArrayList<>());
				enemySpawnLocationMap.get(MAGE).add(position);
				break;
			}
			}
		}
	}

//	private void setClosestStartPosition(final Vector2 position) {
//		Gdx.app.debug(TAG, "setClosestStartPosition INPUT: (" + position.x + "," + position.y + ") " + currentMapName);
//
//		// Get last known position on this map
//		playerStartPositionRect.set(0, 0);
//		closestPlayerStartPosition.set(0, 0);
//		float shortestDistance = 0f;
//
//		// Go through all player start positions and choose closest to last known
//		// position
//		for (MapObject object : spawnsLayer.getObjects()) {
//			if (object.getName().equalsIgnoreCase(PLAYER_START)) {
//				((RectangleMapObject) object).getRectangle().getPosition(playerStartPositionRect);
//				float distance = position.dst2(playerStartPositionRect);
//
//				Gdx.app.debug(TAG, "distance: " + distance + " for " + currentMapName);
//
//				if (distance < shortestDistance || shortestDistance == 0) {
//					closestPlayerStartPosition.set(playerStartPositionRect);
//					shortestDistance = distance;
//					Gdx.app.debug(TAG, "closest START is: (" + closestPlayerStartPosition.x + ","
//							+ closestPlayerStartPosition.y + ") " + currentMapName);
//				}
//			}
//		}
//		playerStartLocationTable.put(currentMapName, closestPlayerStartPosition.cpy());
//	}
//
//	public void setClosestStartPositionFromScaledUnits(Vector2 position) {
//		if (UNIT_SCALE > 0) {
//			convertedUnits.set(position.x / UNIT_SCALE, position.y / UNIT_SCALE);
//			setClosestStartPosition(convertedUnits);
//		}
//	}

}
