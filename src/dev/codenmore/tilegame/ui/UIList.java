package dev.codenmore.tilegame.ui;


import dev.codenmore.tilegame.gfx.Assets;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIList extends UIObject{

    private BufferedImage[] images;
    private boolean opened;
    private String placeholder;
    private ClickListener clicker;
    private ArrayList<UIListElement> elements;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param placeholder
     * @param options
     */
    public UIList(float x, float y , int width, int height, String placeholder, ArrayList<UIListElement> options) {
        super (x,y,width, height);
        this.placeholder = placeholder;
        opened = false;
        elements = options;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        g.setFont(Assets.font40);
        if(opened) {
            int pos = 2;
            g.drawRect((int)x,(int)y-height/2+20,placeholder.length() * 30,height);
            g.drawString(placeholder,(int) x,(int) y+40);
            for(UIListElement element : elements) {
                element.setX(x);
                element.setY(y + height*pos);
                element.updateBounds();
                g.drawRect((int)element.getX(),(int) element.getY()-element.getHeight()/2+20,element.getWidth(),element.getHeight());
                g.drawString(element.getText(),(int) element.getX(),(int) element.getY()+40);
                pos++;
            }
        }else {
            g.drawRect((int)x,(int)y-height/2+20,placeholder.length() * 30,height);
            g.drawString(placeholder,(int) x,(int) y+40);
        }

    }

    @Override
    public void onClick() {
        opened = !opened;
    }


    // CUSTOM Mouse Events for List Elements
    @Override
    public void onMouseMove(MouseEvent e)
    {
        super.onMouseMove(e);
        for(UIListElement element : elements){
            element.onMouseMove(e);
        }
    }

    @Override
    public void onMouseRelease(MouseEvent e)
    {
        super.onMouseRelease(e);
        for(UIListElement element : elements){
            element.onMouseRelease(e);
        }
    }





}
