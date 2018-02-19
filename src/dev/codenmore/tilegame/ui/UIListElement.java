package dev.codenmore.tilegame.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIListElement extends UIObject{

    private BufferedImage[] images;
    private ClickListener clicker;
    private ArrayList<UIObject> elements;
    private String text;

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
        this.width = text.length() * 30;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
    }

    @Override
    public void onClick() {
        clicker.onClick();
    }

    public String getText()
    {
        return this.text;
    }




}
