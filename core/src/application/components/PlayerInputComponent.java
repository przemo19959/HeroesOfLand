package application.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import application.entity.Entity.Direction;
import application.entity.Entity.State;
import application.game.MainGameScreen;
import application.maps.MapManager;
import application.projectiles.ProjectileManager;

public class PlayerInputComponent extends InputComponent {
	private Vector3 lastMouseCoordinates;

	public PlayerInputComponent() {
		this.lastMouseCoordinates = new Vector3();
	}

	@Override
	public boolean keyDown(int keycode) { //@formatter:off
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A) setKeyState(Keys.LEFT, true);
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) setKeyState(Keys.RIGHT, true);
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W) setKeyState(Keys.UP, true);
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S) setKeyState(Keys.DOWN, true);
		if (keycode==Input.Keys.P) setKeyState(Keys.FIRE, true);
		if( keycode == Input.Keys.Q) setKeyState(Keys.QUIT, true);
		return true; //@formatter:on
	}

	@Override
	public boolean keyUp(int keycode) { //@formatter:off
		if( keycode == Input.Keys.LEFT || keycode == Input.Keys.A) setKeyState(Keys.LEFT, false);
		if( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) setKeyState(Keys.RIGHT, false);
		if( keycode == Input.Keys.UP || keycode == Input.Keys.W ) setKeyState(Keys.UP, false);
		if( keycode == Input.Keys.DOWN || keycode == Input.Keys.S) setKeyState(Keys.DOWN, false);
		if (keycode==Input.Keys.P) setKeyState(Keys.FIRE, false);
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
		MainGameScreen.camera.unproject(lastMouseCoordinates);
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
	@Override
	public void update(float delta) {
		processInput(delta);
	}
	
	
	public static void hide(){
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.QUIT, false);
	}
	
	private void processInput(float delta){
		if(keys.get(Keys.QUIT)) Gdx.app.exit();
		changeDirectionFlagAndMoveEntity(delta, Keys.LEFT, Direction.LEFT);
		changeDirectionFlagAndMoveEntity(delta, Keys.RIGHT, Direction.RIGHT);
		changeDirectionFlagAndMoveEntity(delta, Keys.UP, Direction.UP);
		changeDirectionFlagAndMoveEntity(delta, Keys.DOWN, Direction.DOWN);
		
		if(mouseButtons.get(Mouse.SELECT)) {
			mouseButtons.put(Mouse.SELECT, false);
		}
		
		if(mouseButtons.get(Mouse.DOACTION)) {
			mouseButtons.put(Mouse.DOACTION, false);
//			System.out.println(entity.getFrameSprite().getX()+", "+entity.getFrameSprite().getY());
			ProjectileManager.createProjectile(entity,ProjectileManager.FIRE_BALL,ProjectileManager.FIRE_EXPLOSION,MainGameScreen.player.getCurrentPosition(),
			                                   getScaledMouseXYCoordinates(false));
		}
	}
	
	private Vector2 getScaledMouseXYCoordinates(boolean scaled) {
		Vector2 point=new Vector2();
		point.x=(scaled)?lastMouseCoordinates.x*MapManager.UNIT_SCALE:lastMouseCoordinates.x;
		point.y=(scaled)?lastMouseCoordinates.y*MapManager.UNIT_SCALE:lastMouseCoordinates.y;
		return point;
	}
		
	private void changeDirectionFlagAndMoveEntity(float delta, Keys key, Direction direction) {
		if(keys.get(key)){
			entity.setDirectionFlag(direction, true);
			moveEntity(delta, direction, State.WALKING);
		}else
			entity.setDirectionFlag(direction, false);
	}
}
