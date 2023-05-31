package TankGame.game;

import TankGame.GameConstants;
import TankGame.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author anthony-pc
 */
public class Tank extends GameObject {
    private float saveX;
    private float saveY;
    private final float initialSpawnX;
    private final float initialSpawnY;
    private float screen_x;
    private float screen_y;
    private float vx;
    private float vy;
    private float angle;

    private float R = GameConstants.DEFAULT_SPEED;
    private final float ROTATIONSPEED = 3.0f;

    private float health;
    private float lives;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed;
    private boolean collided;
    private boolean speedActive;
    private boolean fireRateActive;
    private boolean respawned;

    long timeSinceLastShot = 0L;
    long cooldown = GameConstants.DEFAULT_FIRE_RATE;

    private Timer timer;


    Tank(float x, float y, BufferedImage img) {
        super(x, y, img);
        this.initialSpawnX = x;
        this.initialSpawnY = y;
        this.health = 50;
        this.vx = 0;
        this.vy = 0;
        this.angle = 0;
        this.collided = false;
        this.lives = 3;
        this.respawned = false;
        centerScreen();
    }

    public void update(GameWorld gw) {
        if (!collided) {
            saveCoordinatesX();
            saveCoordinatesY();
        }
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        if (this.ShootPressed && ((this.timeSinceLastShot + this.cooldown) < System.currentTimeMillis())) {
            this.timeSinceLastShot = System.currentTimeMillis();
            gw.gameObj.add(GameObject.getNewInstance("88", this.shootX(), this.shootY(), angle));
            gw.activeSounds.add(ResourceManager.getSound("shotfiring"));

            Animation a = new Animation(ResourceManager.getAnimation("bullet"), this.shootX(), this.shootY(), 10);
            gw.activeAnimations.add(a);
        }
        this.hitbox.setLocation((int) x, (int) y);

        if (this.health <= 0) {
            Animation b = new Animation(ResourceManager.getAnimation("nuke"), this.x + (float) (img.getWidth() / 2), this.y + (float) (img.getHeight() / 2), 100);
            gw.activeAnimations.add(b);
            ResourceManager.getSound("shotexplosion").play();
            this.lives -= 1;
            this.health = 50;
            this.x = initialSpawnX;
            this.y = initialSpawnY;
            centerScreen();
            respawnShield();
        }
    }

    public Rectangle getHitbox() {
        return this.hitbox.getBounds();
    }

    public float getScreen_x() {
        return screen_x;
    }

    public float getScreen_y() {
        return screen_y;
    }

    private int shootX() {
        return (int) (this.x + (img.getWidth()) / 2 + (26 * Math.cos(Math.toRadians(angle))));
    }

    private int shootY() {
        return (int) (this.y + ((img.getHeight() / 2)) + (26 * Math.sin(Math.toRadians(angle))));
    }

    void setX(float x) {
        this.x = x;
    }

    void setY(float y) {
        this.y = y;
    }

    public int getX() {
        return (int) this.x;
    }

    public int getY() {
        return (int) this.y;
    }

    public boolean isCollided() {
        return collided;
    }

    public boolean isRespawned() {
        return respawned;
    }

    public float getLives() {
        return lives;
    }

    @Override
    public void collides(GameObject g, GameWorld gw) {
        if (g instanceof Wall || g instanceof BreakableWall) {
            setCollided(true);
            handleCollision();
        }
        if (g instanceof Speed) {
            Sound soundEffect = ResourceManager.getSound("pickup");
            soundEffect.setVolume((float) .6);
            if (!speedActive) {
                soundEffect.play();
                ((Speed) g).setCollided(true);
                activateSpeed();
            }
        }
        if (g instanceof Health) {
            Sound soundEffect = ResourceManager.getSound("pickup");
            soundEffect.setVolume((float) 0.6);
            if (health >= 50) {
                health = 50;
            } else if (health > 45) {
                soundEffect.play();
                ((Health) g).setCollided(true);
                health = 50;
            } else if (health <= 45) {
                soundEffect.play();
                ((Health) g).setCollided(true);
                health += 7;
            }
        }
        if (g instanceof FireRate) {
            Sound reload = ResourceManager.getSound("reload");
            reload.setVolume((float) 0.6);
            if (!fireRateActive) {
                ((FireRate) g).setCollided(true);
                reload.play();
                activateFireRate();
            }
        }
    }

    public void activateFireRate() {
        fireRateActive = true;
        timer = new Timer();
        cooldown = 50;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivateFireRate();
            }
        }, GameConstants.DEFAULT_POWERUP_TIMER);
    }

    public void activateSpeed() {
        speedActive = true;
        timer = new Timer();
        R += 3;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivateSpeed();
            }
        }, GameConstants.DEFAULT_POWERUP_TIMER); // 10 seconds in milliseconds
    }

    public void respawnShield() {
        this.respawned = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                deactivateRespawn();
            }
        }, 5000);
    }

    public void deactivateFireRate() {
        fireRateActive = false;
        cooldown = GameConstants.DEFAULT_FIRE_RATE;
    }

    public void deactivateSpeed() {
        speedActive = false;
        R = GameConstants.DEFAULT_SPEED;
    }

    public void deactivateRespawn() {
        this.respawned = false;
    }

    public void saveCoordinatesX() {
        saveX = x;
    }

    public void saveCoordinatesY() {
        saveY = y;
    }

    private void centerScreen() {
        this.screen_x = this.x;
        this.screen_y = this.y;

        if (x > (float)((GameConstants.GAME_SCREEN_WIDTH) / 4)) {
            this.screen_x = this.x - (GameConstants.GAME_SCREEN_WIDTH / 4);
        }
        if (x <= (float)(GameConstants.GAME_SCREEN_WIDTH / 4)) {
            this.screen_x = 0;
        }
        if (x >= (float)((GameConstants.WORLD_WIDTH) - (GameConstants.GAME_SCREEN_WIDTH / 4))) {
            this.screen_x = (float)(GameConstants.WORLD_WIDTH - (GameConstants.GAME_SCREEN_WIDTH / 2));
        }

        if (y > (float)(GameConstants.GAME_SCREEN_HEIGHT/ 2)) {
            this.screen_y = this.y - ((float)GameConstants.GAME_SCREEN_HEIGHT / 2);
        }
        if (y <= (float)(GameConstants.GAME_SCREEN_HEIGHT / 2)) {
            this.screen_y = 0;
        }
        if (y >= (float)((GameConstants.WORLD_HEIGHT) - (GameConstants.GAME_SCREEN_HEIGHT / 2))) {
            this.screen_y = (float)(GameConstants.WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT);
        }
    }

    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.WORLD_WIDTH - 80) {
            x = GameConstants.WORLD_WIDTH - 80;
        }
        if (y < 30) {
            y = 30;
        }
        if (y >= GameConstants.WORLD_HEIGHT - 80) {
            y = GameConstants.WORLD_HEIGHT - 80;
        }
    }

    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    public void handleCollision() {
        if (collided) {
            this.x = saveX;
            this.y = saveY;
            collided = false;
        }
    }

    public void takeDamage(int damage) {
        this.health = this.health - damage;
    }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleShootPressed() {
        this.ShootPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void unToggleShootPressed() {
        this.ShootPressed = false;
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
        centerScreen();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
        centerScreen();
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        if (this.lives > 0) {
            g2d.drawImage(this.img, rotation, null);
        }
        if (health > 37) {
            g2d.setColor(Color.GREEN);
        } else if (health > 25) {
            g2d.setColor(Color.YELLOW);
        } else if (health > 12) {
            g2d.setColor(Color.ORANGE);
        } else {
            g2d.setColor(Color.RED);
        }
        g2d.fillRect((int) x, (int) y - 20, (int) this.health, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRect((int) x, (int) y - 20, (int) this.img.getWidth(), 5);

        if (this.lives == 3) {
            g2d.setColor(Color.GREEN);
            g2d.fillRoundRect((int) x + 10, (int) y + 60, 5, 5, 5, 5);
            g2d.fillRoundRect((int) x + 20, (int) y + 60, 5, 5, 5, 5);
            g2d.fillRoundRect((int) x + 30, (int) y + 60, 5, 5, 5, 5);
        } else if (this.lives == 2) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRoundRect((int) x + 10, (int) y + 60, 5, 5, 5, 5);
            g2d.fillRoundRect((int) x + 20, (int) y + 60, 5, 5, 5, 5);
        } else if (this.lives == 1) {
            g2d.setColor(Color.RED);
            g2d.fillRoundRect((int) x + 10, (int) y + 60, 5, 5, 5, 5);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect((int) x + 10, (int) y + 60, 5, 5, 5, 5);
        g2d.drawRoundRect((int) x + 20, (int) y + 60, 5, 5, 5, 5);
        g2d.drawRoundRect((int) x + 30, (int) y + 60, 5, 5, 5, 5);
        if (respawned) {
            g2d.setColor(Color.BLUE);
            g2d.drawRoundRect((int) x, (int) y, this.img.getWidth(), this.img.getHeight(), 200, 200);
        }
    }
}
