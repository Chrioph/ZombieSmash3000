package com.goldenbois.zombiesmash.utils;

import java.io.*;
import java.util.Random;

public class Utils {
	
	public static String loadFileAsString(String path) {
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader (new InputStreamReader(Utils.class.getResource(path).openStream()));
			String line;
			while ((line = br.readLine()) !=null) {
				builder.append(line + "\n");
			}
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public static int parseInt(String number) {
		try {
			return Integer.parseInt(number);
		}
		catch(NumberFormatException nfe) {
			nfe.printStackTrace();
			return 0;
		}
	}
	public static int generateRandomInt(int x){
		Random generator = new Random ();
		return generator.nextInt(x);
	}

}
