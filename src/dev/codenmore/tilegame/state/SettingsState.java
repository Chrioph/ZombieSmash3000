package dev.codenmore.tilegame.state;

import java.awt.*;
import java.util.ArrayList;

import dev.codenmore.tilegame.Game;
import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.Shader;
import dev.codenmore.tilegame.gfx.Texture;
import dev.codenmore.tilegame.gfx.VertexArray;
import dev.codenmore.tilegame.state.elements.Page;
import dev.codenmore.tilegame.ui.*;

public class SettingsState extends State{

	private UIManager uiManager;
	private Page activePage;
	private boolean activePageChanged;
	private ArrayList<Page> pages;

	private VertexArray mesh;
	private float SIZE = 1.0f;
	
	
	
	public SettingsState(Handler handler) {
		super (handler);
		if(Settings.getOpenGl()) {
			float[] vertices = new float[] {
					-SIZE / 2.0f, -SIZE / 2.0f, 0.2f,
					-SIZE / 2.0f,  SIZE / 2.0f, 0.2f,
					SIZE / 2.0f,  SIZE / 2.0f, 0.2f,
					SIZE / 2.0f, -SIZE / 2.0f, 0.2f
			};

			byte[] indices = new byte[] {
					0, 1, 2,
					2, 3, 0
			};

			float[] tcs = new float[] {
					0, 1,
					0, 0,
					1, 0,
					1, 1
			};


			mesh = new VertexArray(vertices, indices, tcs);
		}
		activePageChanged = false;

		pages = new ArrayList<Page>();

		initializePages();

		uiManager= new UIManager(handler);
		handler.getMouseManager().setUIManager(uiManager);

		uiManager.addObject(new UIImageTextButton(110, 110, 330, 90, Assets.settingsMenuButton, new ClickListener() {
			@Override
			public void onClick() {
				activePage = pages.get(0);
				activePageChanged = true;
			}
		}, pages.get(0).getName()));
		uiManager.addObject(new UIImageTextButton(110, 220, 330, 90, Assets.settingsMenuButton, new ClickListener() {
			@Override
			public void onClick() {
				activePage = pages.get(1);
				activePageChanged = true;
			}
		}, pages.get(1).getName()));
		uiManager.addObject(new UIImageTextButton(110, 330, 330, 90, Assets.settingsMenuButton, new ClickListener() {
			@Override
			public void onClick() {
				activePage = pages.get(2);
				activePageChanged = true;
			}
		}, pages.get(2).getName()));



		uiManager.addObject(new UIImageButton( 110 ,  900 , 64 , 64, Assets.buttonBack, new ClickListener() {
			
			
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(new MenuState(handler));
			}
		}));
	}

	public void initializePages()
	{
		initializeGameSettingsPage();
		initializeAudioSettingsPage();
		initializeDisplaySettingsPage();
	}
	
	@Override
	public void tick() {
		uiManager.tick();
		
		
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.settingsBackground, 0, 0,1920,1080, null);
		uiManager.render(g);
		if(activePage != null && activePageChanged) {
			uiManager.removeAllTmpObjects();
			for(UIObject o : activePage.getUiManager().getObjects()) {
				uiManager.addTmpObject(o);
			}
			activePageChanged = false;
		}
	}

	@Override
	public void renderOpenGL() {
		// render Background
		Texture bgTexture = new Texture(Assets.settingsBackground);
		Shader.BG.enable();
		//Shader.ENTITY.setUniformMat4f("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
		bgTexture.bind();
		mesh.render();
		Shader.BG.disable();
	}

	public void initializeGameSettingsPage()
	{
		UIManager gameUiManager = new UIManager(handler);

		gameUiManager.addObject(new UIImageText(500, 110, 1310, 90, Assets.settingsHeader, "Game Settings"));
		gameUiManager.addObject(new UIImage(500, 220, 650, 750, Assets.settingsBodyElement));
		gameUiManager.addObject(new UIImage(1160, 220, 650, 750, Assets.settingsBodyElement));


		gameUiManager.addObject(new UIList(530, 250, 590, 100,"Difficulty", new ArrayList<UIListElement>() {{
			add(new UIListElement(0, 0, 570, 80, "Easy", Assets.dropDownElement, () -> handler.getGame().getWorldGenerator().setDifficulty("EASY")));
			add(new UIListElement(0, 0, 570, 80, "Normal", Assets.dropDownElement, () -> handler.getGame().getWorldGenerator().setDifficulty("NORMAL")));
			add(new UIListElement(0, 0, 570, 80, "Hard", Assets.dropDownElement, () -> handler.getGame().getWorldGenerator().setDifficulty("HARD")));
		}}, false, Assets.dropDown, true));



		Page gameSettingsPage = new Page("Game", 1);
		gameSettingsPage.setUiManager(gameUiManager);

		pages.add(gameSettingsPage);

	}

	public void initializeDisplaySettingsPage()
	{
		UIManager displayUiManager = new UIManager(handler);

		displayUiManager.addObject(new UIImageText(500, 110, 1310, 90, Assets.settingsHeader, "Display Settings"));
		displayUiManager.addObject(new UIImage(500, 220, 650, 750, Assets.settingsBodyElement));
		displayUiManager.addObject(new UIImage(1160, 220, 650, 750, Assets.settingsBodyElement));

		displayUiManager.addObject(new UIList(530, 250, 590, 100,"Resolution", new ArrayList<UIListElement>() {{
			//@TODO: get list of available resolutions from some api, for now static values
			add(new UIListElement(0, 0, 570, 80, "1920x1080", Assets.dropDownElement, () -> {
				// Change resolution to 1920x1080
				handler.getGame().resizeDisplay(1920, 1080);
			}));
			add(new UIListElement(0, 0, 570, 80, "1280x720", Assets.dropDownElement, () -> {
				// Change resolution to 1280x720
				handler.getGame().resizeDisplay(1280, 720);
			}));
			add(new UIListElement(0, 0, 570, 80, "1680x1050", Assets.dropDownElement, () -> {
				// Change resolution to 1680x1050
				handler.getGame().resizeDisplay(1680, 1050);
			}));
		}}, false, Assets.dropDown, true));

		Page displaySettingsPage = new Page("Display", 2);
		displaySettingsPage.setUiManager(displayUiManager);
		pages.add(displaySettingsPage);
	}

	public void initializeAudioSettingsPage()
	{
		UIManager audioUiManager = new UIManager(handler);

		audioUiManager.addObject(new UIImageText(500, 110, 1310, 90, Assets.settingsHeader, "Audio Settings"));
		audioUiManager.addObject(new UIImage(500, 220, 650, 750, Assets.settingsBodyElement));
		audioUiManager.addObject(new UIImage(1160, 220, 650, 750, Assets.settingsBodyElement));

		Page audioSettingsPage = new Page("Audio", 3);
		audioSettingsPage.setUiManager(audioUiManager);
		pages.add(audioSettingsPage);
	}

}
