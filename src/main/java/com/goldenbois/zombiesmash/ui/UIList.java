package com.goldenbois.zombiesmash.ui;


import com.goldenbois.zombiesmash.gfx.Assets;

import org.newdawn.slick.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UIList extends UIObject{

    private Image[] images;
    private boolean opened;
    private boolean alwaysOpen;
    private boolean showPlaceholder;
    private Image backgroundImage;
    private String placeholder;
    private ClickListener clicker;
    private ArrayList<UIListElement> elements;

    public int getPaddingX() {
        return paddingX;
    }

    public void setPaddingX(int paddingX) {
        this.paddingX = paddingX;
    }

    public int getPaddingY() {
        return paddingY;
    }

    public void setPaddingY(int paddingY) {
        this.paddingY = paddingY;
    }

    private int paddingX = 10;
    private int paddingY = 0;

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    private int spacing = 0;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param placeholder
     * @param options
     */
    public UIList(float x, float y , int width, int height, String placeholder, ArrayList<UIListElement> options, boolean alwaysExtended, Image image, boolean showPlaceholder) {
        super (x,y,width, height);
        this.placeholder = placeholder;
        this.alwaysOpen = alwaysExtended;
        this.backgroundImage = image;
        this.showPlaceholder = showPlaceholder;
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
        int pos = 0;
        g.setFont(Assets.font40);
        g.drawImage(this.backgroundImage, x, y);
        if(showPlaceholder) {
            g.drawString(placeholder,(int) x+width/2-placeholder.length()*15,(int) y+height/2+10);
            pos = 1;
        }

        if(opened || alwaysOpen) {
            for(UIListElement element : elements) {
                element.setOffsetX(paddingX);
                element.setOffsetY(paddingY);
                element.setX(x+element.getOffsetX());
                element.setY(y +element.getOffsetY() + (element.getHeight()+spacing)*pos);
                element.updateBounds();
                g.drawImage(element.getBackgroundImage(), element.getX(), element.getY());
                g.drawString(element.getText(),(int) element.getX()+element.getWidth()/2-element.getText().length()*13,(int) element.getY()+element.getHeight()/2+10);
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
    public void onMouseMove(int oldx, int oldy, int newx, int newy)
    {
        super.onMouseMove(oldx, oldy, newx, newy);
        for(UIListElement element : elements){
            element.onMouseMove(oldx, oldy, newx, newy);
        }
    }

    @Override
    public void onMouseRelease(int button, int x, int y)
    {
        super.onMouseRelease(button, x, y);
        for(UIListElement element : elements){
            element.onMouseRelease(button, x, y);
        }
    }





}
