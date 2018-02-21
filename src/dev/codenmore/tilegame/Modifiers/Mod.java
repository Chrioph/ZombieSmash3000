package dev.codenmore.tilegame.Modifiers;

public class Mod {

    private String name;
    private double modifier;

    public Mod(String name, double modifier)
    {
        this.name = name;
        this.modifier = modifier;
    }

    public double getModifier()
    {
        return modifier;
    }
}
