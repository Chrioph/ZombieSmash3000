package dev.codenmore.tilegame.state;

import java.awt.Color;

import dev.codenmore.tilegame.ui.ClickListener;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.ui.ConfirmDialog;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.ui.UIImageButton;
import dev.codenmore.tilegame.ui.UITextButton;
import dev.codenmore.tilegame.ui.UIManager;

public class MenuState extends State {

	private UIManager uiManager;
	private ConfirmDialog exitDialog;
	
	public MenuState(Handler handler) {
		super (handler);
		uiManager = new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);
		exitDialog = new ConfirmDialog(handler, "Quit game?", "Do you really want to exit?",
				() -> handler.getGame().close());
		
		uiManager.addObject(new UIImageButton(  1920 / 2 - 256 ,  1080 / 2 - 128 - 90 ,  512 ,  256, Assets.buttonStart, new ClickListener() {
			

			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(new GameState(handler));
				
			}
		}));
		// test mode: start in the homeworld with building materials
		uiManager.addObject(new UITextButton(1920 / 2 - 256, 1080 / 2 + 128 - 90 + 40, 512, 130, "TEST MODE", Assets.font56, new ClickListener() {

			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(new GameState(handler, true));
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
		if (exitDialog.isOpen()) {
			exitDialog.tick();
			return;
		}
		listenExit();
		uiManager.tick();
		
	}

	@Override
	public void render(Graphics g) {
		uiManager.render(g);
		Text.drawString(g, "START IN THE HOMEWORLD WITH MATERIALS", 1920 / 2, 1080 / 2 + 128 - 90 + 40 + 130 + 40, true,
				new Color(90, 90, 90), Assets.font28);
		exitDialog.render(g);
	}

	private void listenExit() {
		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) {
			exitDialog.open();
		}
	}
}
