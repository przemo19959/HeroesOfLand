package application.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;

import application.game.MainGameScreen;
import application.maps.MapManager;
import application.pathfinder.Tile;
import application.projectiles.ProjectileManager;
import application.characters.Character;
import application.characters.CharacterManager;

public class PlayerInputComponent extends InputComponent {
	private Vector3 moveMousePosition;
	private Vector3 leftButtonMouseLastPosition;
	private Vector3 rightButtonMouseLastPosition;
	private CharacterManager characterManager;
	
	private boolean move;
	private boolean enemyClicked;
	private Character attackedCharacter;
	
	private GraphPath<Tile> tilePath;
	private Queue<Vector2> entityPath;
	
	public PlayerInputComponent(CharacterManager characterManager) {
		this.characterManager=characterManager;
		leftButtonMouseLastPosition=new Vector3();
		rightButtonMouseLastPosition=new Vector3();
		moveMousePosition=new Vector3();
		entityPath=new Queue<>(40);
	}

	@Override
	public boolean keyDown(int keycode) { //@formatter:off
		if( keycode == Input.Keys.Q) setKeyState(Keys.QUIT, true);
		return true; //@formatter:on
	}

	@Override
	public boolean keyUp(int keycode) { //@formatter:off
		if( keycode == Input.Keys.Q) setKeyState(Keys.QUIT, false);
		return true; //@formatter:on
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { //@formatter:off
//		if( button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT ) setClickedMouseCoordinates(screenX, screenY);
		if( button == Input.Buttons.LEFT) selectMouseButtonPressed(screenX,screenY);
		if( button == Input.Buttons.RIGHT) doActionMouseButtonPressed(screenX,screenY);
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
		moveMousePosition.set(screenX, screenY, 0);
		MainGameScreen.camera.unproject(moveMousePosition);
		MainGameScreen.drawHealthBar(false,null);
		for(Character character:characterManager.getCharacters()) {
			if(!character.equals(entity) && isPointInRectangle(character.getEntityHitBox(), getScaledMouseXYCoordinates(moveMousePosition, true))) {
				MainGameScreen.drawHealthBar(true,character);
				break;
			}	
		}
		return true;
	}
	
	private boolean isPointInRectangle(Rectangle rectangle, Vector2 point) {
		if(point.x>rectangle.x && point.x<rectangle.x+rectangle.width && point.y>rectangle.y && point.y<rectangle.y+rectangle.height)
			return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	private void setKeyState(Keys key, boolean pressed) {
		keys.put(key, pressed);
	}
			
	private void selectMouseButtonPressed(int x, int y){
		mouseButtons.put(Mouse.SELECT, true);
		leftButtonMouseLastPosition.set(x, y,0);
		MainGameScreen.camera.unproject(leftButtonMouseLastPosition);
		for(Character character:characterManager.getCharacters()) {
			if(!character.equals(entity) && isPointInRectangle(character.getEntityHitBox(), getScaledMouseXYCoordinates(leftButtonMouseLastPosition, true))) {
				enemyClicked=true;
				attackedCharacter=character;
				break;
			}	
		}
	}
	
	private void doActionMouseButtonPressed(int x, int y){
		mouseButtons.put(Mouse.DOACTION, true);
		rightButtonMouseLastPosition.set(x, y,0);
		MainGameScreen.camera.unproject(rightButtonMouseLastPosition);
	}
	
	private void selectMouseButtonReleased(){
		mouseButtons.put(Mouse.SELECT, false);
	}
	
	private void doActionMouseButtonReleased(){
		mouseButtons.put(Mouse.DOACTION, false);
	}
	
	/**
	 * Ta metoda przetwarza zdarzenie wej�ciowe encji. W zale�no�ci od kierunku ruchu, liczona jest kolejna pozycja
	 * na podstawie czasy delty, zmieniany jest stan na chodz�cy (bowiem wcze�niej posta� mog�a sta�), dodatkowo zmieniana
	 * jest aktualna ramka postaci
	 * @param delta - czas delta
	 */
	@Override
	public void update(float delta) {
		processInput();
		if(move) { //@formatter:off
//			System.out.println("move");
			if(entityPath.size>0) moveTowardPoint(delta, entityPath.first());
			else move=false;
		} //@formatter:on
	}
	
	private void processInput(){
		if(keys.get(Keys.QUIT)) Gdx.app.exit();		
		if(mouseButtons.get(Mouse.SELECT)) {
			mouseButtons.put(Mouse.SELECT, false);
			tilePath=MapManager.calculatePath(entity.getCurrentEntityPosition(),getScaledMouseXYCoordinates(leftButtonMouseLastPosition, false));
			addTilesCentersToQueue();
		}
		if(mouseButtons.get(Mouse.DOACTION)) {
			mouseButtons.put(Mouse.DOACTION, false);
			ProjectileManager.createProjectile(entity,ProjectileManager.FIRE_BALL,entity.getCurrentEntityPosition().cpy(),
			                                   getScaledMouseXYCoordinates(rightButtonMouseLastPosition, false));
		}
	}
	
	public Character stopMovingAndAttack() {
		if(move && enemyClicked) {
			move=false;
			enemyClicked=false;
			return attackedCharacter;
		}
		return null;
	}
	
	private void addTilesCentersToQueue() {
		if(tilePath!=null) {
			move=true;
			entityPath.clear();
			for(Tile tile:tilePath)
				entityPath.addLast(tile.getCenter());
		}
	}
	
	private Vector2 getScaledMouseXYCoordinates(Vector3 mouseButtonLastPosition, boolean scaled) {
		Vector2 point=new Vector2();
		point.x=(scaled)?mouseButtonLastPosition.x/MapManager.UNIT_SCALE:mouseButtonLastPosition.x;
		point.y=(scaled)?mouseButtonLastPosition.y/MapManager.UNIT_SCALE:mouseButtonLastPosition.y;
		return point;
	}
			
	private void moveTowardPoint(float delta, Vector2 goalPosition) {
		if(Vector2.dst(goalPosition.x, goalPosition.y, entity.getCurrentEntityPosition().x, entity.getCurrentEntityPosition().y)>0.05) {
			entity.calculateNextPositionToward(goalPosition.cpy(), delta);
		}else
			entityPath.removeFirst();
	}
}
