package dev.codenmore.tilegame.Modifiers;

import java.io.Serializable;

public class Mod implements Serializable {

    private String name;
    private double modifier;

    public double getModifier()
    {
        return modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }



    public Mod(String name, double modifier)
    {
        this.name = name;
        this.modifier = modifier;
    }

    public Mod() {

    }


}
