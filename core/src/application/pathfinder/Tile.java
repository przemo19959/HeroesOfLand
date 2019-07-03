package application.pathfinder;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Tile {
	public static final int[] noUpConnectionTileIDs= {928,929,930,931,932,933};
	public static final int[] noDownConnectionTileIDs= {896,897,898,899,900,901};
	
	private int leftBottomX;
	private int leftBottomY;
	private int index;
	
	private float centerX;
	private float centerY;
	
	private boolean noUpConnections, noDownConnections;
	public enum ConnectDirection{
		NoUp, NoDown
	}
	
	public Tile(int leftBottomX, int leftBottomY) {
		this.leftBottomX = leftBottomX;
		this.leftBottomY = leftBottomY;
		centerX=leftBottomX+0.5f;
		centerY=leftBottomY+0.5f;
	}
	
	public boolean isNoUpConnections() {
		return noUpConnections;
	}
	
	public boolean isNoDownConnections() {
		return noDownConnections;
	}
	
	public void setNoUpConnections(boolean noUpConnections) {
		this.noUpConnections = noUpConnections;
	}
	
	public void setNoDownConnections(boolean noDownConnections) {
		this.noDownConnections = noDownConnections;
	}

	public void drawTileAsRectanle(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(leftBottomX, leftBottomY, 1, 1);
	}
		
// TODO Remove unused code found by UCDetector
// 	public void drawLineBetweenTiles(ShapeRenderer shapeRenderer, Tile tile) {
// 		shapeRenderer.line(centerX, centerY, tile.centerX, tile.centerY);
// 	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Vector2 getCenter() {
		return new Vector2(centerX, centerY);
	}

	float distanceFrom(Tile tile) {
		return Vector2.dst(centerX, centerY, tile.centerX, tile.centerY);
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d)", leftBottomX,leftBottomY);
	}
}
