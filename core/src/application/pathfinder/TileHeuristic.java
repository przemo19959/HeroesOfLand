package application.pathfinder;

import com.badlogic.gdx.ai.pfa.Heuristic;

class TileHeuristic implements Heuristic<Tile> {
	@Override
	public float estimate(Tile node, Tile endNode) {
		return node.distanceFrom(endNode);
	}
}
