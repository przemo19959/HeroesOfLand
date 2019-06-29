package application.components;

import com.badlogic.gdx.InputProcessor;

import application.entity.Entity;
import application.entity.Entity.Direction;
import application.entity.Entity.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasa abstrakcyjna komponentu wej�ciowego. Ma na celu obs�ug� zdarze� wej�ciowych pochodz�cych od u�ytkownika. Posiada aktualny stan, kierunek encji oraz mapy dla przycisk�w klawiatury i myszy,
 * wskazuj�cych na to czy dany przycisk zosta� wci�ni�ty.
 */
public abstract class InputComponent implements InputProcessor {
	protected Entity entity;
	
	/**
	 * Enum przechowuj�ce list� aktywnych (obs�ugiwanych w grze) przycisk�w
	 */
	protected enum Keys {
		LEFT,
		RIGHT,
		UP,
		DOWN,
		QUIT,
		FIRE
	}
	
	/**
	 * Enum z mo�liwymi stanami przycisk�w myszy.
	 */
	protected enum Mouse {
		SELECT,
		DOACTION
	}

	protected static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
	protected static Map<Mouse, Boolean> mouseButtons = new HashMap<Mouse, Boolean>();

	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
		keys.put(Keys.FIRE, false);
	};

	static {
		mouseButtons.put(Mouse.SELECT, false);
		mouseButtons.put(Mouse.DOACTION, false);
	};
	
	protected InputComponent() {
	}
	
	/**
	 * Abstrakcyjna metoda do przetworzenia zdarze� wej�ciowych gry.
	 * @param entity - encja
	 * @param delta - czas delta klatki
	 */
	public abstract void update(float delta);
	
	public void setEntity(Entity entity) {
		this.entity=entity;
	}

	protected void moveEntity(float delta, Direction direction, State state) {
		entity.calculateNextPosition(delta);
		entity.setState(state);
		entity.setDirection(direction);
	}
}
