package com.goldenbois.zombiesmash.gfx;

import org.newdawn.slick.*;

public class Assets {
	
	public static Image dirt,grass,stone,finish,solidWood,wood,chest;

	// Dungeon
	public static Image dungeonWallStraightDown, dungeonWallStraightUp, dungeonWallLeftCorner;
	public static Image	dungeonWallRightCorner, dungeonWallLeft, dungeonWallRight, dungeonWallUpperCornerLeft;
	public static Image dungeonWallUpperCornerRight, dungeonWallMiddle, dungeonWallSingleBlock, dungeonFloor;
	public static Image dungeonWallSingleMiddle, dungeonWallSingleLeft, dungeonWallSingleRight, dungeonWallSingleMiddleUpwards;
	public static Image dungeonWallSingleUp, dungeonWallSingleDown;


	public static Image zombie, ogre, gravestone;
	public static Image log, sword, bow, rock, tree, heart, armor, healthPlus, seeds;
	public static Image arrow, arrowRight, arrowDown, arrowLeft, arrowUp;
	public static Image inventoryScreen,settingsBackground,HUDWindow,deathScreen,craftingScreen;
	public static Image settingsBodyElement, settingsHeader;
	public static Image dropDown, dropDownElement, nextLevelSelectionOption;

	
	private static final int width = 32,height =32;
	public static Image[] player1_down , player1_up, player1_left, player1_right, aDown, aLeft, aRight, aUp;;
	public static Image player1LookingDown , player1LookingUp, player1LookingLeft, player1LookingRight;
	public static Image[] zombie_down , zombie_up, zombie_left, zombie_right, zaDown, zaLeft, zaRight, zaUp;
	public static Image[] ogre_down , ogre_up, ogre_left, ogre_right, oaDown, oaLeft, oaRight, oaUp;
	public static Image[] buttonStart, buttonSettings, buttonBack;
	public static Image[] settingsMenuButton;
	
	public static Font font56, font28, font40,font100;
	
	public static void init() {
		try {
			font56 = FontLoader.loadFont("/fonts/slkscr.ttf", 56);
			font28 = FontLoader.loadFont("/fonts/slkscr.ttf", 28);
			font40 = FontLoader.loadFont("/fonts/slkscr.ttf", 40);
			font100 = FontLoader.loadFont("/fonts/slkscr.ttf", 100);

			org.newdawn.slick.SpriteSheet sheet = new org.newdawn.slick.SpriteSheet(new Image("/textures/Sheet2.png"), width, height);
			org.newdawn.slick.SpriteSheet dungeonWallsSheet = new org.newdawn.slick.SpriteSheet(new Image("/textures/dungeonWallsSheet.png"), width, height);
			org.newdawn.slick.SpriteSheet ogreSheet = new org.newdawn.slick.SpriteSheet(new Image("/textures/OgreSpreadsheet.png"), width, height);

			HUDWindow = new Image("/textures/HUDWindow.png");
			settingsBackground = new Image("/textures/Settings_Screen2.png");
			settingsBodyElement = new Image("/textures/SettingsBodyElement.png");
			dropDown = new Image("/textures/UIDropdown.png");
			dropDownElement = new Image("/textures/UIDropdownOption.png");
			nextLevelSelectionOption = new Image("/textures/NextLevelSelectionOption.png");
			settingsHeader = new Image("/textures/SettingsSubPageHead.png");
			settingsMenuButton = new Image[2];
			settingsMenuButton[0] = new Image("/textures/SettingsChoiceButton.png");
			settingsMenuButton[1] = new Image("/textures/SettingsButtonPressed.png");
			inventoryScreen = new Image("/textures/inventoryScreen.png");
			deathScreen = sheet.getSprite(10, 0);
			craftingScreen = new Image("/textures/CraftingScreen.png");


			wood = sheet.getSprite(10, 1);
			solidWood = sheet.getSprite(11, 0);
			dirt = sheet.getSprite(2, 0);
			grass = sheet.getSprite(3, 0);
			stone = sheet.getSprite(0, 1);
			finish = sheet.getSprite(8, 1);
			chest = sheet.getSprite(9, 2);

			// Dungeon
			dungeonWallLeft = dungeonWallsSheet.getSprite(0, 1);
			dungeonWallLeftCorner = dungeonWallsSheet.getSprite(0, 2);
			dungeonWallRightCorner = dungeonWallsSheet.getSprite(2, 2);
			dungeonWallUpperCornerLeft = dungeonWallsSheet.getSprite(0, 0);
			dungeonWallUpperCornerRight = dungeonWallsSheet.getSprite(2, 0);
			dungeonWallRight = dungeonWallsSheet.getSprite(2, 1);
			dungeonWallStraightDown = dungeonWallsSheet.getSprite(1, 2);
			dungeonWallStraightUp = dungeonWallsSheet.getSprite(1, 0);
			dungeonWallMiddle = dungeonWallsSheet.getSprite(1, 1);

			dungeonWallSingleBlock = new Image("/textures/DungeonTileWallSingleBlock.png");
			dungeonWallSingleMiddle = new Image("/textures/dungeonTileWalLSingleMiddle.png");
			dungeonWallSingleLeft = new Image("/textures/dungeonTileWallSingleLeft.png");
			dungeonWallSingleRight = new Image("/textures/dungeonTileWallSingleRight.png");
			dungeonWallSingleMiddleUpwards = new Image("/textures/dungeonTileWallSingleMiddleUpwards.png");
			dungeonWallSingleUp = new Image("/textures/dungeonTileWallSingleUp.png");
			dungeonWallSingleDown = new Image("/textures/dungeonTileWallSingleDown.png");
			dungeonFloor = new Image("/textures/dungeonTileFloor.png");


			tree = sheet.getSprite(1, 1);
			log = sheet.getSprite(1, 0);
			rock = sheet.getSprite(0, 0);
			sword = sheet.getSprite(3, 3);
			bow = sheet.getSprite(2, 6);
			heart = sheet.getSprite(7, 3);
			armor = sheet.getSprite(8, 0);
			healthPlus = sheet.getSprite(9, 0);
			seeds = sheet.getSprite(8, 2);

			arrow = sheet.getSprite(3, 6);
			arrowRight = sheet.getSprite(4, 6);
			arrowDown = sheet.getSprite(5, 6);
			arrowLeft = sheet.getSprite(6, 6);
			arrowUp = sheet.getSprite(7, 6);

			//Player 1
			player1LookingDown = sheet.getSprite(2, 1);
			player1LookingLeft = sheet.getSprite(2, 5);
			player1LookingUp = sheet.getSprite(3, 5);
			player1LookingRight = sheet.getSprite(1, 5);
			gravestone = sheet.getSprite(9, 1);

			player1_down = new Image[2];
			player1_down[0] = sheet.getSprite(3, 1);
			player1_down[1] = sheet.getSprite(0, 2);

			player1_up = new Image[2];
			player1_up[0] = sheet.getSprite(1, 2);
			player1_up[1] = sheet.getSprite(2, 2);

			player1_left = new Image[2];
			player1_left[0] = sheet.getSprite(1, 3);
			player1_left[1] = sheet.getSprite(2, 3);


			player1_right = new Image[2];
			player1_right[0] = sheet.getSprite(3, 2);
			player1_right[1] = sheet.getSprite(0, 3);

			//Zombie

			zombie = sheet.getSprite(6, 1);

			zombie_down = new Image[2];
			zombie_down[0] = sheet.getSprite(7, 1);
			zombie_down[1] = sheet.getSprite(4, 2);

			zombie_up = new Image[2];
			zombie_up[0] = sheet.getSprite(5, 2);
			zombie_up[1] = sheet.getSprite(6, 2);

			zombie_left = new Image[2];
			zombie_left[0] = sheet.getSprite(5, 3);
			zombie_left[1] = sheet.getSprite(6, 3);


			zombie_right = new Image[2];
			zombie_right[0] = sheet.getSprite(7, 2);
			zombie_right[1] = sheet.getSprite(4, 3);

			//Ogre

			ogre = ogreSheet.getSprite(4, 0);

			ogre_right = new Image[2];
			ogre_right[0] = ogreSheet.getSprite(0, 0);
			ogre_right[1] = ogreSheet.getSprite(0, 0);

			ogre_left = new Image[2];
			ogre_left[0] = ogreSheet.getSprite(2, 0);
			ogre_left[1] = ogreSheet.getSprite(2, 0);

			ogre_down = new Image[2];
			ogre_down[0] = ogreSheet.getSprite(4, 0);
			ogre_down[1] = ogreSheet.getSprite(4, 0);


			ogre_up = new Image[2];
			ogre_up[0] = ogreSheet.getSprite(6, 0);
			ogre_up[1] = ogreSheet.getSprite(6, 0);

			//Start
			buttonStart = new Image[2];
			buttonStart[0] = sheet.getSprite(4, 0);
			buttonStart[1] = sheet.getSprite(6, 0);

			//Settings

			buttonSettings = new Image[2];
			buttonSettings[0] = sheet.getSprite(4, 1);
			buttonSettings[1] = sheet.getSprite(5, 1);

			//Back

			buttonBack = new Image[2];
			buttonBack[0] = sheet.getSprite(0, 6);
			buttonBack[1] = sheet.getSprite(1, 6);

			//PLayer1 Attack
			aUp = new Image[2];
			aUp[0] = sheet.getSprite(3, 4);
			aUp[1] = sheet.getSprite(3, 5);

			aDown = new Image[2];
			aDown[0] = sheet.getSprite(0, 4);
			aDown[1] = sheet.getSprite(0, 5);

			aRight = new Image[2];
			aRight[0] = sheet.getSprite(1, 4);
			aRight[1] = sheet.getSprite(1, 5);

			aLeft = new Image[2];
			aLeft[0] = sheet.getSprite(2, 4);
			aLeft[1] = sheet.getSprite(2, 5);

			//Zombie Aggro

			zaDown = new Image[2];
			zaDown[0] = sheet.getSprite(8, 4);
			zaDown[1] = sheet.getSprite(11, 4);

			zaRight = new Image[2];
			zaRight[0] = sheet.getSprite(8, 5);
			zaRight[1] = sheet.getSprite(11, 5);

			zaLeft = new Image[2];
			zaLeft[0] = sheet.getSprite(9, 5);
			zaLeft[1] = sheet.getSprite(10, 5);


			//Ogre Attack

			oaRight = new Image[2];
			oaRight[0] = ogreSheet.getSprite(0, 0);
			oaRight[1] = ogreSheet.getSprite(0, 0);

			oaLeft = new Image[2];
			oaLeft[0] = ogreSheet.getSprite(2, 0);
			oaLeft[1] = ogreSheet.getSprite(2, 0);

			oaDown = new Image[2];
			oaDown[0] = ogreSheet.getSprite(4, 0);
			oaDown[1] = ogreSheet.getSprite(4, 0);


			oaUp = new Image[2];
			oaUp[0] = ogreSheet.getSprite(6, 0);
			oaUp[1] = ogreSheet.getSprite(6, 0);
		}catch(SlickException e) {

		}

		//Item Placing Animation

	}
		
		

		
}
