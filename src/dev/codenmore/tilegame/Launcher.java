package dev.codenmore.tilegame;



public class Launcher {
	
	public static void main (String[] args) {
		if(args.length == 1 && args[0].equals("--debug")) {
			Settings.setDebug(true);
		}
		Game game = new Game("Game",1920,1080);
		game.start();
	}
	
	
	

}
