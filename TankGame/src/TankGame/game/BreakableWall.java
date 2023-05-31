package TankGame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWall extends GameObject {
    private boolean collided ;

    BreakableWall(float x, float y, BufferedImage img){
        super(x, y, img );
        collided = false;
    }

    public Rectangle getHitbox(){
        return this.hitbox.getBounds();
    }

    @Override
    public void update(GameWorld gw) {}

    @Override
    public void collides(GameObject g, GameWorld gw) {}

    @Override
    public boolean isCollided() {
        return collided;
    }
    public void setCollided(boolean collided){
        this.collided = collided;
    }

    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, (int)x, (int)y, null);
    }
}
