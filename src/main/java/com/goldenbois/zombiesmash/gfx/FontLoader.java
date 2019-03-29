package com.goldenbois.zombiesmash.gfx;

import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import java.io.IOException;

public class FontLoader {

	
	public static Font loadFont(String path, float size) {
		try {
			java.awt.Font awtFont = java.awt.Font.
					createFont(java.awt.Font.TRUETYPE_FONT,FontLoader.class.getResource(path).openStream()).deriveFont(java.awt.Font.PLAIN, size);
			org.newdawn.slick.UnicodeFont uniFont = new org.newdawn.slick.UnicodeFont(awtFont);
			uniFont.addAsciiGlyphs();
			uniFont.loadGlyphs();
			return uniFont;
		} catch (java.awt.FontFormatException  | IOException | SlickException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
		
	}
}
