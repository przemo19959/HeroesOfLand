package application.entities.concrete.dtos;

import com.badlogic.gdx.math.Vector2;

import application.components.InputComponent;
import application.entities.CreateDTO;

public class CharacterDTO extends CreateDTO {
	private final InputComponent inputComponent;
	private final int healthPoints;
	
	public CharacterDTO(String entitySpritePath, Vector2 startPosition,//
	                    InputComponent inputComponent, int healthPoints) {
		super(entitySpritePath, startPosition);
		this.inputComponent = inputComponent;
		this.healthPoints = healthPoints;
	}
	
	//@formatter:off
	public InputComponent getInputComponent() {return inputComponent;}
	public int getHealthPoints() {return healthPoints;}
	//@formatter:on
}
