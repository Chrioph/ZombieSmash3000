package dev.codenmore.tilegame.ui;

import dev.codenmore.tilegame.gfx.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Freddy on 26.02.2018.
 */
public class UIImageTextButton extends UIImageButton {

    private String text;

    public UIImageTextButton(float x, float y , int width, int height, BufferedImage[] images, ClickListener clicker, String text) {
        super (x,y,width, height, images, clicker);
        this.text = text;
    }

    public void render(Graphics g)
    {
        super.render(g);
        g.setFont(Assets.font40);
        g.drawString(text, (int)x+width/2-((text.length()/2)*30), (int) y+height/2+10);
    }
}
