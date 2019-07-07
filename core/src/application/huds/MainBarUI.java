package application.huds;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

import application.game.Utility;

class MainBarUI extends Window {
	private Image hpBar;
	private Image mpBar;
	private Image xpBar;

	// Attributes
	private int currentHealthPoints;
	private int currentManaPoints;
	private int currentExpPoints;

	private int maxHealthPoints;
	private int maxManaPoints;
	private int nextLevelPoints;

	private Label hp;
	private Label mp;
	private Label xp;

	private float initialBarWidth;

	public enum Attribiute {
		HEALTH,
		MANA,
		EXPERIENCE
	}

	public MainBarUI(int maxHealthPoints, int maxManaPoints,int nextLevelPoints ) {
		super("", Utility.STATUSUI_SKIN);
		
		this.maxHealthPoints=maxHealthPoints;
		this.maxManaPoints=maxManaPoints;
		this.nextLevelPoints=nextLevelPoints;
		currentHealthPoints=maxHealthPoints;
		currentManaPoints=maxManaPoints;

		WidgetGroup group = new WidgetGroup();
		WidgetGroup group2 = new WidgetGroup();
		WidgetGroup group3 = new WidgetGroup();

		// images
		hpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("HP_Bar"));
		initialBarWidth = hpBar.getWidth();
		Image bar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		mpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("MP_Bar"));
		Image bar2 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));
		xpBar = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("XP_Bar"));
		xpBar.setWidth(0f);
		Image bar3 = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Bar"));

		// labels
		hp = new Label("hp:" + String.valueOf(maxHealthPoints) + "/" + String.valueOf(maxHealthPoints), Utility.STATUSUI_SKIN);
		mp = new Label("mp:" + String.valueOf(maxManaPoints) + "/" + String.valueOf(maxManaPoints), Utility.STATUSUI_SKIN);
		xp = new Label("exp:" + String.valueOf(currentExpPoints) + "/" + String.valueOf(nextLevelPoints), Utility.STATUSUI_SKIN);

		// buttons
		// inventoryButton = new ImageButton(Utility.STATUSUI_SKIN, "inventory-button");
		// inventoryButton.getImageCell().size(32, 32);
		// inventoryButton.clearListeners();

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

		defaults().pad(0, 5, 0, 5).fill();
		pad(10, 10, 10, 10);
		add(group).size(bar.getWidth(), bar.getHeight());
		add(group3).size(bar2.getWidth(), bar2.getHeight());
		add(group2).size(bar3.getWidth(), bar3.getHeight());
		row();
		add(hp).align(Align.center);
		add(xp).align(Align.center);
		add(mp).align(Align.center);

		pack();
	}

	public void setAttribiuteValue(Attribiute attribiute, int newValue) {
		switch (attribiute) {
			case HEALTH : {
				currentHealthPoints += newValue;
				hp.setText(String.format("hp:%d/%d", currentHealthPoints, maxHealthPoints));
				hpBar.setWidth(initialBarWidth * ((currentHealthPoints+0f) / (maxHealthPoints+0f)));
				break;
			}
			case MANA : {
				currentManaPoints += newValue;
				mp.setText(String.format("mp:%d/%d", currentManaPoints, maxManaPoints));
				hpBar.setWidth(initialBarWidth * ((currentManaPoints+0.5f) / (maxManaPoints+0.5f)));
				break;
			}
			case EXPERIENCE : {
				currentExpPoints += newValue;
				xp.setText(String.format("exp:%d/%d", currentExpPoints, nextLevelPoints));
				hpBar.setWidth(initialBarWidth * ((currentExpPoints+0.5f) / (nextLevelPoints+0.5f)));
				break;
			}
		}
	}
}
