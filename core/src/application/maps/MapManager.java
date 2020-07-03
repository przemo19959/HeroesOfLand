package application.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Json;

import application.game.Utility;
import application.pathfinder.Tile;
import application.pathfinder.Tile.ConnectDirection;
import application.pathfinder.TileGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MapManager {
	private static final String NO_COLLISION_LAYER = "ERROR: There is no collision layer in current map!";

	private static final String TAG = MapManager.class.getSimpleName();

	// All maps for the game
	private Hashtable<String, String> mapTable;
	private Hashtable<String, Vector2> playerStartLocationTable;

	private Map<String, List<Vector2>> enemySpawnLocationMap;
	private static TileGraph tileGraph;
	private static Tile[][] tileArray;
	private LayerGraph layerGraph;
	private TiledMapTileSet tileSet;

	// maps
	private final static String BATTLE_FIELD = "BATTLE_FIELD";

	public enum MapLayerName {
		MAP_COLLISION_LAYER,
		MAP_SPAWNS_LAYER,
		MAP_PORTAL_LAYER
	}

	// spawns names
	public final static String PLAYER = "PLAYER";
	private final static String PLAYER_START = "PLAYER_START";
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
		tileGraph = new TileGraph();
	}

	private void loadMap(String mapName) {
		playerStart.set(0, 0);
		String mapFullPath = mapTable.get(mapName);

		if(mapFullPath == null || mapFullPath.isEmpty()) {
			Gdx.app.debug(TAG, "Map is invalid");
			return;
		}

		if(currentMap != null)
			currentMap.dispose();

		Utility.loadAssetOfGivenType(mapFullPath, TiledMap.class);
		if(Utility.isAssetLoaded(mapFullPath)) {
			currentMap = Utility.getAssetOfGivenType(mapFullPath, TiledMap.class);
			currentMapName = mapName;
		} else {
			Gdx.app.debug(TAG, "Map not loaded");
			return;
		}

		collisionLayer = getMapLayerIfExists(MapLayerName.MAP_COLLISION_LAYER);
		portalLayer = getMapLayerIfExists(MapLayerName.MAP_PORTAL_LAYER);
		spawnsLayer = currentMap.getLayers().get(MapLayerName.MAP_SPAWNS_LAYER.toString());//@formatter:off
		if (spawnsLayer == null) Gdx.app.debug(TAG, "No spawn layer!");
		else getSpawnLocationsForEntities(); //@formatter:on

		Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")");

		// dodaj kafelki i po³¹czenia do grafu kafelków
		layerGraph = initLayerGraphFromJSON();
		tileSet = currentMap.getTileSets().getTileSet(0);
		tileArray = new Tile[currentMap.getProperties().get("height", Integer.class)][currentMap.getProperties().get("width", Integer.class)];
		scanTileLayersAndCreateTileNodes();
		connectAvailableTiles();		
	}

	private void connectAvailableTiles() {
		for(int i = 0;i < tileArray.length;i++) {
			for(int j = 0;j < tileArray[i].length;j++) {
				if(tileArray[i][j] != null) { //@formatter:off
					if(!tileArray[i][j].isNoUpConnections() && i > 0 && tileArray[i - 1][j] != null) tileGraph.connectTiles(tileArray[i][j], tileArray[i - 1][j]); // góra
					if(j + 1 < tileArray[i].length && tileArray[i][j + 1] != null) tileGraph.connectTiles(tileArray[i][j], tileArray[i][j + 1]); // prawo
					if(j > 0 && tileArray[i][j - 1] != null) tileGraph.connectTiles(tileArray[i][j], tileArray[i][j - 1]); // lewo
					if(!tileArray[i][j].isNoDownConnections() && i + 1 < tileArray.length && tileArray[i + 1][j] != null) tileGraph.connectTiles(tileArray[i][j], tileArray[i + 1][j]); // dó³
				} //@formatter:on
			}
		}
	}

	private void scanTileLayersAndCreateTileNodes() {
		int rowIndex = 0,columnIndex = 0;
		int tileLeftBottomX = 0;
		int tileLeftBottomY = (tileArray.length - 1);
		int tileID = 0;
		for(int tileIndex = 0;tileIndex < layerGraph.backgroundLayer.size;tileIndex++) {
			tileID = layerGraph.backgroundLayer.get(tileIndex);
			if(tileID != 0 && tileSet.getTile(tileID).getProperties().containsKey("walkable")) {
				if((boolean) (tileSet.getTile(tileID).getProperties().get("walkable"))) {
					tileArray[rowIndex][columnIndex] = new Tile(tileLeftBottomX, tileLeftBottomY);
					checkIfGivenTileCantHaveConnections(tileArray[rowIndex][columnIndex], tileIndex, ConnectDirection.NoDown);
					checkIfGivenTileCantHaveConnections(tileArray[rowIndex][columnIndex], tileIndex, ConnectDirection.NoUp);
					tileGraph.addTile(tileArray[rowIndex][columnIndex]);
				} else
					tileArray[rowIndex][columnIndex] = null;
			} else
				tileArray[rowIndex][columnIndex] = null;
			columnIndex++;
			tileLeftBottomX += 1;
			if(columnIndex != 0 && columnIndex % tileArray[0].length == 0) {
				rowIndex++;
				columnIndex = 0;
				tileLeftBottomX = 0;
				tileLeftBottomY -= 1;
			}
		}
	}

	private void checkIfGivenTileCantHaveConnections(Tile tile, int tileIndex, ConnectDirection direction) {
		int tileID = 0;
		switch (direction) { //@formatter:off
			case NoUp:tileID=layerGraph.decorationLayer.get(tileIndex);if(tileID!=0 && Arrays.binarySearch(Tile.noUpConnectionTileIDs, tileID)>0) tile.setNoUpConnections(true);break;
			case NoDown:tileID=layerGraph.topLayer.get(tileIndex);if(tileID!=0 && Arrays.binarySearch(Tile.noDownConnectionTileIDs, tileID)>0) tile.setNoDownConnections(true);break;
		}//@formatter:on
	}

	private LayerGraph initLayerGraphFromJSON() {
		Json json = new Json();
		return json.fromJson(LayerGraph.class, Gdx.files.internal("maps/battle_field.json").readString());
	}

	public void drawGrid(ShapeRenderer shapeRenderer) {
		for(int i = 0;i < tileArray.length;i++) {
			for(int j = 0;j < tileArray[i].length;j++) {
				if(tileArray[i][j] != null) {
					tileArray[i][j].drawTileAsRectanle(shapeRenderer);
					// tileArray[i][j].drawCenterAsPoint(shapeRenderer);
				}
			}
		}
	}

	public static GraphPath<Tile> calculatePath(Vector2 startPosition, Vector2 endPosition) {
		GraphPath<Tile> tilePath = null;
		Tile startTile = tileArray[19 - (int) (startPosition.y)][(int) (startPosition.x)];
		Tile goalTile = tileArray[19 - (int) (endPosition.y)][(int) (endPosition.x)];
		if(startTile != null && goalTile != null) {
			tilePath = tileGraph.findPath(startTile, goalTile);
		} else
			Gdx.app.debug(TAG, "Tiles are null: Pos: " + startPosition + ",Tile: " + startTile + " Pos: " + endPosition + ", " + goalTile);
		return tilePath;
	}

	// public void drawPath(ShapeRenderer shapeRenderer) {
	// if(tilePath != null) {
	// shapeRenderer.setColor(Color.RED);
	// for(int i=1;i<tilePath.getCount();i++) {
	// tilePath.get(i).drawLineBetweenTiles(shapeRenderer, tilePath.get(i-1));
	// }
	// shapeRenderer.setColor(Color.GREEN);
	// }
	// }

	private MapLayer getMapLayerIfExists(MapLayerName layerName) { // formatter:off
		MapLayer layer = currentMap.getLayers().get(layerName.toString());
		if(layer == null)
			Gdx.app.debug(TAG, "No " + layerName + " layer!");
		return layer; // formatter:on
	}

	public TiledMap getCurrentMap() {
		if(currentMap == null) {
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
		return new Vector2(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
	}

	private void getSpawnLocationsForEntities() {
		for(MapObject object:spawnsLayer.getObjects()) {
			Vector2 position = new Vector2();
			position.x = ((RectangleMapObject) object).getRectangle().getX();
			position.y = ((RectangleMapObject) object).getRectangle().getY();
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
	
	public boolean isCollisionWithCollisionLayer(Rectangle hitBox) {
		if(doesGivenLayerExists(MapLayerName.MAP_COLLISION_LAYER)) {
			Rectangle rectangle = null;
			for(MapObject object:getLayer(MapLayerName.MAP_COLLISION_LAYER).getObjects()) {
				if(object instanceof RectangleMapObject) {
					rectangle = ((RectangleMapObject) object).getRectangle();
					if(hitBox.overlaps(rectangle)) {
						return true;
					}
				}
			}
		}else
			Gdx.app.debug(TAG, NO_COLLISION_LAYER);
		return false;
	}

	private boolean doesGivenLayerExists(MapLayerName layerName) {
		return getLayer(layerName) != null;
	}
}
