package dev.codenmore.tilegame;



public class Launcher {
	
	public static void main (String[] args) {
		if(args.length == 1 && args[0].equals("--debug")) {
			Settings.setDebug(true);
		}
		Settings.init();
		Game game = new Game("Game",Settings.getResolutionX(),Settings.getResolutionY());
		game.start();
	}
	
	
	

}
