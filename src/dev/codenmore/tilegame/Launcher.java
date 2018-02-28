package dev.codenmore.tilegame;



public class Launcher {
	
	public static void main (String[] args) {
		parseArgs(args);

		Settings.init();
		Game game = new Game("Game",Settings.getResolutionX(),Settings.getResolutionY());
		game.start();
	}

	private static void parseArgs(String[] args)
	{
		for(String arg : args)
		{
			if(arg.equals("--debug")) {
				Settings.setDebug(true);
			}else if(arg.equals("--opengl")) {
				Settings.setOpenGl(true);
			}
		}
	}
	
	
	

}
