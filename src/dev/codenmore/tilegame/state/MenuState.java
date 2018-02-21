package dev.codenmore.tilegame.state;

import java.awt.Color;

import dev.codenmore.tilegame.ui.ClickListener;
import java.awt.Graphics;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.ui.UIImageButton;
import dev.codenmore.tilegame.ui.UIManager;

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
				State.setState(new GameState(handler));
				
			}
		}));
		uiManager.addObject(new UIImageButton   ( 1920-70 , 1080-100 , 64 , 64, Assets.buttonSettings, new ClickListener() {
			
			
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(new SettingsState(handler));
			}
		}));
	}
	
	@Override
	public void tick() {
		uiManager.tick();
		
	}

	@Override
	public void render(Graphics g) {
		uiManager.render(g);
		
	}

}
