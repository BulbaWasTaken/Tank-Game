package TankGame.game;


import TankGame.GameConstants;
import TankGame.Launcher;
import TankGame.ResourceManager;
import TankGame.menus.StartMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;
    private boolean isRunning = true;
    private Timer timer;
    Sound bgmusic;

    List<GameObject> gameObj = new ArrayList<>(600);
    List<Animation> activeAnimations = new ArrayList<>(15);
    List<Sound> activeSounds = new ArrayList<>(15);

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }


    @Override
    public void run() {
        try {
            resetGame();
            bgmusic = ResourceManager.getSound("battlemusic");
            bgmusic.setLooping();
            bgmusic.setVolume(0.07f);
            bgmusic.play();
            while (isRunning) {
                this.tick++;
                this.t1.update(this);
                this.t2.update(this);

                for (int i = 0; i < gameObj.size(); i++) {
                    if (this.gameObj.get(i) != t1 && this.gameObj.get(i) != t2) {
                        this.gameObj.get(i).update(this);
                    }
                }

                this.checkCollision();
                for (int i = 0; i < activeAnimations.size(); i++) {
                    this.activeAnimations.get(i).update();
                }
                if (!activeAnimations.isEmpty()) {
                    for (int i = 0; i < activeSounds.size(); i++) {
                        activeSounds.get(i).play();
                        activeSounds.remove(i);
                    }
                }
                this.repaint();   // redraw game
                this.activeAnimations.removeIf(a -> !a.isRunning());
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our
                 * loop run at a fixed rate per/sec.
                 */
                Thread.sleep(1000 / 144);

                if (t1.getLives() == 0 || t2.getLives() == 0) {
                    endScreenDelay();
                }
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
        if (t1.getLives() == 0) {
            this.lf.setWinner("Tank 2 Wins!");
        } else if (t2.getLives() == 0) {
            this.lf.setWinner("Tank 1 Wins!");
        }
        bgmusic.stop();
        this.lf.addEndPanel();
        this.lf.setFrame("end");
    }

    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(GameWorld.class.getClassLoader().getResourceAsStream("maps/map1.csv")));
        try (BufferedReader mapReader = new BufferedReader(isr)) {
            for (int i = 0; mapReader.ready(); i++) {
                String[] items = mapReader.readLine().split(",");
                for (int j = 0; j < items.length; j++) {
                    String objectType = items[j];
                    if ("0".equals(objectType)) continue;
                    gameObj.add(GameObject.getNewInstance(objectType, j * 30, i * 30, 0));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading map.");
            System.exit(-2);
        }

        t1 = (Tank) GameObject.getNewInstance("10", GameConstants.PLAYER_ONE_SPAWN_X, GameConstants.PLAYER_ONE_SPAWN_Y, 0);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);
        //1770f 1320f
        t2 = (Tank) GameObject.getNewInstance("20", GameConstants.PLAYER_TWO_SPAWN_X, GameConstants.PLAYER_TWO_SPAWN_Y, 0);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_NUMPAD0);
        this.lf.getJf().addKeyListener(tc2);

        this.gameObj.add(t1);
        this.gameObj.add(t2);
    }

    public void resetGame() {
        this.tick = 0;
        gameObj.clear();
        activeAnimations.clear();
        isRunning = true;
        InitializeGame();
    }

    public void endScreenDelay() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endScreen();
            }
        }, 1500);
    }

    public void endScreen() {
        this.isRunning = false;
    }

    private void checkCollision() {
        for (int i = 0; i < this.gameObj.size(); i++) {
            GameObject obj1 = this.gameObj.get(i);
            if (obj1 instanceof Wall || obj1 instanceof Speed || obj1 instanceof Health || obj1 instanceof FireRate || obj1 instanceof BreakableWall) {
                continue;
            }
            for (int j = 0; j < this.gameObj.size(); j++) {
                if (i == j) continue;
                GameObject obj2 = this.gameObj.get(j);
                if (obj2 instanceof Tank && obj1 instanceof Tank || obj2 instanceof Bullets) continue;
                if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj1.collides(obj2, this);
                }
            }
        }
        this.gameObj.removeIf(GameObject::isCollided);
    }

    private void drawFloor(Graphics2D buffer) {
        BufferedImage floor = ResourceManager.getSprite("floor");
        for (int i = 0; i < GameConstants.WORLD_WIDTH; i += 320) {
            for (int j = 0; j < GameConstants.WORLD_HEIGHT; j += 240) {
                buffer.drawImage(floor, i, j, null);
            }
        }
    }

    private void renderMap(Graphics2D g2, BufferedImage world) {
        BufferedImage miniMap = world.getSubimage(0, 0, GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        g2.scale(.1, .1);
        g2.drawImage(miniMap, (GameConstants.GAME_SCREEN_WIDTH * 10) / 2 - (GameConstants.WORLD_WIDTH / 2), 100, null);
    }

    private void renderSplitScreen(Graphics2D g2, BufferedImage world) {
        BufferedImage lh = world.getSubimage((int) this.t1.getScreen_x(), (int) this.t1.getScreen_y(), (GameConstants.GAME_SCREEN_WIDTH / 2), GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rh = world.getSubimage((int) this.t2.getScreen_x(), (int) this.t2.getScreen_y(), (GameConstants.GAME_SCREEN_WIDTH / 2), GameConstants.GAME_SCREEN_HEIGHT);
        g2.drawImage(lh, 0, 0, null);
        g2.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH / 2, 0, null);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        this.drawFloor(buffer);

        //this.gameObj.forEach(obj -> obj.drawImage(buffer));
        for (int i = 0; i < this.gameObj.size(); i++) {
            this.gameObj.get(i).drawImage(buffer);
        }

        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);

        //this.activeAnimations.forEach(a -> a.drawImage(buffer));
        for (int i = 0; i < this.activeAnimations.size(); i++) {
            this.activeAnimations.get(i).drawImage(buffer);
        }

        renderSplitScreen(g2, world);
        g2.setColor(Color.BLACK);
        g2.drawLine(GameConstants.GAME_SCREEN_WIDTH/2, 0,(GameConstants.GAME_SCREEN_WIDTH/2), GameConstants.GAME_SCREEN_HEIGHT);
        renderMap(g2, world);
    }
}
