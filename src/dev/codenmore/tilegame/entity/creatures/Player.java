package dev.codenmore.tilegame.entity.creatures;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


import dev.codenmore.tilegame.Handler;
import dev.codenmore.tilegame.Settings;
import dev.codenmore.tilegame.crafting.CraftingScreen;
import dev.codenmore.tilegame.entity.Entity;
import dev.codenmore.tilegame.entity.statics.SolidWoodBlock;
import dev.codenmore.tilegame.entity.statics.StaticEntity;
import dev.codenmore.tilegame.entity.statics.Tree;
import dev.codenmore.tilegame.entity.statics.WoodBlock;
import dev.codenmore.tilegame.gfx.Animation;
import dev.codenmore.tilegame.gfx.Assets;
import dev.codenmore.tilegame.gfx.BowShot;
import dev.codenmore.tilegame.gfx.Text;
import dev.codenmore.tilegame.gfx.WeaponDrawing;
import dev.codenmore.tilegame.gfx.SwordSwing;
import dev.codenmore.tilegame.inventory.ChestInventory;
import dev.codenmore.tilegame.inventory.Inventory;
import dev.codenmore.tilegame.items.CraftableItem;
import dev.codenmore.tilegame.items.Item;
import dev.codenmore.tilegame.tiles.Tile;

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
    /** currently playing melee swing, null when not attacking */
    private transient SwordSwing swordSwing;
    /** currently playing bow shot, null when not shooting */
    private transient BowShot bowShot;
    /** short text shown above the player, e.g. after switching weapons */
    private transient String popupText;
    private transient long popupStart;
    private static final long POPUP_DURATION = 900;

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
        super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);
        maxHealth = 10;
        health = 10;
        damage = 3;
        bounds.x = 20;
        bounds.y = 5;
        bounds.width = 22;
        bounds.height = 59;


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
        if (swordSwing != null && swordSwing.isFinished())
            swordSwing = null;
        if (bowShot != null && bowShot.isFinished())
            bowShot = null;
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
            showPopup(rangedToggled ? "BOW" : "SWORD");
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
        Rectangle ar = new Rectangle();
        int meleeDirection;
        if (rangedToggled == false) {
            Rectangle cb = getCollisionBounds(0, 0);


            int arSize = 50;
            ar.width = arSize;
            ar.height = arSize;

            if (handler.getKeyManager().aUp) {
                ar.x = cb.x + cb.width / 2 - arSize / 2;
                ar.y = cb.y - arSize;
                meleeDirection = SwordSwing.UP;
            } else if (handler.getKeyManager().aDown) {
                ar.x = cb.x + cb.width / 2 - arSize / 2;
                ar.y = cb.y + cb.height;
                meleeDirection = SwordSwing.DOWN;
            } else if (handler.getKeyManager().aLeft) {
                ar.x = cb.x - arSize;
                ar.y = cb.y + cb.height / 2 - arSize / 2;
                meleeDirection = SwordSwing.LEFT;
            } else if (handler.getKeyManager().aRight) {
                ar.x = cb.x + cb.width;
                ar.y = cb.y + cb.height / 2 - arSize / 2;
                meleeDirection = SwordSwing.RIGHT;
            } else {
                return;
            }
            // play the swing for every melee attack, also when it misses
            swordSwing = new SwordSwing(meleeDirection);
            playerDirection = meleeDirection;

        } else if (ammunition > 0) {
            int shotDirection;
            if (handler.getKeyManager().aRight) {
                shotDirection = BowShot.RIGHT;
                arrow = new Arrow(handler, (int) x + 40, (int) (y), 0, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aDown) {
                shotDirection = BowShot.DOWN;
                arrow = new Arrow(handler, (int) x, (int) (y + 64), 1, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aLeft) {
                shotDirection = BowShot.LEFT;
                arrow = new Arrow(handler, (int) x - 40, (int) (y), 2, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();
            } else if (handler.getKeyManager().aUp) {
                shotDirection = BowShot.UP;
                arrow = new Arrow(handler, (int) x, (int) (y - 49), 3, rangedDamage);
                handler.getWorld().getEntityManager().addProjectile(arrow);
                ammunition -= 1;
                ar = arrow.getBounds();

            } else
                return;
            bowShot = new BowShot(shotDirection);
            playerDirection = shotDirection;

        } else {
            // bow equipped but the quiver is empty
            if (!(handler.getKeyManager().aUp || handler.getKeyManager().aDown
                    || handler.getKeyManager().aLeft || handler.getKeyManager().aRight))
                return;
            showPopup("NO ARROWS!");
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
        g.setColor(Color.RED);
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
        boolean swinging = swordSwing != null && !isDead;
        boolean shooting = bowShot != null && !isDead;
        boolean weaponBehind = (swinging && swordSwing.isBehindPlayer()) || (shooting && bowShot.isBehindPlayer())
                || (!swinging && !shooting && playerDirection == 3);
        if (weaponBehind)
            renderWeapon(g, swinging, shooting);
        g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        if (!weaponBehind)
            renderWeapon(g, swinging, shooting);
        renderPopup(g);

        renderPlacingItem(g);

        if (Settings.getDebug()) {
            Rectangle ar = new Rectangle();
            Rectangle cb = getCollisionBounds(0, 0);
            ar.width = arSize;
            ar.height = arSize;
            // jeweilige Hitbox hier eintragen
            ar.x = cb.x + cb.width;
            ar.y = cb.y + cb.height / 2 - arSize / 2;
            //
            g.drawImage(getCurrentAnimationFrame(), (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
            g.setColor(Color.BLACK);
            g.fillRect(ar.x, ar.y, ar.width, ar.height);
            g.setColor(Color.red);
            g.fillRect((int) (bounds.x + x - handler.getGameCamera().getxOffset()), (int) (bounds.y + y - handler.getGameCamera().getyOffset()), (int) bounds.width, (int) bounds.height);
        }

    }

    /**
     * Draws the equipped weapon: the attack animation while attacking, otherwise the
     * weapon held at the player's side so it is always visible which one is equipped.
     */
    private void renderWeapon(Graphics g, boolean swinging, boolean shooting) {
        if (isDead)
            return;
        Graphics2D g2d = (Graphics2D) g;
        int sx = (int) (x - handler.getGameCamera().getxOffset());
        int sy = (int) (y - handler.getGameCamera().getyOffset());
        if (swinging) {
            // hand position of the attack pose sprite (64x64) for each direction
            int handX, handY;
            switch (swordSwing.getDirection()) {
                case SwordSwing.RIGHT: handX = sx + 46; handY = sy + 38; break;
                case SwordSwing.LEFT:  handX = sx + 18; handY = sy + 38; break;
                case SwordSwing.DOWN:  handX = sx + 32; handY = sy + 42; break;
                default:               handX = sx + 32; handY = sy + 30; break;
            }
            swordSwing.render(g2d, handX, handY);
            return;
        }
        if (shooting) {
            // the bow is held at the height of the arrow's shaft
            int handX, handY;
            switch (bowShot.getDirection()) {
                case BowShot.RIGHT: handX = sx + 44; handY = sy + 32; break;
                case BowShot.LEFT:  handX = sx + 20; handY = sy + 32; break;
                case BowShot.DOWN:  handX = sx + 31; handY = sy + 42; break;
                default:            handX = sx + 31; handY = sy + 4; break;
            }
            bowShot.render(g2d, handX, handY);
            return;
        }
        if (inputDisabled)
            return;
        renderHeldWeapon(g2d, sx, sy);
    }

    /** Weapon resting in the player's hand while not attacking. */
    private void renderHeldWeapon(Graphics2D g, int sx, int sy) {
        // hand position and the angle the weapon points to, per facing direction
        double handX, handY, angle;
        switch (playerDirection) {
            case 0:  handX = sx + 38; handY = sy + 44; angle = Math.toRadians(rangedToggled ? 0 : 60); break;
            case 2:  handX = sx + 26; handY = sy + 44; angle = Math.toRadians(rangedToggled ? 180 : 120); break;
            case 3:  handX = sx + 14; handY = sy + 42; angle = Math.toRadians(rangedToggled ? 180 : 100); break;
            default: handX = sx + 52; handY = sy + 44; angle = Math.toRadians(rangedToggled ? 0 : 80); break;
        }
        if (rangedToggled)
            WeaponDrawing.drawBow(g, handX, handY, angle, 36, 0);
        else
            WeaponDrawing.drawSword(g, handX, handY, angle, 40);
    }

    private void showPopup(String text) {
        popupText = text;
        popupStart = System.currentTimeMillis();
    }

    private void renderPopup(Graphics g) {
        if (popupText == null)
            return;
        long t = System.currentTimeMillis() - popupStart;
        if (t >= POPUP_DURATION) {
            popupText = null;
            return;
        }
        double p = t / (double) POPUP_DURATION;
        Graphics2D g2d = (Graphics2D) g;
        java.awt.Composite old = g2d.getComposite();
        // rise and fade out during the last third
        float alpha = (float) Math.min(1, (1 - p) * 3);
        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        int cx = (int) (x - handler.getGameCamera().getxOffset()) + width / 2;
        int cy = (int) (y - handler.getGameCamera().getyOffset()) - 14 - (int) (20 * p);
        Text.drawString(g, popupText, cx + 2, cy + 2, true, Color.BLACK, Assets.font28);
        Text.drawString(g, popupText, cx, cy, true, Color.WHITE, Assets.font28);
        g2d.setComposite(old);
    }

    private void renderPlacingItem(Graphics g) {
        if (placingItem > 0) {
            g.setColor(Color.PINK);
            if (playerDirection == 0) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH * 1.5 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (int) (x + DEFAULT_CREATURE_WIDTH * 1.5 - handler.getGameCamera().getxOffset()), (int) (y + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth(), getCurrentPlacingEntity(0, 0).getHeight(), null);
            } else if (playerDirection == 1) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT * 1.5 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (int) (x + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y + DEFAULT_CREATURE_HEIGHT * 1.5 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth(), getCurrentPlacingEntity(0, 0).getHeight(), null);
            } else if (playerDirection == 2) {
                g.fillRect((int) (x - 2 - DEFAULT_CREATURE_WIDTH / 2 - 128 - handler.getGameCamera().getxOffset()), (int) (y - 2 + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (int) (x - DEFAULT_CREATURE_WIDTH / 2 - 128 - handler.getGameCamera().getxOffset()), (int) (y + DEFAULT_CREATURE_HEIGHT / 2 - 64 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth(), getCurrentPlacingEntity(0, 0).getHeight(), null);
            } else if (playerDirection == 3) {
                g.fillRect((int) (x - 2 + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - 2 - DEFAULT_CREATURE_HEIGHT / 2 - 128 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth() + 4, getCurrentPlacingEntity(0, 0).getHeight() + 4);
                g.drawImage(getCurrentPlacementItem(), (int) (x + DEFAULT_CREATURE_WIDTH / 2 - 64 - handler.getGameCamera().getxOffset()), (int) (y - DEFAULT_CREATURE_HEIGHT / 2 - 128 - handler.getGameCamera().getyOffset()), getCurrentPlacingEntity(0, 0).getWidth(), getCurrentPlacingEntity(0, 0).getHeight(), null);
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

    private BufferedImage getCurrentAnimationFrame() {
        if (isDead)
            return Assets.gravestone;

        // attack pose with outstretched arm while the sword swing or bow shot plays
        int attackDirection = swordSwing != null ? swordSwing.getDirection()
                : bowShot != null ? bowShot.getDirection() : -1;
        if (attackDirection >= 0) {
            switch (attackDirection) {
                case SwordSwing.RIGHT: return Assets.aRight[0];
                case SwordSwing.DOWN:  return Assets.aDown[0];
                case SwordSwing.LEFT:  return Assets.aLeft[0];
                default:               return Assets.aUp[0];
            }
        }

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

    private BufferedImage getCurrentPlacementItem() {
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
