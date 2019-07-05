package application.huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import application.game.MyGdxGame;
import application.game.Utility;

class EnemyUI extends Window {
	private static final String TAG=EnemyUI.class.getSimpleName();
	private Label enemyName;
	private Label enemyInfo;
	
	private Image hpBar;
	
	public EnemyUI() {
		super("", Utility.STATUSUI_SKIN);
		WidgetGroup group = new WidgetGroup();
		
		hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
		Image bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		enemyName=new Label("", Utility.STATUSUI_SKIN);
		enemyInfo=new Label("", Utility.STATUSUI_SKIN);
		
		hpBar.setPosition(3, 6);
		group.addActor(bar);
		group.addActor(hpBar);
		
		defaults().fill();
		add(enemyName);
		row();
		add(group).size(bar.getWidth(), bar.getHeight());
		row();
		add(enemyInfo);
		pack();
		
		setPosition(MyGdxGame.VIEW_WIDTH/2f-getWidth()/2f, MyGdxGame.VIEW_HEIGHT-getHeight());
	}
	
	void setValues(String enemyName, String enemyInfo, int healthPointsInProcents) {
		this.enemyName.setText(enemyName);
		this.enemyInfo.setText(enemyInfo);
		if(healthPointsInProcents>=0 && healthPointsInProcents<=1)
			hpBar.setWidth(hpBar.getWidth()*healthPointsInProcents);
		else
			Gdx.app.debug(TAG, "Wrong value in health points: "+healthPointsInProcents);
	}

}
