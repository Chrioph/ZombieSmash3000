package dev.codenmore.tilegame.state;

import java.awt.Graphics;
import java.util.ArrayList;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.ui.*;

public class SettingsState extends State{

	private UIManager uiManager;
	
	
	
	public SettingsState(Handler handler) {
		super (handler);
		uiManager= new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);

		uiManager.addObject(new UIList(300, 150, 200, 64,"Resolution", new ArrayList<UIListElement>() {{
			//@TODO: get list of available resolutions from some api, for now static values
			add(new UIListElement(0, 0, 200, 40, "1920x1080", () -> {
                // Change resolution to 1920x1080
				System.out.println("Resize 1920x1080");
                handler.getGame().resizeDisplay(1920, 1080);
            }));
			add(new UIListElement(0, 0, 200, 40, "1280x720", () -> {
                // Change resolution to 1280x720
				System.out.println("Resize 1280x720");
                handler.getGame().resizeDisplay(1280, 720);
            }));
			add(new UIListElement(0, 0, 200, 40, "1680x1050", () -> {
				// Change resolution to 1280x720
				System.out.println("Resize 1680x1050");
				handler.getGame().resizeDisplay(1680, 1050);
			}));
		}}));
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
