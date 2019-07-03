package application.pathfinder;

import com.badlogic.gdx.ai.pfa.Connection;

class TileConnection implements Connection<Tile>{
	private Tile fromTile;
	private Tile toTile;
	private float cost;
	
	TileConnection(Tile fromTile, Tile toTile) {
		this.fromTile = fromTile;
		this.toTile = toTile;
		cost=fromTile.distanceFrom(toTile);
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public Tile getFromNode() {
		return fromTile;
	}

	@Override
	public Tile getToNode() {
		return toTile;
	}
}
