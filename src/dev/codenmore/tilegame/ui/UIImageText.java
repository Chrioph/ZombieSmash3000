package dev.codenmore.tilegame.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Freddy on 26.02.2018.
 */
public class UIImageText extends UIImage {

    private String text;

    public UIImageText(float x, float y, int width, int height, BufferedImage image, String text){
        super(x,y,width,height,image);
        this.text = text;
    }

    public void render(Graphics g)
    {
        super.render(g);
        g.drawString(text, (int)x+width/2-((text.length()/2)*30), (int) y+height/2+10);
    }
}
