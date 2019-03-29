package com.goldenbois.zombiesmash.ui;

import org.newdawn.slick.*;

/**
 * Created by Freddy on 26.02.2018.
 */
public class UIImage extends UIObject {

    private Image image;

    public UIImage(float x, float y, int width, int height, Image image)
    {
        super(x,y,width,height);
        this.image = image;
    }

    @Override
    public void tick() {

    }

    @Override
    public void onClick() {

    }

    public void render(Graphics g)
    {
        g.drawImage(image, x, y);
    }
}
