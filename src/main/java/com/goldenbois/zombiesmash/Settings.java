package com.goldenbois.zombiesmash;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by Freddy on 19.02.2018.
 */
public class Settings {

    private static String settingsFile = "settings.xml";

    private static int width = 1920;
    private static int height = 1080;
    private static double scaleX = 1.0;
    private static double scaleY = 1.0;
    private static boolean debug = false;

    private static boolean renderDungeon = false;

    public static void init()
    {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(new FileInputStream("settings.xml"));

            try {
                width = Integer.parseInt(properties.getProperty("resolutionX"));
                height = Integer.parseInt(properties.getProperty("resolutionY"));
                scaleX = Double.parseDouble(properties.getProperty("ScaleX"));
                scaleY = Double.parseDouble(properties.getProperty("ScaleY"));
            }catch(NumberFormatException numberE) {
               properties.setProperty("resolutionX", "1920");
                properties.setProperty("resolutionY", "1080");
                properties.setProperty("Scale", "1");
                try {
                    properties.storeToXML(new FileOutputStream("settings.xml"),"");
                }catch(Exception e2) {
                }
                width = 1920;
                height = 1080;
            }

        }catch(Exception e) {
            // there is no settings file yet, so create one with default options
            Properties saveProps = new Properties();
            saveProps.setProperty("resolutionX", "1920");
            saveProps.setProperty("resolutionY", "1080");
            saveProps.setProperty("ScaleX", "1.0");
            saveProps.setProperty("ScaleY", "1.0");
            try {
                saveProps.storeToXML(new FileOutputStream("settings.xml"), "");
            }catch(Exception e2) {
                System.out.println("Failed saving settings: " + e2.getMessage());
            }

        }

    }

    public static void save()
    {
        calcScale();
        Properties saveProps = new Properties();
        saveProps.setProperty("resolutionX", String.valueOf(width));
        saveProps.setProperty("resolutionY", String.valueOf(height));
        saveProps.setProperty("ScaleX", String.valueOf(scaleX));
        saveProps.setProperty("ScaleY", String.valueOf(scaleY));
        try {
            saveProps.storeToXML(new FileOutputStream("settings.xml"), "");
        }catch(Exception e2) {
            System.out.println("Failed saving settings: " + e2.getMessage());
        }
    }

    private static void calcScale()
    {
        // default scale is 1.0 for Full HD (1920x1080)
        scaleX = 1.0/(1920.0/width);
        scaleY = 1.0/(1080.0/height);
    }

    public static int getResolutionX()
    {
        return width;
    }

    public static int getResolutionY()
    {
        return height;
    }

    public static double getScaleX()
    {
        return scaleX;
    }

    public static double getScaleY()
    {
        return scaleY;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static void setDebug(boolean mode) {
        debug = mode;
    }

    public static boolean getRenderDungeon() {
        return renderDungeon;
    }

    public static void setRenderDungeon(boolean b) {
        renderDungeon = true;
    }


    public static void setResolutionX(int resX)
    {
        width = resX;
    }

    public static void setResolutionY(int resY)
    {
        height = resY;
    }

}
