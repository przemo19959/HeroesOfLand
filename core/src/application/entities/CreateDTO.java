package application.entities;

import com.badlogic.gdx.math.Vector2;

public class CreateDTO {
	private final String entitySpritePath;
	private final Vector2 startPosition;
	
	public CreateDTO(String entitySpritePath, Vector2 startPosition) {
		this.entitySpritePath = entitySpritePath;
		this.startPosition = startPosition;
	}
	
	//@formatter:off
	public String getEntitySpritePath() {return entitySpritePath;}
	public Vector2 getStartPosition() {return startPosition;}
	//@formatter:on
}
