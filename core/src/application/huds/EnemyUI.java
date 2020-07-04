package application.huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

import application.entities.concrete.dtos.CharacterDTO;
import application.entities.concrete.entities.Character;
import application.game.MyGdxGame;
import application.game.Utility;

class EnemyUI extends Table {
	private static final String TAG=EnemyUI.class.getSimpleName();
	
	private final Label enemyName;
	private final Label enemyInfo;
	
	private final Image hpBar;
	private final float initialBarWidth;
	
	public EnemyUI() {
		super(Utility.STATUSUI_SKIN);
		//or dialogDim, but there is something with size, and although center is on, table is shifted on right
		setBackground("generic_background");
		
		WidgetGroup group = new WidgetGroup();
		hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
		Image bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		enemyName=new Label("", Utility.STATUSUI_SKIN);
		enemyInfo=new Label("", Utility.STATUSUI_SKIN);
		
		defaults().center();
		
		hpBar.setPosition(3, 6); //set hpBar position relative to bar
		initialBarWidth = hpBar.getWidth();
		group.addActor(hpBar);
		group.addActor(bar);
		
		add(enemyName);
		row();
		add(group).size(bar.getWidth(), bar.getHeight());
		row();
		add(enemyInfo);
		pack();
				
		setPosition(MyGdxGame.VIEW_WIDTH/2f-getWidth()/2f, MyGdxGame.VIEW_HEIGHT-getHeight());
	}
	
	void setEnemyValues(Character enemy) {
		if(enemy!=null) {
			CharacterDTO characterDTO=enemy.getCharacterDTO();
			this.enemyName.setText((characterDTO!=null)?characterDTO.getName():"??");
			this.enemyInfo.setText((characterDTO!=null)?characterDTO.getInfo():"??");
			hpBar.setWidth(initialBarWidth * ((enemy.getCurrentHealthPoints()+0f) / (enemy.getMaxHealthPoints()+0f)));
		}
	}

}
