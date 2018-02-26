package dev.codenmore.tilegame.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Freddy on 26.02.2018.
 */
public class UIImage extends UIObject {

    private BufferedImage image;

    public UIImage(float x, float y, int width, int height, BufferedImage image)
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
        g.drawImage(image, (int)x, (int)y, width, height, null);
    }
}
