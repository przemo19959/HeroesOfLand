package application.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import application.entity.Entity;

public class AnimationEntityObserver {
	private Array<AnimationEntity> animationEntities;
	
	public AnimationEntityObserver() {
		animationEntities=new Array<>(50);
	}
	
	public void addAnimation(String animationSpritePath, Entity caster, int width, int height,int numberOfFrames) {
		AnimationEntity animationEntity=new AnimationEntity(animationSpritePath, caster, width, height, numberOfFrames);
		animationEntities.add(animationEntity);
	}
	
	private void removeAnimation(AnimationEntity animationEntity) {
		animationEntities.removeValue(animationEntity, true);
	}
	
	public void updateAllAnimations(Batch batch,float delta) {
		for(AnimationEntity animationEntity:animationEntities) {
			if(animationEntity.update(batch,delta))
				removeAnimation(animationEntity);
		}
	}
	
}
