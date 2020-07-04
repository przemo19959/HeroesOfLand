package application.entities.concrete.dtos;

import com.badlogic.gdx.math.Vector2;

import application.components.InputComponent;
import application.entities.CreateDTO;

public class CharacterDTO extends CreateDTO {
	private final InputComponent inputComponent;
	private final int healthPoints;
	private final String name;
	private final String info;

	public CharacterDTO(String entitySpritePath, Vector2 startPosition, //
						InputComponent inputComponent, int healthPoints, String name, String info) {
		super(entitySpritePath, startPosition);
		this.inputComponent = inputComponent;
		this.healthPoints = healthPoints;
		this.name=name;
		this.info=info;
	}

	//@formatter:off
	public InputComponent getInputComponent() {return inputComponent;}
	public int getHealthPoints() {return healthPoints;}
	public String getName() {return name;}
	public String getInfo() {return info;}
	//@formatter:on
}
