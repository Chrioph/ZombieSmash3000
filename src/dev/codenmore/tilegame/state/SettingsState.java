package dev.codenmore.tilegame.state;

import java.awt.Graphics;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.ui.ClickListener;
import dev.codenmore.tilegame.ui.UIImageButton;
import dev.codenmore.tilegame.ui.UIManager;

public class SettingsState extends State{

	private UIManager uiManager;
	
	
	
	public SettingsState(Handler handler) {
		super (handler);
		uiManager= new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);
		
		uiManager.addObject(new UIImageButton( 50 ,  50 , 64 , 64, Assets.buttonBack, new ClickListener() {
			
			
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(new MenuState(handler));
			}
		}));
	}
	
	@Override
	public void tick() {
		uiManager.tick();
		
		
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.settingsBackground, 0, 0,handler.getWidth(),handler.getHeight(), null);
		uiManager.render(g);
		
	}

}
