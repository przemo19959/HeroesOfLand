package application.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import application.characters.Character.Direction;
import application.entity.Entity;
import application.game.Utility;
import application.maps.MapManager;
import application.characters.Character;

/**
 * Przyjmuje siê, ¿e tekstury s¹ skierowane pierwotnie zawsze w prawo (tak ¿e dla tego kierunku, nie jest konieczna rotacja).
 */
class AnimationEntity{
	private static final String TAG=Entity.class.getSimpleName();
	
	private Vector2 currentAnimationPosition;
	private float frameTime=0f;
	private TextureRegion animationTextureRegion;
	private Direction direction;
	private Animation<TextureRegion> animation;
	
	private int width;
	private int height;
	
	AnimationEntity(String animationSpritePath, Entity caster, int width, int height,int numberOfFrames) {
		Utility.loadAssetOfGivenType(animationSpritePath, Texture.class);
		currentAnimationPosition = new Vector2(caster.getCurrentEntityPosition());
		this.direction=(caster instanceof Character)?((Character)caster).getCurrentEntityDirection():Direction.LEFT;
		setPositionAccordingToDirection(caster.getCurrentEntityPosition());
		this.width=width;
		this.height=height;
		loadAttackAnimations(animationSpritePath, numberOfFrames);
	}
	
	private void setPositionAccordingToDirection(Vector2 casterPosition) {
		switch(direction) {
			case RIGHT: currentAnimationPosition=new Vector2(casterPosition).add(0.5f, -0.5f);break;
			case UP: currentAnimationPosition=new Vector2(casterPosition).add(0.5f, 0.5f);break;
			case LEFT:currentAnimationPosition=new Vector2(casterPosition).add(-0.5f,0.5f);break;
			case DOWN:currentAnimationPosition=new Vector2(casterPosition).add(-0.5f,-0.5f);break;
		}
	}
	
	boolean update(Batch batch, float delta) {
		frameTime = (frameTime + delta) % 5;
		animationTextureRegion=animation.getKeyFrame(frameTime);
		float rotation=getRotationForGivenDirection(direction);
		batch.draw(animationTextureRegion, currentAnimationPosition.x, currentAnimationPosition.y, 0, 0, width*MapManager.UNIT_SCALE
		           ,height*MapManager.UNIT_SCALE, 1, 1, rotation);
		return (animation.isAnimationFinished(frameTime))?true:false; 
	}
	
	private float getRotationForGivenDirection(Direction direction) {
		switch(direction){
			case UP: return 90f;
			case LEFT: return 180f;
			case DOWN:return 270f;
			case RIGHT:
			default: return 0f;
		}
	}
	
	private void loadAttackAnimations(String animationSpritePath, int numberOfFrames) {
		Texture texture = Utility.getAssetOfGivenType(animationSpritePath, Texture.class);
		TextureRegion[][] textureFrames = TextureRegion.split(texture, width, height);
		animationTextureRegion = textureFrames[0][0];
		Array<TextureRegion> frames = new Array<>(numberOfFrames);
		for(int i = 0;i < 6;i++) {
			TextureRegion region = textureFrames[0][i];
			if(region == null)
				Gdx.app.debug(TAG, "Got null animation frame " + 0 + "," + i);
			frames.insert(i, region);
		}
		animation = new Animation<>(0.05f, frames, PlayMode.NORMAL);
	}	
}
