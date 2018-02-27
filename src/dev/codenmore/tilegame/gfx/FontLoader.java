package dev.codenmore.tilegame.gfx;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class FontLoader {

	
	public static Font loadFont(String path, float size) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, FontLoader.class.getResource(path).openStream()).deriveFont(Font.PLAIN, size);
		} catch (FontFormatException  | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
		
	}
}
