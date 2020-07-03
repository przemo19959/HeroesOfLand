package application.pathfinder;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Tile {
	//concrete tile numbers for concrete tileset for which there is no connection
	public static final int[] noUpConnectionTileIDs= {928,929,930,931,932,933};
	public static final int[] noDownConnectionTileIDs= {896,897,898,899,900,901};
	
	private final int leftBottomX;
	private final int leftBottomY;
	private final float centerX;
	private final float centerY;
	
	private boolean noUpConnections, noDownConnections;
	public enum ConnectDirection{
		NoUp, NoDown
	}
	
	private int index;
	
	public Tile(int leftBottomX, int leftBottomY) {
		this.leftBottomX = leftBottomX;
		this.leftBottomY = leftBottomY;
		centerX=leftBottomX+0.5f;
		centerY=leftBottomY+0.5f;
	}
	
	//********************************** methods **************************************************
	public void drawTileAsRectanle(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(leftBottomX, leftBottomY, 1, 1);
	}
		
// TODO Remove unused code found by UCDetector
// 	public void drawLineBetweenTiles(ShapeRenderer shapeRenderer, Tile tile) {
// 		shapeRenderer.line(centerX, centerY, tile.centerX, tile.centerY);
// 	}
	
	public Vector2 getCenter() {
		return new Vector2(centerX, centerY);
	}

	float distanceFrom(Tile tile) {
		return Vector2.dst(centerX, centerY, tile.centerX, tile.centerY);
	}
	
	@Override
	public String toString() {
		return String.format("[%d,%d]", leftBottomX,leftBottomY);
	}
	//*********************************************************************************************
	
	//@formatter:off
	public void setIndex(int index) {this.index = index;}
	public int getIndex() {return index;}
	public void setNoUpConnections(boolean noUpConnections) {this.noUpConnections = noUpConnections;}
	public boolean isNoUpConnections() {return noUpConnections;}
	public void setNoDownConnections(boolean noDownConnections) {this.noDownConnections = noDownConnections;}
	public boolean isNoDownConnections() {return noDownConnections;}
	//@formatter:on
}
