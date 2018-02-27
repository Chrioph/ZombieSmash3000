package dev.codenmore.tilegame.state.elements;

import dev.codenmore.tilegame.ui.UIManager;

/**
 * Created by Freddy on 26.02.2018.
 */
public class Page implements Comparable{
    private UIManager uiManager;
    private String name;
    private int position;

    public Page(String name, int position)
    {
        this.name = name;
        this.position = position;
    }

    public String getName()
    {
        return name;
    }

    public int getPosition()
    {
        return position;
    }

    public void setUiManager(UIManager uiManager)
    {
        this.uiManager = uiManager;
    }

    public UIManager getUiManager()
    {
        return uiManager;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Page) {
            Page page = (Page) o;

            if(page.getName() == this.name && page.getPosition() == this.position) {
                return 0;
            }
        }
        return -1;

    }
}
