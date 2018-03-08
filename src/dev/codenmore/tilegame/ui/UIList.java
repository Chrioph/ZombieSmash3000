package dev.codenmore.tilegame.ui;


import dev.codenmore.tilegame.gfx.Assets;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIList extends UIObject{

    private BufferedImage[] images;
    private boolean opened;
    private boolean alwaysOpen;
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
    public UIList(float x, float y , int width, int height, String placeholder, ArrayList<UIListElement> options, boolean alwaysExtended) {
        super (x,y,width, height);
        this.placeholder = placeholder;
        this.alwaysOpen = alwaysExtended;
        opened = false;
        elements = options;
        if(alwaysExtended) {
            toggleElementsVisible();
        }

    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setFont(Assets.font40);
        g.drawImage(Assets.dropDown, (int)x, (int)y, 590, 100, null);
        g.drawString(placeholder,(int) x+590/2-placeholder.length()*15,(int) y+100/2+10);
        if(opened || alwaysOpen) {
            int pos = 1;


            for(UIListElement element : elements) {
                element.setX(x+10);
                element.setY(y+10 + 70*pos);
                element.updateBounds();
                g.drawImage(Assets.dropDownElement, (int)x+10, (int)y+10+70*pos, 570, 80, null);
                g.drawString(element.getText(),(int) element.getX()+20,(int) element.getY()+50);
                pos++;
            }
        }

    }

    @Override
    public void onClick() {
        opened = !opened;
        if(!alwaysOpen) {
            toggleElementsVisible();
        }
    }

    private void toggleElementsVisible()
    {
        for(UIListElement element : elements) {
            element.toggleVisible();
        }
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
