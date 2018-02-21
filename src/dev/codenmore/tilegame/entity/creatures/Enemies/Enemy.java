package dev.codenmore.tilegame.entity.creatures.Enemies;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Modifiers.Mod;
import dev.codenmore.tilegame.entity.creatures.Creature;

import java.util.ArrayList;

public abstract class Enemy extends Creature {

    public Enemy(Handler handler , float x, float y, int width, int height) {
        super(handler, x, y,width,height);
        damage=DEFAULT_CREATURE_DAMAGE;
        speed=DEFAULT_SPEED;
        xMove=0;
        yMove=0;
    }
}
