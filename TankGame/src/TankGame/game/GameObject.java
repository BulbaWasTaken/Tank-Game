package TankGame.game;

import TankGame.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;


public abstract class GameObject {

    protected float x;
    protected float y;
    protected BufferedImage img;
    protected Rectangle hitbox;

    GameObject(float x, float y, BufferedImage img){
        this.x = x;
        this.y = y;
        this.img = img;
        this.hitbox = new Rectangle((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }
    public static GameObject getNewInstance(String type, float x, float y, float angle){
        return switch (type){
            case "1" -> new Wall(x,y, ResourceManager.getSprite("wall"));
            case "2" -> new BreakableWall(x,y, ResourceManager.getSprite("breakWall1"));
            case "3" -> new BreakableWall(x,y, ResourceManager.getSprite("breakWall2"));
            case "4" -> new Speed(x,y, ResourceManager.getSprite("speed"));
            case "5" -> new FireRate(x,y, ResourceManager.getSprite("shield"));
            case "6" -> new Health(x,y, ResourceManager.getSprite("health"));
            case "10" -> new Tank(x,y, ResourceManager.getSprite("tank1"));
            case "20" -> new Tank(x,y, ResourceManager.getSprite("tank2"));
            case "88" -> new Bullets(x, y, angle, ResourceManager.getSprite("bullet"));
            default -> throw new IllegalArgumentException("Type not Supported");
        };
    }
    public abstract Rectangle getHitbox();
    public abstract boolean isCollided();
    public abstract void collides(GameObject g, GameWorld gw);
    public abstract void update(GameWorld gw);
    public abstract void drawImage(Graphics g);
}
