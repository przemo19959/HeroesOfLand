package application.huds;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import application.game.MyGdxGame;
import application.game.Utility;
import application.characters.Character;

class EnemyUI extends Window {
//	private static final String TAG=EnemyUI.class.getSimpleName();
	private Label enemyName;
	private Label enemyInfo;
	
	private Image hpBar;
	private float initialBarWidth;
	
	public EnemyUI() {
		super("", Utility.STATUSUI_SKIN);
		WidgetGroup group = new WidgetGroup();
		
		hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
		initialBarWidth = hpBar.getWidth();
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
	
	void setValues(String enemyName, String enemyInfo, Character enemy) {
		this.enemyName.setText(enemyName);
		this.enemyInfo.setText(enemyInfo);
		if(enemy!=null)
			hpBar.setWidth(initialBarWidth * ((enemy.getHealthPoints()+0f) / (enemy.getMaxHealthPoints()+0f)));
	}

}
