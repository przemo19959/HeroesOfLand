package application.pathfinder;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class TileGraph implements IndexedGraph<Tile>{
	private TileHeuristic tileHeuristic=new TileHeuristic();
	private Array<Tile> tiles=new Array<>();
	private Array<TileConnection> connections=new Array<>();
	
	private ObjectMap<Tile, Array<Connection<Tile>>> connectionMap=new ObjectMap<>();
	private int lastNodeIndex=0;
	
	public void addTile(Tile tile) {
		tile.setIndex(lastNodeIndex);
		lastNodeIndex++;
		tiles.add(tile);
	}
	
	public void connectTiles(Tile fromTile, Tile toTile) {
		TileConnection connection=new TileConnection(fromTile, toTile);
		if(!connectionMap.containsKey(fromTile))
			connectionMap.put(fromTile, new Array<>());
		connectionMap.get(fromTile).add(connection);
		connections.add(connection);
	}
	
	public GraphPath<Tile> findPath(Tile startTile, Tile goalTile){
		GraphPath<Tile> tilePath=new DefaultGraphPath<>();
		new IndexedAStarPathFinder<>(this).searchNodePath(startTile, goalTile, tileHeuristic, tilePath);
		return tilePath;
	}
	
// TODO Remove unused code found by UCDetector
// 	public void printCount() {
// 		System.out.println("Tiles size: "+tiles.size+", Connections size: "+connections.size);
// 	}
		
	@Override
	public Array<Connection<Tile>> getConnections(Tile fromNode) {
		if(connectionMap.containsKey(fromNode))
			return connectionMap.get(fromNode);
		return new Array<>(0);
	}

	@Override
	public int getIndex(Tile node) {
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		return lastNodeIndex;
	}
}
