package application.characters;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.components.EnemyInputComponent;
import application.components.PlayerInputComponent;
import application.entity.EntityManager;
import application.maps.MapManager;

public class CharacterManager {
	private static final String TAG = CharacterManager.class.getSimpleName();
	private Map<String, List<Vector2>> enemySpawnLocations;
	private static Array<Character> characters;
	private static Character player;
	private static EntityManager entityManager;
	
	// sprites main folder
	private static final String CHARACTER_FOLDER = "sprites/characters";
	
	//sprites paths
	private static final String WARRIOR_PATH = "/Warrior.png";
	private static final String MAGE_PATH = "/Mage.png";
	
	public CharacterManager(Map<String, List<Vector2>> enemySpawnLocations, EntityManager entityManager) {
		characters = new Array<>(100);
		this.enemySpawnLocations=enemySpawnLocations;
		CharacterManager.entityManager=entityManager;
	}

// TODO Remove unused code found by UCDetector
// 	public void removeCharacter(Character character) {
// 		boolean result=characters.removeValue(character, true);
// 		System.out.println(result);
// 	}

	public Array<Character> getCharacters() {
		return characters;
	}
	
	public Character createCharacter(String entityType, Vector2 position) {
		Character character=getCharacter(entityType);
		if(character!=null) {
			characters.add(character);
			entityManager.addEntity(character);
			character.init(position, true);
		}
		return character;
	}
	
	private Character getCharacter(String entityType) {
		switch (entityType) { //@formatter:off
			case MapManager.PLAYER:player=new Character(CHARACTER_FOLDER + WARRIOR_PATH,new Vector2(), new PlayerInputComponent(this));return player;
			case MapManager.MAGE:return new Character(CHARACTER_FOLDER + MAGE_PATH,new Vector2(), new EnemyInputComponent());
			default: Gdx.app.debug(TAG, "Entity type "+entityType+" not defined!!!");return null;
		} //@formatter:on
	}
	
	public void initAllCharacters() {
		for (String enemyName : enemySpawnLocations.keySet()) {
			switch (enemyName) {
				case MapManager.MAGE: {
					for(Vector2 position:enemySpawnLocations.get(enemyName))
						createCharacter(MapManager.MAGE, position);
					break;
				}
			}
		}
	}
}
