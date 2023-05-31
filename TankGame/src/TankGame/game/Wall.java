package TankGame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {
    Wall(float x, float y, BufferedImage img){
        super(x, y, img);
    }

    public Rectangle getHitbox(){
        return this.hitbox.getBounds();
    }

    @Override
    public void collides(GameObject g, GameWorld gw) {

    }

    @Override
    public boolean isCollided() {
        return false;
    }


    @Override
    public void update(GameWorld gw) {

    }

    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, (int)x, (int)y, null);

    }
}
