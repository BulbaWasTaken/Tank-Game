package TankGame;


import TankGame.game.GameWorld;
import TankGame.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;


public class ResourceManager {
    private static final Map<String, BufferedImage> sprites = new HashMap<>();
    private static final Map<String, List<BufferedImage>> animations = new HashMap<>();
    private static final Map<String, Integer> animationInfo = new HashMap<>() {{
        put("bullet", 32);
        put("nuke", 24);
    }};
    private static final Map<String, Sound> sounds = new HashMap<>();

    public static void initSprites() {
        try {
            ResourceManager.sprites.put("tank1", loadSprite("tank/tank1.png"));
            ResourceManager.sprites.put("tank2", loadSprite("tank/tank2.png"));
            ResourceManager.sprites.put("startmenu", loadSprite("menu/title.png"));
            ResourceManager.sprites.put("wall", loadSprite("walls/unbreak.jpg"));
            ResourceManager.sprites.put("breakWall1", loadSprite("walls/break1.jpg"));
            ResourceManager.sprites.put("breakWall2", loadSprite("walls/break2.jpg"));
            ResourceManager.sprites.put("health", loadSprite("powerups/health.png"));
            ResourceManager.sprites.put("shield", loadSprite("powerups/shield.png"));
            ResourceManager.sprites.put("speed", loadSprite("powerups/speed.png"));
            ResourceManager.sprites.put("floor", loadSprite("floor/bg.bmp"));
            ResourceManager.sprites.put("bullet", loadSprite("bullet/bullet.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void initAnimations() {
        String animationBasePath = "animations/%s/%s_%04d.png";
        ResourceManager.animationInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> temp = new ArrayList<>(frameCount);
            try {
                for (int i = 0; i < frameCount; i++) {
                    String framePath = animationBasePath.formatted(animationName, animationName, i);
                    temp.add(loadSprite(framePath));
                }
                ResourceManager.animations.put(animationName, temp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private static void initSounds(){
        try {
            ResourceManager.sounds.put("bullet", ResourceManager.loadSound("sounds/bullet.wav"));
            ResourceManager.sounds.put("menumusic", ResourceManager.loadSound("sounds/menumusic.wav"));
            ResourceManager.sounds.put("battlemusic", ResourceManager.loadSound("sounds/Music.mid"));
            ResourceManager.sounds.put("pickup", ResourceManager.loadSound("sounds/pickup.wav"));
            ResourceManager.sounds.put("reload", ResourceManager.loadSound("sounds/reload.wav"));
            ResourceManager.sounds.put("shotexplosion", ResourceManager.loadSound("sounds/shotexplosion.wav"));
            ResourceManager.sounds.put("shotfiring", ResourceManager.loadSound("sounds/shotfiring.wav"));

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }


    public static BufferedImage getSprite(String key) {
        if (!ResourceManager.sprites.containsKey((key))) {
            throw new RuntimeException("%s is missing from resource map.".formatted(key));
        }
        return ResourceManager.sprites.get(key);
    }
    public static List<BufferedImage> getAnimation(String type) {
        if (!ResourceManager.animations.containsKey((type))) {
            throw new RuntimeException("%s is missing from resource map.".formatted(type));
        }
        return ResourceManager.animations.get(type);
    }
    public static Sound getSound(String type){
        if (!ResourceManager.sounds.containsKey((type))) {
            throw new RuntimeException("%s is missing from resource map.".formatted(type));
        }
        return ResourceManager.sounds.get(type);
    }

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource(path), "Could not find %s".formatted(path)));
    }
    private static Sound loadSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path)));
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);
        s.setVolume(2f);
        return s;
    }

    public static void loadAssets() {
        initSprites();
        initAnimations();
        initSounds();
    }
}
