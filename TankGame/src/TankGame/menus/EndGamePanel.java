package TankGame.menus;

import TankGame.Launcher;
import TankGame.ResourceManager;
import TankGame.game.GameWorld;
import TankGame.game.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;
    Sound menumusic = ResourceManager.getSound("menumusic");


    public EndGamePanel(Launcher lf) {
        this.lf = lf;
        menuBackground = ResourceManager.getSprite("startmenu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JLabel winnerLabel = new JLabel(this.lf.getWinner());
        winnerLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        winnerLabel.setForeground(Color.PINK);
        winnerLabel.setBounds(150, 300, 250, 50);

        JButton start = new JButton("Restart");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 350, 150, 50);
        start.addActionListener(actionEvent -> {
            menumusic.stop();
            this.lf.setFrame("game");
        });


        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 400, 150, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));
        menumusic.setLooping();
        menumusic.setVolume(1.2f);
        menumusic.play();

        this.add(winnerLabel);
        this.add(start);
        this.add(exit);
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
    }
}
