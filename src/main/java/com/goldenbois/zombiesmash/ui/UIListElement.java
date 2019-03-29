package com.goldenbois.zombiesmash.ui;

import com.goldenbois.zombiesmash.Settings;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;

public class UIListElement extends UIObject{

    private Image[] images;
    private ClickListener clicker;
    private ArrayList<UIObject> elements;
    private String text;
    private boolean isVisible;
    private int offsetX = 10;
    private int offsetY = 0;

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    private Image backgroundImage;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param clicker
     */
    public UIListElement(float x, float y , int width, int height,String text, Image image, ClickListener clicker) {
        super (x,y,width, height);
        this.clicker=clicker;
        this.text = text;
        this.backgroundImage = image;
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

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }




}
