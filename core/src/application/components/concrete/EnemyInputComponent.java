package application.components.concrete;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

import application.components.InputComponent;
import application.entities.EntityManager;
import application.entities.concrete.entities.Character;
import application.game.MainGameScreen;
import application.maps.MapManager;
import application.pathfinder.Tile;

public class EnemyInputComponent extends InputComponent {
	private static final float FOLLOW_DISTANCE=10f;
	
	private GraphPath<Tile> tilePath;
	private Queue<Vector2> entityPath;
	private boolean move;
	
	public EnemyInputComponent(EntityManager entityManager) {
		super(entityManager);
		entityPath=new Queue<>(40);
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
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

	@Override
	public void update(float delta) {
//		if(collidedWithEntity==false) {
//			followPlayer();
//			if(move) {
//				if(entityPath.size>0) moveTowardPoint(delta, entityPath.first());
//				else move=false;
//			}
//		}
	}
	
	private void followPlayer() {
		Vector2 currentplayerPosition=new Vector2(getEntityManager().getPlayer().getCurrentEntityPosition());
		if(currentplayerPosition.dst2(entity.getCurrentEntityPosition())<FOLLOW_DISTANCE) {
			tilePath=MapManager.calculatePath(entity.getCurrentEntityPosition(), currentplayerPosition);
			addTilesCentersToQueue();
		}
	}
	
	public Character stopMovingAndAttack() {
		return null;
	}
		
	private void addTilesCentersToQueue() {
		if(tilePath!=null) {
			move=true;
			entityPath.clear();
			for(Tile tile:tilePath)
				entityPath.addLast(tile.getCenter());
			entityPath.removeFirst(); //konieczne, inaczej w metodzie moveTowardPoint goalPosition oraz entity.getCurrentEntityPosition
			//s¹ takie same i wróg stoi.
		}
	}
	
	private void moveTowardPoint(float delta, Vector2 goalPosition) {
		if(Vector2.dst(goalPosition.x, goalPosition.y, entity.getCurrentEntityPosition().x, entity.getCurrentEntityPosition().y)>0.05) {
			entity.calculateNextPositionToward(goalPosition.cpy(), delta);
		}else
			entityPath.removeFirst();
	}
}
