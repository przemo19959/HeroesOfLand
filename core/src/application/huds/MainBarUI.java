package application.huds;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import application.game.Utility;

public class MainBarUI extends Window {
	private Image hpBar;
	private Image mpBar;
	private Image xpBar;
	private ImageButton inventoryButton;
	// Attributes
	private int currentHealthPoints = 50;
	private int currentManaPoints = 50;
	private int currentExpPoints = 0;
	
	private int maxHealthPoints=50;
	private int maxManaPoints=50;
	private int nextLevelPoints=100;

	public MainBarUI() {
		super("", Utility.STATUSUI_SKIN);
		
		WidgetGroup group = new WidgetGroup();
		WidgetGroup group2 = new WidgetGroup();
		WidgetGroup group3 = new WidgetGroup();

		// images
		hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
		Image bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		mpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"));
		Image bar2 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		xpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"));
		Image bar3 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));

		// labels
		Label hp = new Label("hp:"+String.valueOf(currentHealthPoints)+"/"+String.valueOf(maxHealthPoints), Utility.STATUSUI_SKIN);
		Label mp = new Label("mp:"+String.valueOf(currentManaPoints)+"/"+String.valueOf(maxManaPoints), Utility.STATUSUI_SKIN);
		Label xp = new Label("exp:"+String.valueOf(currentExpPoints)+"/"+String.valueOf(nextLevelPoints), Utility.STATUSUI_SKIN);

		// buttons
//		inventoryButton = new ImageButton(Utility.STATUSUI_SKIN, "inventory-button");
//		inventoryButton.getImageCell().size(32, 32);
//		inventoryButton.clearListeners();
		
		// Align images
		hpBar.setPosition(3, 6);
		mpBar.setPosition(3, 6);
		xpBar.setPosition(3, 6);
		// add to widget groups
		group.addActor(bar);
		group.addActor(hpBar);
		
		group2.addActor(bar2);
		group2.addActor(mpBar);
		
		group3.addActor(bar3);
		group3.addActor(xpBar);
		
		defaults().pad(0,5,0,5).fill();
		pad(10, 10, 10, 10);
		add(group).size(bar.getWidth(), bar.getHeight());
		add(group3).size(bar2.getWidth(), bar2.getHeight());
		add(group2).size(bar3.getWidth(), bar3.getHeight());
		row();
		add(hp).align(Align.center);
		add(xp).align(Align.center);
		add(mp).align(Align.center);
		
		pack();
		
		hpBar.setWidth(hpBar.getWidth()-10);
	}
	
	public void setValueOfBar() {
		
	}

}
