package dev.codenmore.tilegame.gfx;

import java.awt.Font;
import java.awt.image.BufferedImage;

public class Assets {
	
	public static BufferedImage dirt,grass,stone;
	public static BufferedImage player1,zombie;
	public static BufferedImage wood, sword, bow, rock, tree, heart, armor, healthPlus;
	public static BufferedImage arrow, arrowRight, arrowDown, arrowLeft, arrowUp;
	public static BufferedImage inventoryScreen,settingsBackground;
	private static final int width = 32,height =32;
	public static BufferedImage[] player1_down , player1_up, player1_left, player1_right;
	public static BufferedImage[] zombie_down , zombie_up, zombie_left, zombie_right;
	public static BufferedImage[] aDown, aLeft, aRight, aUp;
	public static BufferedImage[] zaDown, zaLeft, zaRight, zaUp;
	public static BufferedImage[] buttonStart, buttonSettings, buttonBack;
	
	public static Font font56, font28, font40;
	
	public static void init() {
		font56 = FontLoader.loadFont("res/fonts/slkscr.ttf",56);
		font28 = FontLoader.loadFont("res/fonts/slkscr.ttf",28);
		font40 = FontLoader.loadFont("res/fonts/slkscr.ttf",40);
		
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/textures/Sheet2.png"));
		
		settingsBackground = ImageLoader.loadImage("/textures/SettingsBackground.png");
		inventoryScreen =ImageLoader.loadImage("/textures/inventoryScreen.png");
		player1 = sheet.crop(width*2, height, width, height); 
		
		dirt =     sheet.crop(width*2, 0, width, height);
		grass =    sheet.crop(width*3, 0, width, height);
		stone =    sheet.crop(0, height, width, height);
		
		tree =     sheet.crop(width,height,width,height);
		wood =     sheet.crop(width, 0, width, height);
		rock =     sheet.crop(0, 0, width, height);
		sword=     sheet.crop(width*3, height*3, width, height);
		bow  =     sheet.crop(width*2, height*6, width, height);
		heart=     sheet.crop(width*7, height*3, width, height);
		armor=     sheet.crop(0, height*7, width, height);
		healthPlus=sheet.crop(width*8, height*3, width, height);
		
		arrow     =   sheet.crop(width*3, height*6, width, height);
		arrowRight=   sheet.crop(width*4, height*6, width, height);
		arrowDown =   sheet.crop(width*5, height*6, width, height);
		arrowLeft =   sheet.crop(width*6, height*6, width, height);
		arrowUp   =   sheet.crop(width*7, height*6, width, height);
		
		//Player 1
		player1 =  sheet.crop(width*2, height, width, height); 
		
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
		
		//Player 2
		
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
		
		//Zombie Attack
		zaUp = new BufferedImage[2];
		zaUp[0]=sheet.crop(width*7, height*4, width, height);
		zaUp[1]=sheet.crop(width*7, height*5, width, height);
		
		zaDown = new BufferedImage[2];
		zaDown[0]=sheet.crop(4, height*4, width, height);
		zaDown[1]=sheet.crop(4, height*5, width, height);
		
		zaRight = new BufferedImage[2];
		zaRight[0]=sheet.crop(width*5, height*4, width, height);
		zaRight[1]=sheet.crop(width*5, height*5, width, height);
		
		zaLeft = new BufferedImage[2];
		zaLeft[0]=sheet.crop(width*6, height*4, width, height);
		zaLeft[1]=sheet.crop(width*6, height*5, width, height);
	}
		
		

		
}
