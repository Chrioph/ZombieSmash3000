package dev.codenmore.tilegame.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Assets {
	
	public static BufferedImage dirt,grass,stone,finish,solidWood,wood,chest;

	// Dungeon
	public static BufferedImage dungeonWallStraightDown, dungeonWallStraightUp, dungeonWallLeftCorner;
	public static BufferedImage	dungeonWallRightCorner, dungeonWallLeft, dungeonWallRight, dungeonWallUpperCornerLeft;
	public static BufferedImage dungeonWallUpperCornerRight, dungeonWallMiddle, dungeonWallSingleBlock, dungeonFloor;
	public static BufferedImage dungeonWallSingleMiddle, dungeonWallSingleLeft, dungeonWallSingleRight, dungeonWallSingleMiddleUpwards;
	public static BufferedImage dungeonWallSingleUp, dungeonWallSingleDown;


	public static BufferedImage zombie, ogre, gravestone;
	public static BufferedImage log, sword, bow, rock, tree, heart, armor, healthPlus, seeds;
	public static BufferedImage arrow, arrowRight, arrowDown, arrowLeft, arrowUp;
	public static BufferedImage inventoryScreen,settingsBackground,HUDWindow,deathScreen,craftingScreen;
	public static BufferedImage settingsBodyElement, settingsHeader;
	public static BufferedImage dropDown, dropDownElement, nextLevelSelectionOption;

	
	private static final int width = 32,height =32;
	public static BufferedImage[] player1_down , player1_up, player1_left, player1_right, aDown, aLeft, aRight, aUp;;
	public static BufferedImage player1LookingDown , player1LookingUp, player1LookingLeft, player1LookingRight;
	public static BufferedImage[] zombie_down , zombie_up, zombie_left, zombie_right, zaDown, zaLeft, zaRight, zaUp;
	public static BufferedImage[] ogre_down , ogre_up, ogre_left, ogre_right, oaDown, oaLeft, oaRight, oaUp;
	public static BufferedImage[] buttonStart, buttonSettings, buttonBack;
	public static BufferedImage[] settingsMenuButton;
	
	public static Font font56, font28, font40,font100;
	
	public static void init() {
		font56 = FontLoader.loadFont("/fonts/slkscr.ttf",56);
		font28 = FontLoader.loadFont("/fonts/slkscr.ttf",28);
		font40 = FontLoader.loadFont("/fonts/slkscr.ttf",40);
        font100 = FontLoader.loadFont("/fonts/slkscr.ttf",100);
		
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/textures/Sheet2.png"));
		SpriteSheet dungeonWallsSheet = new SpriteSheet(ImageLoader.loadImage("/textures/dungeonWallsSheet.png"));
		SpriteSheet ogreSheet = new SpriteSheet(ImageLoader.loadImage("/textures/OgreSpreadsheet.png"));
		
		HUDWindow = ImageLoader.loadImage("/textures/HUDWindow.png");
		settingsBackground = ImageLoader.loadImage("/textures/Settings_Screen2.png");
		settingsBodyElement = ImageLoader.loadImage("/textures/SettingsBodyElement.png");
		dropDown = ImageLoader.loadImage("/textures/UIDropdown.png");
		dropDownElement = ImageLoader.loadImage("/textures/UIDropdownOption.png");
        nextLevelSelectionOption = ImageLoader.loadImage("/textures/NextLevelSelectionOption.png");
		settingsHeader = ImageLoader.loadImage("/textures/SettingsSubPageHead.png");
		settingsMenuButton = new BufferedImage[2];
		settingsMenuButton[0] = ImageLoader.loadImage("/textures/SettingsChoiceButton.png");
		settingsMenuButton[1] = ImageLoader.loadImage("/textures/SettingsButtonPressed.png");
		inventoryScreen =ImageLoader.loadImage("/textures/inventoryScreen.png");
		deathScreen = sheet.crop(width*10, 0, width, height);
		craftingScreen =ImageLoader.loadImage("/textures/CraftingScreen.png");
		

		
		wood=      sheet.crop(width*10, height, width, height);
		solidWood= sheet.crop(width*11, 0, width, height);
		dirt =     sheet.crop(width*2, 0, width, height);
		grass =    sheet.crop(width*3, 0, width, height);
		stone =    sheet.crop(0, height, width, height);
		finish=    sheet.crop(width*8, height, width, height);
		chest=     sheet.crop(width,height,width,height);

		// Dungeon
		dungeonWallLeft = dungeonWallsSheet.crop(0, height, width, height);
		dungeonWallLeftCorner = dungeonWallsSheet.crop(0, height*2, width, height);
		dungeonWallRightCorner = dungeonWallsSheet.crop(width*2, height*2, width, height);
		dungeonWallUpperCornerLeft = dungeonWallsSheet.crop(0, 0, width, height);
		dungeonWallUpperCornerRight = dungeonWallsSheet.crop(width*2, 0, width, height);
		dungeonWallRight = dungeonWallsSheet.crop(width*2, height, width, height);
		dungeonWallStraightDown = dungeonWallsSheet.crop(width, height*2, width, height);
		dungeonWallStraightUp = dungeonWallsSheet.crop(width, 0, width, height);
		dungeonWallMiddle = dungeonWallsSheet.crop(width, height, width, height);

		dungeonWallSingleBlock = ImageLoader.loadImage("/textures/DungeonTileWallSingleBlock.png");
		dungeonWallSingleMiddle = ImageLoader.loadImage("/textures/dungeonTileWalLSingleMiddle.png");
		dungeonWallSingleLeft = ImageLoader.loadImage("/textures/dungeonTileWallSingleLeft.png");
		dungeonWallSingleRight = ImageLoader.loadImage("/textures/dungeonTileWallSingleRight.png");
		dungeonWallSingleMiddleUpwards = ImageLoader.loadImage("/textures/dungeonTileWallSingleMiddleUpwards.png");
		dungeonWallSingleUp = ImageLoader.loadImage("/textures/dungeonTileWallSingleUp.png");
		dungeonWallSingleDown = ImageLoader.loadImage("/textures/dungeonTileWallSingleDown.png");
		dungeonFloor = ImageLoader.loadImage("/textures/dungeonTileFloor.png");


		tree =     sheet.crop(width,height,width,height);
		log =     sheet.crop(width, 0, width, height);
		rock =     sheet.crop(0, 0, width, height);
		sword=     sheet.crop(width*3, height*3, width, height);
		bow  =     sheet.crop(width*2, height*6, width, height);
		heart=     sheet.crop(width*7, height*3, width, height);
		armor=     sheet.crop(width*8, 0, width, height);
		healthPlus=sheet.crop(width*9, 0, width, height);
		seeds=     sheet.crop(width*8, height*2, width,height);
		
		arrow     =   sheet.crop(width*3, height*6, width, height);
		arrowRight=   sheet.crop(width*4, height*6, width, height);
		arrowDown =   sheet.crop(width*5, height*6, width, height);
		arrowLeft =   sheet.crop(width*6, height*6, width, height);
		arrowUp   =   sheet.crop(width*7, height*6, width, height);
		
		//Player 1
		player1LookingDown =sheet.crop(width*2, height, width, height);
		player1LookingLeft =sheet.crop(width*2, height*5, width, height);
		player1LookingUp   =sheet.crop(width*3, height*5, width, height);
		player1LookingRight=sheet.crop(width*1, height*5, width, height);
		gravestone         =sheet.crop(width*9, height, width, height);
		
		player1_down = new BufferedImage[2];
		player1_down[0]=sheet.crop( width*3 , height, width, height);
		player1_down[1]=sheet.crop( 0 , height*2, width, height);
		
		player1_up = new BufferedImage[2];
		player1_up[0]=sheet.crop( width , height*2, width, height);
		player1_up[1]=sheet.crop( width*2 , height*2, width, height);
		
		player1_left = new BufferedImage[2];
		player1_left[0]=sheet.crop( width , height*3, width, height);
		player1_left[1]=sheet.crop( width*2 , height*3, width, height);		
		
		
		player1_right = new BufferedImage[2];
		player1_right[0]=sheet.crop( width*3 , height*2, width, height);
		player1_right[1]=sheet.crop( 0 , height*3, width, height);
		
		//Zombie
		
		zombie =  sheet.crop(width*6, height, width, height); 
		
		zombie_down = new BufferedImage[2];
		zombie_down[0]=sheet.crop( width*7 , height, width, height);
		zombie_down[1]=sheet.crop( width*4 , height*2, width, height);
		
		zombie_up = new BufferedImage[2];
		zombie_up[0]=sheet.crop( width *5, height*2, width, height);
		zombie_up[1]=sheet.crop( width*6 , height*2, width, height);
		
		zombie_left = new BufferedImage[2];
		zombie_left[0]=sheet.crop( width*5 , height*3, width, height);
		zombie_left[1]=sheet.crop( width*6 , height*3, width, height);		
		
		
		zombie_right = new BufferedImage[2];
		zombie_right[0]=sheet.crop( width*7 , height*2, width, height);
		zombie_right[1]=sheet.crop( width*4 , height*3, width, height);
		
		//Ogre
		
		ogre =  ogreSheet.crop(width*4, 0, width*2, height*2);
				
		ogre_right = new BufferedImage[2];
		ogre_right[0]=ogreSheet.crop( 0 , 0, width*2, height*2);
		ogre_right[1]=ogreSheet.crop( 0 , 0, width*2, height*2);
				
		ogre_left = new BufferedImage[2];
		ogre_left[0]=ogreSheet.crop( width *2, 0, width*2, height*2);
		ogre_left[1]=ogreSheet.crop( width*2 , 0, width*2, height*2);
			
		ogre_down = new BufferedImage[2];
		ogre_down[0]=ogreSheet.crop( width*4 , 0,width*2, height*2);
		ogre_down[1]=ogreSheet.crop( width*4 , 0, width*2, height*2);
				
				
		ogre_up = new BufferedImage[2];
		ogre_up[0]=ogreSheet.crop( width*6 , 0, width*2, height*2);
		ogre_up[1]=ogreSheet.crop( width*6 , 0, width*2, height*2);
		
		//Start
		buttonStart = new BufferedImage[2];
		buttonStart[0]=sheet.crop(width*4, 0, width*2, height);
		buttonStart[1]=sheet.crop(width*6, 0, width*2, height);
		
		//Settings
		
		buttonSettings= new BufferedImage[2];
		buttonSettings[0]=sheet.crop(width*4, height, width, height);
		buttonSettings[1]=sheet.crop(width*5, height, width, height);
		
		//Back
		
		buttonBack = new BufferedImage[2];
		buttonBack[0]=sheet.crop(0, height*6, width, height);
		buttonBack[1]=sheet.crop(width, height*6, width, height);
		
		//PLayer1 Attack
		aUp = new BufferedImage[2];
		aUp[0]=sheet.crop(width*3, height*4, width, height);
		aUp[1]=sheet.crop(width*3, height*5, width, height);
		
		aDown = new BufferedImage[2];
		aDown[0]=sheet.crop(0, height*4, width, height);
		aDown[1]=sheet.crop(0, height*5, width, height);
		
		aRight = new BufferedImage[2];
		aRight[0]=sheet.crop(width, height*4, width, height);
		aRight[1]=sheet.crop(width, height*5, width, height);
		
		aLeft = new BufferedImage[2];
		aLeft[0]=sheet.crop(width*2, height*4, width, height);
		aLeft[1]=sheet.crop(width*2, height*5, width, height);
		
		//Zombie Aggro
		
		zaDown = new BufferedImage[2];
		zaDown[0]=sheet.crop(width*8, height*4, width, height);
		zaDown[1]=sheet.crop(width*11, height*4, width, height);
		
		zaRight = new BufferedImage[2];
		zaRight[0]=sheet.crop(width*8, height*5, width, height);
		zaRight[1]=sheet.crop(width*11, height*5, width, height);
		
		zaLeft = new BufferedImage[2];
		zaLeft[0]=sheet.crop(width*9, height*5, width, height);
		zaLeft[1]=sheet.crop(width*10, height*5, width, height);


		//Ogre Attack

		oaRight = new BufferedImage[2];
		oaRight[0]=ogreSheet.crop( 0 , 0, width*2, height*2);
		oaRight[1]=ogreSheet.crop( 0 , 0, width*2, height*2);

		oaLeft = new BufferedImage[2];
		oaLeft[0]=ogreSheet.crop( width *2, 0, width*2, height*2);
		oaLeft[1]=ogreSheet.crop( width*2 , 0, width*2, height*2);

		oaDown = new BufferedImage[2];
		oaDown[0]=ogreSheet.crop( width*4 , 0,width*2, height*2);
		oaDown[1]=ogreSheet.crop( width*4 , 0, width*2, height*2);


		oaUp = new BufferedImage[2];
		oaUp[0]=ogreSheet.crop( width*6 , 0, width*2, height*2);
		oaUp[1]=ogreSheet.crop( width*6 , 0, width*2, height*2);


		//Item Placing Animation

	}
		
		

		
}
