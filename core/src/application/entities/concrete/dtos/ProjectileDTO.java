package application.entities.concrete.dtos;

import com.badlogic.gdx.math.Vector2;

import application.entities.CreateDTO;
import application.entities.concrete.entities.Character;

public class ProjectileDTO extends CreateDTO {
	private final Character caster;
	private final Vector2 endPosition;

	public ProjectileDTO(	String entitySpritePath, Vector2 startPosition, //
							Character caster, Vector2 endPosition) {
		super(entitySpritePath, startPosition);
		this.caster = caster;
		this.endPosition=endPosition;
	}
	
	//@formatter:off
	public Character getCaster() {return caster;}
	public Vector2 getEndPosition() {return endPosition;}
	//@formatter:on
}
