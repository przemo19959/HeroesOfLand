package application.game;


import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import application.game.Entity.Direction;
import application.game.Entity.State;

public class PlayerController implements InputProcessor {

	enum Keys {
		LEFT, RIGHT, UP, DOWN, QUIT
	}

	enum Mouse {
		SELECT, DOACTION
	}

	private static Map<Keys, Boolean> keys = new HashMap<PlayerController.Keys, Boolean>();
	private static Map<Mouse, Boolean> mouseButtons = new HashMap<PlayerController.Mouse, Boolean>();
	private Vector3 lastMouseCoordinates;

	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
	};

	static {
		mouseButtons.put(Mouse.SELECT, false);
		mouseButtons.put(Mouse.DOACTION, false);
	};

	private Entity player;

	public PlayerController(Entity player){
		this.lastMouseCoordinates = new Vector3();
		this.player = player;
	}

	@Override
	public boolean keyDown(int keycode) { //@formatter:off
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A) setKeyState(Keys.LEFT, true);
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) setKeyState(Keys.RIGHT, true);
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W) setKeyState(Keys.UP, true);
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S) setKeyState(Keys.DOWN, true);
		if( keycode == Input.Keys.Q) setKeyState(Keys.QUIT, true);
		return true; //@formatter:on
	}

	@Override
	public boolean keyUp(int keycode) { //@formatter:off
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A) setKeyState(Keys.LEFT, false);
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) setKeyState(Keys.RIGHT, false);
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W ) setKeyState(Keys.UP, false);
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S) setKeyState(Keys.DOWN, false);
		if( keycode == Input.Keys.Q) setKeyState(Keys.QUIT, false);
		return true; //@formatter:on
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { //@formatter:off
		if( button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT ) setClickedMouseCoordinates(screenX, screenY);
		if( button == Input.Buttons.LEFT) selectMouseButtonPressed();
		if( button == Input.Buttons.RIGHT) doActionMouseButtonPressed();
		return true; //@formatter:on
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { //@formatter:off
		if( button == Input.Buttons.LEFT) selectMouseButtonReleased();
		if( button == Input.Buttons.RIGHT) doActionMouseButtonReleased();
		return true; //@formatter:on
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void dispose(){
		
	}
	
	private void setKeyState(Keys key, boolean pressed) {
		keys.put(key, pressed);
	}
		
	public void setClickedMouseCoordinates(int x,int y){
		lastMouseCoordinates.set(x, y, 0);
	}
	
	public void selectMouseButtonPressed(){
		mouseButtons.put(Mouse.SELECT, true);
	}
	
	public void doActionMouseButtonPressed(){
		mouseButtons.put(Mouse.DOACTION, true);
	}
	
	public void selectMouseButtonReleased(){
		mouseButtons.put(Mouse.SELECT, false);
	}
	
	public void doActionMouseButtonReleased(){
		mouseButtons.put(Mouse.DOACTION, false);
	}
	
	/**
	 * Ta metoda przetwarza zdarzenie wejœciowe encji. W zale¿noœci od kierunku ruchu, liczona jest kolejna pozycja
	 * na podstawie czasy delty, zmieniany jest stan na chodz¹cy (bowiem wczeœniej postaæ mog³a staæ), dodatkowo zmieniana
	 * jest aktualna ramka postaci
	 * @param delta - czas delta
	 */
	public void update(float delta){
		processInput(delta);
	}
	
	public static void hide(){
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
	}
	
	private void processInput(float delta){ //@formatter:off
		if( keys.get(Keys.LEFT)) moveEntity(delta, Direction.LEFT, State.WALKING);
		else if( keys.get(Keys.RIGHT)) moveEntity(delta, Direction.RIGHT, State.WALKING);
		else if( keys.get(Keys.UP)) moveEntity(delta, Direction.UP, State.WALKING);
		else if(keys.get(Keys.DOWN)) moveEntity(delta, Direction.DOWN, State.WALKING);
		else if(keys.get(Keys.QUIT)) Gdx.app.exit();
		else player.setState(Entity.State.IDLE); //@formatter:on
		
		//Mouse input
		if(mouseButtons.get(Mouse.SELECT)) {
			mouseButtons.put(Mouse.SELECT, false);
		}

	}
	
	private void moveEntity(float delta, Direction direction, State state) {
		player.calculateNextPosition(direction, delta);
		player.setState(state);
		player.setDirection(direction);
	}
}
