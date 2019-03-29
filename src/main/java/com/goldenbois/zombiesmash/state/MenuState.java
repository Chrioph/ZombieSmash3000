package com.goldenbois.zombiesmash.state;

import com.goldenbois.zombiesmash.ui.ClickListener;
import org.newdawn.slick.*;
import java.awt.event.KeyEvent;

import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.ui.UIImageButton;
import com.goldenbois.zombiesmash.ui.UIManager;

public class MenuState extends State {

	private UIManager uiManager;
	
	public MenuState(Handler handler) {
		super (handler);
		uiManager = new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);
		
		uiManager.addObject(new UIImageButton(  1920 / 2 - 256 ,  1080 / 2 - 128 ,  512 ,  256, Assets.buttonStart, new ClickListener() {
			

			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				setState(new GameState(handler));
				
			}
		}));
		uiManager.addObject(new UIImageButton   ( 1920-70 , 1080-100 , 64 , 64, Assets.buttonSettings, new ClickListener() {
			
			
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				setState(new SettingsState(handler));
			}
		}));
	}
	
	@Override
	public void tick() {
		listenExit();
		uiManager.tick();
		
	}

	@Override
	public void render(Graphics g) {
		uiManager.render(g);
		
	}

	private void listenExit() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
			handler.getGame().close();
		}
	}
}
