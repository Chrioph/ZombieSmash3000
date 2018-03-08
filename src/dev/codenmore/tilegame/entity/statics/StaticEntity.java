package dev.codenmore.tilegame.entity.statics;

import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.entity.Entity;

/**
 * Static entity top class
 */
public abstract class StaticEntity extends Entity {
    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public StaticEntity(Handler handler, float x, float y, int width, int height) {
        super(handler, x, y, width, height);
    }


}
