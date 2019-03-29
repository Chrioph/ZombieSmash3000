package com.goldenbois.zombiesmash.entity.creatures;


import com.goldenbois.zombiesmash.Settings;
import com.goldenbois.zombiesmash.crafting.CraftingScreen;
import com.goldenbois.zombiesmash.entity.statics.SolidWoodBlock;
import com.goldenbois.zombiesmash.entity.statics.WoodBlock;
import com.goldenbois.zombiesmash.inventory.Inventory;
import org.newdawn.slick.*;
import java.awt.event.KeyEvent;


import com.goldenbois.zombiesmash.Handler;
import com.goldenbois.zombiesmash.entity.Entity;
import com.goldenbois.zombiesmash.entity.statics.StaticEntity;
import com.goldenbois.zombiesmash.entity.statics.Tree;
import com.goldenbois.zombiesmash.gfx.Animation;
import com.goldenbois.zombiesmash.gfx.Assets;
import com.goldenbois.zombiesmash.items.CraftableItem;
import com.goldenbois.zombiesmash.items.Item;
import org.newdawn.slick.geom.Rectangle;

/**
 * Player class
 */
public class Player extends Creature {

    /**
     * TODO: Maybe move these to creature top class
     */

    private int knockbackCounter;
    private int ammunition = 5;
    private int armor = 4;
    private int maxArmor = 5;
    private int rangedDamage = 2;
    private boolean isDead = false;
    private int enemyDirection;
    private int playerDirection = 1;



    private boolean inputDisabled;


    private int placingItem = 0;

    private boolean rangedToggled = false;

    private long lastAttackTimer, attackCooldown = 800, attackTimer = attackCooldown;
    private Arrow arrow;

    private Inventory inventory;
    private transient CraftingScreen craftingScreen;

    /**
     * Constructor
     *
     * @param handler
     * @param x
     * @param y
     */
    public Player(Handler handler, float x, float y) {
        super(handler, x, y, DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT);
        maxHealth = 10;
        health = 10;
        damage = 3;
        bounds.setX(20);
        bounds.setY(5);
        bounds.setWidth(22);
        bounds.setHeight(59);


        inventory = new Inventory(handler);
        craftingScreen = new CraftingScreen(handler);
    }

    /**
     * Constructor for serialization
     */
    public Player() {
        super();
        craftingScreen = new CraftingScreen(handler);
    }

    /**
     *
     */
    protected void loadAnimations() {
        animDown = new Animation(500, Assets.player1_down);
        animUp = new Animation(500, Assets.player1_up);
        animLeft = new Animation(500, Assets.player1_left);
        animRight = new Animation(500, Assets.player1_right);

        animAUp = new Animation(400, Assets.aUp);
        animADown = new Animation(400, Assets.aDown);
        animALeft = new Animation(400, Assets.aLeft);
        animARight = new Animation(400, Assets.aRight);

        idle = Assets.player1LookingDown;
    }

    /**
     * own tick function
     */
    @Override
    public void tick() {
        if (knockbackCounter > 0) {
            knockback(enemyDirection);
            knockbackCounter--;
        }
        animDown.tick();
        animUp.tick();
        animLeft.tick();
        animRight.tick();
        animADown.tick();
        animAUp.tick();
        animALeft.tick();
        animARight.tick();
        checkAlive();
        if (!(knockbackCounter > 0))
            getInput();

        move();
        handler.getGameCamera().centerOnEntity(this);

        checkToggledRangeAttacks();

        checkAttacks();


        inventory.tick();
        craftingScreen.tick();
    }

    private void checkAlive() {
        if (health <= 0) {
            health = 0;
            isDead = true;
        }

    }

    private void checkToggledRangeAttacks() {
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_T)&&!inputDisabled) {
            rangedToggled = !rangedToggled;
        }

    }

    private void checkAttacks() {
        if (attackTimer < attackCooldown) {
            attackTimer += System.currentTimeMillis() - lastAttackTimer;
            lastAttackTimer = System.currentTimeMillis();
            return;
        }
        if (attackTimer > attackCooldown + 30) {
            attackTimer = 0;
            return;
        }
        if (inventory.isActive() || craftingScreen.isActive())
            return;
        if(inputDisabled)
            return;
        Rectangle ar = new Rectangle(0,0,0,0);
        if (rangedToggled == false) {
            Rectangle cb = getCollisionBounds(0, 0);


            int arSize = 50;
            ar.setWidth(arSize);
            ar.setHeight(arSize);

            if (handler.getKeyManager().aUp) {
                ar.setX(cb.getX() + cb.getWidth() / 2 - arSize / 2);
                ar.setY(cb.getY() - arSize);
            } else if (handler.getKeyManager().aDown) {
                ar.setX(cb.getX() + cb.getWidth() / 2 - arSize / 2);
                ar.setY(cb.getY() + cb.getHeight());
            } else if (handler.getKeyManager().aLeft) {
                ar.setX(cb.getX() - arSize);
                ar.setY(cb.getY() + cb.getHeight() / 2 - arSize / 2);
            } else if (handler.getKeyManager().aRight) {
                ar.setX(cb.getX() + cb.getWidth());
                ar.setY(cb.getY() + cb.getHeight() / 2 - arSize / 2);
            } else {
                return;
            }

        } else if (ammunition > 0) {
            if (handler.getKeyManager().aRight) {
                arrow = new Arrow(handler, (int) x + 40, (int) (y), 0, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aDown) {
                arrow = new Arrow(handler, (int) x, (int) (y + 64), 1, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aLeft) {
                arrow = new Arrow(handler, (int) x - 40, (int) (y), 2, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aUp) {
                arrow = new Arrow(handler, (int) x, (int) (y - 49), 3, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();

            } else
                return;


        }
        attackTimer = 0;

        for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
            if (e.equals(this))
                continue;
            if (e.getCollisionBounds(0, 0).intersects(ar)) {
                e.hurt(damage);
                e.knockback();
                e.setKnockbackCounter(7);
                return;
            }
        }
        for (Entity e : handler.getWorld().getEntityManager().getProjectiles()) {
            if (e.equals(this))
                continue;
            if (e.getCollisionBounds(0, 0).intersects(ar)) {
                e.hurt(damage);
                e.knockback();
                e.setKnockbackCounter(7);
                return;
            }
        }
    }

    //GameOver Fenster �ffnen
    public void die() {
    }

    public void hurtAnimation(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(0, 0, 1920, 4);
        g.fillRect(0, 0, 4, 1080);
        g.fillRect(0, 1080 - 4, 1920, 4);
        g.fillRect(1920 - 4, 0, 4, 1080);
    }


    private void getInput() {
        if (inputDisabled)
            return;

        xMove = 0;
        yMove = 0;

        if (inventory.isActive() || craftingScreen.isActive())
            return;
        xAttack = 0;
        yAttack = 0;
        if (handler.getKeyManager().up) {
            yMove = -speed;
            playerDirection = 3;
        }
        if (handler.getKeyManager().down) {
            yMove = speed;
            playerDirection = 1;
        }
        if (handler.getKeyManager().right) {
            xMove = speed;
            playerDirection = 0;
        }
        if (handler.getKeyManager().left) {
            xMove = -speed;
            playerDirection = 2;
        }

        if (handler.getKeyManager().aUp)
            yAttack = -1;
        else if (handler.getKeyManager().aDown)
            yAttack = 1;
        else if (handler.getKeyManager().aRight)
            xAttack = 1;
        else if (handler.getKeyManager().aLeft)
            xAttack = -1;
        ;
    }


    @Override
    public void render(Graphics g) {
        super.render(g);
        int arSize = 50;
        g.drawImage(getCurrentAnimationFrame(), (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));

        renderPlacingItem(g);

        if (Settings.getDebug()) {
            Rectangle ar = new Rectangle(0,0,0,0);
            Rectangle cb = getCollisionBounds(0, 0);
            ar.setWidth(arSize);
            ar.setHeight(arSize);
            // jeweilige Hitbox hier eintragen
            ar.setX(cb.getX() + cb.getWidth());
            ar.setY(cb.getY() + cb.getHeight() / 2 - arSize / 2);
            //
            g.drawImage(getCurrentAnimationFrame(), (x - handler.getGameCamera().getxOffset()), (y - handler.getGameCamera().getyOffset()));
            g.setColor(Color.black);
            g.fillRect(ar.getX(), ar.getY(), ar.getWidth(), ar.getHeight());
            g.setColor(Color.red);
            g.fillRect((int) (bounds.getX() + x - handler.getGameCamera().getxOffset()), (int) (bounds.getY() + y - handler.getGameCamera().getyOffset()), (int) bounds.getWidth(), (int) bounds.getHeight());
        }

    }

    private void renderPlacingItem(Graphics g) {
        if (placingItem > 0) {
            g.setColor(Color.pink);
            if (playerDirection == 0) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH * 1.5 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (x + DEFAULT_CREATURE_WIDTH - handler.getGameCamera().getxOffset()), (y + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()));
            } else if (playerDirection == 1) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT * 1.5 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (x + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (y + DEFAULT_CREATURE_HEIGHT - handler.getGameCamera().getyOffset()));
            } else if (playerDirection == 2) {
                g.fillRect((int) (x - 2 - DEFAULT_CREATURE_WIDTH / 2 - 128 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (x - DEFAULT_CREATURE_WIDTH / 2 - 128 - handler.getGameCamera().getxOffset()), (y + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()));
            } else if (playerDirection == 3) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - 2 - DEFAULT_CREATURE_HEIGHT / 2 - 128 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (x + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - DEFAULT_CREATURE_HEIGHT / 2 - 128 - handler.getGameCamera().getyOffset()));
            }
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_R)) {
                if (playerDirection == 0)
                    handler.getWorld().getEntityManager().getProjectiles().add(getCurrentPlacingEntity((x + DEFAULT_CREATURE_WIDTH * 1.5f), (y + DEFAULT_CREATURE_HEIGHT / 2 - 64)));
                if (playerDirection == 1)
                    handler.getWorld().getEntityManager().getProjectiles().add(getCurrentPlacingEntity((x + DEFAULT_CREATURE_WIDTH / 2 - 64), (int) (y + DEFAULT_CREATURE_HEIGHT * 1.5f)));
                if (playerDirection == 2)
                    handler.getWorld().getEntityManager().getProjectiles().add(getCurrentPlacingEntity((x - DEFAULT_CREATURE_WIDTH / 2 - 128), (int) (y + DEFAULT_CREATURE_HEIGHT / 2 - 64)));
                if (playerDirection == 3)
                    handler.getWorld().getEntityManager().getProjectiles().add(getCurrentPlacingEntity((x + DEFAULT_CREATURE_WIDTH / 2 - 64), (int) (y - DEFAULT_CREATURE_HEIGHT / 2 - 128)));
                inventory.getInventoryItems().get(placingItem).setCount(inventory.getInventoryItems().get(placingItem).getCount() - 1);
                placingItem = 0;

            }
        }
    }


    public void knockback(int direction) {
        xMove = 0;
        yMove = 0;
        enemyDirection = direction;
        if (direction == 0) {
            xMove = -5.0f;
            yMove = -5.0f;
        }
        if (direction == 1) {
            xMove = -5.0f;
            yMove = 5.0f;
        }
        if (direction == 2) {
            xMove = 5.0f;
            yMove = -5.0f;
        }
        if (direction == 3) {
            xMove = 5.0f;
            yMove = 5.0f;
        }
        if (direction == 4) {
            xMove = -5.0f;
        }
        if (direction == 6) {
            xMove = 5.0f;
        }
        if (direction == 7) {
            yMove = -5.0f;
        }
        if (direction == 8) {
            yMove = 5.0f;
        }
        move();

    }

    public void postRender(Graphics g) {
        inventory.render(g);
        if (!inputDisabled)
            craftingScreen.render(g);
    }

    public boolean isPlayer() {
        return true;
    }

    public boolean collisionWithFinish(int x, int y) {
        return handler.getWorld().getTile(x, y).isFinish();
    }

    private Image getCurrentAnimationFrame() {
        if (isDead)
            return Assets.gravestone;

        if (xMove < 0)
            return animLeft.getCurrentFrame();
        if (xMove > 0)
            return animRight.getCurrentFrame();
        if (yMove < 0)
            return animUp.getCurrentFrame();
        if (yMove > 0)
            return animDown.getCurrentFrame();


        if (xAttack < 0)
            return animALeft.getCurrentFrame();
        if (xAttack > 0)
            return animARight.getCurrentFrame();
        if (yAttack < 0)
            return animAUp.getCurrentFrame();
        if (yAttack > 0)
            return animADown.getCurrentFrame();


        if (playerDirection == 0)
            return Assets.player1LookingRight;
        if (playerDirection == 1)
            return Assets.player1LookingDown;
        if (playerDirection == 2)
            return Assets.player1LookingLeft;
        else
            return Assets.player1LookingUp;
    }

    private Image getCurrentPlacementItem() {
        if (placingItem == Item.seedItem.getId()) {
            return Assets.tree;
        }
        if (placingItem == CraftableItem.woodItem.getId()) {
            return Assets.wood;
        }
        if (placingItem == CraftableItem.solidWoodItem.getId()) {
            return Assets.solidWood;
        }
        return null;
    }

    private StaticEntity getCurrentPlacingEntity(float x, float y) {
        if (placingItem == Item.seedItem.getId())
            return new Tree(handler, x, y);
        if (placingItem == CraftableItem.woodItem.getId())
            return new WoodBlock(handler, x, y);
        if (placingItem == CraftableItem.solidWoodItem.getId())
            return new SolidWoodBlock(handler, x, y);
        else return null;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }


    public int getMaxArmor() {
        return maxArmor;
    }

    public void setMaxArmor(int maxArmor) {
        this.maxArmor = maxArmor;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getAmmunition() {
        return ammunition;
    }

    public void setAmmunition(int ammunition) {
        this.ammunition = ammunition;
    }

    public boolean isRangedToggled() {
        return rangedToggled;
    }

    public void setRangedToggled(boolean rangedToggled) {
        this.rangedToggled = rangedToggled;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getRangedDamage() {
        return rangedDamage;
    }

    public void setRangedDamage(int rangedDamage) {
        this.rangedDamage = rangedDamage;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public CraftingScreen getCraftingScreen() {
        return craftingScreen;
    }

    public void setCraftingScreen(CraftingScreen craftingScreen) {
        this.craftingScreen = craftingScreen;
    }

    public int getKnockbackCounter() {
        return knockbackCounter;
    }

    public void setKnockbackCounter(int knockbackCounter) {
        this.knockbackCounter = knockbackCounter;
    }

    @Override
    public void knockback() {
        // TODO Auto-generated method stub

    }

    public int getPlacingItem() {
        return placingItem;
    }

    public void setPlacingItem(int id) {
        placingItem = id;
    }

    public void setPlacingitem(int id) {
        placingItem = id;
    }
    public boolean isInputDisabled() {
        return inputDisabled;
    }

    public void setInputDisabled(boolean inputDisabled) {
        this.inputDisabled = inputDisabled;
    }
}
