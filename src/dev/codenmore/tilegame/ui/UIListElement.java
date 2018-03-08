package dev.codenmore.tilegame.ui;

import dev.codenmore.tilegame.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIListElement extends UIObject{

    private BufferedImage[] images;
    private ClickListener clicker;
    private ArrayList<UIObject> elements;
    private String text;
    private boolean isVisible;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param clicker
     */
    public UIListElement(float x, float y , int width, int height,String text, ClickListener clicker) {
        super (x,y,width, height);
        this.clicker=clicker;
        this.text = text;
        isVisible = false;
    }

    @Override
    public void tick() {

    }

    public void updateBounds()
    {
        this.bounds = new Rectangle((int) (x * Settings.getScaleX()), (int) (y  * Settings.getScaleY()),(int)(width * Settings.getScaleX()), (int)(height * Settings.getScaleY()));
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    @Override
    public void onClick() {
        if(isVisible) {
            clicker.onClick();
        }
    }

    public String getText()
    {
        return this.text;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public void toggleVisible()
    {
        isVisible=!isVisible;
    }




}
