package application.components.concrete;

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
import application.components.InputComponent;
import application.entities.EntityManager;
import application.entities.concrete.dtos.ProjectileDTO;
import application.entities.concrete.entities.Character;

public class PlayerInputComponent extends InputComponent {
	private static final String TAG = PlayerInputComponent.class.getSimpleName();

	
	private Vector3 leftButtonMouseLastPosition;
	private Vector3 rightButtonMouseLastPosition;
	private Vector3 moveMousePosition;
	private Queue<Vector2> pathCenters;

	private boolean move;
	private boolean enemyClicked;
	private boolean attack;
	private Character attackedCharacter;
	private GraphPath<Tile> tilePath;

	public PlayerInputComponent(EntityManager entityManager) {
		super(entityManager);

		leftButtonMouseLastPosition = new Vector3();
		rightButtonMouseLastPosition = new Vector3();
		moveMousePosition = new Vector3();
		pathCenters = new Queue<>(40);
	}

	//********************************** Key processing *******************************************
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

	private void setKeyState(Keys key, boolean pressed) {
		keys.put(key, pressed);
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	//*********************************************************************************************

	//********************************** Mouse processing *****************************************
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) { //@formatter:off
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
		MainGameScreen.drawHealthBar(false, null);
		for(Character character:getEntityManager().getEntitiesOfType(Character.class)) {
			if(character.equals(entity)==false && isPointInRectangle(character.getEntityHitBox(), getMouseXYCoordinates(moveMousePosition, true))) {
				MainGameScreen.drawHealthBar(true, character);
				break;
			}
		}
		return true;
	}

	private boolean isPointInRectangle(Rectangle rectangle, Vector2 point) {
		if(point.x > rectangle.x && point.x < rectangle.x + rectangle.width && point.y > rectangle.y && point.y < rectangle.y + rectangle.height)
			return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	private void selectMouseButtonPressed(int x, int y) {
		mouseButtons.put(Mouse.SELECT, true);
		leftButtonMouseLastPosition.set(x, y, 0);
		MainGameScreen.camera.unproject(leftButtonMouseLastPosition);
		for(Character character:getEntityManager().getEntitiesOfType(Character.class)) {
			if(character.equals(entity) == false && //
				isPointInRectangle(character.getEntityHitBox(), getMouseXYCoordinates(leftButtonMouseLastPosition, true))) {
				enemyClicked = true;
				attackedCharacter = character;
				break;
			}
		}
	}

	private void doActionMouseButtonPressed(int x, int y) {
		mouseButtons.put(Mouse.DOACTION, true);
		rightButtonMouseLastPosition.set(x, y, 0);
		MainGameScreen.camera.unproject(rightButtonMouseLastPosition);
	}

	private void selectMouseButtonReleased() {
		mouseButtons.put(Mouse.SELECT, false);
	}

	private void doActionMouseButtonReleased() {
		mouseButtons.put(Mouse.DOACTION, false);
	}

	/**
	 * Ta metoda przetwarza zdarzenie wejœciowe encji. W zale¿noœci od kierunku ruchu, liczona jest kolejna pozycja na podstawie czasy delty, zmieniany jest stan na chodz¹cy (bowiem wczeœniej postaæ
	 * mog³a staæ), dodatkowo zmieniana jest aktualna ramka postaci
	 * 
	 * @param delta - czas delta
	 */
	@Override
	public void update(float delta) {
		processInput();
		if(move) { //@formatter:off
//			System.out.println("move");
			Gdx.app.debug(TAG, "Enemy clicked?: "+enemyClicked);
//			if(pathCenters.size>0) moveTowardPoint(delta, pathCenters.first());
			if((enemyClicked && pathCenters.size>1) || (enemyClicked==false && pathCenters.size>0)) moveTowardPoint(delta, pathCenters.first());
			else if(move && enemyClicked && pathCenters.size==1) { //attack
				move=false;
				enemyClicked=false;
			}
			else move=false;
		} //@formatter:on
	}

	private void processInput() {
		if(keys.get(Keys.QUIT))
			Gdx.app.exit();
		if(mouseButtons.get(Mouse.SELECT)) {
			mouseButtons.put(Mouse.SELECT, false);
			tilePath = MapManager.calculatePath(entity.getCurrentEntityPosition(), getMouseXYCoordinates(leftButtonMouseLastPosition, false));
			addTilesCentersToQueue();
		}
		if(mouseButtons.get(Mouse.DOACTION)) {
			mouseButtons.put(Mouse.DOACTION, false);
			getEntityManager().createAndGetEntity(new ProjectileDTO("sprites/projectiles/fireball.png", entity.getCurrentEntityPosition().cpy(), entity, getMouseXYCoordinates(
				rightButtonMouseLastPosition, false)));
		}
	}
	
	private void addTilesCentersToQueue() {
		if(tilePath != null) {
			move = true;
			pathCenters.clear();
			for(Tile tile:tilePath)
				pathCenters.addLast(tile.getCenter());
			Gdx.app.debug(TAG, "Tile path: "+pathCenters);
		}
	}
	
	public Character stopMovingAndAttack() {
		if(move && enemyClicked) {
			move = false;
			enemyClicked = false;
			return attackedCharacter;
		}
		return null;
	}

	private Vector2 getMouseXYCoordinates(Vector3 mouseButtonLastPosition, boolean scaled) {
		Vector2 point = new Vector2();
		point.x = (scaled) ? mouseButtonLastPosition.x / MapManager.UNIT_SCALE : mouseButtonLastPosition.x;
		point.y = (scaled) ? mouseButtonLastPosition.y / MapManager.UNIT_SCALE : mouseButtonLastPosition.y;
		return point;
	}

	private void moveTowardPoint(float delta, Vector2 goalPosition) {
		if(goalPosition.dst(entity.getCurrentEntityPosition()) > 0.05) {
			entity.calculateNextPositionToward(goalPosition.cpy(), delta);
		} else {
			pathCenters.removeFirst();
		}
	}
}
