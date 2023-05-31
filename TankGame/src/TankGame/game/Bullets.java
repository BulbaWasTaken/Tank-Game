package TankGame.game;

import TankGame.GameConstants;
import TankGame.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullets extends GameObject {
    private float vx;
    private float vy;
    private float angle;
    private float R = 20;

    private boolean collided = false;

    Bullets(float x, float y, float angle, BufferedImage img) {
        super(x, y, img);
        this.angle = angle;
        this.collided = false;
    }

    public Rectangle getHitbox() {
        return this.hitbox.getBounds();
    }

    public boolean isCollided() {
        return collided;
    }

    @Override
    public void collides(GameObject g, GameWorld gw) {
        Animation a = new Animation(ResourceManager.getAnimation("bullet"), this.x, this.y, 10);
        if (g instanceof BreakableWall) {
            collided = true;
            gw.activeAnimations.add(a);
            ((BreakableWall) g).setCollided(true);

        }
        if (g instanceof Wall) {
            collided = true;
            gw.activeAnimations.add(a);
        }
        if (g instanceof Tank) {
            if (!((Tank) g).isRespawned()) {
                collided = true;
                gw.activeAnimations.add(a);
                ((Tank) g).takeDamage(GameConstants.BASE_DAMAGE);
            }
        }
    }

    public void update(GameWorld gw) {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        this.hitbox.setLocation((int) x, (int) y);
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        rotation.scale(1.5, 1.5);
        g2d.drawImage(this.img, rotation, null);
    }
}
